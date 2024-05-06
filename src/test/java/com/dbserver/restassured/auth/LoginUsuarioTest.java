package com.dbserver.restassured.auth;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.utils.TestUtils;

@SpringBootTest
class LoginUsuarioTest {
    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
    }

    @Test
    @DisplayName("Deve ser possível realizar login com admin ao informar dados corretamente")
    void givenDadosLoginAdminValidosWhenEfetuoLoginThenRetornarToken() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        TestUtils.fazerLoginEObterResponse(dadosLogin)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("token", notNullValue())
                .and().assertThat().body("admin", equalTo(true));
    }

    @Test
    @DisplayName("Deve ser possível realizar login com usuario ao informar dados corretamente")
    void givenDadosLoginUsuarioValidosWhenEfetuoLoginThenRetornarToken() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();
        TestUtils.fazerLoginEObterResponse(dadosLogin)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("token", notNullValue())
                .and().assertThat().body("admin", equalTo(false));
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro ao enviar dados de login com senha incorreta")
    void givenSenhaIncorretaWhenEfetuoLoginThenRetornarRetornarMensagemDeErro() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginSenhaIncorreta();
        TestUtils.fazerLoginEObterResponse(dadosLogin)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().assertThat().body("erro", equalTo("Dados de login inválidos."));

    }

    @Test
    @DisplayName("Deve retornar mensagem de erro ao enviar dados de login com email incorreto")
    void givenEmailIncorretoWhenEfetuoLoginThenRetornarRetornarMensagemDeErro() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginEmailIncorreto();
        TestUtils.fazerLoginEObterResponse(dadosLogin)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().assertThat().body("erro", equalTo("Dados de login inválidos."));
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro ao enviar dados de login com email com formato inválido")
    void givenEmailInvalidoWhenEfetuoLoginThenRetornarRetornarMensagemDeErro() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginEmailFormatoInvalido();
        TestUtils.fazerLoginEObterResponse(dadosLogin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Email com formato inválido."));
    }

}
