package com.brayanvanz.park_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClientResponseDto {

    private Long id;
    private String name;
    private String cpf;
}
