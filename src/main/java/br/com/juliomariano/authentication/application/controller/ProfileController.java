package br.com.juliomariano.authentication.application.controller;

import org.openapitools.api.ProfileApi;
import org.openapitools.model.OkResponse;
import org.openapitools.model.PasswordUpdateForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliomariano.authentication.application.service.ProfileService;
import br.com.juliomariano.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ProfileController implements ProfileApi {
    
    private final ProfileService profileService;

    @Override
    public ResponseEntity<OkResponse> _updatePassword(PasswordUpdateForm passwordUpdateForm) {
        PasswordUtil.validateMatch(passwordUpdateForm.getMatch());
        String currentPassword = passwordUpdateForm.getCurrentPassword();
        String newPassword = passwordUpdateForm.getMatch().getPassword();
        profileService.updatePassword(currentPassword, newPassword);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Password updated successfully!"));
    }

    @Override
    public ResponseEntity<OkResponse> _closeAccount(String password) {
        profileService.closeAccount(password);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Closed account successfully!"));
    }

}
