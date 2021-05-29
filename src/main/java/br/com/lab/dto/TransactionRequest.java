package br.com.lab.dto;

import br.com.lab.model.TransactionTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TransactionRequest {

    @JsonProperty("conta_id")
    private Long accountId;

    @JsonProperty("usuario_id")
    private Long userId;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("envolvido")
    private String recipientName;

    @JsonProperty("tipo")
    private TransactionTypeEnum type;

    @JsonProperty("data_transacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate transactionDate;

    @JsonProperty("data_pagamento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate paymentDate;

    @JsonProperty("valor")
    private BigDecimal amount;

    private boolean status;

}
