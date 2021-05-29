package br.com.lab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private Long id;

    @JsonProperty("conta_id")
    private Long accountId;

    @JsonProperty("usuario_id")
    private Long userId;

    @JsonProperty("transferencia_id")
    private Long transferId;

    @JsonProperty("parcelamento_id")
    private Long installment;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("envolvido")
    private String recipientName;

    @JsonProperty("tipo")
    private TransactionTypeEnum type;

    @JsonProperty("data_transacao")
    private LocalDate transactionDate;

    @JsonProperty("data_pagamento")
    private LocalDate paymentDate;

    @JsonProperty("valor")
    private BigDecimal amount;

    private boolean status;

}
