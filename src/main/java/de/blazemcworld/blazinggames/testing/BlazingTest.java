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

public abstract class BlazingTest {
    public abstract boolean runAsync();
    public boolean run() {
        try {
            runTest();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract void runTest() throws Exception;

    // methods for test runner
    protected void assertBoolean(String condition, boolean assertion) throws TestFailedException {
        if (!assertion) {
            throw new TestFailedException(getClass(), "assertBoolean failed: \"" + condition + "\"");
        }
    }

    protected void assertEquals(Object expected, Object actual) throws TestFailedException {
        if (expected == null && actual == null) {
            return;
        }
        
        if (expected == null || actual == null || !expected.equals(actual)) {
            throw new TestFailedException(getClass(), "assertEquals expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }
}