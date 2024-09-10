package com.springboot.demo_park_api.repository;

import com.springboot.demo_park_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    //dessa forma retorna o role e conseguimos receber ele no nosso novo método
    @Query("select u.role from Usuario u where u.username like :username")
    Usuario.Role findRoleByUsername(String username);
}
