package br.com.juliomariano.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;

class PasswordResetTest extends TestContext {

    private final PasswordResetRepository passwordResetRepository;

    private User user;

    public PasswordResetTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordResetRepository passwordResetRepository) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.passwordResetRepository = passwordResetRepository;
    }

    @BeforeEach
    void beforeEach() {
        getUserRepository().deleteAll();
        passwordResetRepository.deleteAll();
        user = getUserRepository().save(new User(getRandomUsername(), getRandomPassword()));
    }

    private PasswordReset savePasswordReset(PasswordReset passwordReset) {
        return passwordResetRepository.save(passwordReset);
    }

    @Test
    void user_notNull() {
        assertThrowsExactly(DataIntegrityViolationException.class,
            () -> savePasswordReset(new PasswordReset(null)));
    }

    @Test
    void user_unique() {
        assertDoesNotThrow(() -> savePasswordReset(new PasswordReset(user)));
        assertThrowsExactly(DataIntegrityViolationException.class,
            () -> savePasswordReset(new PasswordReset(user)));
    }

    @Test
    void onDelete_cascadeDoesNotDeleteUser() {
        savePasswordReset(new PasswordReset(user));
        passwordResetRepository.deleteAll();
        assertEquals(user, getUserRepository().findById(user.getId()).get());
    }

    @Test
    void onUserDelete_deletePasswordReset() {
        Integer passwordResetId = savePasswordReset(new PasswordReset(user)).getId();

        assertTrue(passwordResetRepository.findById(passwordResetId).isPresent());
        getUserRepository().deleteById(user.getId());
        assertFalse(passwordResetRepository.findById(passwordResetId).isPresent());
    }

    @Test
    void isExpired() {
        PasswordReset passwordReset = savePasswordReset(new PasswordReset(user));
        assertFalse(passwordReset.isExpired());
        passwordReset.setExpireDate(LocalDateTime.now().minusSeconds(1));
        assertTrue(passwordReset.isExpired());
    }

}
