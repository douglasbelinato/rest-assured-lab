package br.com.lab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Long id;

    @JsonProperty("nome")
    private String name;

    @JsonProperty("visivel")
    private boolean visible;

    @JsonProperty("usuario_id")
    private Long userId;

}
