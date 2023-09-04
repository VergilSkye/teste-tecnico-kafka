package dev.vergil.boletos.kafka.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;




public class AssociadoResponse {

    @JsonProperty("uuid")
    private UUID uuid;
    @JsonProperty("documento")
    private String documento;
    @JsonProperty("nome")
    private String nome;

    public AssociadoResponse() {
    }

    public AssociadoResponse( @JsonProperty("uuid") UUID uuid,     @JsonProperty("documento") String documento,     @JsonProperty("nome") String nome) {
        this.uuid = uuid;
        this.documento = documento;
        this.nome = nome;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "AssociadoResponse{" +
                "uuid=" + uuid +
                ", documento='" + documento + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}