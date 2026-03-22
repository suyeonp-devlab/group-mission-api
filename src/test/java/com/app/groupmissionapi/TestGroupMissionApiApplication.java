package com.app.groupmissionapi;

import org.springframework.boot.SpringApplication;

public class TestGroupMissionApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(GroupMissionApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
