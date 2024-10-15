package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.Cliente;
import com.springboot.demo_park_api.entity.ClienteVaga;
import com.springboot.demo_park_api.entity.Vaga;
import com.springboot.demo_park_api.util.EstacionamentoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EstacionamentoService {

    private final ClienteVagaService clienteVagaService;

    private final ClienteService clienteService;

    private final VagaService vagaService;

    //gerar entrada do veículo no estacionamento
    public ClienteVaga checkIn(ClienteVaga clienteVaga) {

        //recupera cpf e faz consulta pelo objetoCliente desse cpf
        Cliente cliente = clienteService.buscarPorCpf(clienteVaga.getCliente().getCpf());

        //substitui o Objeto cliente com todas infos ao invés da interior que só retornava o CPF:
        clienteVaga.setCliente(cliente);

        Vaga vaga = vagaService.buscarPorVagaLivre();
        vaga.setStatus(Vaga.StatusVaga.OCUPADA);
        clienteVaga.setVaga(vaga);

        clienteVaga.setDataEntrada(LocalDateTime.now());

        clienteVaga.setRecibo(EstacionamentoUtils.gerarRecibo());

        return clienteVagaService.salvar(clienteVaga);
    }
}
