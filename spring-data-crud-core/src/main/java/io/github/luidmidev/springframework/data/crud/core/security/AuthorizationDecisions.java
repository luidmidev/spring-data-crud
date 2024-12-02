package io.github.luidmidev.springframework.data.crud.core.security;

import org.springframework.security.authorization.AuthorizationDecision;

public final class AuthorizationDecisions {
    private AuthorizationDecisions() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final AuthorizationDecision GRANTED = new AuthorizationDecision(true);
    public static final AuthorizationDecision DENIED = new AuthorizationDecision(false);
}
