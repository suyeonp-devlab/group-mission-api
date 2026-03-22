package com.app.groupmissionapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class GroupMissionApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
