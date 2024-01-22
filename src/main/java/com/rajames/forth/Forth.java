package com.rajames.forth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * forth4j
 */
public class Forth {

    private static final Logger log = LogManager.getLogger(Forth.class.getName());

    public static void main(String[] args) {
        log.info("Here we go!");
        new ForthRepl().run();
        log.info("All Done! Bye, bye!!");
    }
}
