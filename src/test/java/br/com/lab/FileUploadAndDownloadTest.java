package br.com.lab;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

class FileUploadAndDownloadTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
    }

    @Test
    void testShouldUploadFile() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/test/resources/test.txt"))
                .when()
                .post("upload")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void testFileIsRequiredToUpload() {
        given()
                .log().all()
                .when()
                .post("upload")
                .then()
                .log().all()
                .statusCode(404)
                .body("error", is("Arquivo não enviado"));
    }

    @Test
    void testShouldDownloadFile() throws IOException {
        var fileInBytes = given()
                .log().all()
                .when()
                .get("download")
                .then()
                .statusCode(200)
                .extract().asByteArray();

        var file = new File("src/test/resources/file.jpg");

        try (var fos = new FileOutputStream(file);) {
            fos.write(fileInBytes);
        }

        assertThat(file.length(), greaterThan(1L));
    }
}
