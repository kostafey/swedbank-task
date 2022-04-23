package com.kostafey.swedbanktest.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class InitDB {
    private static final String CREATE_FLOOR_SQL = """
        CREATE TABLE IF NOT EXISTS Floor (            
            id BIGINT not null auto_increment primary key, 
            floor_number INT,                              
            height NUMERIC(20, 2),                                    
            weight_capacity NUMERIC(20, 2)) """;

    private static final String CREATE_CELL_SQL = """
        CREATE TABLE IF NOT EXISTS Cell (
            id BIGINT not null auto_increment primary key, 
            floor_id INT,                                  
            occupied BOOLEAN,
            weight_used NUMERIC(20, 2)) """;

    private static final String CREATE_ORDER_SQL = """
        CREATE TABLE IF NOT EXISTS ParkingOrder (
            id BIGINT not null auto_increment primary key, 
            start TIMESTAMP,
            end TIMESTAMP,
            price NUMERIC(20, 2),
            paid BOOLEAN,
            cell_id INT) """;

    private static final ArrayList<Floor> floorsData = new ArrayList<Floor>(
        Arrays.asList(
            new Floor(1, -3, new BigDecimal(3.2), new BigDecimal(10000), 
                Arrays.asList(
                    new Cell(1, 1, new BigDecimal(0), false),
                    new Cell(2, 1, new BigDecimal(0), false),
                    new Cell(3, 1, new BigDecimal(0), false),
                    new Cell(4, 1, new BigDecimal(0), false),
                    new Cell(5, 1, new BigDecimal(0), false),
                    new Cell(6, 1, new BigDecimal(0), false),
                    new Cell(7, 1, new BigDecimal(0), false),
                    new Cell(8, 1, new BigDecimal(0), false))),
            new Floor(2, -2, new BigDecimal(3.2), new BigDecimal(10000), 
                Arrays.asList(
                    new Cell(9, 2, new BigDecimal(0), false),
                    new Cell(10, 2, new BigDecimal(0), false),
                    new Cell(11, 2, new BigDecimal(0), false),
                    new Cell(12, 2, new BigDecimal(0), false),
                    new Cell(13, 2, new BigDecimal(0), false),
                    new Cell(14, 2, new BigDecimal(0), false),
                    new Cell(15, 2, new BigDecimal(0), false),
                    new Cell(16, 2, new BigDecimal(0), false))),
            new Floor(3, -1, new BigDecimal(3.5), new BigDecimal(10000), 
                Arrays.asList(
                    new Cell(17, 3, new BigDecimal(0), false),
                    new Cell(18, 3, new BigDecimal(0), false),
                    new Cell(19, 3, new BigDecimal(0), false),
                    new Cell(20, 3, new BigDecimal(0), false),
                    new Cell(21, 3, new BigDecimal(0), false),
                    new Cell(22, 3, new BigDecimal(0), false),
                    new Cell(23, 3, new BigDecimal(0), false),
                    new Cell(24, 3, new BigDecimal(0), false)))
        ));

    public static void createDB() {
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection = ConnManager.getConnection();
            statement = dbConnection.createStatement();
            statement.execute(CREATE_FLOOR_SQL);
            statement.execute(CREATE_CELL_SQL);
            statement.execute(CREATE_ORDER_SQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeData() {
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection = ConnManager.getConnection();
            DecimalFormat df = new DecimalFormat("#.00");
            for (Floor f : floorsData) {
                statement = dbConnection.createStatement();
                String insertDataSQL = String.format(
                    "MERGE INTO Floor (id, floor_number, height, weight_capacity) " + 
                    "VALUES (%d, %d, %s, %s)", 
                    f.getId(), f.getFloorNumber(), 
                    df.format(f.getHeight()), df.format(f.getWeightCapacity()));
                statement.executeUpdate(insertDataSQL);
                for (Cell c : f.cells) {
                    statement = dbConnection.createStatement();
                    insertDataSQL = String.format(
                        "MERGE INTO Cell (id, floor_id) " +
                        "VALUES (%d, %d)", 
                        c.getId(), c.getFloorId(), df.format(c.getWeightUsed()),
                        c.getOccupied().toString());
                    statement.executeUpdate(insertDataSQL);
                }            
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (dbConnection != null) {
                    dbConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

