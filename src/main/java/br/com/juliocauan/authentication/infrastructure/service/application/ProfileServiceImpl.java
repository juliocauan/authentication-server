package br.com.juliocauan.authentication.infrastructure.service.application;

import org.openapitools.model.PasswordUpdateForm;
import org.openapitools.model.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class ProfileServiceImpl extends ProfileService {

    private final UserServiceImpl userService;
    private final PasswordService passwordService;
    
    @Override
    public final Profile getProfileContent() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Profile().username(user.getUsername());
    }

    @Override
    public final void updatePassword(PasswordUpdateForm passwordUpdateForm) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = new UserEntity(userService.getByUsername(username));

        passwordService.validatePasswordMatch(passwordUpdateForm.getNewPasswordMatch());
        passwordService.validatePasswordMatch(passwordUpdateForm.getCurrentPassword(), entity.getPassword());
        passwordService.validatePasswordSecurity(passwordUpdateForm.getNewPasswordMatch().getPassword());
        
        String newPassword = passwordService.encode(passwordUpdateForm.getNewPasswordMatch().getPassword());
        entity.setPassword(newPassword);
        userService.save(entity);
    }
    
}
