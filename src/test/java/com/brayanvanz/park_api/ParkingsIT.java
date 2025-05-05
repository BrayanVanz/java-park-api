package com.brayanvanz.park_api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.brayanvanz.park_api.dtos.PageableDto;
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

    @Test
    public void findByReceipt_AdminRole_ReturnParkingResponseStatus200() {
        webTestClient
            .get()
            .uri("/api/v1/parkings/check-in/{receipt}", "20250313-101300")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("plate").isEqualTo("FIT-1020")
            .jsonPath("brand").isEqualTo("FIAT")
            .jsonPath("model").isEqualTo("PALIO")
            .jsonPath("color").isEqualTo("GREEN")
            .jsonPath("clientCpf").isEqualTo("94636819063")
            .jsonPath("receipt").isEqualTo("20250313-101300")
            .jsonPath("entryDate").isEqualTo("2025-03-13 10:15:00")
            .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void findByReceipt_ClientRole_ReturnParkingResponseStatus200() {
        webTestClient
            .get()
            .uri("/api/v1/parkings/check-in/{receipt}", "20250313-101300")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tristan@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("plate").isEqualTo("FIT-1020")
            .jsonPath("brand").isEqualTo("FIAT")
            .jsonPath("model").isEqualTo("PALIO")
            .jsonPath("color").isEqualTo("GREEN")
            .jsonPath("clientCpf").isEqualTo("94636819063")
            .jsonPath("receipt").isEqualTo("20250313-101300")
            .jsonPath("entryDate").isEqualTo("2025-03-13 10:15:00")
            .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void findByReceipt_NonExistentReceipt_ReturnErrorMessageStatus404() {
        webTestClient
            .get()
            .uri("/api/v1/parkings/check-in/{receipt}", "20250313-999999")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tristan@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo("404")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-in/20250313-999999")
            .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void checkOut_ExistentReceipt_ReturnStatus200() {
        webTestClient
            .put()
            .uri("/api/v1/parkings/check-out/{receipt}", "20250313-101300")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("plate").isEqualTo("FIT-1020")
            .jsonPath("brand").isEqualTo("FIAT")
            .jsonPath("model").isEqualTo("PALIO")
            .jsonPath("color").isEqualTo("GREEN")
            .jsonPath("clientCpf").isEqualTo("94636819063")
            .jsonPath("receipt").isEqualTo("20250313-101300")
            .jsonPath("entryDate").isEqualTo("2025-03-13 10:15:00")
            .jsonPath("parkingSpaceCode").isEqualTo("A-01")
            .jsonPath("exitDate").exists()
            .jsonPath("amount").exists()
            .jsonPath("discount").exists();
    }

    @Test
    public void checkOut_NonExistentReceipt_ReturnErroMessageStatus404() {
        webTestClient
            .put()
            .uri("/api/v1/parkings/check-out/{receipt}", "20250313-000000")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo("404")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-out/20250313-000000")
            .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void checkOut_ClientRole_ReturnErroMessageStatus403() {
        webTestClient
            .put()
            .uri("/api/v1/parkings/check-out/{receipt}", "20250313-101300")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo("403")
            .jsonPath("path").isEqualTo("/api/v1/parkings/check-out/20250313-101300")
            .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    @SuppressWarnings({ "rawtypes", "null" })
    public void findAllParkingsByCpf_ClientCpf_ReturnStatus200() {
        PageableDto responseBody = webTestClient
            .get()
            .uri("/api/v1/parkings/cpf/{cpf}?size=1&page=0", "94636819063")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();
    
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        Assertions.assertThat(responseBody.getSize()).isEqualTo(1);
        
        responseBody = webTestClient
            .get()
            .uri("/api/v1/parkings/cpf/{cpf}?size=1&page=1", "94636819063")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();
    
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        Assertions.assertThat(responseBody.getSize()).isEqualTo(1);
    }
    
    @Test
    public void findAllParkingsByCpf_ClientRole_ReturnErrorMessageStatus403() {
        webTestClient
            .get()
            .uri("/api/v1/parkings/cpf/{cpf}", "94636819063")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo("403")
            .jsonPath("path").isEqualTo("/api/v1/parkings/cpf/94636819063")
            .jsonPath("method").isEqualTo("GET");
    }
}
