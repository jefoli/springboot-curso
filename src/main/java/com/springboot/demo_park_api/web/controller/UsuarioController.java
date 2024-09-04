package com.springboot.demo_park_api.web.controller;

import com.springboot.demo_park_api.entity.Usuario;
import com.springboot.demo_park_api.service.UsuarioService;
import com.springboot.demo_park_api.web.dto.UsuarioCreateDto;
import com.springboot.demo_park_api.web.dto.UsuarioResponseDto;
import com.springboot.demo_park_api.web.dto.UsuarioSenhaDto;
import com.springboot.demo_park_api.web.dto.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RequiredArgsConstructor - Lombok - inj. dep. será realizada via método construtor
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    //injeção de dependências para service
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@RequestBody UsuarioCreateDto createDto) {
        Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDto));

        //resposta para o usuário:
        //HttpStatus.CREATED - constante da spring
        //.body() - inclui corpo da resposta que foi salvo no db.
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(UsuarioMapper.toDto(user));
    }

    //usamos patch para realizar alteração parcial, porém podemos usar @PutMapping tbm.
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody UsuarioSenhaDto dto) {
        Usuario user = usuarioService.editarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha(), dto.getConfirmaSenha());
        return ResponseEntity.noContent().build();
    }

    //listar todos usuários
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> getAll() {
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toListDto(users));
    }

}
