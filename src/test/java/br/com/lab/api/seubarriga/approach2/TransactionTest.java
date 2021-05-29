package br.com.lab.api.seubarriga.approach2;

import br.com.lab.api.seubarriga.approach2.operations.AccountOperationsUtil;
import br.com.lab.api.seubarriga.approach2.operations.LoginOperationsUtil;
import br.com.lab.api.seubarriga.approach2.operations.TransactionOperationsUtil;
import br.com.lab.dto.TransactionRequest;
import br.com.lab.model.Account;
import br.com.lab.model.Transaction;
import br.com.lab.model.TransactionTypeEnum;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;

class TransactionTest {

    private static Account account;
    private static final String TRANSACTIONS_PATH = "transacoes";
    private static final String ACCOUNTS_PATH = "contas";
    private static final String ID_PATH_PARAM = "/{id}";
    private static final String ID = "id";

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://barrigarest.wcaquino.me";

        var token = LoginOperationsUtil.login().then().extract().path("token");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "JWT " + token)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        account = AccountOperationsUtil.createAccount().then().extract().as(Account.class);
    }

    @Test
    @DisplayName("Test workflow transaction")
    void testWorkflowTransaction() {
        var transaction = Transaction.builder().build();
        var request = TransactionRequest.builder().build();

        // Should not add transaction without required fields
        TransactionOperationsUtil.createTransaction(request)
                .then()
                .statusCode(400)
                .body("$", hasSize(7))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório"
                ));

        // Should not add transaction when future transaction date
        request = TransactionRequest.builder()
                .accountId(account.getId())
                .userId(account.getUserId())
                .description("Electricity Bill")
                .recipientName("Electricity Company SA")
                .type(TransactionTypeEnum.DESP)
                .transactionDate(LocalDate.now().plusDays(5))
                .paymentDate(LocalDate.now())
                .amount(new BigDecimal("80.5"))
                .status(true).build();

        TransactionOperationsUtil.createTransaction(request)
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));

        // Should add transaction
        request = TransactionRequest.builder()
                .accountId(account.getId())
                .userId(account.getUserId())
                .description("Electricity Bill")
                .recipientName("Electricity Company SA")
                .type(TransactionTypeEnum.DESP)
                .transactionDate(LocalDate.now())
                .paymentDate(LocalDate.now())
                .amount(new BigDecimal("80.5"))
                .status(true).build();

        transaction = TransactionOperationsUtil.createTransaction(request)
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().as(Transaction.class);

        // Should not delete an account if it has transactions
        AccountOperationsUtil.deleteAccount(account.getId())
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"));

        // Should delete transaction
        TransactionOperationsUtil.deleteTransaction(transaction.getId())
                .then()
                .statusCode(204);
    }

    @AfterAll
    public static void tearDown() {
        AccountOperationsUtil.deleteAccount(account.getId());
        RestAssured.reset();
    }
}
