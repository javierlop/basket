package com.jlg.basketshop.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A OrderLine.
 */
@Entity
@Table(name = "order_line")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "orderline")
public class OrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Column(name = "total")
    private Long total;

    @OneToOne
    @NotNull
    @JoinColumn(unique = true)
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public OrderLine number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getTotal() {
        return total;
    }

    public OrderLine total(Long total) {
        this.total = total;
        return this;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Item getItem() {
        return item;
    }

    public OrderLine item(Item item) {
        this.item = item;
        return this;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderLine orderLine = (OrderLine) o;
        if(orderLine.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, orderLine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OrderLine{" +
            "id=" + id +
            ", number='" + number + "'" +
            ", total='" + total + "'" +
            '}';
    }
}
