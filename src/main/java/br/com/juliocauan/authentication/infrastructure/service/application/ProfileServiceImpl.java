package br.com.juliocauan.authentication.infrastructure.service.application;

import org.openapitools.model.PasswordUpdateForm;
import org.openapitools.model.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileServiceImpl extends ProfileService {

    private final UserServiceImpl userService;
    private final PasswordService passwordService;
    
    @Override
    public final Profile getProfileContent() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Profile().username(user.getUsername());
    }

    @Override
    public final void alterPassword(PasswordUpdateForm passwordUpdateForm) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = UserMapper.domainToEntity(userService.getByUsername(username));

        passwordService.checkPasswordConfirmation(passwordUpdateForm.getNewPasswordMatch());
        passwordService.checkCurrentPassword(entity.getPassword(), passwordUpdateForm.getOldPassword());
        
        String newPassword = passwordService.encodePassword(passwordUpdateForm.getNewPasswordMatch().getPassword());
        entity.setPassword(newPassword);
        userService.save(entity);
    }
    
}