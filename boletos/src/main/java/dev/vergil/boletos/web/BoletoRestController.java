package dev.vergil.boletos.web;


import dev.vergil.boletos.repository.BoletoRepository;
import dev.vergil.boletos.service.BoletoService;
import dev.vergil.boletos.service.dto.BoletoDTO;
import dev.vergil.boletos.service.dto.PagamentoBoletoDTO;
import jakarta.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link dev.vergil.boletos.domain.Boleto}.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BoletoRestController {
    private final Logger log = LoggerFactory.getLogger(BoletoRestController.class);

    private final BoletoService boletoService;

    private final BoletoRepository boletoRepository;

    @PostMapping("/boletos")
    public ResponseEntity<BoletoDTO> createBoleto(@Valid @RequestBody BoletoDTO boletoDTO) throws URISyntaxException {
        log.debug("REST request to save Boleto : {}", boletoDTO);
        BoletoDTO result = boletoService.save(boletoDTO);
        return ResponseEntity
                .created(new URI("/api/boletos/" + result.getId()))
                .body(result);
    }

    @PostMapping("/boletos/pay")
    public ResponseEntity<BoletoDTO> createBoleto(@Valid @RequestBody PagamentoBoletoDTO pagamentoBoletoDTO) throws URISyntaxException {
        log.debug("REST request to pay a Boleto : {}", pagamentoBoletoDTO);
        BoletoDTO result = boletoService.pay(pagamentoBoletoDTO);
        return ResponseEntity
                .accepted()
                .body(result);
    }


    @PutMapping("/boletos/{id}")
    public ResponseEntity<BoletoDTO> updateBoleto(
            @PathVariable(value = "id") final Long uuid,
            @Valid @RequestBody BoletoDTO boletoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Boleto : {}, {}", uuid, boletoDTO);

        if (!boletoRepository.existsById(uuid)) {
            //throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BoletoDTO result = boletoService.update(boletoDTO);
        return ResponseEntity
                .ok()
                .body(result);
    }


    @GetMapping("/boletos")
    public List<BoletoDTO> getAllBoletos() {
        log.debug("REST request to get all Boletos");
        return boletoService.findAll();
    }

    @GetMapping("/boletos/{id}")
    public ResponseEntity<BoletoDTO> getBoleto(@PathVariable Long id) {
        log.debug("REST request to get Boleto : {}", id);
        Optional<BoletoDTO> boletoDTO = boletoService.findOne(id);
        return boletoDTO.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/associados/por-uuid-associado/{uuid}")
    public List<BoletoDTO> getBoletosByUuidAssociado(@PathVariable UUID uuid) {
        log.debug("REST request to get Boleto by uuid-associado : {}", uuid);
        return boletoService.findAllBoletoByUuidAssociado(uuid);
    }


    @DeleteMapping("/boletos/{id}")
    public ResponseEntity<Void> deleteBoleto(@PathVariable Long id) {
        log.debug("REST request to delete Boleto : {}", id);
        boletoService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
