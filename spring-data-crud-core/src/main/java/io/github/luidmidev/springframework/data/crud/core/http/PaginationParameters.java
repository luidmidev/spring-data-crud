package io.github.luidmidev.springframework.data.crud.core.http;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "page",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "integer")
)
@Parameter(
        name = "size",
        in = ParameterIn.QUERY,
        schema = @Schema(type = "integer")
)
@Parameter(
        name = "sort",
        in = ParameterIn.QUERY,
        array = @ArraySchema(schema = @Schema(type = "string"))
)
@Parameter(
        name = "pageable", hidden = true
)
public @interface PaginationParameters {
}
