package br.com.lab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    @JsonProperty("conta_id")
    private Long accountId;

    @JsonProperty("conta")
    private String accountName;

    @JsonProperty("saldo")
    private BigDecimal amount;

}
