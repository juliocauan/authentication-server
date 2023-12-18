package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.model.JWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.AuthenticationService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AuthenticationServiceImpl extends AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;

  @Override
  public final JWT authenticate(String username, String password) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    return new JWT().token(jwtProvider.generateToken(auth));
  }

  @Override
  public final void registerUser(String username, String password) {
    userService.registerNew(UserEntity
      .builder()
        .username(username)
        .password(password)
      .build());
  }

  @Override
  public final void registerAdmin(String username, String password, String adminKey) {
    PasswordUtil.validateAdminPassword(adminKey);
    userService.registerNew(UserEntity
      .builder()
        .username(username)
        .password(password)
        .roles(buildRoleSet("ADMIN"))
      .build());
  }

  private final Set<RoleEntity> buildRoleSet(String roleName) {
    RoleEntity role = new RoleEntity(roleService.getByName(roleName));
    Set<RoleEntity> roleSet = new HashSet<>();
    roleSet.add(new RoleEntity(role));
    return roleSet;
  }

}
