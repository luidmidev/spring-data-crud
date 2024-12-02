package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import io.github.luidmidev.springframework.web.problemdetails.ProblemDetailsException;

public class NotFoundProvider<ID> {

    protected ProblemDetailsException notFoundModel(String model, ID id) {
        return ApiError.notFound(notFoundMessage(model, id));
    }

    protected String notFoundMessage(String model, ID id) {
        return "Not found %s with id %s".formatted(model, id);
    }
}
