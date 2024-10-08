package br.com.juliomariano.authentication.application.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.PasswordUpdateForm;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.application.service.AuthenticationService;
import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;

class ProfileControllerTest extends TestContext {

        private final AuthenticationService authenticationService;
        private final PasswordEncoder encoder;

        private final String url = "/profile";
        private final String authorizationHeader = "Authorization";

        private final String username = getRandomUsername();
        private final String rawPassword = getRandomPassword();
        private final String errorInvalidPassword = "Passwords don't match!";
        private final String errorNotAuthorized = "Full authentication is required to access this resource";

        public ProfileControllerTest(UserRepository userRepository, RoleRepository roleRepository,
                        ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationService authenticationService,
                        PasswordEncoder encoder) {
                super(userRepository, roleRepository, objectMapper, mockMvc);
                this.authenticationService = authenticationService;
                this.encoder = encoder;
        }

        @BeforeEach
        void beforeEach() {
                getUserRepository().deleteAll();
                getUserRepository().save(new User(username, encoder.encode(rawPassword)));
        }

        private final String getBearerToken() {
                return "Bearer " + authenticationService.authenticate(username, rawPassword).getJWT();
        }

        @Test
        void updateUserPassword() throws Exception {
                String newPassword = getRandomPassword();
                PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
                                .currentPassword(rawPassword)
                                .match(new PasswordMatch()
                                                .password(newPassword)
                                                .passwordConfirmation(newPassword));

                getMockMvc().perform(
                                patch(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(writeValueAsString(passwordUpdateForm)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Password updated successfully!"));
        }

        @Test
        void updateUserPassword_error_invalidInput() throws Exception {
                String invalidInput = getRandomString(5);
                PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
                                .currentPassword(invalidInput)
                                .match(new PasswordMatch()
                                                .password(invalidInput)
                                                .passwordConfirmation(invalidInput));

                getMockMvc().perform(
                                patch(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(writeValueAsString(passwordUpdateForm)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Input validation error!"))
                                .andExpect(jsonPath("$.fieldErrors", hasSize(3)));
        }

        @Test
        void updateUserPassword_error_passwordConfirmation() throws Exception {
                PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
                                .currentPassword(rawPassword)
                                .match(new PasswordMatch()
                                                .password(getRandomPassword())
                                                .passwordConfirmation(getRandomPassword()));

                getMockMvc().perform(
                                patch(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(writeValueAsString(passwordUpdateForm)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(errorInvalidPassword));
        }

        @Test
        void updateUserPassword_error_incorrectPassword() throws Exception {
                String newPassword = getRandomPassword();
                PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
                                .currentPassword(newPassword)
                                .match(new PasswordMatch()
                                                .password(newPassword)
                                                .passwordConfirmation(newPassword));

                getMockMvc().perform(
                                patch(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(writeValueAsString(passwordUpdateForm)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(errorInvalidPassword));
        }

        @Test
        void updateUserPassword_error_weakPassword() throws Exception {
                String newPassword = getRandomString(8);
                PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm()
                                .currentPassword(rawPassword)
                                .match(new PasswordMatch()
                                                .password(newPassword)
                                                .passwordConfirmation(newPassword));

                getMockMvc().perform(
                                patch(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(writeValueAsString(passwordUpdateForm)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Password is not strong!"));
        }

        @Test
        void updateUserPassword_error_unauthorized() throws Exception {
                getMockMvc().perform(
                                patch(url))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value(errorNotAuthorized));
        }

        @Test
        void closeAccount() throws Exception {
                getMockMvc().perform(
                                delete(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .queryParam("password", rawPassword))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Closed account successfully!"));
        }

        @Test
        void closeAccount_error_incorrectPassword() throws Exception {
                getMockMvc().perform(
                                delete(url)
                                                .header(authorizationHeader, getBearerToken())
                                                .queryParam("password", getRandomPassword()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value(errorInvalidPassword));
        }

        @Test
        void closeAccount_error_unauthorized() throws Exception {
                getMockMvc().perform(
                                delete(url))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value(errorNotAuthorized));
        }

}
