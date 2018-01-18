package com.popolam.olxparser.model;

import com.popolam.olxparser.model.lun.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Project: olxparser
 * Created by p0p0lam on 24.11.2014.
 */
public class CommonDB {
    private static final Logger logger = LoggerFactory.getLogger(CommonDB.class);

    Connection conn = null;
    final String tableName;
    public CommonDB(String tableName) {
        this.tableName = tableName;
        try {
            conn = getConnection();
            prepareDatabase();
        } catch (Exception e) {
            logger.error("Can't get DB connection");
        }
    }

    private void dropTable(){
        try {
            conn.prepareStatement("drop table parsed").execute();
        } catch (SQLException e){

        }
    }

    private void prepareDatabase() {
        if (conn!=null) {
            try {
                conn.prepareStatement("select * from parsed").executeQuery();
                logger.info("Table parsed exists");
            } catch (SQLException e) {
                logger.error("Can't find table parsed", e);
                try {
                    conn.prepareStatement("create CACHED table parsed(ID   IDENTITY," +
                            " prov_id BIGINT NOT NULL," +
                            " address VARCHAR(100) NOT NULL," +
                            " area VARCHAR(20)," +
                            " roomno VARCHAR(10)," +
                            " descr VARCHAR(1000) NOT NULL," +
                            " cost VARCHAR(50) ," +
                            " link varchar (500) NOT NULL)").execute();
                    conn.prepareStatement("create unique index idx_prov_id on parsed(prov_id)").execute();
                    logger.info("Table parsed created");
                } catch (SQLException ee) {
                    logger.error("Can't create table parsed", ee);
                }
            }

        }
    }

    public boolean insertResult(Ad result){
        try{
            logger.debug("Inserting ad {}", result.getProvId());
            PreparedStatement stmt = conn.prepareStatement("insert into parsed(prov_id, address, area, roomno, descr, link, cost) values (?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, result.getProvId());
            stmt.setString(2, result.getAddress());
            stmt.setString(3, result.getArea());
            stmt.setString(4, result.getRoomNo());
            stmt.setString(5, result.getDetails());
            stmt.setString(6, result.getLink());
            stmt.setString(7, result.getPrice());
            stmt.execute();
            return true;
        } catch (SQLException e){
            //logger.error("Can't insert result", e);
            return false;
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.prepareStatement("SHUTDOWN").execute();
                conn.close();
            } catch (SQLException e) {
                logger.error("Can't close connection", e);
            }
            conn=null;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:db/"+tableName, "SA", "");
    }
}
