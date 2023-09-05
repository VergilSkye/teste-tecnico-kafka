package dev.vergil.boletos.web;

import dev.vergil.boletos.IntegrationTest;
import dev.vergil.boletos.domain.Boleto;
import dev.vergil.boletos.kafka.consumer.associado.AssociadoConsumerService;
import dev.vergil.boletos.repository.BoletoRepository;
import dev.vergil.boletos.service.dto.BoletoDTO;
import dev.vergil.boletos.service.mapper.BoletoMapper;
import dev.vergil.library.kafka.model.AssociadoResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static dev.vergil.boletos.web.TestUtil.NumberMatcher;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BoletoRestController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
@EnableKafka
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = false,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:3333",
                "port=3333"
        })
public class BoletoResourceIT {

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(1);

    private static final LocalDate DEFAULT_VENCIMENTO = LocalDate.now(ZoneId.systemDefault()).plusDays(1);

    private static final LocalDate UPDATED_VENCIMENTO = LocalDate.now(ZoneId.systemDefault());

    private static final UUID DEFAULT_UUID_ASSOCIADO = UUID.randomUUID();
    private static final UUID UPDATED_UUID_ASSOCIADO = UUID.randomUUID();

    private static final String DEFAULT_DOCUMENTO_PAGADOR = "65703801095";
    private static final String UPDATED_DOCUMENTO_PAGADOR = "78650990029";

    private static final String DEFAULT_NOME_PAGADOR = "john";
    private static final String UPDATED_NOME_PAGADOR = "doe";
    public static final String DEFAULT_SITUACAO = "ABERTO";
    public static final String UPDATED_SITUACAO = "PAGO";

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private BoletoMapper boletoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoletoMockMvc;

    @MockBean
    private AssociadoConsumerService associadoConsumerService;

    private Boleto boleto;

