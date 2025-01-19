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

import java.util.logging.Logger;

public class TestRunner implements Runnable {
    private final TestList test;
    private final Logger logger;

    public TestRunner(TestList test, Logger logger) {
        this.test = test;
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.info("Running test: " + test.name());
        boolean passed = test.test.run();
        if (!passed) {
            logger.severe("Test failed: " + test.name());
        } else {
            logger.info("Test passed: " + test.name());
        }
        TestBlazingGames.scheduleNextTest(test, passed);
    }
}