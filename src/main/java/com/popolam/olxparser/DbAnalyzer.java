package com.popolam.olxparser;

import com.popolam.olxparser.model.LunDB;
import com.popolam.olxparser.model.lun.Ad;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DbAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(DbAnalyzer.class);
    private LunDB db;
    private RestTemplate restTemplate;
    private final OkHttpClient client;

    public static void main(String[] args) {
        DbAnalyzer dbAnalyzer = new DbAnalyzer();
        dbAnalyzer.getGrouping();
        dbAnalyzer.close();
    }

    public DbAnalyzer() {
        db = new LunDB();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        client = new OkHttpClient();
    }

    void getGrouping(){
        Map<String, List<Ad>> items = db.getGropByAdress();
        final int[] allcount = {0};
        items.values().stream().forEach(ads -> allcount[0] +=ads.size());
        logger.info("All count: {}", allcount[0]);

        for (String address : items.keySet()) {
            List<Ad> ads = items.get(address);
            logger.info("Address: {}, items count: {}", address, ads.size());
            for (Ad ad : ads) {
                logger.info("Cost: {}, link: {}", ad.getPrice(), ad.getLink());
            }
        }

    }

    void checkAll(){
        List<Ad> ads = db.findAll();
        logger.info("Found {} ads", ads.size());
        for (int i = 0; i < ads.size(); i++) {
            Ad ad = ads.get(i);
            boolean isAdExists = isAdExists(ad);
            if (isAdExists){
                logger.info("Ad: {}", ad);
            } else {
                db.removeAd(ad);
            }

        }
    }

    private boolean isAdExists(Ad ad){
        Request request = new Request.Builder()
                .url(ad.getLink()).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            logger.debug("Response uri: {}", response.request().url());
            return !"https://www.lun.ua/".equals(response.request().url().toString());
        } catch (IOException e){
            logger.error("Failed to get link: {}", ad.getLink());
        } finally {
            if (response!=null){
                response.close();
            }
        }
        return false;

    }


    void close(){
        db.closeCompactConnection();
    }
}
