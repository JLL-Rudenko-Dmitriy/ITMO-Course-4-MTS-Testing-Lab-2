package ru.mts.itmo.aqa;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@Epic("Тестирование API")
@Feature("REST: авторизация")
class AuthTest {

    @Test
    @DisplayName("Login - Bearer token - профиль авторизованного пользователя")
    void loginAndGetCurrentUser() {
        RequestSpecification spec = ApiSpecifications.playground();

        String token = login(spec);
        checkCurrentUser(spec, token);
    }

    @Test
    @DisplayName("GET /auth/me без токена: отказ в доступе с описанием ошибки")
    void rejectRequestWithoutToken() {
        checkUnauthorizedAccess(ApiSpecifications.playground());
    }

    @Step("Выполнить login и извлечь access_token")
    private String login(RequestSpecification spec) {
        return given().spec(spec)
                .body(Map.of("username", "Bret", "password", "Password@123"))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("access_token", instanceOf(String.class))
                .body("access_token", not(blankOrNullString()))
                .extract().path("access_token");
    }

    @Step("Передать полученный Bearer token и проверить профиль")
    private void checkCurrentUser(RequestSpecification spec, String token) {
        given().spec(spec)
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/auth/me")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", greaterThan(0))
                .body("username", equalToIgnoringCase("Bret"))
                .body("email", equalTo("sincere@april.biz"));
    }

    @Step("Проверить отказ в доступе без токена")
    private void checkUnauthorizedAccess(RequestSpecification spec) {
        given().spec(spec)
        .when()
                .get("/auth/me")
        .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("status", equalTo(401))
                .body("error", containsString("Unauthorized"))
                .body("message", containsString("Bearer access token"));
    }
}
