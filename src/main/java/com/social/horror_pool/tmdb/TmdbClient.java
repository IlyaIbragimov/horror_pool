package com.social.horror_pool.tmdb;

import com.social.horror_pool.dto.tmdb.TmdbMovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.payload.tmdb.TmdbDiscoverMovieAllResponse;
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

    public Optional<TmdbMovieDTO> getMovieById(long tmdbId, String language) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
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

    public TmdbDiscoverMovieAllResponse discoverHorrors(Integer page, String language) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path("/discover/movie")
                .queryParam("with_genre", 27)
                .queryParam("page", page)
                .queryParam("language", language)
                .toUriString();

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
}
