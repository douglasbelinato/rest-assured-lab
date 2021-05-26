package br.com.lab;

import io.restassured.RestAssured;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
    }

    @Test
    void testValidXmlSchema() {
        given()
            .log().all()
        .when()
            .get("usersXml")
        .then()
            .log().all()
            .statusCode(200)
            .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"));
    }

    @Test
    void testInvalidXmlSchema() {
        assertThrows(SAXParseException.class, () -> given()
            .log().all()
        .when()
            .get("invalidUsersXml")
        .then()
            .log().all()
            .statusCode(200)
            .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd")));
    }

    @Test
    void testValidJsonSchema() {
        given()
            .log().all()
        .when()
            .get("users")
        .then()
            .log().all()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"));
    }
}
