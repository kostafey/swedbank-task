package com.swedbanktest.db;

import java.math.BigDecimal;
import java.util.Optional;

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
@Table(name = "Cell")
@NoArgsConstructor
public class Cell {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter @Setter private Integer id;

    @Column(name = "floor_id")
    @Getter @Setter private Integer floorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="floor_id", insertable=false, updatable=false)
    public Floor floor;

    @Column(name = "weight_used")
    @Setter private BigDecimal weightUsed;

    public BigDecimal getWeightUsed() {
        return Optional.ofNullable(this.weightUsed).orElse(new BigDecimal(0));
    }

    @Column(name = "occupied")
    @Setter private Boolean occupied;

    public Boolean getOccupied() {
        return Optional.ofNullable(this.occupied).orElse(false);
    }

    public Cell(Integer id, Integer floorId, BigDecimal weightUsed, Boolean occupied) {
        this.setId(id);
        this.setFloorId(floorId);
        this.setWeightUsed(weightUsed);
        this.setOccupied(occupied);
    }
}