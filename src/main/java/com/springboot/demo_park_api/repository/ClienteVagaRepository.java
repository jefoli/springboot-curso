package com.springboot.demo_park_api.repository;

import com.springboot.demo_park_api.entity.ClienteVaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {
}
