package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.Cliente;
import com.springboot.demo_park_api.entity.ClienteVaga;
import com.springboot.demo_park_api.entity.Vaga;
import com.springboot.demo_park_api.util.EstacionamentoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Transactional
    public ClienteVaga checkOut(String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);

        LocalDateTime dataSaida = LocalDateTime.now();

        BigDecimal valor = EstacionamentoUtils.calcularCusto(clienteVaga.getDataEntrada(), dataSaida);

        clienteVaga.setValor(valor);

        long totalDeVezes = clienteVagaService.getTotalDeVezesEstacionamentoCompleto(clienteVaga.getCliente().getCpf());

        //acessar calcular desconto
        BigDecimal desconto = EstacionamentoUtils.calcularDesconto(valor, totalDeVezes);
        clienteVaga.setDesconto(desconto);

        clienteVaga.setDataSaida(dataSaida);
        clienteVaga.getVaga().setStatus(Vaga.StatusVaga.LIVRE);

        return clienteVagaService.salvar(clienteVaga);

    }
}
