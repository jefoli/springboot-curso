package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.Cliente;
import com.springboot.demo_park_api.exception.EntityNotFoundException;
import com.springboot.demo_park_api.repository.ClienteRepository;
import com.springboot.demo_park_api.repository.projection.ClienteProjection;
import com.springboot.demo_park_api.web.exception.CpfUniqueViolationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente) {

        //exceção caso já tenha um clente com o cpf cadastrado no DB

        try {
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' não pode ser cadastrado, já existe no sistema", cliente.getCpf()));
        }
    }

    @Transactional
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente id=%s não encontrado no sistema", id))
        );
    }

    //recurso para listar os clientes
    @Transactional
    public Page<ClienteProjection> buscarTodos(Pageable pegeable) {
        return clienteRepository.findAllPageable(pegeable); //retorna uma lista de clients
    }

    public Cliente buscarPorUsuarioId(Long id) {
        return clienteRepository.findByUsuarioId(id);
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente com CPF '%s' não encontrado", cpf))
        );
    }
}