package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.ClienteVaga;
import com.springboot.demo_park_api.repository.ClienteVagaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClienteVagaService {

    private final ClienteVagaRepository repository;

    //metodo para salvar um cliente na vaga
    @Transactional
    public ClienteVaga salvar(ClienteVaga clienteVaga) {
        return repository.save(clienteVaga);
    }
}
