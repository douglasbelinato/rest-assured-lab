package br.com.lab.api.seubarriga.approach2.operations;

import br.com.lab.api.seubarriga.util.MathUtil;
import br.com.lab.dto.AccountCreationRequest;
import br.com.lab.dto.AccountUpdateRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class AccountOperationsUtil {

    private AccountOperationsUtil() {}

    private static final String ACCOUNTS_PATH = "contas";
    private static final String ID_PATH_PARAM = "/{id}";
    private static final String ID = "id";

    public static Response listAccounts() {
        return given()
                .when()
                .get(ACCOUNTS_PATH);
    }

    public static Response createAccount() {
        var request = AccountCreationRequest.builder().name("Account " + MathUtil.generateRandomInt()).build();
        return createAccount(request);
    }

    public static Response createAccount(AccountCreationRequest request) {
        return given()
                .body(request)
                .when()
                .post(ACCOUNTS_PATH);
    }

    public static Response updateAccount(Long id, AccountUpdateRequest request) {
        return given()
                .pathParam(ID, id)
                .body(request)
                .when()
                .put(ACCOUNTS_PATH + ID_PATH_PARAM);
    }

    public static Response deleteAccount(Long id) {
        return given()
                .pathParam(ID, id)
                .when()
                .delete(ACCOUNTS_PATH + ID_PATH_PARAM);
    }

}
