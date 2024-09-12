package com.springboot.demo_park_api.jwt;

import com.springboot.demo_park_api.entity.Usuario;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtUserDetails extends User {
    private Usuario usuario;


    public JwtUserDetails(Usuario usuario) {
        //envia pra sessao do spring que o usuario tem esse name, senha e perfil.
        super(usuario.getUsername(), usuario.getPassword(), AuthorityUtils.createAuthorityList(usuario.getRole().name()));
        this.usuario = usuario;
    }

    public Long getId(){
        return this.usuario.getId();
    }

    public String getRole() {
        return this.usuario.getRole().name();
    }
}
