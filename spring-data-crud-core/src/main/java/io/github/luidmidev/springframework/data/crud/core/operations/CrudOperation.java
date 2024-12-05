package io.github.luidmidev.springframework.data.crud.core.operations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrudOperation {
    /**
     * Represents a create operation (write only).
     */
    CREATE(true, false),

    /**
     * Represents an update operation (write only).
     */
    UPDATE(true, false),

    /**
     * Represents a delete operation (write only).
     */
    DELETE(true, false),

    /**
     * Represents a list operation (read only).
     */
    LIST(false, true),

    /**
     * Represents a paginated read operation.
     */
    PAGE(false, true),

    /**
     * Represents a find operation (read only).
     */
    FIND(false, true),

    /**
     * Represents a count operation (read only).
     */
    COUNT(false, true),

    /**
     * Represents an existence check operation (read only).
     */
    EXISTS(false, true);


    // Indicates if the operation involves writing.
    private final boolean write;

    // Indicates if the operation involves reading.
    private final boolean read;

    /**
     * Checks if the operation is read-only.
     *
     * @return true if the operation is read-only, false otherwise.
     */
    public boolean isReadOnly() {
        return read && !write;
    }

    /**
     * Checks if the operation is write-only.
     *
     * @return true if the operation is write-only, false otherwise.
     */
    public boolean isWriteOnly() {
        return write && !read;
    }
}
