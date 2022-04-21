package com.kostafey.swedbanktest.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnManager {
    private static final boolean USE_IN_PROCESS_DB = true;

    // For test & debug purpose - runs DB in the same JVM as app.
    private static Connection getInProcessConnection() {
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:./.db/swedbank-db", "sa", "");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Actually obtains Connection.
     */
    private static Connection getRemoteConnection() {
        // Run H2 as server DB:
        // java -jar ~/.m2/repository/com/h2database/h2/1.4.200/h2-1.4.200.jar -tcpAllowOthers

        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:h2:tcp://localhost:9092/./.db/swedbank-db;ifexists=true;",
                    "sa",
                    "");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    static Connection getConnection() {
        if (USE_IN_PROCESS_DB) {
            return getInProcessConnection();
        } else {
            return getRemoteConnection();
        }        
    }
}
