package com.dbserver.restassured.models.pauta;


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
public class CriarPautaDados {
    private String assunto;
    private String categoria;
}
