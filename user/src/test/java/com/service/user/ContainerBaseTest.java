package com.service.user;


import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.port;

@Testcontainers
public class ContainerBaseTest {

    @LocalServerPort
    private int port;


    @Container
    public static GenericContainer<?> mongoDBContainer = new GenericContainer("mongo:5.0")
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "datmt_root")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "datmt_root");


    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.data.mongodb.database", ()-> "app_db");
        registry.add("spring.data.mongodb.username", ()-> "datmt_root");
        registry.add("spring.data.mongodb.password", ()-> "datmt_root");
    }

    @BeforeEach
    public void initPort(){
        RestAssured.port = port;
    }

    @BeforeAll
    public static void beforeAll() {
        mongoDBContainer.start();
    }
}
