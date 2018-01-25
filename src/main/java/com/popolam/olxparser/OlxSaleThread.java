package com.popolam.olxparser;

import com.popolam.olxparser.model.Ads;
import com.popolam.olxparser.model.DbUtils;
import com.popolam.olxparser.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by p0p0lam on 17.01.2017.
 */
public class OlxSaleThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(OlxSaleThread.class);
    private static final Logger loggerUpd = LoggerFactory.getLogger("com.popolam.updated");


    public static final String ROOMS_FROM="3";
    public static final String ROOMS_TO="4";
    public static final String PRICE_TO="3500000";
    DbUtils db;
    public static final int PAGES =2;
    boolean isNeedMatch = false;
    private static final String OLX_URL =String.format("https://www.olx.ua/i2/nedvizhimost/prodazha-kvartir/kiev/?json=1&search[filter_float_number_of_rooms:from]=%1$s&search[filter_float_number_of_rooms:to]=%2$s&search[district_id]=9&search[order]=created_at:desc&search[filter_float_price:to]=%3$s",
            ROOMS_FROM, ROOMS_TO, PRICE_TO);
    @Override
    public void run() {
        db = new DbUtils("olx_sale");

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the HTTP GET request, marshaling the response to a String
        try {
            for (int i = 1; i <= PAGES; i++) {
                logger.info("Getting page {}", i);
                //String resultStr = restTemplate.getForObject(urlOlx, String.class, "");
                String pagedUrl = OLX_URL + "&page=" + i;
                Result result = restTemplate.getForObject(pagedUrl, Result.class, "");
                //Result result = mapper.readValue(resultStr, Result.class);
                if (result.getAds() != null) {
                    logger.info("Got result! Ads size: {}", result.getAds().size());
                    result.getAds().stream().filter(ads -> processAd(ads)).forEach(ads -> {
                        logger.info("Find interesting ad: {}", ads);
                        App.openUrl(ads.getUrl());
                    });
                }

            }
            logger.debug("Exiting");
        } catch (RestClientException e) {
            logger.error("Can't get result", e);
        } catch (Exception e){
            logger.error("Can't get result", e);
        } catch (Throwable e){
            logger.error("Can't get result", e);
        }
        finally {
            db.closeConnection();
        }

    }



    private boolean processAd(Ads ad){
        try {
            Ads existing = db.findAd(Long.valueOf(ad.getId()));
            if (existing!=null){
                if (!ad.getList_label().equals(existing.getList_label()) || !ad.getList_label_ad().equals(existing.getList_label_ad())){
                    logger.info("Existing ad {} changed! old price: {}, new price: {}, old priceAd: {}, new priceAd: {}", ad.getUrl(), existing.getList_label(), ad.getList_label(), existing.getList_label_ad(), ad.getList_label_ad());
                    loggerUpd.info("Existing ad {} changed! old price: {}, new price: {}, old priceAd: {}, new priceAd: {}", ad.getUrl(), existing.getList_label(), ad.getList_label(), existing.getList_label_ad(), ad.getList_label_ad());
                    db.updateResult(ad);
                    return false;
                }
            }
        } catch (NumberFormatException e){
            //ignore
        }
        return db.insertResult(ad);
    }
}
