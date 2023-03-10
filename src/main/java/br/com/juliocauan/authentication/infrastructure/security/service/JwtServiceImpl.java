package br.com.juliocauan.authentication.infrastructure.security.service;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.JwtService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class JwtServiceImpl implements JwtService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder encoder;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;

  public JWTResponse authenticate(SigninForm signinForm) {
    Authentication auth = authenticationManager.authenticate(parseAsAuthToken(signinForm));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtProvider.generateToken(auth);
    UserDetails userPrincipal = (UserDetails) auth.getPrincipal();
    Set<EnumRole> roles = RoleMapper.authoritiesToEnumRole(userPrincipal.getAuthorities());
    return parseAsJWTResponse(token, userPrincipal, roles);
  }

  public void validateAndRegisterNewUser(SignupForm signupForm) {
    userService.checkDuplicatedUsername(signupForm.getUsername());
    Set<RoleEntity> roles = new HashSet<>();
    roles.add(RoleMapper.domainToEntity(roleService.getByName(signupForm.getRole() == null ? EnumRole.USER : signupForm.getRole())));
    UserEntity userEntity = UserMapper.formToEntity(signupForm, roles, encoder);
    userService.save(userEntity);
  }

  private UsernamePasswordAuthenticationToken parseAsAuthToken(SigninForm signinForm) {
    return new UsernamePasswordAuthenticationToken(signinForm.getUsername(), signinForm.getPassword());
  }

  private JWTResponse parseAsJWTResponse(String token, UserDetails userPrincipal, Set<EnumRole> roles) {
    return new JWTResponse()
        .token(token)
        .username(userPrincipal.getUsername())
        .roles(roles);
  }

}