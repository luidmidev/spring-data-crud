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

import java.util.List;

/**
 * A client for performing CRUD operations via REST API.
 * <p>
 * The {@link CrudClient} provides methods to interact with a RESTful service to
 * perform CRUD (Create, Read, Update, Delete) operations on entities of type {@link M}.
 * It uses {@link RestTemplate} to send requests to a specified base URL.
 * </p>
 *
 * @param <M>  the type of the model entity
 * @param <D>  the type of the DTO (Data Transfer Object) used for creation and updates
 * @param <ID> the type of the entity's ID
 */
public class CrudClient<M extends Persistable<ID>, D, ID> {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Class<M> modelClass;

    /**
     * Creates a new {@link CrudClient} instance with a custom {@link RestTemplate}.
     *
     * @param baseUrl     the base URL of the REST API
     * @param modelClass  the class of the model entity
     * @param restTemplate the {@link RestTemplate} used for making HTTP requests
     */
    public CrudClient(String baseUrl, Class<M> modelClass, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.modelClass = modelClass;
    }

    /**
     * Creates a new {@link CrudClient} instance with the default {@link RestTemplate}.
     *
     * @param baseUrl     the base URL of the REST API
     * @param modelClass  the class of the model entity
     */
    public CrudClient(String baseUrl, Class<M> modelClass) {
        this(baseUrl, modelClass, new RestTemplate());
    }

    /**
     * Retrieves a paginated list of entities based on search and filter criteria.
     *
     * @param search   the search term to filter results
     * @param pageable the pagination information
     * @param filters  additional filter criteria
     * @return a {@link Page} containing the entities
     */
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

    /**
     * Retrieves a single entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity with the specified ID
     */
    public M find(ID id) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(id.toString())
                .toUriString();

        return restTemplate.getForObject(url, modelClass);
    }

    /**
     * Retrieves a list of entities by their IDs.
     *
     * @param ids the list of IDs of the entities to retrieve
     * @return the list of entities with the specified IDs
     */
    public List<M> find(List<ID> ids) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("ids");

        for (var id : ids) {
            url.queryParam("ids", id.toString());
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

    /**
     * Retrieves a list of entities by their IDs.
     *
     * @param ids the IDs of the entities to retrieve
     * @return the list of entities with the specified IDs
     */
    public List<M> find(ID... ids) {
        return find(List.of(ids));
    }

    /**
     * Retrieves the total count of entities.
     *
     * @return the total count of entities
     * @throws IllegalStateException if the count result is null
     */
    public long count() {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("count")
                .toUriString();

        var result = restTemplate.getForObject(url, Long.class);
        if (result == null) {
            throw new IllegalStateException("Count result is null");
        }
        return result;
    }

    /**
     * Checks if an entity with the specified ID exists.
     *
     * @param id the ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     * @throws IllegalStateException if the existence result is null
     */
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

    /**
     * Creates a new entity using the provided DTO.
     *
     * @param dto the DTO containing the data for the new entity
     * @return the created entity
     */
    public M create(D dto) {
        var response = restTemplate.postForEntity(baseUrl, dto, modelClass);
        return response.getBody();
    }

    /**
     * Updates an existing entity with the specified ID using the provided DTO.
     *
     * @param id  the ID of the entity to update
     * @param dto the DTO containing the updated data for the entity
     * @return the updated entity
     * @throws IllegalStateException if the update result is null
     */
    public M update(ID id, D dto) {
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

    /**
     * Deletes the entity with the specified ID.
     *
     * @param id the ID of the entity to delete
     */
    public void delete(ID id) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(id.toString())
                .toUriString();
        restTemplate.delete(url);
    }
}
