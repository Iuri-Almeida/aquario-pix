package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavePix, String> {

    @Query(value = "SELECT * FROM CHAVE_PIX c WHERE c.TIPO = ?1 AND c.CHAVE = ?2", nativeQuery = true)
    Optional<ChavePix> findById(String tipo, String chave);
}
