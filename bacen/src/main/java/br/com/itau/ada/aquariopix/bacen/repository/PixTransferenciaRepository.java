package br.com.itau.ada.aquariopix.bacen.repository;

import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PixTransferenciaRepository extends JpaRepository<PixTransferencia, String> {
}
