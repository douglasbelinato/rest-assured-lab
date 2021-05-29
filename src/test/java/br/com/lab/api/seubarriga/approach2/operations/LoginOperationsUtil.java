package br.com.lab.api.seubarriga.approach2.operations;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class LoginOperationsUtil {

    private LoginOperationsUtil() {}

    public static Response login() {
        var requestBody = new HashMap<String, String>();
        requestBody.put("email", "doug@test.com");
        requestBody.put("senha", "doug0505");

        return login(requestBody);
    }

    public static Response login(Map<String, String> requestBody) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("signin");
    }

}
