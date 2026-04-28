package com.issufibadji.operadoracartao.business.services;


import com.issufibadji.operadoracartao.infrastructure.entities.CartaoEntity;
import com.issufibadji.operadoracartao.infrastructure.entities.ClienteEntity;
import com.issufibadji.operadoracartao.infrastructure.repositories.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteJpaRepository clienteRepository;
    private final GeraDadosCartaoService geraCartao;

    public ClienteEntity solicitarCartao(ClienteEntity cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Usuário já possui um cartão.");
        }

        CartaoEntity cartao = geraCartao.gerarParaCliente(cliente);
        cliente.setCartao(cartao);
        return clienteRepository.save(cliente);

    }

    public ClienteEntity buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).
                orElseThrow(()-> new IllegalArgumentException("Cliente não encontrado"));
    }
}