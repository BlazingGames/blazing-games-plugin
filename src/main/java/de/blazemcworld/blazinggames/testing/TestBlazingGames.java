/*
 * Copyright 2025 The Blazing Games Maintainers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.blazemcworld.blazinggames.testing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import de.blazemcworld.blazinggames.BlazingGames;

public class TestBlazingGames extends BlazingGames {
    public static final int TEST_RUNNERS = 5;
    private static final ArrayList<TestList> tests = new ArrayList<>(List.of(TestList.values()));
    private static final ArrayList<TestList> failedTests = new ArrayList<>();
    private static boolean started = false;
    private static int remainingRunners = TEST_RUNNERS;

    @Override
    public void onEnable() {
        try {
            super.onEnable();

            if (!started) {
                started = true;

                if (tests.isEmpty()) {
                    getLogger().severe("No tests found!");
                    exit(false);
                    return;
                }

                getLogger().info("Starting " + tests.size() + " tests with " + TEST_RUNNERS + " runners...");

                int runners = TEST_RUNNERS > tests.size() ? tests.size() : TEST_RUNNERS;
                if (runners != TEST_RUNNERS) {
                    getLogger().warning("Not enough tests to run with " + TEST_RUNNERS + " runners! Running with " + runners + " instead");
                    remainingRunners = tests.size();
                }

                for (int i = 0; i < runners; i++) {
                    scheduleNextTest(null, false);
                }

                getLogger().info("Tests starting soon...");
            } else {
                getLogger().severe("Plugin was loaded twice. Exiting.");
                exit(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An unhandled exception occurred in onEnable. Exiting.");
            exit(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("An unhandled exception occurred in onDisable. Exiting.");
            exit(false);
        }
    }
    
    @Override
    public @NotNull FileConfiguration getConfig() {
        var config = new YamlConfiguration();

        // defaults (config.yml)
        try (
            InputStream stream = getClass().getResourceAsStream("/config.yml");
            Reader reader = new InputStreamReader(stream);
        ) {
            config.setDefaults(YamlConfiguration.loadConfiguration(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // overrides (config.testing.yml)
        try (
            InputStream stream = getClass().getResourceAsStream("/config.testing.yml");
            Reader reader = new InputStreamReader(stream);
        ) {
            config.load(reader);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        return config;
    }

    public static void exit(boolean success) {
        File file = new File("TESTS_RESULT");
        file.delete();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(success ? "true" : "false");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Bukkit.getServer().shutdown();
        }
    }

    public static synchronized void scheduleNextTest(final TestList currentTest, final boolean passed) {
        final TestList test = nextTest(currentTest, passed);
        if (test != null) {
            final int taskId = test.test.runAsync()
                ? Bukkit.getScheduler().runTaskLaterAsynchronously(get(), new TestRunner(test, get().getLogger()), 10).getTaskId()
                : Bukkit.getScheduler().runTaskLater(get(), new TestRunner(test, get().getLogger()), 10).getTaskId();
            Bukkit.getScheduler().runTaskLater(get(), () -> runPreTestSync(test, taskId), 5);
        } else {
            remainingRunners--;
            if (remainingRunners <= 0) {
                final boolean isSuccess = failedTests.isEmpty();
                get().getLogger().info("All tests are done.");
                if (isSuccess) {
                    get().getLogger().info("All tests passed! Exiting cleanly.");
                } else {
                    get().getLogger().severe("Some tests failed:");
                    for (TestList testList : failedTests) {
                        get().getLogger().severe("* " + testList.name());
                    }
                }
                Bukkit.getScheduler().runTaskLater(get(), () -> exit(isSuccess), 20);
            }
        }
    }

    private static void runPreTestSync(final TestList test, final int taskId) {
        try {
            test.test.preRunSync();
        } catch (Exception e) {
            BlazingGames.get().getLogger().severe("Failed to run preRunSync for " + test.name());
            Bukkit.getScheduler().cancelTask(taskId);
            scheduleNextTest(test, false);
        }
    }

    public static synchronized TestList nextTest(final TestList currentTest, final boolean passed) {
        if (currentTest != null) {
            if (!passed) {
                failedTests.add(currentTest);
            }
        }
        if (tests.isEmpty()) {
            return null;
        }
        return tests.remove(0);
    }
}
