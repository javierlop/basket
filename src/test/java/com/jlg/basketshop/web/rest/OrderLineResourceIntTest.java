package com.jlg.basketshop.web.rest;

import com.jlg.basketshop.BasketApp;

import com.jlg.basketshop.domain.OrderLine;
import com.jlg.basketshop.domain.Item;
import com.jlg.basketshop.repository.OrderLineRepository;
import com.jlg.basketshop.repository.search.OrderLineSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrderLineResource REST controller.
 *
 * @see OrderLineResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BasketApp.class)
public class OrderLineResourceIntTest {

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    private static final Long DEFAULT_TOTAL = 1L;
    private static final Long UPDATED_TOTAL = 2L;

    @Inject
    private OrderLineRepository orderLineRepository;

    @Inject
    private OrderLineSearchRepository orderLineSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restOrderLineMockMvc;

    private OrderLine orderLine;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrderLineResource orderLineResource = new OrderLineResource();
        ReflectionTestUtils.setField(orderLineResource, "orderLineSearchRepository", orderLineSearchRepository);
        ReflectionTestUtils.setField(orderLineResource, "orderLineRepository", orderLineRepository);
        this.restOrderLineMockMvc = MockMvcBuilders.standaloneSetup(orderLineResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderLine createEntity(EntityManager em) {
        OrderLine orderLine = new OrderLine()
                .number(DEFAULT_NUMBER)
                .total(DEFAULT_TOTAL);
        // Add required entity
        Item item = ItemResourceIntTest.createEntity(em);
        em.persist(item);
        em.flush();
        orderLine.setItem(item);
        return orderLine;
    }

    @Before
    public void initTest() {
        orderLineSearchRepository.deleteAll();
        orderLine = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderLine() throws Exception {
        int databaseSizeBeforeCreate = orderLineRepository.findAll().size();

        // Create the OrderLine

        restOrderLineMockMvc.perform(post("/api/order-lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderLine)))
                .andExpect(status().isCreated());

        // Validate the OrderLine in the database
        List<OrderLine> orderLines = orderLineRepository.findAll();
        assertThat(orderLines).hasSize(databaseSizeBeforeCreate + 1);
        OrderLine testOrderLine = orderLines.get(orderLines.size() - 1);
        assertThat(testOrderLine.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testOrderLine.getTotal()).isEqualTo(DEFAULT_TOTAL);

        // Validate the OrderLine in ElasticSearch
        OrderLine orderLineEs = orderLineSearchRepository.findOne(testOrderLine.getId());
        assertThat(orderLineEs).isEqualToComparingFieldByField(testOrderLine);
    }

    @Test
    @Transactional
    public void getAllOrderLines() throws Exception {
        // Initialize the database
        orderLineRepository.saveAndFlush(orderLine);

        // Get all the orderLines
        restOrderLineMockMvc.perform(get("/api/order-lines?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(orderLine.getId().intValue())))
                .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
                .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.intValue())));
    }

    @Test
    @Transactional
    public void getOrderLine() throws Exception {
        // Initialize the database
        orderLineRepository.saveAndFlush(orderLine);

        // Get the orderLine
        restOrderLineMockMvc.perform(get("/api/order-lines/{id}", orderLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderLine.getId().intValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingOrderLine() throws Exception {
        // Get the orderLine
        restOrderLineMockMvc.perform(get("/api/order-lines/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderLine() throws Exception {
        // Initialize the database
        orderLineRepository.saveAndFlush(orderLine);
        orderLineSearchRepository.save(orderLine);
        int databaseSizeBeforeUpdate = orderLineRepository.findAll().size();

        // Update the orderLine
        OrderLine updatedOrderLine = orderLineRepository.findOne(orderLine.getId());
        updatedOrderLine
                .number(UPDATED_NUMBER)
                .total(UPDATED_TOTAL);

        restOrderLineMockMvc.perform(put("/api/order-lines")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOrderLine)))
                .andExpect(status().isOk());

        // Validate the OrderLine in the database
        List<OrderLine> orderLines = orderLineRepository.findAll();
        assertThat(orderLines).hasSize(databaseSizeBeforeUpdate);
        OrderLine testOrderLine = orderLines.get(orderLines.size() - 1);
        assertThat(testOrderLine.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testOrderLine.getTotal()).isEqualTo(UPDATED_TOTAL);

        // Validate the OrderLine in ElasticSearch
        OrderLine orderLineEs = orderLineSearchRepository.findOne(testOrderLine.getId());
        assertThat(orderLineEs).isEqualToComparingFieldByField(testOrderLine);
    }

    @Test
    @Transactional
    public void deleteOrderLine() throws Exception {
        // Initialize the database
        orderLineRepository.saveAndFlush(orderLine);
        orderLineSearchRepository.save(orderLine);
        int databaseSizeBeforeDelete = orderLineRepository.findAll().size();

        // Get the orderLine
        restOrderLineMockMvc.perform(delete("/api/order-lines/{id}", orderLine.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean orderLineExistsInEs = orderLineSearchRepository.exists(orderLine.getId());
        assertThat(orderLineExistsInEs).isFalse();

        // Validate the database is empty
        List<OrderLine> orderLines = orderLineRepository.findAll();
        assertThat(orderLines).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrderLine() throws Exception {
        // Initialize the database
        orderLineRepository.saveAndFlush(orderLine);
        orderLineSearchRepository.save(orderLine);

        // Search the orderLine
        restOrderLineMockMvc.perform(get("/api/_search/order-lines?query=id:" + orderLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.intValue())));
    }
}
