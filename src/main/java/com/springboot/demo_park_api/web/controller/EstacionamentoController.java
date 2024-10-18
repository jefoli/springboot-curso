package com.springboot.demo_park_api.web.controller;

import com.springboot.demo_park_api.entity.ClienteVaga;
import com.springboot.demo_park_api.repository.projection.ClienteVagaProjection;
import com.springboot.demo_park_api.service.ClienteVagaService;
import com.springboot.demo_park_api.service.EstacionamentoService;
import com.springboot.demo_park_api.web.dto.EstacionamentoCreateDto;
import com.springboot.demo_park_api.web.dto.EstacionamentoResponseDto;
import com.springboot.demo_park_api.web.dto.PageableDto;
import com.springboot.demo_park_api.web.dto.mapper.ClienteVagaMapper;
import com.springboot.demo_park_api.web.dto.mapper.PageableMapper;
import com.springboot.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Estacionamento", description = "Operações de registro de entrada e saída de um veículo do estacionamento.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {
    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dar entrada de um veículo no estacionamento. " +
        "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = EstacionamentoResponseDto.class))),

            @ApiResponse(responseCode = "404", description = "Causas Possíveis> <br/>" +
                    "- CPF do cliente não cadastrado no sistema; <br/>" +
                    "- Nenhuma vaga livre foi localizada;",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))),

            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))),

        })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkin(@RequestBody @Valid EstacionamentoCreateDto dto) {
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(dto); //converte cliente dto para um ClienteVaga.
        estacionamentoService.checkIn(clienteVaga);

        //transformar o obj retornado em um responseDto
        EstacionamentoResponseDto responseDto = ClienteVagaMapper.toDto(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);

    }

    @GetMapping("/check-in/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDto> getByRecibo(@PathVariable String recibo){
        //localizar o recibo:
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Operação de check-out", description = "Recurso para dar saída de um veículo do estacionamento. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                @Parameter(in = PATH, name = "recibo", description = "Numero do recibo gerado pelo check-in")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = EstacionamentoResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Número do recibo inexistente ou " +
                        "o veículo já passou pelo check-out",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                        schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                    content = @Content(mediaType = " application/json;charset=UTF-8",
                         schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/check-out/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkout(@PathVariable String recibo){
        ClienteVaga clienteVaga = estacionamentoService.checkOut(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os " +
            "registros de estacionamentos do cliente por CPF. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = " N' do CPF referente ao cliente a ser consultado",
                        required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Representa a página retornada",
                        content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Campo padrão de ordenação 'dataEntrada,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDto.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAllEstacionamentosPorCpf(@PathVariable String cpf,
                                                                   @PageableDefault(size = 5, sort = "dataEntrada",
                                                                   direction = Sort.Direction.ASC) Pageable pageable){
        //recebe resultado da consulta:
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorClienteCpf(cpf, pageable);

        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }


}
