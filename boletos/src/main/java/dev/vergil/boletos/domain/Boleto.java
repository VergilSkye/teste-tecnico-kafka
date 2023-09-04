package dev.vergil.boletos.domain;

import dev.vergil.boletos.domain.validation.CpfOrCnpj;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;



/**
 * Boleto Entidade\n@author virgilio
 */
@Entity
@Table(name = "boleto", uniqueConstraints={
        @UniqueConstraint(columnNames = {"uuid", "uuid_associado"})
})
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Boleto implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String statusPago = "PAGO";
    public static final String statusAberto = "ABERTO";

    /**
     * Identificador único do boleto
     */
    @Id
    @GeneratedValue
    @Column(name = "uuid")
    private UUID uuid;

    /**
     * Valor do pagamento do boleto
     */
    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "valor", precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    /**
     * A data de vencimento do boleto. Tipo não levantado
     */
    @NotNull
    @Column(name = "vencimento", nullable = false)
    @FutureOrPresent
    private LocalDate vencimento;

    /**
     * Identificador do associado
     */
    @NotNull
    @Column(name = "uuid_associado", nullable = false)
    private UUID uuidAssociado;

    /**
     * Documento do pagador
     */
    @NotNull
    @Size(max = 14)
    @Column(name = "documento_pagador", length = 14, nullable = false)
    @CpfOrCnpj
    private String documentoPagador;

    /**
     * Nome do pagador
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "nome_pagador", length = 50, nullable = false)
    private String nomePagador;

    @NotNull
    @Column(name = "situacao", nullable = false)
    private String situacao;

    /**
     * Nome do pagador
     */
    @Size(max = 50)
    @Column(name = "nome_fantasia_pagador", length = 50)
    private String nomeFantasiaPagador;


    public UUID getUuid() {
        return this.uuid;
    }

    public Boleto Uuid(UUID id) {
        this.setUuid(id);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

    public Boleto valor(BigDecimal valor) {
        this.setValor(valor);
        return this;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getVencimento() {
        return this.vencimento;
    }

    public Boleto vencimento(LocalDate vencimento) {
        this.setVencimento(vencimento);
        return this;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public UUID getUuidAssociado() {
        return this.uuidAssociado;
    }

    public Boleto uuidAssociado(UUID uuidAssociado) {
        this.setUuidAssociado(uuidAssociado);
        return this;
    }

    public void setUuidAssociado(UUID uuidAssociado) {
        this.uuidAssociado = uuidAssociado;
    }

    public String getDocumentoPagador() {
        return this.documentoPagador;
    }

    public Boleto documentoPagador(String documentoPagador) {
        this.setDocumentoPagador(documentoPagador);
        return this;
    }

    public void setDocumentoPagador(String documentoPagador) {
        this.documentoPagador = documentoPagador;
    }

    public String getNomePagador() {
        return this.nomePagador;
    }

    public Boleto nomePagador(String nomePagador) {
        this.setNomePagador(nomePagador);
        return this;
    }

    public void setNomePagador(String nomePagador) {
        this.nomePagador = nomePagador;
    }

    public String getSituacao() {
        return this.situacao;
    }

    public Boleto situacao(String situacao) {
        this.setSituacao(situacao);
        return this;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getNomeFantasiaPagador() {
        return this.nomeFantasiaPagador;
    }

    public Boleto nomeFantasiaPagador(String nomeFantasiaPagador) {
        this.setNomeFantasiaPagador(nomeFantasiaPagador);
        return this;
    }

    public void setNomeFantasiaPagador(String nomeFantasiaPagador) {
        this.nomeFantasiaPagador = nomeFantasiaPagador;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Boleto)) {
            return false;
        }
        return uuid != null && uuid.equals(((Boleto) o).uuid);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Boleto{" +
                "Uuid=" + getUuid() +
                ", valor=" + getValor() +
                ", vencimento='" + getVencimento() + "'" +
                ", uuidAssociado='" + getUuidAssociado() + "'" +
                ", documentoPagador='" + getDocumentoPagador() + "'" +
                ", nomePagador='" + getNomePagador() + "'" +
                ", situacao='" + getSituacao() + "'" +
                ", nomeFantasiaPagador='" + getNomeFantasiaPagador() + "'" +
                "}";
    }
}
