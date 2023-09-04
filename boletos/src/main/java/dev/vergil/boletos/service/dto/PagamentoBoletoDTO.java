package dev.vergil.boletos.service.dto;

import dev.vergil.boletos.domain.validation.CpfOrCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A DTO for paying a bank slip
 */
@Data
@Schema(description = "Pagamento de Boleto \n@author virgilio")
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("common-java:DuplicatedBlocks")

public class PagamentoBoletoDTO {

    /**
     * Identificador único do boleto
     */
    @Schema(description = "Identificador único do boleto")
    private Long id;

    /**
     * Documento do pagador
     */
    @NotNull
    @Size(max = 14)
    @Schema(description = "Documento do pagador", required = true)
    @CpfOrCnpj
    private String documentoPagador;

    /**
     * Valor do pagamento do boleto
     */
    @NotNull
    @DecimalMin(value = "0")
    @Schema(description = "Valor do pagamento do boleto", required = true)
    private BigDecimal valor;


    public void setDocumentoPagador(String documentoPagador) {
        this.documentoPagador = documentoPagador != null
                ? documentoPagador.replaceAll("[^\\d]", "")
                : null;
    }
}
