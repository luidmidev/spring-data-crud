package io.github.luidmidev.springframework.data.crud.core.export;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExportAuthController<ID> extends ExportController<ID> {

    @GetMapping("/report")
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    default ResponseEntity<ByteArrayResource> reportPage(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "properties", required = false) List<String> properties,
            @RequestParam(name = "direction", required = false) Sort.Direction direction
    ) {
        return ExportController.super.reportPage(fields, titles, search, size, page, properties, direction);
    }


    @GetMapping("/report/all")
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    default ResponseEntity<ByteArrayResource> reportAll(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles
    ) {
        return ExportController.super.reportAll(fields, titles);
    }


    @GetMapping("/report/find")
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    default ResponseEntity<ByteArrayResource> reportFind(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles,
            @RequestParam("id") ID id
    ) {
        return ExportController.super.reportFind(fields, titles, id);
    }
}
