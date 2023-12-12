package br.com.juliocauan.authentication.infrastructure.service.application;

import org.openapitools.model.JWT;
import org.openapitools.model.PasswordMatch;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.AuthenticationService;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AuthenticationServiceImpl extends AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final PasswordService passwordService;
  private final UserServiceImpl userService;

  @Override
  public final JWT authenticate(String username, String password) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    return new JWT().token(jwtProvider.generateToken(auth));
  }

  @Override
  public final void registerUser(String username, PasswordMatch password) {
    userService.checkDuplicatedUsername(username);
    passwordService.validatePasswordMatch(password);
    passwordService.validatePasswordSecurity(password.getPassword());
    userService.save(UserEntity
      .builder()
        .id(null)
        .username(username)
        .password(passwordService.encode(password.getPassword()))
      .build());
  }

}
