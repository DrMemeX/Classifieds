package ru.drmemex.classifieds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClassifiedsApp {

    public static void main(String[] args) {
        SpringApplication.run(ClassifiedsApp.class, args);
    }
}
