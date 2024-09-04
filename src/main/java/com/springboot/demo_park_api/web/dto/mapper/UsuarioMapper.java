package com.springboot.demo_park_api.web.dto.mapper;


import com.springboot.demo_park_api.entity.Usuario;
import com.springboot.demo_park_api.web.dto.UsuarioCreateDto;
import com.springboot.demo_park_api.web.dto.UsuarioResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

//método que faz a conversão de usuário response para usuário DTO.
public class UsuarioMapper {

    public static Usuario toUsuario(UsuarioCreateDto createDto) {
        return new ModelMapper().map(createDto, Usuario.class);
    }

    public static UsuarioResponseDto toDto(Usuario usuario) {

        //operacao que vai remover ROLE_
        String role = usuario.getRole().name().substring("ROLE_".length());
        PropertyMap<Usuario, UsuarioResponseDto> props = new PropertyMap<Usuario, UsuarioResponseDto>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        //var
        ModelMapper mapper = new ModelMapper();

        //podemos acessar addMappings - Esse método recebe um param do tipo PropertyMap
        mapper.addMappings(props);
        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    //met para trabalhar com listas
    public static List<UsuarioResponseDto> toListDto(List<Usuario> usuarios) {
        //tranformar em objeto dto e depois transformar em usuarioResponseDto
        //map java - vai transforma em usuarioResponseDto
        return usuarios.stream().map(user -> toDto(user)).collect(Collectors.toList());
    }
}
