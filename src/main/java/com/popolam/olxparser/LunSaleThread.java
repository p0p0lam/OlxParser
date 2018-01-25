package com.popolam.olxparser;

import com.popolam.olxparser.model.LunDB;
import com.popolam.olxparser.model.lun.Ad;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.StringJoiner;

import static com.popolam.olxparser.App.openUrl;

/**
 * Created by p0p0lam on 16.01.2017.
 */
public class LunSaleThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LunSaleThread.class);
    private static final Logger loggerUpd = LoggerFactory.getLogger("com.popolam.updated");
    public static final int PAGES=2;
    LunDB db;
    private String url;
    public LunSaleThread(){

        //url = URLEncoder.encode("https://www.lun.ua/аренда-квартир-киев", "UTF-8");
        //url = URLEncoder.encode("https://www.lun.ua/продажа-квартир-киев", "UTF-8");
        //url = String.format(url + "?district=5&district=m27&roomCount=%1$s&roomCount=%2$s&priceMin=%3$s&priceMax=%4$s&currency=2",
        //        priceFrom, priceTo, roomsFrom, roomsTo);
        url = "https://www.lun.ua/продажа-квартир-киев" + "?district=5&district=m27&roomCount=3&areaTotalMin=70&order=update-time&street=955&street=330&street=1605&street=1119&priceMax=130000&currency=1";


    }
    @Override
    public void run() {
        db = new LunDB();
        try{
            for (int page = 1; page <= PAGES; page++) {
                logger.info("Starting GET page {}", page);
                String pagedUrl = url+"&page="+String.valueOf(page);
                Document doc = Jsoup.connect(pagedUrl)
                        .validateTLSCertificates(false)
                        .followRedirects(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                        .timeout(20000)
                        .get();


                Elements results = doc.select("div.realty-card");
                logger.info("Got {} results", results.size());
                for (Element result : results) {

                        try {
                            //String url = "https://www.lun.ua" + result.select("a.advertisement-card-header__link-wrapper").attr("href");
                            String id = result.attr("data-jss").replaceAll("^realty-", "");
                            String url = "https://www.lun.ua/a/" + id;
                            String price = result.select("div.realty-card-characteristics__price").first().text();
                            String address  = result.select("span.realty-card-header-title__street").attr("title");
                            String description = result.select("div.realty-card-description").first().text();
                            if (description!=null && description.length()>500){
                                description = description.substring(0,499);
                            }
                            Elements params = result.select("div.realty-card-characteristics-list__item");
                            StringBuilder sbParams = new StringBuilder();
                            if (params!=null){
                                for (Element param : params) {
                                    sbParams.append(param.text()).append("\n");
                                }
                            }
                            if (sbParams.length()==0){
                                sbParams.append("No params");
                            }
                            Ad ad = new Ad();
                            ad.setProvId(Long.valueOf(id));
                            ad.setAddress(address);
                            ad.setLink(url);
                            ad.setDetails(description);
                            ad.setParams(sbParams.toString());
                            ad.setPrice(price);
                            logger.debug("Got ad: {}", ad);
                            Ad existing = db.findAd(ad.getProvId());
                            if (existing != null) {
                                if (!existing.getPrice().equals(ad.getPrice())) {
                                    logger.info("Existing ad {} changed! old price: {}, new price: {}", ad.getLink(), existing.getPrice(), ad.getPrice());
                                    loggerUpd.info("Existing ad {} changed! old price: {}, new price: {}", ad.getLink(), existing.getPrice(), ad.getPrice());
                                    db.updateResult(ad);
                                }
                            } else {
                                if (db.insertResult(ad)) {
                                    logger.info("Got ad: {}", ad);
                                    App.openUrl(url);
                                }
                            }
                        } catch (Exception e){
                            logger.error("Can't parse ad", e);
                        }
                }
            }
        } catch (IOException e){
            logger.error("Can't get lun page", e);
        }  finally {
            db.closeConnection();
        }
    }
}
