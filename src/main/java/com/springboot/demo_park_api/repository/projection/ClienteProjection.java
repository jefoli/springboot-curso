package com.springboot.demo_park_api.repository.projection;

public interface ClienteProjection {

    //cada método get é ref. a um campo do cliente.

    Long getId();

    String getNome();

    String getCpf();
}
