package br.com.juliocauan.authentication.controller;

import java.util.Set;
import java.util.UUID;

import org.openapitools.api.AuthApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.Profile;
import org.openapitools.model.ProfileRoles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.security.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final JwtService jwtService;

  @Override
  public ResponseEntity<JWTResponse> _signinUser(@Valid SigninForm signinForm) {
    JWTResponse response = jwtService.authenticate(signinForm);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

	@Override
  public ResponseEntity<String> _signupUser(@Valid SignupForm signupForm) {
    jwtService.validateAndRegisterNewUser(signupForm);
    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
  }

  @Override
  public ResponseEntity<Profile> _profileContent() {
    Profile profile = jwtService.getProfileContent();
    return ResponseEntity.status(HttpStatus.OK).body(profile);
  }

  @Override
  public ResponseEntity<ProfileRoles> _alterUserRole(UUID userId, @NotNull @Size(min = 1) @Valid Set<EnumRole> roles) {
    ProfileRoles profileRoles = jwtService.alterUserRole(userId, roles);
    return ResponseEntity.status(HttpStatus.OK).body(profileRoles);
  }

}
