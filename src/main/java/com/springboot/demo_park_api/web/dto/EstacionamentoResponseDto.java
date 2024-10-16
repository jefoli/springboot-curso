package com.springboot.demo_park_api.web.dto;

//classe para usar as anotações do lombok

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //não mostra as infos o objeto de saída como nulo.
public class EstacionamentoResponseDto {

    private String placa;
    private String marca;
    private String modelo;
    private String cor;
    private String clienteCpf;
    private String recibo;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime dataEntrada;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")

    private LocalDateTime dataSaida;
    private String vagaCodigo;
    private BigDecimal valor;
    private BigDecimal desconto;
}
