package com.mjdsft.ingester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 *
 */
@SpringBootApplication
public class IngesterApplication {

    /**
     * Answer an instance for the following arguments
     */
    public IngesterApplication() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        SpringApplication.run(IngesterApplication.class, args);

    }

}
