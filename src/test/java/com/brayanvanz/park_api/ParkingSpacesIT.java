package com.brayanvanz.park_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.brayanvanz.park_api.dtos.ParkingSpaceSaveDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking_spaces/parking_spaces-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking_spaces/parking_spaces-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingSpacesIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void save_ValidData_ReturnLocationStatus201() {
        webTestClient
            .post()
            .uri("/api/v1/parking-spaces")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(new ParkingSpaceSaveDto("A-05", "AVAILABLE"))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void save_UnauthorizedUser_ReturnErrorMessageStatus404() {
        webTestClient
            .post()
            .uri("/api/v1/parking-spaces")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .bodyValue(new ParkingSpaceSaveDto("A-05", "AVAILABLE"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo(403)
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void save_ExistentCode_ReturnErrorMessageStatus409() {
        webTestClient
            .post()
            .uri("/api/v1/parking-spaces")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(new ParkingSpaceSaveDto("A-01", "AVAILABLE"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("status").isEqualTo(409)
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void save_InvalidData_ReturnErrorMessageStatus422() {
        webTestClient
            .post()
            .uri("/api/v1/parking-spaces")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(new ParkingSpaceSaveDto("", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("status").isEqualTo(422)
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces");

        webTestClient
            .post()
            .uri("/api/v1/parking-spaces")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(new ParkingSpaceSaveDto("A-501", "UNAVAILABLE"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("status").isEqualTo(422)
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void findByCode_ExistentCode_ReturnParkingSpaceStatus200() {
        webTestClient
            .get()
            .uri("/api/v1/parking-spaces/{code}", "A-01")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("id").isEqualTo(10)
            .jsonPath("code").isEqualTo("A-01")
            .jsonPath("status").isEqualTo("AVAILABLE");
    }

    @Test
    public void findByCode_UnauthorizedUser_ReturnErrorMessageStatus403() {
        webTestClient
            .get()
            .uri("/api/v1/parking-spaces/{code}", "A-01")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo(403)
            .jsonPath("method").isEqualTo("GET")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces/A-01");
    }

    @Test
    public void findByCode_NonExistentCode_ReturnErrorMessageStatus404() {
        webTestClient
            .get()
            .uri("/api/v1/parking-spaces/{code}", "A-10")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo(404)
            .jsonPath("method").isEqualTo("GET")
            .jsonPath("path").isEqualTo("/api/v1/parking-spaces/A-10");
    }
}
