package ru.mts.itmo.aqa;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@Feature("REST: посты")
class PostsTest {

    @Test
    @DisplayName("POST - PATCH - GET: изменено только название")
    void createUpdateAndGetPost() {
        RequestSpecification spec = ApiSpecifications.playground();
        Map<String, Object> post = Map.of(
                "title", "Initial API test post",
                "body", "This body must survive the title update",
                "user_id", 1);
        String updatedTitle = "Updated API test post";

        String id = createPost(spec, post);
        updatePostTitle(spec, id, updatedTitle, post);
        checkPost(spec, id, updatedTitle, post);
    }

    @Step("Создать пост и извлечь его id")
    private String createPost(RequestSpecification spec, Map<String, Object> post) {
        return given().spec(spec)
                .body(post)
        .when()
                .post("/posts")
        .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", instanceOf(String.class))
                .body("id", not(blankOrNullString()))
                .body("title", equalTo(post.get("title")))
                .body("body", equalTo(post.get("body")))
                .body("user_id", equalTo(post.get("user_id")))
                .extract().path("id");
    }

    @Step("Изменить только название созданного поста")
    private void updatePostTitle(RequestSpecification spec, String id, String updatedTitle,
                                 Map<String, Object> post) {
        given().spec(spec)
                .pathParam("id", id)
                .body(Map.of("title", updatedTitle))
        .when()
                .patch("/posts/{id}")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("title", equalTo(updatedTitle))
                .body("body", equalTo(post.get("body")))
                .body("user_id", equalTo(post.get("user_id")));
    }

    @Step("Получить тот же пост и проверить сохранённые изменения")
    private void checkPost(RequestSpecification spec, String id, String updatedTitle,
                           Map<String, Object> post) {
        given().spec(spec)
                .pathParam("id", id)
        .when()
                .get("/posts/{id}")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(id))
                .body("title", equalTo(updatedTitle))
                .body("body", equalTo(post.get("body")))
                .body("user_id", equalTo(post.get("user_id")));
    }
}
