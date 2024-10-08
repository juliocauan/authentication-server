package br.com.juliomariano.authentication.application.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.OkResponse;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SendResetTokenRequest;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.SignupFormAdmin;
import org.openapitools.model.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliomariano.authentication.application.service.AuthenticationService;
import br.com.juliomariano.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

  private final AuthenticationService authenticationService;

  @Override
  public ResponseEntity<UserData> _login(SigninForm signinForm) {
    UserData userData = authenticationService.authenticate(
        signinForm.getUsername(),
        signinForm.getPassword());
    return ResponseEntity.status(HttpStatus.OK).body(userData);
  }

  @Override
  public ResponseEntity<OkResponse> _signup(SignupForm signupForm) {
    String username = signupForm.getUsername();
    String password = signupForm.getMatch().getPassword();

    PasswordUtil.validateMatch(signupForm.getMatch());

    authenticationService.registerUser(username, password);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OkResponse().message("User [%s] registered successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _signupAdmin(SignupFormAdmin signupFormAdmin) {
    String username = signupFormAdmin.getUsername();
    String password = signupFormAdmin.getMatch().getPassword();
    String adminKey = signupFormAdmin.getAdminKey();

    PasswordUtil.validateMatch(signupFormAdmin.getMatch());

    authenticationService.registerAdmin(username, password, adminKey);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OkResponse().message("Admin [%s] registered successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _sendResetToken(SendResetTokenRequest requestBody) {
    String username = requestBody.getUsername();
    authenticationService.sendToken(username);

    return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        "Email sent to [%s]!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _resetPassword(PasswordMatch passwordMatch, String token) {
    PasswordUtil.validateMatch(passwordMatch);
    authenticationService.resetPassword(passwordMatch.getPassword(), token);
    return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Password updated successfully!"));
  }

}
