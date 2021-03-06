package com.swedbanktest.db;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Floor")
@NoArgsConstructor @AllArgsConstructor
public class Floor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter @Setter private Integer id;

    @Column(name = "floor_number")
    @Getter @Setter private Integer floorNumber;

    @Column(name = "height")
    @Getter @Setter private BigDecimal height;

    @Column(name = "weight_capacity")
    @Getter @Setter private BigDecimal weightCapacity;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="floor_id")
    public List<Cell> cells;    
}
