package dev.vergil.boletos.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.vergil.boletos.domain.Boleto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Boleto entity.
 */

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    @Modifying
    @Query("update Boleto b set b.situacao = 'PAGO' where b.id=:uuid")
    void payBoleto(Long uuid);

    List<Boleto> findByUuidAssociado(UUID uuidAssociado);

    List<Boleto> findByDocumentoPagador(String documentoPagador);

}
