package com.kostafey.swedbanktest.db;

import java.math.BigDecimal;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Cell")
@NoArgsConstructor @RequiredArgsConstructor
public class Cell {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull @Getter @Setter private Integer id;

    @Column(name = "floor_id")
    @NonNull @Getter @Setter private Integer floorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "floor_id")
    public Floor floor;

    @Column(name = "weight_used")
    @NonNull @Getter @Setter private BigDecimal weightUsed;

    @Column(name = "occupied")
    @NonNull @Getter @Setter private Boolean occupied;
}