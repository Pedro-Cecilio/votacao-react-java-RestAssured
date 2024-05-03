package com.dbserver.restassured.pauta;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dbserver.restassured.fixture.CriarPautaFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import io.restassured.http.ContentType;

class CriarPautaTest {
    private String token;
    private final String ENDPOINT = "pauta";

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
    }

    @BeforeEach
    void configurar() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaEntaoDeveRetornarPautaCriada() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("assunto", equalTo(novaPauta.getAssunto()))
                .and().assertThat().body("categoria", equalTo(novaPauta.getCategoria()))
                .and().assertThat().body("usuario", notNullValue())
                .and().assertThat().body("sessaoVotacao", nullValue());
    }
    @Test
    void dadosEstouLogadoComoUsuarioQuandoTentoCrioUmaNovaPautaEntaoDeveRetornarStatus403() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);        
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaAssuntoNuloEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaAssuntoNulo();

        given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Assunto deve ser informado."));
    }
    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaCategoriaNulaEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCategoriaNula();

        given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Categoria deve ser informada."));
    }
    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaCategoriaInvalidaEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCategoriaInvalida();

        given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Categoria inválida."));
    }
}
