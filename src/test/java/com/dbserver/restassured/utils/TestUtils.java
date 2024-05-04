package com.dbserver.restassured.utils;

import static io.restassured.RestAssured.given;

import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.models.sessaoVotacao.VotoInternoDados;
import com.dbserver.restassured.models.usuario.CriarUsuarioEnvio;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class TestUtils {
    private static final String ENDPOINT_ABRIR_SESSAO_VOTACAO = "votacao/abrir";
    private static final String ENDPOINT_CRIAR_USUARIO = "usuario";
    private static final String ENDPOINT_CRIAR_PAUTA = "pauta";
    private static final String ENDPOINT_VOTO_INTERNO = "votacao/votoInterno";
    private static final String ENDPOINT_LOGIN = "auth/login";

    public static String fazerLoginEObterToken(LoginEnvio dadosLogin) {
        return given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT_LOGIN)
                .then()
                .extract().path("token");
    }
    public static Response fazerLoginEObterResponse(LoginEnvio dadosLogin) {
        return given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT_LOGIN);
    }

    public static Response abrirSessaoVotacao(AbrirSessaoVotacaoDados dados, String token) {
        return given()
                .header("Authorization", token)
                .body(dados)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT_ABRIR_SESSAO_VOTACAO);
    }

    public static Response cadastrarUsuario(CriarUsuarioEnvio dados, String token) {
        return given()
                .header("Authorization", token)
                .body(dados)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT_CRIAR_USUARIO);
    }

    public static Response cadastrarPauta(CriarPautaDados dados, String token) {
        return given()
                .header("Authorization", token)
                .body(dados)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT_CRIAR_PAUTA);
    }

    public static Response inserirVotoInterno(VotoInternoDados dados, String token) {
        return given()
                .header("Authorization", token)
                .body(dados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT_VOTO_INTERNO);
    }
}
