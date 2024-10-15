package com.springboot.demo_park_api.service;

import com.springboot.demo_park_api.entity.Vaga;
import com.springboot.demo_park_api.exception.CodigoUniqueViolationException;
import com.springboot.demo_park_api.exception.EntityNotFoundException;
import com.springboot.demo_park_api.repository.VagaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static com.springboot.demo_park_api.entity.Vaga.StatusVaga.LIVRE;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;

    //method salvar
    @Transactional
    public Vaga salvar(Vaga vaga) {
        try {
            return vagaRepository.save(vaga);
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoUniqueViolationException(
                    String.format("Vaga com código '%s' já cadastrada", vaga.getCodigo())
            );
        }
    }

    @Transactional
    public Vaga buscarPorCodigo(String codigo) {
        //recupera vaga a partir do código + trata o erro se não for encontrada
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vaga com código '%s' não foi encontrada", codigo))

        );
    }

    @Transactional
    public Vaga buscarPorVagaLivre() {
        return vagaRepository.findFirstByStatus(LIVRE).orElseThrow(
                () -> new EntityNotFoundException("Nenhuma vaga livre foi encontrada.")
        );
    }
}
