package br.com.lab.samples;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

class HelloWorldTest {

    @BeforeAll
    public static void setup() {
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

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

}
