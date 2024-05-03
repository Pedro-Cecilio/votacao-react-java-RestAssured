package com.dbserver.restassured.models.sessaoVotacao;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AbrirSessaoVotacaoDados {
    private Long minutos;
    private Integer pautaId;
}
