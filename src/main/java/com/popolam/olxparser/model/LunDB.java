package com.popolam.olxparser.model;

import com.popolam.olxparser.model.lun.Ad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: olxparser
 * Created by p0p0lam on 24.11.2014.
 */
public class LunDB {
    private static final Logger logger = LoggerFactory.getLogger(LunDB.class);

    Connection conn = null;
    public LunDB() {

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
                            " address VARCHAR(200) NOT NULL," +
                            " params VARCHAR(100) NOT NULL," +
                            " descr VARCHAR(1000) NOT NULL," +
                            " cost VARCHAR(60) ," +
                            " link varchar (400) NOT NULL)").execute();
                    conn.prepareStatement("create unique index idx_prov_id on parsed(prov_id)").execute();
                    logger.info("Table parsed created");
                } catch (SQLException ee) {
                    logger.error("Can't create table parsed", ee);
                }
            }

        }
    }

    public void removeAd(Ad ad){
            try {
                PreparedStatement stmt = conn.prepareStatement("delete from parsed where prov_id = ?");
                stmt.setLong(1, ad.getProvId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("Can't execute statement", e);
            }

    }

    public Map<String, List<Ad>> getGropByAdress(){
        List<Ad> all = findAll();
        Map<String, List<Ad>> result =
        all.stream().sorted(Comparator.comparing(Ad::getAddress))
        .collect(
                Collectors.groupingBy(Ad::getAddress)
        );
        return result;
    }

    public List<Ad> findAll(){
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from parsed order by cost desc", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            if (rs!=null && rs.first()){
                List<Ad> result = new ArrayList<>();
                do{
                    Ad ad = new Ad();
                    ad.setProvId(rs.getInt("prov_id"));
                    ad.setAddress(rs.getString("address"));
                    ad.setParams(rs.getString("params"));
                    ad.setDetails(rs.getString("descr"));
                    ad.setLink(rs.getString("link"));
                    ad.setPrice(rs.getString("cost"));
                    result.add(ad);
                } while (rs.next());
                return result;
            }
        } catch (SQLException e){

        }
        return Collections.emptyList();
    }

    public Ad findAd(long id){
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from parsed where prov_id =?", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs!=null && rs.first()){
              Ad ad = new Ad();
                ad.setProvId(id);
                ad.setAddress(rs.getString("address"));
                ad.setParams(rs.getString("params"));
                ad.setDetails(rs.getString("descr"));
                ad.setLink(rs.getString("link"));
                ad.setPrice(rs.getString("cost"));
                return ad;
            }
        } catch (SQLException e) {
            logger.error("Can't execute statement", e);
        }
        return null;
    }

    public boolean updateResult(Ad result){
        logger.debug("Updating ad {}", result.getProvId());
        try {
            PreparedStatement stmt = conn.prepareStatement("update parsed set cost=?, address=?, params =?, descr =?, link=? where prov_id = ?");
            stmt.setString(1, result.getPrice());
            stmt.setString(2, result.getAddress());
            stmt.setString(3, result.getParams());
            stmt.setString(4, result.getDetails());
            stmt.setString(5, result.getLink());
            stmt.setLong(6, result.getProvId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Can't execute statement", e);
        }
        return false;
    }

    public boolean insertResult(Ad result){
        try{
            logger.debug("Inserting ad {}", result.getProvId());
            PreparedStatement stmt = conn.prepareStatement("insert into parsed(prov_id, address, params, descr, link, cost) values (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, result.getProvId());
            stmt.setString(2, result.getAddress());
            stmt.setString(3, result.getParams());
            stmt.setString(4, result.getDetails());
            stmt.setString(5, result.getLink());
            stmt.setString(6, result.getPrice());
            stmt.executeUpdate();
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

    public void closeCompactConnection() {
        if (conn != null) {
            try {
                conn.prepareStatement("SHUTDOWN COMPACT").execute();
                conn.close();
            } catch (SQLException e) {
                logger.error("Can't close connection", e);
            }
            conn=null;
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:db/lundb1", "SA", "");
    }
}
