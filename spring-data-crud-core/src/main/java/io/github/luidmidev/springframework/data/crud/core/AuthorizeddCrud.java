package io.github.luidmidev.springframework.data.crud.core;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.context.SecurityContextHolder;

public interface AuthorizeddCrud {

    AuthorizationDecision GRANTED = new AuthorizationDecision(true);
    AuthorizationDecision DENIED = new AuthorizationDecision(false);

    AuthorizationManager<CrudOperation> PERMIT_ALL = (authentication, operation) -> AuthorizeddCrud.GRANTED;

    default AuthorizationManager<CrudOperation> getAuthorizationManager() {
        return PERMIT_ALL;
    }

    static void verifyAccess(AuthorizeddCrud authorizeddCrud, final CrudOperation operation) {
        var authorizationManager = authorizeddCrud.getAuthorizationManager();
        var context = SecurityContextHolder.getContext();
        authorizationManager.verify(context::getAuthentication, operation);
    }

}
