package com.brayanvanz.park_api;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.brayanvanz.park_api.dtos.UserPasswordDto;
import com.brayanvanz.park_api.dtos.UserResponseDto;
import com.brayanvanz.park_api.dtos.UserSaveDto;
import com.brayanvanz.park_api.enums.Role;
import com.brayanvanz.park_api.exceptions.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("null")
public class UserIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void save_ValidUsernameAndPassword_ReturnCreatedUserStatus201() {
        UserResponseDto responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@gmail.com", "123456"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("yugi@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    public void save_InvalidUsername_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@gmail.", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void save_InvalidPassword_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@gmail.com", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@gmail.com", "123"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("yugi@gmail.com", "123456789"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void save_RepeatedUsername_ReturnErrorMessageStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserSaveDto("tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findById_ExistentId_ReturnUserStatus200() {
        UserResponseDto responseBody = webTestClient
            .get()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("joey@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(Role.ADMIN);

        responseBody = webTestClient
            .get()
            .uri("/api/v1/users/101")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("tea@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(Role.CLIENT);       

        responseBody = webTestClient
            .get()
            .uri("/api/v1/users/101")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("tea@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    public void findById_NonExistentId_ReturnUserStatus404() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users/0")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findById_ClientSearchForOtherClient_ReturnUserStatus403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users/102")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_ValidPassword_ReturnStatus204() {
        webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isNoContent();

        webTestClient
            .patch()
            .uri("/api/v1/users/101")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_DifferentUsers_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/101")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void updatePassword_InvalidFields_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("", "", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("12345", "12345", "12345"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("12345678", "12345678", "12345678"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_InvalidPassword_ReturnErrorMessageStatus400() {
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("123456", "123456", "000000"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/users/100")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserPasswordDto("000000", "123456", "123456"))
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void findAll_AuthorizedAccess_ReturnUsersListStatus200() {
        List<UserResponseDto> responseBody = webTestClient
            .get()
            .uri("/api/v1/users")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "joey@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponseDto.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.size()).isEqualTo(3);
    }

    @Test
    public void findAll_UnauthorizedAccess_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/users")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tea@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
