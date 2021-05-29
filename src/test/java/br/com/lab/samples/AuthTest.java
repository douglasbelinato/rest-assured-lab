package br.com.lab.samples;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

class AuthTest {

    @Test
    void testWithNoAuth() {
        given()
            .log().all()
            .pathParam("id", 1)
        .when()
            .get("https://swapi.dev/api/people/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Luke Skywalker"));
    }

    @Test
    void testBasicAuthRequired() {
        given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    void testBasicAuth() {
        given()
                .log().all()
                .auth().basic("admin", "senha")
                .when()
                .get("http://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"));
    }

    @Test
    void testBasicAuthWithChallenge() {
        given()
                .log().all()
                .auth().preemptive().basic("admin", "senha")
                .when()
                .get("http://restapi.wcaquino.me/basicauth2")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"));
    }

    @Test
    void testBasicAuthWithJwtToken() {
        var requestBody = new HashMap<String, String>();
        requestBody.put("email", "doug@test.com");
        requestBody.put("senha", "doug0505");

        String token = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("http://barrigarest.wcaquino.me/signin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token");

        given()
                .log().all()
                .header("Authorization", "JWT " + token)
                .when()
                .get("http://barrigarest.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void testWebHtmlAuthWithCookie() {
        String cookie = given()
                .log().all()
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .formParam("email", "doug@test.com")
                .formParam("senha", "doug0505")
                .when()
                .post("http://seubarriga.wcaquino.me/logar")
                .then()
                .log().all()
                .statusCode(200)
                .extract().header("set-cookie");

        cookie = cookie.split("=")[1].split(";")[0];

        given()
                .log().all()
                .cookie("connect.sid", cookie)
                .when()
                .get("http://seubarriga.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200)
                .body("html.body.find{it.table.@id = 'tabelaSaldo'}.table.thead.tr.th[0]", containsString("Conta"));
    }

}
