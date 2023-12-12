package br.com.juliocauan.authentication.infrastructure.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class UserServiceImpl extends UserService {
    
    private final UserRepositoryImpl userRepository;
    private final RoleServiceImpl roleService;

    @Override
    public final User save(User user) {
        return userRepository.save(new UserEntity(user));
    }

    @Override
    protected final UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public final void updatePassword(User user, String encodedPassword) {
        UserEntity userEntity = new UserEntity(user);
        userEntity.setPassword(encodedPassword);
        save(userEntity);
    }

    @Override
    public final void updateRoles(String username, Set<String> enumRoles) {
        UserEntity user = new UserEntity(getByUsername(username));
        Set<RoleEntity> roles = enumRoles.stream()
            .map(roleService::getByName)
            .map(RoleEntity::new)
            .collect(Collectors.toSet());
        
        user.setRoles(roles);
        save(user);
    }

    @Override
    public final void delete(String username) {
        delete(getByUsername(username));
    }

    @Override
    public final void delete(User user) {
        userRepository.deleteById(user.getId());
    }
    
}
