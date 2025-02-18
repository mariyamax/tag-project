package tag.sources.models;

import lombok.Getter;

public enum Permission {
    USERS_EDIT("users:edit"),
    BATCH_ALL("batch:all"),
    BATCH_OWNER("batch:owner"),
    MARK_TYPES_SEARCH("mark_types:search"),
    MARK_TYPES_MATCH("mark_types:match");
    @Getter
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}
