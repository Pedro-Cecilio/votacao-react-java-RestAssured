package com.dbserver.restassured.models.usuario;

import com.dbserver.restassured.models.auth.AutenticacaoDados;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@ToString
public class CriarUsuarioEnvio {
    private AutenticacaoDados autenticacaoDto;
    
    private String nome;

    private String sobrenome;

    private String cpf;

    private boolean admin;
}
