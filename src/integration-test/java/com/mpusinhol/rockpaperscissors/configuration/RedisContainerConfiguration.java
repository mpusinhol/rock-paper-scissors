package com.mpusinhol.rockpaperscissors.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

public class RedisContainerConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Container
  private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.4.0")
          .withExposedPorts(6379);

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    redisContainer.start();

    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "spring.data.redis.host=".concat(redisContainer.getHost()),
        "spring.data.redis.port=".concat(String.valueOf(redisContainer.getFirstMappedPort())));
  }
}
