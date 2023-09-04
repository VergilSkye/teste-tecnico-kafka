package dev.vergil.boletos.service.mapper;

import dev.vergil.boletos.domain.Boleto;
import dev.vergil.boletos.service.dto.BoletoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link dev.vergil.boletos.domain.Boleto} and its DTO {@link dev.vergil.boletos.service.dto.BoletoDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoletoMapper extends EntityMapper<BoletoDTO, Boleto> {}