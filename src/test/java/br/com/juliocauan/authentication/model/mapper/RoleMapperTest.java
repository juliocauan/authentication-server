package br.com.juliocauan.authentication.model.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openapitools.model.EnumRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleMapperTest extends TestContext {

    public RoleMapperTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    private final Role getRole(EnumRole role){
        return new Role() {
            @Override
            public Short getId() {return null;}
            @Override
            public EnumRole getName() {return role;}
        };
    }

    @Test
    void authoritiesToEnumRole() {
        Set<SimpleGrantedAuthority> authorities = Stream.of(EnumRole.values())
            .map(role -> new SimpleGrantedAuthority(role.getValue())).collect(Collectors.toSet());
        Set<EnumRole> roles = Stream.of(EnumRole.values()).collect(Collectors.toSet());
        assertEquals(roles, RoleMapper.authoritiesToEnumRole(authorities));
    }

    @Test
    void setRoleToSetEnumRole() {
        Set<EnumRole> expectedSet = Stream.of(EnumRole.values()).collect(Collectors.toSet());
        Set<Role> roles = new HashSet<>();
        for(EnumRole name : EnumRole.values()) roles.add(getRole(name));
        assertEquals(expectedSet, RoleMapper.setRoleToSetEnumRole(roles));
    }
    
}
