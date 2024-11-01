package io.github.luidmidev.springframework.data.crud.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.ketoru.springframework.data.crud.greanted-authorities")
public class GreantedAuthoritiesProperties {

    private AuthoritiesOperations grants = new AuthoritiesOperations();
    private PermitAllOperations permitAll = new PermitAllOperations();


    @Data
    public static class PermitAllOperations {
        private boolean create = false;
        private boolean read = false;
        private boolean update = false;
        private boolean delete = false;
    }

    @Data
    public static class AuthoritiesOperations {
        private String[] create;
        private String[] read;
        private String[] update;
        private String[] delete;
    }

    @Component("authorityManager")
    public class AuthorityManager {

        private final AuthoritiesOperations grants;
        private final PermitAllOperations permitAll;

        public AuthorityManager(GreantedAuthoritiesProperties properties) {
            this.grants = properties.getGrants();
            this.permitAll = properties.getPermitAll();
        }

        public boolean isPermitAllCreate() {
            return permitAll.isCreate();
        }

        public boolean isPermitAllRead() {
            return permitAll.isRead();
        }

        public boolean isPermitAllUpdate() {
            return permitAll.isUpdate();
        }

        public boolean isPermitAllDelete() {
            return permitAll.isDelete();
        }

        public String[] getCreate() {
            return grants.getCreate();
        }

        public String[] getRead() {
            return grants.getRead();
        }

        public String[] getUpdate() {
            return grants.getUpdate();
        }

        public String[] getDelete() {
            return grants.getDelete();
        }

    }

}

