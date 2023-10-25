package br.com.juliocauan.authentication.domain.model;

import java.time.LocalDateTime;

public abstract class PasswordResetToken {
    public static final int TOKEN_EXPIRATION_MINUTES = 10;
    public abstract Long getId();
    public abstract User getUser();
    public abstract String getToken();
    public abstract LocalDateTime getExpireDate();
    public final boolean isExpired() {
        return LocalDateTime.now().isAfter(getExpireDate());
    }
}