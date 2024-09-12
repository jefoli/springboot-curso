package com.springboot.demo_park_api.service;


import com.springboot.demo_park_api.entity.Usuario;
import com.springboot.demo_park_api.exception.EntityNotFoundException;
import com.springboot.demo_park_api.exception.UsernameUniqueViolationException;
import com.springboot.demo_park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/* anotacao lombok (@RequiredArgsConstructor) - cria um metodo construtor com a variavel usuarioRepository
na classe como método construtor - assim o spring faz a injeção de dependências via método construtor*/

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        //salva usuário no BD
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException ex) {
            //relaçar uma nova exceção própria
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", usuario.getUsername()));
        }
    }

    @Transactional
    public Usuario buscarPorId(Long id) {
        //retorna o objeto usuário ou uma exceção
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado.", id))
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if(!novaSenha.equals(confirmaSenha)) {
            throw new RuntimeException("Nova senha não confere com confirmação de senha.");
        }

        Usuario user = buscarPorId(id);

        if(!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new RuntimeException("Sua senha não confere.");
        }

        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Transactional
    public List<Usuario> buscarTodos() {
        //findAll() - retorna uma lista de usuarios.
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário com '%s' não encontrado", username))
        );
    }

    @Transactional
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
