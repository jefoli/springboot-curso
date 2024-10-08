package com.springboot.demo_park_api.web.controller;

import com.springboot.demo_park_api.entity.Usuario;
import com.springboot.demo_park_api.service.UsuarioService;
import com.springboot.demo_park_api.web.dto.UsuarioCreateDto;
import com.springboot.demo_park_api.web.dto.UsuarioResponseDto;
import com.springboot.demo_park_api.web.dto.UsuarioSenhaDto;
import com.springboot.demo_park_api.web.dto.mapper.UsuarioMapper;
import com.springboot.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.Implementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Tag - documenta parte geral
@Tag(name = "Usuário", description = "Contém todas as operações relativas aos recursos para cadastro, edição e leitura de um usuário.")

//@RequiredArgsConstructor - Lombok - inj. dep. será realizada via método construtor
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    //injeção de dependências para service
    private final UsuarioService usuarioService;

    //@Operation - documenta recurso individualmente
    @Operation(summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
    responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Usuário e-mail já cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })

    //precisamos adc anotacao(@Valid) para informar o controle que o objeto precisa ser validado
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@Valid @RequestBody UsuarioCreateDto createDto) {
        Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDto));

        //resposta para o usuário:
        //HttpStatus.CREATED - constante da spring
        //.body() - inclui corpo da resposta que foi salvo no db.
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Recuperar um usuário pelo id", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN|CLIENTE",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE') AND #id == authentication.principal.id)") //adc reg. de permissão de acesso
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Atualizar senha", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN|CLIENTE",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha aualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Senha não confere.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Senha precisa conter 6 digitos.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    //usamos patch para realizar alteração parcial, porém podemos usar @PutMapping tbm.
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UsuarioSenhaDto dto) {
        usuarioService.editarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha(), dto.getConfirmaSenha());
        return ResponseEntity.noContent().build();
    }

    //listar todos usuários
    @Operation(summary = "Listar todos usuários cadastrados", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
            @ApiResponse(responseCode = "200", description = "Usuários recuperados com sucesso",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDto.class)))),

            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDto>> getAll() {
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toListDto(users));
    }

}
