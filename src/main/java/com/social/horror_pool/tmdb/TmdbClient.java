package com.social.horror_pool.tmdb;

import com.social.horror_pool.dto.tmdb.TmdbMovieDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public TmdbMovieDTO getMovieById(long tmdbId, String language) {
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
            ResponseEntity<TmdbMovieDTO> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TmdbMovieDTO.class
            );
            return resp.getBody();
        } catch (RestClientException ex) {
            throw new RuntimeException("TMDB request failed: " + ex.getMessage(), ex);
        }
    }
}
