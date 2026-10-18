package ru.mts.itmo.aqa;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RestAssuredConfig.config;

final class ApiSpecifications {

    static RequestSpecification playground() {
        return jsonSpec("https://playground.nileslabs.com/api/v1")
                .header("X-Playground-Identity", UUID.randomUUID().toString());
    }

    static RequestSpecification graphql() {
        return jsonSpec("https://graphql.postman-echo.com/graphql");
    }

    private static RequestSpecification jsonSpec(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(config()
                        .logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails())
                        .httpClient(httpClientConfig()
                                .setParam("http.connection.timeout", 10_000)
                                .setParam("http.socket.timeout", 30_000)))
                .addFilter(new AllureRestAssured())
                .build();
    }
}
