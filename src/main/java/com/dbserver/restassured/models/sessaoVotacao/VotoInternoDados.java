package com.dbserver.restassured.models.sessaoVotacao;

import com.dbserver.restassured.models.sessaoVotacao.enums.TipoDeVotoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class VotoInternoDados {
    private Integer pautaId;
    private TipoDeVotoEnum tipoDeVoto;
}
