package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.Operation;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class AuthorizeCrudConfigurer {

    static final AuthorizationManager<CrudAuthorizationContext> PERMIT_ALL_AUTHORIZATION_MANAGER = (a, o) -> AuthorizationDecisions.GRANTED;
    static final AuthorizationManager<CrudAuthorizationContext> DENY_ALL_AUTHORIZATION_MANAGER = (a, o) -> AuthorizationDecisions.DENIED;

    @Getter
    private final AuthorizationManagerCrudMatcherRegistry registry;

    private final Supplier<RoleHierarchy> roleHierarchy;

    private String rolePrefix = "ROLE_";

    public AuthorizeCrudConfigurer(ApplicationContext context, Customizer<AuthorizationManagerCrudMatcherRegistry> customizer) {
        this.registry = new AuthorizationManagerCrudMatcherRegistry();
        this.roleHierarchy = SingletonSupplier.of(() -> (context.getBeanNamesForType(RoleHierarchy.class).length > 0) ? context.getBean(RoleHierarchy.class) : new NullRoleHierarchy());
        var grantedAuthorityDefaultsBeanNames = context.getBeanNamesForType(GrantedAuthorityDefaults.class);
        if (grantedAuthorityDefaultsBeanNames.length > 0) {
            GrantedAuthorityDefaults grantedAuthorityDefaults = context.getBean(GrantedAuthorityDefaults.class);
            this.rolePrefix = grantedAuthorityDefaults.getRolePrefix();
        }

        customizer.customize(this.registry);
    }

    public AuthorizationCrudManager authorizationCrudManager() {
        return this.registry.createAuthorizationManager();
    }


    /**
     * Registry for mapping a {@link CrudMatcher} to an {@link AuthorizationManager}.
     *
     * @author Evgeniy Cheban
     */
    public final class AuthorizationManagerCrudMatcherRegistry {

        private static final CrudMatcher ANY_OPERATION = (target, operation) -> true;

        private boolean anyOperationConfigured = false;

        private final List<OperationMatcherEntry<AuthorizationManager<CrudAuthorizationContext>>> mappings = new ArrayList<>();

        private List<CrudMatcher> unmappedMatchers;

        public AuthorizedCrud anyOperation() {
            Assert.state(!this.anyOperationConfigured, "Can't configure anyOperation after itself");
            try {
                return operationMatcher(ANY_OPERATION);
            } finally {
                this.anyOperationConfigured = true;
            }
        }

        public AuthorizedCrud targets(Class<?> targetType) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType));
        }

        public AuthorizedCrud targets(Class<?>... targetTypes) {
            Assert.state(targetTypes.length > 0, "targetTypes cannot be empty");
            return operationMatcher((target, operation) -> Arrays.asList(targetTypes).contains(target.getClass()));
        }

        public AuthorizedCrud targetOperations(Class<?> targetType, Operation... operations) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType) && Arrays.asList(operations).contains(operation));
        }

        public AuthorizedCrud targetRead(Class<?> targetType) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType) && operation.isRead());
        }

        public AuthorizedCrud targetReadOnly(Class<?> targetType) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType) && operation.isReadOnly());
        }

        public AuthorizedCrud targetWrite(Class<?> targetType) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType) && operation.isWrite());
        }

        public AuthorizedCrud targetWriteOnly(Class<?> targetType) {
            return operationMatcher((target, operation) -> target.getClass().equals(targetType) && operation.isWriteOnly());
        }

        public AuthorizedCrud operations(Operation... operations) {
            return operationMatcher((target, operation) -> Arrays.asList(operations).contains(operation));
        }

        public AuthorizedCrud operationRead() {
            return operationMatcher((target, operation) -> operation.isRead());
        }

        public AuthorizedCrud operationWrite() {
            return operationMatcher((target, operation) -> operation.isWrite());
        }

        public AuthorizedCrud operationReadOnly() {
            return operationMatcher((target, operation) -> operation.isReadOnly());
        }

        public AuthorizedCrud operationWriteOnly() {
            return operationMatcher((target, operation) -> operation.isWriteOnly());
        }

        private void addMapping(CrudMatcher matcher, AuthorizationManager<CrudAuthorizationContext> manager) {
            Assert.notNull(matcher, "matcher cannot be null");
            Assert.notNull(manager, "manager cannot be null");

            this.unmappedMatchers = null;
            this.mappings.add(new OperationMatcherEntry<>(matcher, manager));
        }

        public AuthorizedCrud operationMatcher(CrudMatcher... crudMatchers) {
            Assert.state(!this.anyOperationConfigured, "Can't configure operationMatchers after anyOperation");
            return chainOperationMatchers(Arrays.asList(crudMatchers));
        }

        private AuthorizedCrud chainOperationMatchers(List<CrudMatcher> crudMatchers) {
            this.unmappedMatchers = crudMatchers;
            return new AuthorizedCrud(crudMatchers);
        }

        private AuthorizationCrudManager createAuthorizationManager() {
            Assert.state(this.unmappedMatchers == null, () -> "An incomplete mapping was found for " + this.unmappedMatchers + ". Try completing it with something like requestUrls().<something>.hasRole('USER')");
            Assert.state(!this.mappings.isEmpty(), "At least one mapping is required (for example, permitAll)");
            return new AuthorizationCrudManager(this.mappings);
        }
    }

    /**
     * An object that allows configuring the {@link AuthorizationManager} for
     * {@link CrudMatcher}s.
     *
     * @author Evgeniy Cheban
     * @author Josh Cummings
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public class AuthorizedCrud {

        private final List<? extends CrudMatcher> matchers;

        private boolean not;

        AuthorizedCrud(List<? extends CrudMatcher> matchers) {
            this.matchers = matchers;
        }

        public AuthorizedCrud not() {
            this.not = true;
            return this;
        }

        public AuthorizationManagerCrudMatcherRegistry permitAll() {
            return access(PERMIT_ALL_AUTHORIZATION_MANAGER);
        }


        public AuthorizationManagerCrudMatcherRegistry denyAll() {
            return access(DENY_ALL_AUTHORIZATION_MANAGER);
        }

        public AuthorizationManagerCrudMatcherRegistry hasRole(String role) {
            return access(withRoleHierarchy(AuthorityAuthorizationManager.hasAnyRole(AuthorizeCrudConfigurer.this.rolePrefix, new String[]{role})));
        }

        public AuthorizationManagerCrudMatcherRegistry hasAnyRole(String... roles) {
            return access(withRoleHierarchy(AuthorityAuthorizationManager.hasAnyRole(AuthorizeCrudConfigurer.this.rolePrefix, roles)));
        }

        public AuthorizationManagerCrudMatcherRegistry hasAuthority(GrantedAuthority authority) {
            return hasAuthority(authority.getAuthority());
        }

        public AuthorizationManagerCrudMatcherRegistry hasAuthority(String authority) {
            return access(withRoleHierarchy(AuthorityAuthorizationManager.hasAuthority(authority)));
        }

        public AuthorizationManagerCrudMatcherRegistry hasAnyAuthority(String... authorities) {
            return access(withRoleHierarchy(AuthorityAuthorizationManager.hasAnyAuthority(authorities)));
        }

        private AuthorityAuthorizationManager<CrudAuthorizationContext> withRoleHierarchy(AuthorityAuthorizationManager<CrudAuthorizationContext> manager) {
            manager.setRoleHierarchy(AuthorizeCrudConfigurer.this.roleHierarchy.get());
            return manager;
        }

        public AuthorizationManagerCrudMatcherRegistry authenticated() {
            return access(AuthenticatedAuthorizationManager.authenticated());
        }


        public AuthorizationManagerCrudMatcherRegistry fullyAuthenticated() {
            return access(AuthenticatedAuthorizationManager.fullyAuthenticated());
        }


        public AuthorizationManagerCrudMatcherRegistry rememberMe() {
            return access(AuthenticatedAuthorizationManager.rememberMe());
        }


        public AuthorizationManagerCrudMatcherRegistry anonymous() {
            return access(AuthenticatedAuthorizationManager.anonymous());
        }


        public AuthorizationManagerCrudMatcherRegistry access(AuthorizationManager<CrudAuthorizationContext> manager) {
            Assert.notNull(manager, "manager cannot be null");
            return this.addMapping(this.matchers, this.not ? AuthorizationManagers.not(manager) : manager);
        }

        private AuthorizationManagerCrudMatcherRegistry addMapping(List<? extends CrudMatcher> matchers, AuthorizationManager<CrudAuthorizationContext> manager) {
            for (var matcher : matchers) {
                AuthorizeCrudConfigurer.this.registry.addMapping(matcher, manager);
            }
            return AuthorizeCrudConfigurer.this.registry;
        }

    }

}
