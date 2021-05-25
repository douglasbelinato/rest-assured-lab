package br.com.lab;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

class HelloWorldTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
    }

    @Test
    void testHelloWorld() {
        get("ola").then().statusCode(200);
    }

    @Test
    void testHelloWorld2() {
        given().when().get("ola").then().statusCode(200);
    }

}
