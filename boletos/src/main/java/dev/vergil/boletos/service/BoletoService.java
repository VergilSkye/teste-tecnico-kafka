package dev.vergil.boletos.service;

import dev.vergil.boletos.domain.Boleto;
import dev.vergil.boletos.kafka.consumer.associado.AssociadoConsumerService;
import dev.vergil.boletos.repository.BoletoRepository;
import dev.vergil.boletos.service.dto.BoletoDTO;
import dev.vergil.boletos.service.dto.PagamentoBoletoDTO;
import dev.vergil.boletos.service.errors.*;
import dev.vergil.boletos.service.mapper.BoletoMapper;

import dev.vergil.library.kafka.model.AssociadoResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service Implementation for managing {@link dev.vergil.boletos.domain.Boleto}.
 */
@Service
@Transactional
@AllArgsConstructor
public class BoletoService {

    private final Logger log = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoRepository boletoRepository;

    private final BoletoMapper boletoMapper;

    private final AssociadoConsumerService associadoService;


    /**
     * Save a boleto.
     *
     * @param boletoDTO the entity to save.
     * @return the persisted entity.
     */
    public BoletoDTO save(BoletoDTO boletoDTO) throws MismatchUuidAssociadoException, MismatchNameAssociadoException {
        log.debug("Request to save Boleto : {}", boletoDTO);
        // buscar informações do Associado info, Se não da um thorw not find
        AssociadoResponse associado = associadoService.getAssociado(boletoDTO.getDocumentoPagador());
        compareAndValidateAssociado(boletoDTO, associado);
        Boleto boleto = boletoMapper.toEntity(boletoDTO);
        boleto = boletoRepository.save(boleto);
        return boletoMapper.toDto(boleto);
    }



    private void compareAndValidateAssociado(BoletoDTO boletoDTO, AssociadoResponse associado) throws MismatchUuidAssociadoException, MismatchNameAssociadoException {
        if (!Objects.equals(boletoDTO.getUuidAssociado(), associado.getUuid())) {
            throw new MismatchUuidAssociadoException();
        }
        if (!Objects.equals(boletoDTO.getNomePagador(), associado.getNome())) {
            throw new MismatchNameAssociadoException();
        }
    }

    private AssociadoResponse findAssociado(String documentoPagador) {
        return new AssociadoResponse(UUID.randomUUID(), documentoPagador, "fantasia");
    }

    /**
     * Update a boleto.
     *
     * @param boletoDTO the entity to save.
     * @return the persisted entity.
     */
    public BoletoDTO update(BoletoDTO boletoDTO) {
        log.debug("Request to update Boleto : {}", boletoDTO);
        Boleto boleto = boletoMapper.toEntity(boletoDTO);
        boleto = boletoRepository.save(boleto);
        return boletoMapper.toDto(boleto);
    }

    /**
     * Get all the boletos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BoletoDTO> findAll() {
        log.debug("Request to get all Boletos");
        return boletoRepository.findAll().stream().map(boletoMapper::toDto).toList();
    }

    /**
     * Get all the boletos of an associado.
     *
     * @return the list of entities of an associado.
     */
    @Transactional(readOnly = true)
    public List<BoletoDTO> findAllBoletoByUuidAssociado(UUID uuidAssociado) {
        log.debug("Request to get all Boletos by uuid Associado: ", uuidAssociado);
        return boletoRepository.findByUuidAssociado(uuidAssociado).stream().map(boletoMapper::toDto).toList();
    }

    /**
     * Get all the boletos of an associado.
     *
     * @return the list of entities of an associado.
     */
    @Transactional(readOnly = true)
    public List<BoletoDTO> findAllBoletoByDocumentoPagador(String documento) {
        log.debug("Request to get all Boletos by documento: ", documento);
        return boletoRepository.findByDocumentoPagador(documento).stream().map(boletoMapper::toDto).toList();
    }


    /**
     * Get one boleto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BoletoDTO> findOne(Long id) {
        log.debug("Request to get Boleto : {}", id);
        return boletoRepository.findById(id).map(boletoMapper::toDto);
    }

    /**
     * Delete the boleto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Boleto : {}", id);
        boletoRepository.deleteById(id);
    }


    /**
     * Pay a boleto.
     *
     * @param pagamentoBoletoDTO
     * @return the persisted entity.
     */
    public BoletoDTO pay(PagamentoBoletoDTO pagamentoBoletoDTO) throws MismatchValueException, MismatchIdentifierException, PaymentAlreadyMadeException {
        log.debug("Request to pay a Boleto : {}", pagamentoBoletoDTO);
        BoletoDTO boleto = findOne(pagamentoBoletoDTO.getId()).orElseThrow(IllegalArgumentException::new);
        compareAndValidateBoleto(pagamentoBoletoDTO, boleto);
        payBoleto(boleto);
        return boleto;
    }

    private void compareAndValidateBoleto(PagamentoBoletoDTO pagamentoBoletoDTO, BoletoDTO boleto) throws MismatchValueException, MismatchIdentifierException, PaymentAlreadyMadeException {
        Comparator<BigDecimal> c = Comparator.nullsFirst(Comparator.naturalOrder());
        if (c.compare(pagamentoBoletoDTO.getValor(), boleto.getValor()) != 0) {
            throw new MismatchValueException();
        }
        if (!Objects.equals(pagamentoBoletoDTO.getDocumentoPagador(), boleto.getDocumentoPagador())) {
            throw new MismatchIdentifierException();
        }
        if (Objects.equals(boleto.getSituacao(), Boleto.statusPago)) {
            throw new PaymentAlreadyMadeException();
        }
    }

    private void payBoleto(BoletoDTO boleto) {
        boletoRepository.payBoleto(boleto.getId());
        boleto.setSituacao(Boleto.statusPago);
    }
}