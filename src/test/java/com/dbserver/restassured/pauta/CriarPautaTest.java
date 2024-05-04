package com.dbserver.restassured.pauta;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dbserver.restassured.fixture.CriarPautaFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.utils.TestUtils;


@SpringBootTest
class CriarPautaTest {
    private static String tokenAdmin;

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;

        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        tokenAdmin = TestUtils.fazerLoginEObterToken(dadosLogin);
    }

    @BeforeEach
    void configurar() {

    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaEntaoDeveRetornarPautaCriada() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
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
        String tokenUsuario = TestUtils.fazerLoginEObterToken(dadosLogin);

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        TestUtils.cadastrarPauta(novaPauta, tokenUsuario)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaAssuntoNuloEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaAssuntoNulo();

        TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Assunto deve ser informado."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaCategoriaNulaEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCategoriaNula();

        TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Categoria deve ser informada."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioUmaNovaPautaCategoriaInvalidaEntaoDeveRetornarStatus400() {

        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCategoriaInvalida();

        TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Categoria inválida."));
    }
}
