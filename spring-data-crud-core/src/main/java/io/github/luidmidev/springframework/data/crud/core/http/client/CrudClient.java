package io.github.luidmidev.springframework.data.crud.core.http.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CrudClient<M extends Persistable<ID>, D, ID> {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Class<M> modelClass;

    public CrudClient(String baseUrl, Class<M> modelClass, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.modelClass = modelClass;
    }

    public CrudClient(String baseUrl, Class<M> modelClass) {
        this(baseUrl, modelClass, new RestTemplate());
    }

    public Page<M> page(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        var builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("search", search)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        for (var sort : pageable.getSort()) {
            builder.queryParam("sort", sort.getProperty() + "," + sort.getDirection());
        }

        if (filters != null) {
            filters.forEach(builder::queryParam);
        }

        var url = builder.toUriString();
        var response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Page<M>>() {
                }
        );
        return response.getBody();
    }

    public M find(ID id) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(id.toString())
                .toUriString();

        return restTemplate.getForObject(url, modelClass);
    }

    public List<M> find(List<ID> ids) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("ids");

        for (var id : ids) {
            url.queryParam("id", id.toString());
        }

        var response = restTemplate.exchange(
                url.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<M>>() {
                }
        );
        return response.getBody();
    }

    public List<M> find(ID... ids) {
        return find(List.of(ids));
    }

    public long count() {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("count")
                .toUriString();

        var resutl = restTemplate.getForObject(url, Long.class);
        if (resutl == null) {
            throw new IllegalStateException("Count result is null");
        }
        return resutl;
    }

    public boolean exists(ID id) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("exists")
                .queryParam("id", id.toString())
                .toUriString();

        var result = restTemplate.getForObject(url, Boolean.class);
        if (result == null) {
            throw new IllegalStateException("Exists result is null");
        }
        return result;
    }

    public M create(@Valid @NotNull D dto) {
        var response = restTemplate.postForEntity(baseUrl, dto, modelClass);
        return response.getBody();
    }

    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(id.toString())
                .toUriString();

        var httpEntity = new HttpEntity<>(dto);
        var result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, modelClass).getBody();
        if (result == null) {
            throw new IllegalStateException("Update result is null");
        }
        return result;
    }

    public void delete(ID id) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(id.toString())
                .toUriString();
        restTemplate.delete(url);
    }
}
