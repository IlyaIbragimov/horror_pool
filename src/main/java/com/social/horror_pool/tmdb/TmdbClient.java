package com.social.horror_pool.tmdb;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.tmdb.TmdbMovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.payload.tmdb.TmdbDiscoverMovieAllResponse;
import com.social.horror_pool.payload.tmdb.TmdbDiscoverRequest;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class TmdbClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String readToken;

    public TmdbClient(RestTemplate restTemplate,
                      @Value("${tmdb.api.base-url}") String baseUrl,
                      @Value("${tmdb.api.read-token}") String readToken) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.readToken = readToken;
    }

    @Retry(name = "tmdbRetry")
    @RateLimiter(name = "tmdbMovieById")
    public Optional<TmdbMovieDTO> getMovieById(long tmdbId, String language) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/movie/{id}")
                .queryParam("language", language)
                .buildAndExpand(tmdbId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(readToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TmdbMovieDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TmdbMovieDTO.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new APIException("TMDB request failed" + ex);
        }
    }

    @Retry(name = "tmdbRetry")
    @RateLimiter(name = "tmdbDiscover")
    public TmdbDiscoverMovieAllResponse discoverHorrors(Integer page, TmdbDiscoverRequest request) {
        TmdbDiscoverRequest discoverRequest = request == null ? new TmdbDiscoverRequest() : request;
        String sortBy = resolveSortBy(discoverRequest.getSortBy());

        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/discover/movie")
                .queryParam("with_genres", AppConstants.TMDB_HORROR_GENRE_ID)
                .queryParam("page", page)
                .queryParam("language", AppConstants.TMDB_DEFAULT_LANGUAGE)
                .queryParam("sort_by", sortBy)
                .queryParam("vote_count.gte", AppConstants.TMDB_MIN_VOTE_COUNT);

        if (discoverRequest.getReleaseDateFrom() != null) {
            urlBuilder.queryParam("primary_release_date.gte", discoverRequest.getReleaseDateFrom());
        }

        if (discoverRequest.getReleaseDateTo() != null) {
            urlBuilder.queryParam("primary_release_date.lte", discoverRequest.getReleaseDateTo());
        }

        if (discoverRequest.getMinVoteAverage() != null) {
            urlBuilder.queryParam("vote_average.gte", discoverRequest.getMinVoteAverage());
        }

        String url = urlBuilder.toUriString();

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(readToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<TmdbDiscoverMovieAllResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TmdbDiscoverMovieAllResponse.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new APIException("TMDB discover request failed: " + ex.getMessage());
        }
    }

    private String resolveSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return AppConstants.TMDB_DEFAULT_SORT_BY;
        }

        if (!AppConstants.TMDB_SUPPORTED_SORT_BY.contains(sortBy)) {
            throw new APIException("Unsupported TMDB sortBy: " + sortBy);
        }

        return sortBy;
    }
}
