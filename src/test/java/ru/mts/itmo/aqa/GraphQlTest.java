package ru.mts.itmo.aqa;

import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Feature("GraphQL")
class GraphQlTest {

    @Test
    @DisplayName("Query Hello: ожидаемое приветствие без GraphQL errors")
    void queryHello() {
        String query = """
                query Hello {
                  hello(person: { name: "Ivan", age: 35 })
                }
                """;

        given().spec(ApiSpecifications.graphql())
                .body(Map.of("query", query))
        .when()
                .post()
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data", notNullValue())
                .body("data.hello", equalTo("Hello Ivan"))
                .body("$", not(hasKey("errors")));
    }

    @Test
    @DisplayName("Мутирующий CreatePerson: получены id, name и age без GraphQL errors")
    void createPerson() {
        String mutation = """
                mutation CreatePerson {
                  createPerson(person: { name: "Student", age: 20 }) {
                    id
                    name
                    age
                  }
                }
                """;

        given().spec(ApiSpecifications.graphql())
                .body(Map.of("query", mutation))
        .when()
                .post()
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data", notNullValue())
                .body("data.createPerson.id", instanceOf(String.class))
                .body("data.createPerson.id", not(blankOrNullString()))
                .body("data.createPerson.name", equalTo("Student"))
                .body("data.createPerson.age", equalTo(20))
                .body("$", not(hasKey("errors")));
    }

    // Мой дополнительный негативный тест
    @Test
    @DisplayName("Неизвестное поле: HTTP 200, errors и отсутствие успешных данных")
    void rejectUnknownQueryField() {
        String query = "query InvalidField { definitelyUnknownField }";

        given().spec(ApiSpecifications.graphql())
                .body(Map.of("query", query))
        .when()
                .post()
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(
                        "Cannot query field \"definitelyUnknownField\" on type \"Query\"."))
                .body("data", nullValue());
    }
}
