package br.com.lab.api.seubarriga.approach2;

import br.com.lab.api.seubarriga.approach2.operations.AccountOperationsUtil;
import br.com.lab.api.seubarriga.approach2.operations.LoginOperationsUtil;
import br.com.lab.dto.AccountCreationRequest;
import br.com.lab.dto.AccountUpdateRequest;
import br.com.lab.model.Account;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

class AccountTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://barrigarest.wcaquino.me";

        var token = LoginOperationsUtil.login().then().extract().path("token");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "JWT " + token)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Test workflow account")
    void testWorkflowAccount() {
        var account = Account.builder().build();

        // Should create account
        account = AccountOperationsUtil.createAccount()
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().as(Account.class);

        // Should not create account with same name
        var request = AccountCreationRequest.builder().name(account.getName()).build();

        AccountOperationsUtil.createAccount(request)
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"));


        var requestUpdate = AccountUpdateRequest.builder().name(account.getName() + " Updated").build();

        // Should update account
        account = AccountOperationsUtil.updateAccount(account.getId(), requestUpdate)
                .then()
                .statusCode(200)
                .body("id", is(account.getId().intValue()))
                .body("nome", is(requestUpdate.getName()))
                .extract().as(Account.class);

        // Should delete account
        AccountOperationsUtil.deleteAccount(account.getId())
                .then()
                .statusCode(204);
    }

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

}
