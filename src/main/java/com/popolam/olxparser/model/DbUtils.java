package com.popolam.olxparser.model;

import com.popolam.olxparser.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.*;

/**
 * Created by p0p0lam on 16.10.2014.
 */
public class DbUtils {
    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    Connection conn = null;
    private String dbName = "olxdb1";

    public DbUtils(String dbName) {
        this.dbName = dbName;
        init();
    }

    public DbUtils() {
        init();
    }

    private void init() {
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
                            " olx_id BIGINT NOT NULL," +
                            " title VARCHAR(100) NOT NULL," +
                            " description VARCHAR(4000) NOT NULL," +
                            " cost VARCHAR(50) ," +
                            " cost_ad VARCHAR(50) ," +
                            " link varchar (400) NOT NULL)").execute();
                    conn.prepareStatement("create unique index idx_olx_id on parsed(olx_id)").execute();
                    logger.info("Table parsed created");
                } catch (SQLException ee) {
                    logger.error("Can't create table parsed", ee);
                }
            }

        }
    }

    public Ads findAd(long id){
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from parsed where olx_id =?", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs!=null && rs.first()){
                Ads ads = new Ads();
                ads.setId(String.valueOf(id));
                ads.setTitle(rs.getString("title"));
                ads.setDescription(rs.getString("description"));
                ads.setList_label(rs.getString("cost"));
                ads.setList_label_ad(rs.getString("cost_ad"));
                ads.setUrl(rs.getString("link"));
                return ads;
            }
        } catch (SQLException e) {
            logger.error("Can't execute statement", e);
        }
        return null;
    }

    public boolean updateResult(Ads result){
        logger.debug("Updating ad {}", result.getId());
        try {
            PreparedStatement stmt = conn.prepareStatement("update parsed set cost=?, cost_ad =? where olx_id = ?");
            stmt.setString(1, result.getList_label());
            stmt.setString(2, result.getList_label_ad());
            stmt.setLong(3, Long.valueOf(result.getId()));
            stmt.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Can't execute statement", e);
        }
        return false;
    }

    public boolean insertResult(Ads result){
        try{
            logger.debug("Inserting ad {}", result.getId());
            PreparedStatement stmt = conn.prepareStatement("insert into parsed(olx_id, title, description, cost, cost_ad, link) values (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, Long.valueOf(result.getId()));
            stmt.setString(2, result.getTitle());
            stmt.setString(3, result.getDescription());
            stmt.setString(4, result.getList_label());
            stmt.setString(5, result.getList_label_ad());
            stmt.setString(6, result.getUrl());
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
        return DriverManager.getConnection("jdbc:hsqldb:file:db/" + dbName, "SA", "");
    }


}
