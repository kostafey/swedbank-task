package com.kostafey.swedbanktest.db;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Order")
@NoArgsConstructor
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter @Setter private Long id;
    
    @Column(name = "start")
    @Getter @Setter private Date start;

    @Column(name = "end")
    @Getter @Setter private Date end;

    @Column(name = "price")
    @Getter @Setter private BigDecimal price;

    @Column(name = "paid")
    @Getter @Setter private Boolean paid;

    @Column(name = "cell_id")
    @Getter @Setter private Integer cellId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cell_id")
    public Cell cell;

    public Order(Cell cell) {
        this.setCellId(cell.getId());
        this.cell = cell;
        this.start = new Date();
    }
}
