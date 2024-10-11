package com.springboot.demo_park_api.web.dto.mapper;

import com.springboot.demo_park_api.entity.Vaga;
import com.springboot.demo_park_api.web.dto.VagaCreateDto;
import com.springboot.demo_park_api.web.dto.VagaResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VagaMapper {

    public static Vaga toVaga(VagaCreateDto dto) {
        return new ModelMapper().map(dto, Vaga.class);
    }

    //method que retorna response:
    public static VagaResponseDto toDto(Vaga vaga){
        return new ModelMapper().map(vaga, VagaResponseDto.class);
    }
}
