//package com.matchinvest.api.config;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.utility.DockerImageName;
//
//@TestConfiguration
//public abstract class TestDatabaseConfig {
//
//    private static final PostgreSQLContainer<?> postgres =
//        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
//            .withDatabaseName("matchinvest_test")
//            .withUsername("postgres")
//            .withPassword("postgres");
//
//    static {
//        postgres.start();
//    }
//
//    @DynamicPropertySource
//    static void registerProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
//        registry.add("spring.flyway.enabled", () -> false);
//    }
//}
