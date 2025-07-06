package io.github.luidmidev.springframework.data.crud.core.providers;

import org.springframework.transaction.support.TransactionOperations;

public interface TransactionOperationsProvider {

    default TransactionOperations getTransactionOperations() {
        return TransactionOperations.withoutTransaction();
    }
}
