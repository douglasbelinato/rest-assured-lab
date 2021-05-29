package br.com.lab.samples;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasXPath;

class ParametersTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        RestAssured.basePath = "v2";
    }

    @Test
    void testQueryString() {
        given()
                .log().all()
                .when()
                .get("users?format=json")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .contentType(Matchers.containsString("utf-8"));
    }

    @Test
    void testQueryString2() {
        given()
                .log().all()
                .queryParam("format", "xml")
                .when()
                .get("users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML)
                .contentType(Matchers.containsString("utf-8"));
    }

    @Test
    void testHeader() {
        given()
                .log().all()
                .accept(ContentType.HTML)
                .when()
                .get("users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .contentType(Matchers.containsString("UTF-8"));
    }

    @Test
    void testHtmlNavigation() {
        given()
                .log().all()
                .accept(ContentType.HTML)
                .when()
                .get("users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .contentType(Matchers.containsString("UTF-8"))
                .body("html.body.div.table.tbody.tr.size()", is(3))
                .body("html.body.div.table.tbody.tr[1].td[2]", is("25"))
                .appendRootPath("html.body.div.table.tbody")
                .body("tr.find{it.toString().startsWith('2')}.td[1]", is("Maria Joaquina"));
    }

    @Test
    void testHtmlNavigationWithXPath() {
        given()
                .log().all()
                .queryParam("format", "clean")
                .when()
                .get("users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .contentType(Matchers.containsString("UTF-8"))
                .body(hasXPath("count(//table/tr)", is("4")))
                .body(hasXPath("//td[text() = '2']/../td[2]", is("Maria Joaquina")));
    }

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

}
