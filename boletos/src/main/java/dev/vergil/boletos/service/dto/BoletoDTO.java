package dev.vergil.boletos.service.dto;

import dev.vergil.boletos.domain.validation.CpfOrCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A DTO for the {@link dev.vergil.boletos.domain.Boleto} entity.
 */
@Data
@Schema(description = "Boleto Entidade\n@author virgilio")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoletoDTO implements Serializable {

    /**
     * Identificador único do boleto
     */
    @Schema(description = "Identificador único do boleto")
    private Long id;

    /**
     * Valor do pagamento do boleto
     */
    @NotNull
    @DecimalMin(value = "0")
    @Schema(description = "Valor do pagamento do boleto", required = true)
    private BigDecimal valor;

    /**
     * A data de vencimento do boleto. Tipo não levantado
     */
    @NotNull
    @Schema(description = "A data de vencimento do boleto. Tipo não levantado", required = true)
    @PastOrPresent
    private LocalDate vencimento;

    /**
     * Identificador do associado
     */
    @NotNull
    @Schema(description = "Identificador do associado", required = true)
    private UUID uuidAssociado;

    /**
     * Documento do pagador
     */
    @NotNull
    @Size(max = 14)
    @Schema(description = "Documento do pagador", required = true)
    @CpfOrCnpj
    private String documentoPagador;

    /**
     * Nome do pagador
     */
    @NotNull
    @Size(max = 50)
    @Schema(description = "Nome do pagador", required = true)
    private String nomePagador;

    @NotNull
    private String situacao;

    /**
     * Nome do fantasia do pagador
     */
    @Size(max = 50)
    @Schema(description = "Nome do pagador")
    private String nomeFantasiaPagador;

    public void setDocumentoPagador(String documentoPagador) {
        this.documentoPagador = documentoPagador != null
                ? documentoPagador.replaceAll("[^\\d]", "")
                : null;
    }

}
