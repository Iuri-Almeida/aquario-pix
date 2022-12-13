package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ContaBacenRepository extends R2dbcRepository<ContaBacen, Long> {

    Mono<ContaBacen> findByNumeroContaAndAgencia(String numeroConta, String agencia);

}
