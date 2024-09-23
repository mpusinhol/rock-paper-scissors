package com.mpusinhol.rockpaperscissors.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class PostgresContainerConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final String INIT_SCRIPT = "sql-init/V0000__CREATE_SCHEMA.sql";
  @Container
  private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.4")
          .withInitScript(INIT_SCRIPT);

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    postgresContainer.start();

    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "spring.datasource.url=".concat(postgresContainer.getJdbcUrl()),
        "spring.datasource.username=".concat(postgresContainer.getUsername()),
        "spring.datasource.password=".concat(postgresContainer.getPassword()),
        "spring.flyway.url=".concat(postgresContainer.getJdbcUrl()),
        "spring.flyway.username=".concat(postgresContainer.getUsername()),
        "spring.flyway.password=".concat(postgresContainer.getPassword()));
  }
}
