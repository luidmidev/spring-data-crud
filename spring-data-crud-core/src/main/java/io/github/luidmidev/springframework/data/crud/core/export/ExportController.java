package io.github.luidmidev.springframework.data.crud.core.export;


import io.github.luidmidev.springframework.data.crud.core.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.PageableUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExportController<ID> {

    ReadOperations<?, ID> getService();

    @GetMapping("/report")
    default ResponseEntity<ByteArrayResource> reportPage(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "properties", required = false) List<String> properties,
            @RequestParam(name = "direction", required = false) Sort.Direction direction
    ) {
        var pageable = PageableUtils.resolvePage(page, size, direction, properties);
        var config = ExportConfig.of(fields, titles);
        var result = search == null
                ? getService().page(pageable)
                : getService().search(search, pageable);
        return ExportDataService.generate(result.toList(), config);
    }


    @GetMapping("/report/all")
    default ResponseEntity<ByteArrayResource> reportAll(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles
    ) {
        var config = ExportConfig.of(fields, titles);
        return ExportDataService.generate(getService().list(), config);
    }


    @GetMapping("/report/find")
    default ResponseEntity<ByteArrayResource> reportFind(
            @RequestParam("fields") List<String> fields,
            @RequestParam("titles") List<String> titles,
            @RequestParam("id") ID id
    ) {
        var config = ExportConfig.of(fields, titles);
        var result = getService().find(id);
        return ExportDataService.generate(List.of(result), config);
    }


}
