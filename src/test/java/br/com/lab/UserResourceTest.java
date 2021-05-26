package br.com.lab;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserResourceTest {

    @BeforeAll
    public static void setup() {
        baseURI = "http://restapi.wcaquino.me";
    }

    @Test
    void testJsonFirstLevel() {
        given()
        .when()
            .get("users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", containsString("Silva"))
            .body("age", greaterThan(18));
    }

    @Test
    void testJsonSecondLevel() {
        given()
        .when()
            .get("users/2")
        .then()
            .statusCode(200)
            .body("name", containsString("Joaquina"))
            .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    void testJsonList() {
        given()
        .when()
            .get("users/3")
        .then()
            .statusCode(200)
            .body("name", containsString("Ana"))
            .body("filhos", hasSize(2))
            .body("filhos[0].name", is("Zezinho"))
            .body("filhos[1].name", is("Luizinho"))
            .body("filhos.name", hasItem("Luizinho"))
            .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    void testRootElementWithJsonList() {
        given()
        .when()
            .get("users")
        .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
            .body("age[1]", is(25))
            .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
            .body("salary", contains(1234.5677f, 2500, null));
    }

    @Test
    void testUserNotFound() {
        given()
        .when()
            .get("users/4")
        .then()
            .statusCode(404)
            .body("error", is("Usuário inexistente"));
    }

    @Test
    void testAdvancedJsonPathValidations() {
        given()
        .when()
            .get("users")
        .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .body("age.findAll{it <= 25}.size()", is(2))
            .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
            .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
            .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
            .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
            .body("find{it.age <= 25}.name", is("Maria Joaquina"))
            .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
            .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
            .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
            .body("age.collect{it * 2}", hasItems(60, 50, 40))
            .body("id.max()", is(3))
            .body("salary.min()", is(1234.5678f))
            .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)));
    }

    @Test
    void testSimplifiedJsonPathValidations() {
        List<String> names =
            given()
            .when()
                .get("users")
            .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");

        assertEquals(1, names.size());
        assertTrue(names.get(0).equalsIgnoreCase("Maria Joaquina"));
    }

    @Test
    void testShouldSaveUser() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{ \"name\": \"Jose\", \"age\": 35 }")
        .when()
            .post("users")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("name", is("Jose"))
            .body("age", is(35));
    }

    @Test
    void testShouldNotSaveUser() {
        given()
        .log().all()
            .contentType("application/json")
            .body("{ \"age\": 35 }")
        .when()
            .post("users")
        .then()
            .log().all()
            .statusCode(400)
            .body("error", is("Name é um atributo obrigatório"));
    }

    @Test
    void testShouldUpdateUser() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{ \"name\": \"Luiza\", \"age\": 18 }")
        .when()
            .put("users/1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Luiza"))
            .body("age", is(18));
    }

    @Test
    void testShouldUpdateUserUsingUrlParams() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{ \"name\": \"Luiza\", \"age\": 18 }")
        .when()
            .put("{path}/{id}", "users", "1")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Luiza"))
            .body("age", is(18));
    }

    @Test
    void testShouldUpdateUserUsingUrlParams2() {
        given()
            .log().all()
            .contentType("application/json")
            .pathParam("path", "users")
            .pathParam("id", "1")
            .body("{ \"name\": \"Luiza\", \"age\": 18 }")
        .when()
            .put("{path}/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Luiza"))
            .body("age", is(18));
    }

    @Test
    void testShouldRemoveUser() {
        given()
            .log().all()
        .when()
            .delete("users/1")
        .then()
            .log().all()
            .statusCode(204);
    }

    @Test
    void testShouldNotDeleteUser() {
        given()
        .log().all()
        .when()
            .delete("users/1234")
        .then()
            .log().all()
            .statusCode(400)
            .body("error", is("Registro inexistente"));
    }
}
