package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.application.JwtService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class JwtServiceImpl extends JwtService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final PasswordService passwordService;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;

  @Override
  public final JWTResponse authenticate(String username, String password) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication auth = authenticationManager.authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    return new JWTResponse().token(jwtProvider.generateToken(auth));
  }

  @Override
  public final void validateAndRegisterNewUser(String username, String password, EnumRole role) {
    userService.checkDuplicatedUsername(username);
    UserEntity userEntity = buildUserEntity(username, password, role);
    userService.save(userEntity);
  }

  private final UserEntity buildUserEntity(String username, String password, EnumRole role) {
    return UserEntity.builder()
      .id(null)
      .username(username)
      .password(passwordService.encode(password))
      .roles(buildRoleSet(role))
    .build();
  }

  private final Set<RoleEntity> buildRoleSet(EnumRole formRole) {
    Role role = roleService.getByName(formRole == null ? EnumRole.USER : formRole);
    Set<RoleEntity> roleSet = new HashSet<>();
    roleSet.add(RoleMapper.domainToEntity(role));
    return roleSet;
  }

}
