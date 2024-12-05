package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.Crud;
import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperation;
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
import java.util.function.Predicate;
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
            var grantedAuthorityDefaults = context.getBean(GrantedAuthorityDefaults.class);
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

        private static final CrudMatcher ANY_OPERATION = (target, crudOperation) -> true;

        private boolean anyOperationConfigured = false;

        private final List<OperationMatcherEntry<AuthorizationManager<CrudAuthorizationContext>>> mappings = new ArrayList<>();

        private List<CrudMatcher> unmappedMatchers;

        public AuthorizedCrud anyOperation() {
            Assert.state(!this.anyOperationConfigured, "Can't configure anyOperation after itself");
            try {
                return crudMatcher(ANY_OPERATION);
            } finally {
                this.anyOperationConfigured = true;
            }
        }

        public AuthorizedCrud cruds(Class<? extends Crud>... cruds) {
            Assert.state(cruds.length > 0, "cruds cannot be empty");
            return crudMatcher((target, crudOperation) -> Arrays.asList(cruds).contains(target.getClass()));
        }

        public AuthorizedCrud crud(Class<? extends Crud> crud) {
            return crudMatcher((target, crudOperation) -> target.getClass().equals(crud));
        }

        public AuthorizedCrud crudOperations(Class<? extends Crud> cruds, CrudOperation... crudOperations) {
            return crudEqualsAndOperationMatcher(cruds, crudOperation -> Arrays.asList(crudOperations).contains(crudOperation));
        }

        public AuthorizedCrud crudsRead(Class<? extends Crud> crud) {
            return crudEqualsAndOperationMatcher(crud, CrudOperation::isRead);
        }

        public AuthorizedCrud crudsReadOnly(Class<? extends Crud> crud) {
            return crudEqualsAndOperationMatcher(crud, CrudOperation::isReadOnly);
        }

        public AuthorizedCrud crudsWrite(Class<? extends Crud> crud) {
            return crudEqualsAndOperationMatcher(crud, CrudOperation::isWrite);
        }

        public AuthorizedCrud crudsWriteOnly(Class<? extends Crud> crud) {
            return crudEqualsAndOperationMatcher(crud, CrudOperation::isWriteOnly);
        }

        public AuthorizedCrud operationRead() {
            return operationMatcher(CrudOperation::isRead);
        }

        public AuthorizedCrud operationWrite() {
            return operationMatcher(CrudOperation::isWrite);
        }

        public AuthorizedCrud operationReadOnly() {
            return operationMatcher(CrudOperation::isReadOnly);
        }

        public AuthorizedCrud operationWriteOnly() {
            return operationMatcher(CrudOperation::isWriteOnly);
        }

        public AuthorizedCrud operations(CrudOperation... crudOperations) {
            return crudMatcher((target, crudOperation) -> Arrays.asList(crudOperations).contains(crudOperation));
        }

        public AuthorizedCrud crudMatcher(CrudMatcher... crudMatchers) {
            Assert.state(!this.anyOperationConfigured, "Can't configure operationMatchers after anyOperation");
            return chainOperationMatchers(Arrays.asList(crudMatchers));
        }

        public AuthorizedCrud crudEqualsAndOperationMatcher(Class<? extends Crud> crud, Predicate<CrudOperation> operationMatcher) {
            return crudMatcher((target, crudOperation) -> target.getClass().equals(crud) && operationMatcher.test(crudOperation));
        }

        public AuthorizedCrud operationMatcher(Predicate<CrudOperation> operationMatcher) {
            return crudMatcher((target, crudOperation) -> operationMatcher.test(crudOperation));
        }

        private void addMapping(CrudMatcher matcher, AuthorizationManager<CrudAuthorizationContext> manager) {
            Assert.notNull(matcher, "matcher cannot be null");
            Assert.notNull(manager, "manager cannot be null");

            this.unmappedMatchers = null;
            this.mappings.add(new OperationMatcherEntry<>(matcher, manager));
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
