package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContaBacenRepository extends JpaRepository<ContaBacen, Long> {

    @Query(value = "SELECT * FROM CONTA_BACEN c WHERE c.NUMERO_CONTA = ?1 AND c.AGENCIA = ?2", nativeQuery = true)
    Optional<ContaBacen> findByNumeroContaAndAgencia(String numeroConta, String agencia);

}
