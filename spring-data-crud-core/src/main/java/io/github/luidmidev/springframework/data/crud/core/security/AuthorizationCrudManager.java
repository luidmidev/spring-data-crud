package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.function.Supplier;


@Slf4j
@RequiredArgsConstructor
public class AuthorizationCrudManager implements AuthorizationManager<CrudAuthorizationContext> {

    private final List<OperationMatcherEntry<AuthorizationManager<CrudAuthorizationContext>>> mappings;

    public boolean canAccess(Object target, CrudOperation crudOperation) {
        var context = new CrudAuthorizationContext(target, crudOperation);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var decision = check(() -> authentication, context);
        if (decision == null) {
            log.warn("AuthorizationManager returned null decision for {}", context);
            return false;
        }
        return decision.isGranted();
    }


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, CrudAuthorizationContext context) {

        if (log.isTraceEnabled()) {
            log.trace("Authorizing {}", context);
        }

        for (var mapping : mappings) {
            var matcher = mapping.matcher();
            if (matcher.matches(context.target(), context.crudOperation())) {
                var manager = mapping.entry();
                if (log.isTraceEnabled()) {
                    log.trace("Checking authorization on {} using {}", context, manager);
                }
                @SuppressWarnings("deprecation")
                var result = manager.check(authentication, context);
                return result;
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Denying request since did not find matching OperationMatcherEntry");
        }

        return AuthorizationDecisions.DENIED;
    }
}

