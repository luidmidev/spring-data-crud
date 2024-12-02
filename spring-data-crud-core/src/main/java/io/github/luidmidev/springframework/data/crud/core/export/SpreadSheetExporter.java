package io.github.luidmidev.springframework.data.crud.core.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.luidmidev.apache.poi.CellStylizer;
import io.github.luidmidev.apache.poi.WorkbookListMapper;
import io.github.luidmidev.apache.poi.WorkbookManager;
import io.github.luidmidev.apache.poi.WorkbookManagerUtils;
import io.github.luidmidev.apache.poi.exceptions.WorkbookException;
import io.github.luidmidev.apache.poi.model.SpreadSheetFile;
import io.github.luidmidev.apache.poi.model.WorkbookType;
import io.github.luidmidev.springframework.data.crud.core.utils.HeadersUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpreadSheetExporter implements Exporter {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Class<?>, Map<String, Method>> methodCache = new ConcurrentHashMap<>();


    public ResponseEntity<ByteArrayResource> export(List<?> elements, ExportConfig config) {

        try {
            log.debug("Generating report with config: {}", config);
            try (var workbook = getWorkbookModelBuilder(config, elements)) {
                var report = workbook.getSpreadsheet("report");
                return getResponseEntity(report);
            }

        } catch (Exception e) {
            log.error("Error generating report", e);
            throw new ExporterException("Error generating report: " + e.getMessage(), e);
        }
    }

    private <T> WorkbookManager getWorkbookModelBuilder(ExportConfig config, List<T> elements) throws WorkbookException {
        var columns = config.getColumns();
        var headerStyle = CellStylizer.init()
                .fontBold()
                .allBorders(BorderStyle.THIN);


        return WorkbookListMapper.from(elements)
                .map((manager, configuration) -> {
                    for (var column : columns) {
                        configuration.withColumn(column.title(), element -> getValueSafely(column.field(), element));
                    }
                    configuration.configureSheet(sheet -> {
                                for (int i = 0; i < columns.size(); i++) {
                                    sheet.autoSizeColumn(i);
                                    var width = (int) (sheet.getColumnWidth(i) * 1.1);
                                    if (width <= 255 * 256) sheet.setColumnWidth(i, width);
                                }
                            })
                            .withHeaderStyle(headerStyle)
                            .forEachRow((row, element) -> WorkbookManagerUtils.adjustRowHeightByLines(row, manager.getEvaluator()));
                });

    }

    private Object getValueSafely(String attributeOrPath, Object element) {
        try {
            return solveValue(getValue(element, attributeOrPath));
        } catch (Exception e) {
            log.error("Error resolving field: {}", attributeOrPath, e);
            throw new ExporterException("Error resolving field: " + attributeOrPath + " - " + e.getMessage(), e);
        }
    }

    @SneakyThrows
    private Object getValue(Object target, String attributeOrPath) {

        var spliter = Spliter.split(attributeOrPath);

        var attribute = spliter.attribute();

        var value = target instanceof Map<?, ?> map
                ? map.getOrDefault(attribute, null)
                : resolveGetter(target.getClass(), attribute).invoke(target);

        if (value == null) return null;

        var path = spliter.path();
        if (path == null) return value;

        // for collections
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(element -> getValue(element, path)).toList();
        }

        // for arrays
        if (value.getClass().isArray()) {
            return getValueForArray(value, path);
        }

        return getValue(value, path);
    }

    private ArrayList<Object> getValueForArray(Object value, String path) {
        var length = Array.getLength(value);
        var list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(getValue(Array.get(value, i), path));
        }
        return list;
    }


    private Object solveValue(Object value) {
        return switch (value) {
            case String s -> s;
            case Number n -> n;
            case Boolean b -> b;
            case Date d -> d;
            case LocalDate ld -> ld;
            case LocalDateTime ldt -> ldt;
            case Calendar c -> c;
            case RichTextString r -> r;
            case null -> "";
            default -> serializeSafely(value);
        };
    }


    private String serializeSafely(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "<error: " + e.getMessage() + ">";
        }
    }

    private Method resolveGetter(Class<?> targetClass, String attribute) {

        if (targetClass.isPrimitive()) {
            throw new ExporterException("Cannot resolve getter for primitive type: " + targetClass);
        }

        var methods = methodCache.computeIfAbsent(targetClass, cls -> new ConcurrentHashMap<>());

        return methods.computeIfAbsent(attribute, name -> {

            var methodsDeclared = Arrays.stream(targetClass.getDeclaredMethods())
                    .filter(this::isValidMathod)
                    .toList();

            var jsonProperty = methodsDeclared.stream()
                    .filter(method -> method.isAnnotationPresent(JsonProperty.class))
                    .filter(method -> {
                        var value = method.getAnnotation(JsonProperty.class).value();
                        if (JsonProperty.USE_DEFAULT_NAME.equals(value)) return false;
                        return value.equals(name);
                    })
                    .findFirst();

            if (jsonProperty.isPresent()) {
                return jsonProperty.get();
            }

            var camelSuffix = attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
            return methodsDeclared.stream()
                    .filter(method -> List.of("get" + camelSuffix, "is" + camelSuffix, attribute).contains(method.getName()))
                    .findFirst()
                    .orElseThrow(() -> new ExporterException("No property found for attribute: " + attribute));

        });

    }

    private boolean isValidMathod(Method method) {
        if (method.getParameterCount() > 0) return false;
        if (method.getReturnType() == void.class || method.getReturnType() == Void.class) return false;
        if (method.getName().equals("getClass")) return false;
        return Modifier.isPublic(method.getModifiers());
    }


    private static ResponseEntity<ByteArrayResource> getResponseEntity(SpreadSheetFile report) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(resolveMediaType(report.getType())))
                .headers(HeadersUtils.getHeadersForFile(report.getFilename()))
                .body(new ByteArrayResource(report.getContent()));
    }

    private static String resolveMediaType(WorkbookType type) {
        return switch (type) {
            case XLSX -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case XLS -> "application/vnd.ms-excel";
            case XLSM -> "application/vnd.ms-excel.sheet.binary.macroenabled.12";
        };
    }

    private record Spliter(String attribute, String path) {
        static Spliter split(String field) {
            var parts = field.split("\\.", 2);
            return new Spliter(parts[0], parts.length > 1 ? parts[1] : null);
        }
    }
}
