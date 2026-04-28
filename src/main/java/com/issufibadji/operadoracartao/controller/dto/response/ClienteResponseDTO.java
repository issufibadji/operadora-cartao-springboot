package com.issufibadji.operadoracartao.controller.dto.response;


public record ClienteResponseDTO(String nome,
                                 String email,
                                 Integer idade,
                                 String cpf,
                                 CartaoResponseDTO cartao){}



