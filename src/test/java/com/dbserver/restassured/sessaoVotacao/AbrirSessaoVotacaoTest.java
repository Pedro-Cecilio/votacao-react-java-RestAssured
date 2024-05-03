package com.dbserver.restassured.sessaoVotacao;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dbserver.restassured.fixture.AbrirSessaoVotacaoFixture;
import com.dbserver.restassured.fixture.CriarPautaFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;

import io.restassured.http.ContentType;

class AbrirSessaoVotacaoTest {
    private String token;
    private Integer pautaId;
    private static final String ENDPOINT = "votacao/abrir";

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
    }

    @BeforeEach
    void configurar() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        this.pautaId = given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post("pauta")
                .then()
                .extract().path("id");
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoEntaoDeveRetornarStatus200() {
        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoCorretamente(this.pautaId);
        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("pautaId", equalTo(this.pautaId));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoEmPautaInexistenteEntaoDeveRetornarStatus404() {
        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoPautaInexistente();
        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and().assertThat().body("erro", equalTo("Pauta não encontrada."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoMinutosInvalidosEntaoDeveRetornarStatus404() {
        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoMinutosInvalido(this.pautaId);
        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Minutos deve ser maior que 0."));
    }

    @Test
    void dadosEstouLogadoComoUsuarioQuandoTentoAbrirSessaoVotacaoEntaoDeveRetornarStatus403() {
        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoMinutosInvalido(this.pautaId);

        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();

        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoJaAbertaEntaoDeveRetornarStatus400() {
        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoCorretamente(this.pautaId);
        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Pauta já possui uma votação aberta."));
                
    }
}
