package br.com.juliomariano.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;

class UserTest extends TestContext {

    public UserTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @BeforeEach
    public void beforeEach() {
        getUserRepository().deleteAll();
    }

    private void saveUser(String username, String password) {
        getUserRepository().save(new User(username, password));
    }

    private void saveUserWithUsername(String username) {
        saveUser(username, getRandomPassword());
    }

    private void saveUserWithPassword(String password) {
        saveUser(getRandomUsername(), password);
    }

    @Test
    void username_email() {
        String email = getRandomUsername();
        assertDoesNotThrow(() -> saveUserWithUsername(email));

        String notEmail = getRandomString(12);
        assertThrows(ConstraintViolationException.class, () -> saveUserWithUsername(notEmail));
    }

    @Test
    void username_maxLength() {
        String maxLengthUsername = getRandomUsername(60);
        assertDoesNotThrow(() -> saveUserWithUsername(maxLengthUsername));
        assertThrowsExactly(DataIntegrityViolationException.class, () -> saveUserWithUsername(maxLengthUsername + "A"));
    }

    @Test
    void username_unique() {
        String username = getRandomUsername();
        assertDoesNotThrow(() -> saveUserWithUsername(username));
        assertThrows(DataIntegrityViolationException.class, () -> saveUserWithUsername(username));
    }

    @Test
    void username_notNull() {
        String username = null;
        assertThrows(DataIntegrityViolationException.class, () -> saveUserWithUsername(username));
    }

    @Test
    void password_notBlank() {
        String password = getRandomPassword();
        assertDoesNotThrow(() -> saveUserWithPassword(password));

        String passwordNull = null;
        assertThrows(ConstraintViolationException.class, () -> saveUserWithPassword(passwordNull));

        String passwordBlank = getRandomString(6).replaceAll(".", " ");
        assertThrows(ConstraintViolationException.class, () -> saveUserWithPassword(passwordBlank));
    }

}
