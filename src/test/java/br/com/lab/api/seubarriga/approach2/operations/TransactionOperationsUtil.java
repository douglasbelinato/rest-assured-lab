package br.com.lab.api.seubarriga.approach2.operations;

import br.com.lab.dto.TransactionRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class TransactionOperationsUtil {

    private TransactionOperationsUtil() {}

    private static final String TRANSACTIONS_PATH = "transacoes";
    private static final String ID_PATH_PARAM = "/{id}";
    private static final String ID = "id";

    public static Response createTransaction(TransactionRequest request) {
        return given()
                .body(request)
                .when()
                .post(TRANSACTIONS_PATH);
    }

    public static Response deleteTransaction(Long id) {
        return given()
                .pathParam(ID, id)
                .when()
                .delete(TRANSACTIONS_PATH + ID_PATH_PARAM);
    }
}
