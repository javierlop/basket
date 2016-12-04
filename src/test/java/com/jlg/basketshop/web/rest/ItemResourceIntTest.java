package com.jlg.basketshop.web.rest;

import com.jlg.basketshop.BasketApp;

import com.jlg.basketshop.domain.Item;
import com.jlg.basketshop.repository.ItemRepository;
import com.jlg.basketshop.repository.search.ItemSearchRepository;

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
 * Test class for the ItemResource REST controller.
 *
 * @see ItemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BasketApp.class)
public class ItemResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;

    private static final Boolean DEFAULT_IS_3_X_2 = false;
    private static final Boolean UPDATED_IS_3_X_2 = true;

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private ItemSearchRepository itemSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restItemMockMvc;

    private Item item;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ItemResource itemResource = new ItemResource();
        ReflectionTestUtils.setField(itemResource, "itemSearchRepository", itemSearchRepository);
        ReflectionTestUtils.setField(itemResource, "itemRepository", itemRepository);
        this.restItemMockMvc = MockMvcBuilders.standaloneSetup(itemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createEntity(EntityManager em) {
        Item item = new Item()
                .name(DEFAULT_NAME)
                .price(DEFAULT_PRICE)
                .is3X2(DEFAULT_IS_3_X_2);
        return item;
    }

    @Before
    public void initTest() {
        itemSearchRepository.deleteAll();
        item = createEntity(em);
    }

    @Test
    @Transactional
    public void createItem() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();

        // Create the Item

        restItemMockMvc.perform(post("/api/items")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(item)))
                .andExpect(status().isCreated());

        // Validate the Item in the database
        List<Item> items = itemRepository.findAll();
        assertThat(items).hasSize(databaseSizeBeforeCreate + 1);
        Item testItem = items.get(items.size() - 1);
        assertThat(testItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testItem.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testItem.isIs3X2()).isEqualTo(DEFAULT_IS_3_X_2);

        // Validate the Item in ElasticSearch
        Item itemEs = itemSearchRepository.findOne(testItem.getId());
        assertThat(itemEs).isEqualToComparingFieldByField(testItem);
    }

    @Test
    @Transactional
    public void getAllItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the items
        restItemMockMvc.perform(get("/api/items?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].is3X2").value(hasItem(DEFAULT_IS_3_X_2.booleanValue())));
    }

    @Test
    @Transactional
    public void getItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.is3X2").value(DEFAULT_IS_3_X_2.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingItem() throws Exception {
        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        itemSearchRepository.save(item);
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item
        Item updatedItem = itemRepository.findOne(item.getId());
        updatedItem
                .name(UPDATED_NAME)
                .price(UPDATED_PRICE)
                .is3X2(UPDATED_IS_3_X_2);

        restItemMockMvc.perform(put("/api/items")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedItem)))
                .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> items = itemRepository.findAll();
        assertThat(items).hasSize(databaseSizeBeforeUpdate);
        Item testItem = items.get(items.size() - 1);
        assertThat(testItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testItem.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testItem.isIs3X2()).isEqualTo(UPDATED_IS_3_X_2);

        // Validate the Item in ElasticSearch
        Item itemEs = itemSearchRepository.findOne(testItem.getId());
        assertThat(itemEs).isEqualToComparingFieldByField(testItem);
    }

    @Test
    @Transactional
    public void deleteItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        itemSearchRepository.save(item);
        int databaseSizeBeforeDelete = itemRepository.findAll().size();

        // Get the item
        restItemMockMvc.perform(delete("/api/items/{id}", item.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean itemExistsInEs = itemSearchRepository.exists(item.getId());
        assertThat(itemExistsInEs).isFalse();

        // Validate the database is empty
        List<Item> items = itemRepository.findAll();
        assertThat(items).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        itemSearchRepository.save(item);

        // Search the item
        restItemMockMvc.perform(get("/api/_search/items?query=id:" + item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].is3X2").value(hasItem(DEFAULT_IS_3_X_2.booleanValue())));
    }
}
