package com.brayanvanz.park_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.brayanvanz.park_api.dtos.ParkingSaveDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parkings/parkings-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parkings/parkings-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingsIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void checkIn_ValidData_ReturnCreatedAndLocation() {
        ParkingSaveDto saveDto = ParkingSaveDto.builder()
            .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
            .color("BLUE").clientCpf("07141873058")
            .build();
        
        webTestClient
            .post()
            .uri("/api/v1/parkings/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(saveDto)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION)
            .expectBody()
            .jsonPath("plate").isEqualTo("WER-1111")
            .jsonPath("brand").isEqualTo("FIAT")
            .jsonPath("model").isEqualTo("PALIO 1.0")
            .jsonPath("color").isEqualTo("BLUE")
            .jsonPath("clientCpf").isEqualTo("07141873058")
            .jsonPath("receipt").exists()
            .jsonPath("entryDate").exists()
            .jsonPath("parkingSpaceCode").exists();
    }

    @Test
    public void checkIn_ClientRole_ReturnErrorMessageStatus403() {
        ParkingSaveDto saveDto = ParkingSaveDto.builder()
            .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
            .color("BLUE").clientCpf("07141873058")
            .build();
        
        webTestClient
            .post()
            .uri("/api/v1/parkings/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .bodyValue(saveDto)
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo("403")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
            .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void checkIn_NonExistentCpf_ReturnErrorMessageStatus404() {
        ParkingSaveDto saveDto = ParkingSaveDto.builder()
            .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
            .color("BLUE").clientCpf("39055757012")
            .build();
        
        webTestClient
            .post()
            .uri("/api/v1/parkings/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(saveDto)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo("404")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
            .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/parkings/parkings-taken-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/parkings/parkings-taken-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void checkIn_AllParkingSpacesTaken_ReturnErrorMessageStatus404() {
        ParkingSaveDto saveDto = ParkingSaveDto.builder()
            .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
            .color("BLUE").clientCpf("07141873058")
            .build();
        
        webTestClient
            .post()
            .uri("/api/v1/parkings/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(saveDto)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo("404")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
            .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void checkIn_InvalidData_ReturnErrorMessageStatus422() {
        ParkingSaveDto saveDto = ParkingSaveDto.builder()
            .plate("").brand("").model("")
            .color("").clientCpf("")
            .build();
        
        webTestClient
            .post()
            .uri("/api/v1/parkings/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .bodyValue(saveDto)
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("status").isEqualTo("422")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
            .jsonPath("method").isEqualTo("POST");
    }
}
