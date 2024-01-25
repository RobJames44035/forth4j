/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * forth4j
 */
public class Forth4J {

    private static final Logger log = LogManager.getLogger(Forth4J.class.getName());

    public static void main(String[] args) {
        log.info("Here we go!");

        try {
            try (AnnotationConfigApplicationContext context =
                         new AnnotationConfigApplicationContext("com.rajames.forth")) {

                // Get ForthRepl from the context
                ForthRepl repl = context.getBean(ForthRepl.class);

                // Run it
                repl.run();
            }
        } catch (Exception e) {
            log.error("Error initializing application", e);
        }

        log.info("All Done! Bye, bye!!");
    }
}
