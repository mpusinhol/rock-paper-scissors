package com.mpusinhol.rockpaperscissors;

import com.mpusinhol.rockpaperscissors.model.dto.AuthenticationResponse;
import com.mpusinhol.rockpaperscissors.model.dto.ExceptionDTO;
import com.mpusinhol.rockpaperscissors.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;
import static com.mpusinhol.rockpaperscissors.model.entity.UserStubs.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserIT extends BaseIT {

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    class CreateUserTests {

        @Test
        void shouldCreateUserSuccessfully() throws Exception {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content("{\"username\": \"mpusinhol\", \"password\": \"1234\"}")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.header().exists("Location"));

            Optional<User> user = userRepository.findByUsername("mpusinhol");
            assertTrue(user.isPresent());
        }

        @Test
        void shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception {
            User savedUser = userRepository.save(createUser());

            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .content("{\"username\": \"%s\", \"password\": \"1234\"}"
                                            .formatted(savedUser.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);
            ExceptionDTO exceptionDTO = objectMapper.readValue(response, ExceptionDTO.class);
            assertNotNull(exceptionDTO.time());
            assertEquals(400, exceptionDTO.code());
            assertEquals("Username %s already exists".formatted(savedUser.getUsername()), exceptionDTO.message());
        }

        @Test
        void shouldReturnBadRequest_whenUsernameIsAnonymousUser() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .content("{\"username\": \"%s\", \"password\": \"1234\"}"
                                            .formatted(ANONYMOUS_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);
            ExceptionDTO exceptionDTO = objectMapper.readValue(response, ExceptionDTO.class);
            assertNotNull(exceptionDTO.time());
            assertEquals(400, exceptionDTO.code());
            assertEquals("Username %s already exists".formatted(ANONYMOUS_USER), exceptionDTO.message());
        }
    }

    @Nested
    class AuthenticateTests {

        @BeforeEach
        void setUp() {
            userRepository.save(createUser());
        }

        @Test
        void shouldAuthenticateSuccessfully() throws Exception {
            String response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users/login")
                            .content("{\"username\": \"mpusinhol\", \"password\": \"1234\"}")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            AuthenticationResponse authenticationResponse = objectMapper.readValue(response, AuthenticationResponse.class);

            assertNotNull(authenticationResponse);
            assertEquals("Bearer", authenticationResponse.tokenType());
            assertNotNull(authenticationResponse.expiresAt());
            assertNotNull(authenticationResponse.token());
        }

        @Test
        void shouldReturnUnauthorized_whenPasswordIsIncorrect() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/users/login")
                                    .content("{\"username\": \"mpusinhol\", \"password\": \"test\"}")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionDTO exceptionDTO = objectMapper.readValue(response, ExceptionDTO.class);

            assertNotNull(exceptionDTO);
            assertEquals(401, exceptionDTO.code());
            assertEquals("Bad credentials", exceptionDTO.message());
            assertNotNull(exceptionDTO.time());
        }

        @Test
        void shouldReturnNotFound_whenUsernameDoesNotExist() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/users/login")
                                    .content("{\"username\": \"test\", \"password\": \"test\"}")
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

            ExceptionDTO exceptionDTO = objectMapper.readValue(response, ExceptionDTO.class);

            assertNotNull(exceptionDTO);
            assertEquals(401, exceptionDTO.code());
            assertEquals("Bad credentials", exceptionDTO.message());
            assertNotNull(exceptionDTO.time());
        }
    }
}