    private static final String ENTITY_API_URL = "/api/boletos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";



    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boleto createEntity(EntityManager em) {
        Boleto boleto = new Boleto()
                .valor(DEFAULT_VALOR)
                .vencimento(DEFAULT_VENCIMENTO)
                .uuidAssociado(DEFAULT_UUID_ASSOCIADO)
                .documentoPagador(DEFAULT_DOCUMENTO_PAGADOR)
                .nomePagador(DEFAULT_NOME_PAGADOR)
                .situacao(Boleto.statusAberto);
        return boleto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boleto createUpdatedEntity(EntityManager em) {
        Boleto boleto = new Boleto()
                .valor(UPDATED_VALOR)
                .vencimento(UPDATED_VENCIMENTO)
                .uuidAssociado(UPDATED_UUID_ASSOCIADO)
                .documentoPagador(UPDATED_DOCUMENTO_PAGADOR)
                .nomePagador(UPDATED_NOME_PAGADOR)
                .situacao(Boleto.statusPago);
        return boleto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssociadoResponse createAssociadoResponse(EntityManager em) {
        return new AssociadoResponse(DEFAULT_UUID_ASSOCIADO, DEFAULT_DOCUMENTO_PAGADOR, DEFAULT_NOME_PAGADOR);
    }


    @BeforeEach
    public void initTest() {
        when(associadoConsumerService.getAssociado(any())).thenReturn(createAssociadoResponse(em));
        boleto = createEntity(em);
    }

    @Test
    @Transactional
    void createBoleto() throws Exception {
        int databaseSizeBeforeCreate = boletoRepository.findAll().size();
        // Create the Boleto
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);
        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isCreated());

        // Validate the Boleto in the database
        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeCreate + 1);
        Boleto testBoleto = boletoList.get(boletoList.size() - 1);
        assertThat(testBoleto.getValor()).isEqualByComparingTo(DEFAULT_VALOR);
        assertThat(testBoleto.getVencimento()).isEqualTo(DEFAULT_VENCIMENTO);
        assertThat(testBoleto.getUuidAssociado()).isEqualTo(DEFAULT_UUID_ASSOCIADO);
        assertThat(testBoleto.getDocumentoPagador()).isEqualTo(DEFAULT_DOCUMENTO_PAGADOR);
        assertThat(testBoleto.getNomePagador()).isEqualTo(DEFAULT_NOME_PAGADOR);
        assertThat(testBoleto.getSituacao()).isEqualTo(DEFAULT_SITUACAO);
    }

    @Test
    @Transactional
    void checkValorIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setValor(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVencimentoIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setVencimento(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUuidAssociadoIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setUuidAssociado(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocumentoPagadorIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setDocumentoPagador(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomePagadorIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setNomePagador(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSituacaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = boletoRepository.findAll().size();
        // set the field null
        boleto.setSituacao(null);

        // Create the Boleto, which fails.
        BoletoDTO boletoDTO = boletoMapper.toDto(boleto);

        restBoletoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boletoDTO)))
                .andExpect(status().isBadRequest());

        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoletos() throws Exception {
        // Initialize the database
        boletoRepository.saveAndFlush(boleto);

        // Get all the boletoList
        restBoletoMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(boleto.getId().intValue())))
                .andExpect(jsonPath("$.[*].valor").value(hasItem(sameNumber(DEFAULT_VALOR))))
                .andExpect(jsonPath("$.[*].vencimento").value(hasItem(DEFAULT_VENCIMENTO.toString())))
                .andExpect(jsonPath("$.[*].uuidAssociado").value(hasItem(DEFAULT_UUID_ASSOCIADO.toString())))
                .andExpect(jsonPath("$.[*].documentoPagador").value(hasItem(DEFAULT_DOCUMENTO_PAGADOR)))
                .andExpect(jsonPath("$.[*].nomePagador").value(hasItem(DEFAULT_NOME_PAGADOR)))
                .andExpect(jsonPath("$.[*].situacao").value(hasItem(DEFAULT_SITUACAO)));
    }

    @Test
    @Transactional
    void getBoleto() throws Exception {
        // Initialize the database
        boletoRepository.saveAndFlush(boleto);

        // Get the boleto
        restBoletoMockMvc
                .perform(get(ENTITY_API_URL_ID, boleto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(boleto.getId().intValue()))
                .andExpect(jsonPath("$.valor").value(sameNumber(DEFAULT_VALOR)))
                .andExpect(jsonPath("$.vencimento").value(DEFAULT_VENCIMENTO.toString()))
                .andExpect(jsonPath("$.uuidAssociado").value(DEFAULT_UUID_ASSOCIADO.toString()))
                .andExpect(jsonPath("$.documentoPagador").value(DEFAULT_DOCUMENTO_PAGADOR))
                .andExpect(jsonPath("$.nomePagador").value(DEFAULT_NOME_PAGADOR))
                .andExpect(jsonPath("$.situacao").value(DEFAULT_SITUACAO));
    }

    @Test
    @Transactional
    void putExistingBoleto() throws Exception {
        // Initialize the database
        boletoRepository.saveAndFlush(boleto);

        int databaseSizeBeforeUpdate = boletoRepository.findAll().size();

        // Update the boleto
        Boleto updatedBoleto = boletoRepository.findById(boleto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBoleto are not directly saved in db
        em.detach(updatedBoleto);
        updatedBoleto
                .valor(UPDATED_VALOR)
                .vencimento(UPDATED_VENCIMENTO)
                .uuidAssociado(UPDATED_UUID_ASSOCIADO)
                .documentoPagador(UPDATED_DOCUMENTO_PAGADOR)
                .nomePagador(UPDATED_NOME_PAGADOR)
                .situacao(UPDATED_SITUACAO);
        BoletoDTO boletoDTO = boletoMapper.toDto(updatedBoleto);

        restBoletoMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, boletoDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(boletoDTO))
                )
                .andExpect(status().isOk());

        // Validate the Boleto in the database
        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeUpdate);
        Boleto testBoleto = boletoList.get(boletoList.size() - 1);
        assertThat(testBoleto.getValor()).isEqualByComparingTo(UPDATED_VALOR);
        assertThat(testBoleto.getVencimento()).isEqualTo(UPDATED_VENCIMENTO);
        assertThat(testBoleto.getUuidAssociado()).isEqualTo(UPDATED_UUID_ASSOCIADO);
        assertThat(testBoleto.getDocumentoPagador()).isEqualTo(UPDATED_DOCUMENTO_PAGADOR);
        assertThat(testBoleto.getNomePagador()).isEqualTo(UPDATED_NOME_PAGADOR);
        assertThat(testBoleto.getSituacao()).isEqualTo(UPDATED_SITUACAO);
    }

    @Test
    @Transactional
    void deleteBoleto() throws Exception {
        // Initialize the database
        boletoRepository.saveAndFlush(boleto);

        int databaseSizeBeforeDelete = boletoRepository.findAll().size();

        // Delete the boleto
        restBoletoMockMvc
                .perform(delete(ENTITY_API_URL_ID, boleto.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Boleto> boletoList = boletoRepository.findAll();
        assertThat(boletoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    /**
     * Creates a matcher that matches when the examined number represents the same value as the reference BigDecimal.
     *
     * @param number the reference BigDecimal against which the examined number is checked.
     */
    public static NumberMatcher sameNumber(BigDecimal number) {
        return new NumberMatcher(number);
    }


}
