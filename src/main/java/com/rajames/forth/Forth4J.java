package com.rajames.forth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * forth4j
 */
@SuppressWarnings("EmptyTryBlock")
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
