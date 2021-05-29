package br.com.lab.samples;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserResourceXmlTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";

        var requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.log(LogDetail.ALL);

        var responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(200);

        RestAssured.requestSpecification = requestSpecBuilder.build();
        RestAssured.responseSpecification = responseSpecBuilder.build();
    }

    @Test
    void testXmlNavigation() {
        given()
        .when()
            .get("usersXml/3")
        .then()
//            .statusCode(200)
            .body("user.name", is("Ana Julia"))
            .body("user.@id", is("3"))
            .body("user.filhos.name.size()", is(2))
            .body("user.filhos.name[0]", is("Zezinho"))
            .body("user.filhos.name[1]", is("Luizinho"))
            .body("user.filhos.name", hasItem("Luizinho"))
            .body("user.filhos.name", hasItems("Luizinho", "Zezinho"));
    }

    @Test
    void testXmlNavigationManipulatingRootPath() {
        given()
        .when()
            .get("usersXml/3")
        .then()
//            .statusCode(200)
            .rootPath("user")
            .body("name", is("Ana Julia"))
            .body("@id", is("3"))

            .rootPath("user.filhos")
            .body("name.size()", is(2))

            .detachRootPath("filhos")
            .body("filhos.name[0]", is("Zezinho"))
            .body("filhos.name[1]", is("Luizinho"))

            .appendRootPath("filhos")
            .body("name", hasItem("Luizinho"))
            .body("name", hasItems("Luizinho", "Zezinho"));
    }

    @Test
    void testAdvancedXmlValidations() {
        given()
        .when()
            .get("usersXml")
        .then()
//            .statusCode(200)
            .body("users.user.size()", is(3))
            .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
            .body("users.user.@id", hasItems("1", "2", "3"))
            .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
            .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
            .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
            .body("users.user.age.collect{it.toInteger() *2}", hasItems(40, 50, 60))
            .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"));
    }

    @Test
    void testSimplifiedXmlValidations() {
        List<NodeImpl> nodes =
        given()
        .when()
            .get("usersXml")
        .then()
            .statusCode(200)
            .extract().path("users.user.name.findAll{it.toString().contains('n')}");

        assertEquals(2 , nodes.size());
        assertEquals("Maria Joaquina" , nodes.get(0).toString());
        assertTrue("ANA JULIA".equalsIgnoreCase(nodes.get(1).toString()));
    }

    @Test
    void testAdvancedXmlValidationsWithXPath() {
        given()
        .when()
        .   get("usersXml")
        .then()
//            .statusCode(200)
            .body(hasXPath("count(/users/user)", is("3")))
            .body(hasXPath("/users/user[@id = '1']"))
            .body(hasXPath("//user[@id = '2']"))
            .body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
            .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
            .body(hasXPath("/users/user/name", is("João da Silva")))
            .body(hasXPath("//name", is("João da Silva")))
            .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
            .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
            .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
            .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
            .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
            .body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")));
    }

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }
}
