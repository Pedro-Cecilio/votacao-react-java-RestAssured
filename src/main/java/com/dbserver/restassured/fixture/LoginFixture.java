package com.dbserver.restassured.fixture;

import com.dbserver.restassured.models.auth.LoginEnvio;




public class LoginFixture {
    private static final String SENHA_ADMIN_CORRETA = "testes123";
    private static final String EMAIL_ADMIN_CORRETO = "testesAdmin@email.com";
    private static final String SENHA_USUARIO_CORRETA = "testes123";
    private static final String EMAIL_USUARIO_CORRETO = "testesUsuario@email.com";

    public static LoginEnvio dadosLoginAdminValido() {
        return LoginEnvio.builder()
                .email(EMAIL_ADMIN_CORRETO)
                .senha(SENHA_ADMIN_CORRETA)
                .build();
    }
    public static LoginEnvio dadosLoginUsuarioValido() {
        return LoginEnvio.builder()
                .email(EMAIL_USUARIO_CORRETO)
                .senha(SENHA_USUARIO_CORRETA)
                .build();
    }
    public static LoginEnvio dadosLoginSenhaIncorreta() {
        return LoginEnvio.builder()
                .email(EMAIL_ADMIN_CORRETO)
                .senha("senhaIncorreta")
                .build();
    }
    public static LoginEnvio dadosLoginEmailIncorreto() {
        return LoginEnvio.builder()
                .email("emailIncorreto8080@email.com")
                .senha(SENHA_ADMIN_CORRETA)
                .build();
    }
    public static LoginEnvio dadosLoginSenhaFormatoInvalido() {
        return LoginEnvio.builder()
                .email(EMAIL_ADMIN_CORRETO)
                .senha("senha")
                .build();
    }
    public static LoginEnvio dadosLoginEmailFormatoInvalido() {
        return LoginEnvio.builder()
                .email("emailFormatoInvalido")
                .senha(SENHA_ADMIN_CORRETA)
                .build();
    }

}
