package org.personal.kafkamavenrepo;

import org.springframework.boot.SpringApplication;

public class TestKafkaMavenRepoApplication {

    public static void main(String[] args) {
        SpringApplication.from(KafkaMavenRepoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
