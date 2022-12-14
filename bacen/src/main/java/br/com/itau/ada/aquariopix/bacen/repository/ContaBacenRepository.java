package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContaBacenRepository extends JpaRepository<ContaBacen, Long> {

    @Query(value = "SELECT * FROM CHAVE_PIX c WHERE c.TIPO = ?1 AND c.CHAVE = ?2", nativeQuery = true)
    Optional<ContaBacen> findByNumeroContaAndAgencia(String numeroConta, String agencia);

}
