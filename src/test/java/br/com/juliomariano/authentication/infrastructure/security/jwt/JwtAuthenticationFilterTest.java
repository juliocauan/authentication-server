package br.com.juliomariano.authentication.infrastructure.security.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;

class JwtAuthenticationFilterTest extends TestContext {

    private final String urlSecured = "/api/auth/admin";
    private final String authorizationHeader = "Authorization";

    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public JwtAuthenticationFilterTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Test
    void doFilterInternal_error_tokenNull() throws Exception{
        getMockMvc().perform(
            get(urlSecured))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void doFilterInternal_error_tokenNotBearer() throws Exception{
        getMockMvc().perform(
            get(urlSecured)
                .header(authorizationHeader, "NotBearer"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }
    
}
