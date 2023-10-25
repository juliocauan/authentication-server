package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.repository.PasswordResetTokenRepository;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;

public interface PasswordResetTokenRepositoryImpl extends PasswordResetTokenRepository, JpaRepository<PasswordResetTokenEntity, Long> {
    
}