package com.springboot.demo_park_api.service;


import com.springboot.demo_park_api.entity.Usuario;
import com.springboot.demo_park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/* anotacao lombok (@RequiredArgsConstructor) - cria um metodo construtor com a variavel usuarioRepository
na classe como método construtor - assim o spring faz a injeção de dependências via método construtor*/

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        //salva usuário no BD
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario buscarPorId(Long id) {
        //retorna o objeto usuário ou uma exceção
        return usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado.")
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String password) {
        Usuario user = buscarPorId(id);
        user.setPassword(password);
        return user;
    }

    @Transactional
    public List<Usuario> buscarTodos() {
        //findAll() - retorna uma lista de usuarios.
        return usuarioRepository.findAll();
    }
}
