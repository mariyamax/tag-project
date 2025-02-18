package tag.sources.models;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {
    ADMIN(Set.of(Permission.BATCH_ALL, Permission.USERS_EDIT, Permission.MARK_TYPES_MATCH, Permission.MARK_TYPES_SEARCH)),
    CUSTOMER(Set.of(Permission.BATCH_OWNER)),
    ASSESSOR_MAIN(Set.of(Permission.MARK_TYPES_SEARCH, Permission.MARK_TYPES_MATCH)),
    ASSESSOR_MATCH(Set.of(Permission.MARK_TYPES_MATCH)),
    ASSESSOR_SEARCH(Set.of(Permission.MARK_TYPES_SEARCH));
    private final Set<Permission> permissions;

    Role(final Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
