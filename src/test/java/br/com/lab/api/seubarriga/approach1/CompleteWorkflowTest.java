package br.com.lab.api.seubarriga.approach1;

import br.com.lab.dto.AccountCreationRequest;
import br.com.lab.dto.AccountUpdateRequest;
import br.com.lab.dto.TransactionRequest;
import br.com.lab.model.Account;
import br.com.lab.model.Balance;
import br.com.lab.model.Transaction;
import br.com.lab.model.TransactionTypeEnum;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompleteWorkflowTest {

    private static final String ACCOUNTS_PATH = "contas";
    private static final String TRANSACTIONS_PATH = "transacoes";
    private static final String BALANCE_PATH = "saldo";
    private static final String ID_PATH_PARAM = "/{id}";
    private static final String ID = "id";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static Account account = Account.builder().build();
    private static Transaction transaction = Transaction.builder().build();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://barrigarest.wcaquino.me";

        var requestBody = new HashMap<String, String>();
        requestBody.put("email", "doug@test.com");
        requestBody.put("senha", "doug0505");

        var token = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post("signin")
                .then()
                    .statusCode(200)
                    .extract().path("token");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader(AUTHORIZATION_HEADER, "JWT " + token)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void testShouldAddAccount() {
        var request = AccountCreationRequest.builder().name("Account RestAssured").build();

        account = given()
            .body(request)
        .when()
            .post(ACCOUNTS_PATH)
        .then()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .extract().as(Account.class);
    }

    @Test
    @Order(2)
    void testShouldUpdateAccount() {
        var request = AccountUpdateRequest.builder().name("Account RestAssured 1").build();

        account = given()
                .pathParam(ID, account.getId())
                .body(request)
                .when()
                .put(ACCOUNTS_PATH + ID_PATH_PARAM)
                .then()
                .statusCode(200)
                .body("id", is(account.getId().intValue()))
                .body("nome", is(request.getName()))
                .extract().as(Account.class);
    }

    @Test
    @Order(3)
    void testShouldNotAddAccountWhenNameAlreadyExists() {
        var request = AccountCreationRequest.builder().name("Account RestAssured 1").build();

        given()
                .body(request)
                .when()
                .post(ACCOUNTS_PATH)
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    @Order(4)
    void testShouldAddTransaction() {
        var request = TransactionRequest.builder()
                .accountId(account.getId())
                .userId(account.getUserId())
                .description("Electricity Bill")
                .recipientName("Electricity Company SA")
                .type(TransactionTypeEnum.DESP)
                .transactionDate(LocalDate.now())
                .paymentDate(LocalDate.now())
                .amount(new BigDecimal("80.5"))
                .status(true).build();

        transaction = given()
                .body(request)
                .when()
                .post(TRANSACTIONS_PATH)
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().as(Transaction.class);
    }

    @Test
    @Order(5)
    void testShouldNotAddTransactionWithoutRequiredFields() {
        var request = TransactionRequest.builder().build();

        given()
                .body(request)
                .when()
                .post(TRANSACTIONS_PATH)
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
    }

    @Test
    @Order(6)
    void testShouldNotAddTransactionWhenFutureTransactionDate() {
        var request = TransactionRequest.builder()
                .accountId(account.getId())
                .userId(account.getUserId())
                .description("Electricity Bill")
                .recipientName("Electricity Company SA")
                .type(TransactionTypeEnum.DESP)
                .transactionDate(LocalDate.now().plusDays(5))
                .paymentDate(LocalDate.now())
                .amount(new BigDecimal("80.5"))
                .status(true).build();

        given()
                .body(request)
                .when()
                .post(TRANSACTIONS_PATH)
                .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    @Order(7)
    void testShouldNotDeleteAccountIfThereAreTransactions() {
        given()
                .pathParam(ID, account.getId())
                .when()
                .delete(ACCOUNTS_PATH + ID_PATH_PARAM)
                .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"));
    }

    @Test
    @Order(8)
    void testShouldGetBalance() {
        var balances = given()
                .when()
                .get(BALANCE_PATH)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .extract().jsonPath().getList(".", Balance.class);

        var balance = balances.get(0);

        assertEquals(account.getId(), balance.getAccountId());
        assertEquals(account.getName(), balance.getAccountName());
        assertEquals(transaction.getAmount(), balance.getAmount());
    }

    @Test
    @Order(9)
    void testShouldDeleteTransaction() {
        given()
                .pathParam(ID, transaction.getId())
                .when()
                .delete(TRANSACTIONS_PATH + ID_PATH_PARAM)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void testShouldDeleteAccount() {
        given()
                .pathParam(ID, account.getId())
                .when()
                .delete(ACCOUNTS_PATH + ID_PATH_PARAM)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(11)
    void testShouldNotAccessWithoutToken() {
        var filterableRequestSpecification = (FilterableRequestSpecification) RestAssured.requestSpecification;
        filterableRequestSpecification.removeHeader(AUTHORIZATION_HEADER);

        given()
                .when()
                .get(ACCOUNTS_PATH)
                .then()
                .statusCode(401);
    }

    @AfterAll
    public static void tearDown() {
        RestAssured.reset();
    }

}
