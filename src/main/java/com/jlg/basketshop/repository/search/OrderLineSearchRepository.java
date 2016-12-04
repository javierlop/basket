package com.jlg.basketshop.repository.search;

import com.jlg.basketshop.domain.OrderLine;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the OrderLine entity.
 */
public interface OrderLineSearchRepository extends ElasticsearchRepository<OrderLine, Long> {
}
