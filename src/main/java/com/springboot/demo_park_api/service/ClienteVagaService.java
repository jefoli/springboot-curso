package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.ClienteVaga;
import com.springboot.demo_park_api.exception.EntityNotFoundException;
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

    @Transactional
    public ClienteVaga buscarPorRecibo(String recibo) {
        return repository.findByReciboAndDataSaidaIsNull(recibo).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Recibo '%s' não encontrado no sistema ou check-out já realizado", recibo)
                )
        );
    }

    @Transactional
    public long getTotalDeVezesEstacionamentoCompleto(String cpf) {
        return repository.countByClienteCpfAndDataSaidaIsNotNull(cpf);
    }
}
