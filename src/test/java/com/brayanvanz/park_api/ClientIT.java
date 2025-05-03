package com.brayanvanz.park_api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.brayanvanz.park_api.dtos.ClientResponseDto;
import com.brayanvanz.park_api.dtos.ClientSaveDto;
import com.brayanvanz.park_api.dtos.PageableDto;
import com.brayanvanz.park_api.exceptions.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clients/clients-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("null")
public class ClientIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void save_ValidInput_ReturnClientStatus201() {
        ClientResponseDto responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("Yugi Muto", "09387940004"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(ClientResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getName()).isEqualTo("Yugi Muto");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("09387940004");
    }

    @Test
    public void save_UserNotAuthorized_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("Yugi Muto", "09387940004"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void save_CpfAlreadyRegistered_ReturnErrorMessageStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("Yugi Muto", "94636819063"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void save_InvalidData_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("Yuu", "00000000000"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "yugi@gmail.com", "123456"))
            .bodyValue(new ClientSaveDto("Yuu", "946.368.190-63"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void findById_ExistentId_ReturnClientStatus200() {
        ClientResponseDto responseBody = webTestClient
            .get()
            .uri("/api/v1/clients/10")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClientResponseDto.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findById_ExistentIdByClient_ReturnErrorMessage403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clients/0")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findById_NonExistentIdByAdmin_ReturnErrorMessage404() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clients/0")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void findAll_PaginationAdmin_ReturnClientsStatus200() {
        PageableDto responseBody = webTestClient
            .get()
            .uri("/api/v1/clients")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = webTestClient
            .get()
            .uri("/api/v1/clients?size=1&page=1")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void findAll_PaginationClient_ReturnErrorMessage403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clients")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
