package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContaBacenRepository extends JpaRepository<ContaBacen, Long> {

    @Query(value = "SELECT * FROM CONTA_BACEN c WHERE c.BANCO = ?1 AND c.NUMERO_CONTA = ?2 AND c.AGENCIA = ?3", nativeQuery = true)
    Optional<ContaBacen> findByBancoContaAndAgencia(String banco, String numeroConta, String agencia);

}
