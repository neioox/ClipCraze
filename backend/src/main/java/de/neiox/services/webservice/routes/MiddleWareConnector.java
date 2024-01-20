package de.neiox.services.webservice.routes;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpStatus;

@RestController
public class MiddleWareConnector {

    private final String FLASK_BASE_URL = "http://localhost:5000";  // Update with your Flask app's URL
    private final WebClient webClient;

    public MiddleWareConnector(WebClient.Builder webClientBuilder, ClientHttpConnector connector) {
        this.webClient = webClientBuilder.baseUrl(FLASK_BASE_URL).clientConnector(connector).build();
    }

    private Mono<ResponseEntity<String>> handleRequest(HttpMethod method, String uri) {
        return webClient.method(method)
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse ->
                        Mono.error(new RuntimeException("Error occurred while processing request. Status code: " + clientResponse.statusCode())))
                .toEntity(String.class)
                .doOnNext(response -> System.out.println("Response: " + response.getBody()));
    }


    @PostMapping("/api/crop4tiktok/{filename}")
    public Mono<ResponseEntity<String>> crop4TikTok(@PathVariable String filename) {
        return handleRequest(HttpMethod.POST, "/crop4tiktok/" + filename);
    }

    @RequestMapping(value = "/api/addsubtitles2vid/{filename}", method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<ResponseEntity<String>> addSubtitlesToVideo(@PathVariable String filename) {
        return handleRequest(HttpMethod.GET, "/addsubtitles2vid/" + filename);
    }

    @RequestMapping(value = "/api/genSubtitle/{filename}", method = RequestMethod.POST)
    public Mono<ResponseEntity<String>> generateSubtitle(@PathVariable String filename) {
        return handleRequest(HttpMethod.POST, "/genSubtitle/" + filename);
    }

    @RequestMapping(value = "/api/generateText/{topic}", method = RequestMethod.POST)
    public Mono<ResponseEntity<String>> generateText(@PathVariable String topic) {

        System.out.println("");
        return handleRequest(HttpMethod.POST, "/generateText/" + topic);
    }
}
