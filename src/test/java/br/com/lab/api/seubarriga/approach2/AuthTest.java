package br.com.lab.api.seubarriga.approach2;

import br.com.lab.api.seubarriga.approach2.operations.AccountOperationsUtil;
import br.com.lab.api.seubarriga.approach2.operations.LoginOperationsUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class AuthTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://barrigarest.wcaquino.me";
    }

    private static final String CONTAS_PATH = "contas";

    @Test
    void testShouldNotAccessWithoutToken() {
        AccountOperationsUtil.listAccounts()
                .then()
                .statusCode(401);
    }

    @Test
    void testShouldAccessWithToken() {
        var token = LoginOperationsUtil.login()
                            .then()
                                .log().all()
                                .statusCode(200)
                                .extract().path("token");

        given()
            .header("Authorization", "JWT " + token)
        .when()
            .get(CONTAS_PATH)
        .then()
            .log().all()
            .statusCode(200);
    }

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

}
