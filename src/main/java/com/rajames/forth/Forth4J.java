package com.rajames.forth;

import com.rajames.forth.config.ForthConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static java.lang.System.exit;

/**
 * forth4j
 */
@SuppressWarnings("EmptyTryBlock")
public class Forth4J {

    private static final Logger log = LogManager.getLogger(Forth4J.class.getName());

    public static void main(String[] args) {
        log.info("Here we go!");

        try {
            try (AnnotationConfigApplicationContext ignored =
                         new AnnotationConfigApplicationContext("com.rajames.forth")) {
            }
            new ForthRepl().run();
        } catch (Exception e) {
            log.error("Error initializing application", e);
            return;
        }

        log.info("All Done! Bye, bye!!");
    }
}
