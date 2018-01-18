package com.popolam.olxparser;

import com.popolam.olxparser.model.*;
import com.popolam.olxparser.model.lun.Ad;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    // The connection URL
    static  final String priceTo="15000";
    static  final String priceFrom = "7000";
    static  final String roomsFrom="3";
    static  final String roomsTo="3";
    private static final String OLX_RENT_URL =String.format("https://www.olx.ua/i2/nedvizhimost/arenda-kvartir/dolgosrochnaya-arenda-kvartir/kiev/?json=1&search[filter_float_price:from]=%1$s&search[filter_float_price:to]=%2$s&search[filter_float_number_of_rooms:from]=%3$s&search[filter_float_number_of_rooms:to]=%4$s&search[district_id]=9&search[order]=created_at:desc",
            priceFrom, priceTo, roomsFrom, roomsTo);
    public static final String LUN_URL = String.format("https://www.lun.ua/аренда-квартир-киев?district=5&district=m27&roomCount=%1$s&roomCount=%2$s&priceMin=%3$s&priceMax=%4$s&currency=2",
            roomsFrom, roomsTo, priceFrom, priceTo);
            //URLDecoder.decode("http://olx.ua/i2/nedvizhimost/arenda-kvartir/dolgosrochnaya-arenda-kvartir/kiev/?json=1&search%5Bfilter_float_price%3Ato%5D=12000&search%5Bfilter_float_number_of_rooms%3Afrom%5D=3&search%5Bfilter_float_number_of_rooms%3Ato%5D=4&search%5Bdistrict_id%5D=9&search%5Border%5D=created_at%3Adesc");
    private static final String OLX_LG_URL =
            URLDecoder.decode("http://olx.ua/i2/elektronika/telefony/mobilnye-telefony/kiev/q-lg/?json=1");
    ScheduledExecutorService service;
    final OlxThread olxThread;
    final OlxThread lgOlxThread;
    final LunThread lunThread;
    final LunSaleThread lunSaleThread;
    final OlxSaleThread olxSaleThread;

    public App() {

        service = Executors.newScheduledThreadPool(3);
        olxThread = new OlxThread(OLX_RENT_URL);
        lgOlxThread = new OlxThread(OLX_LG_URL, false, 2);
        lunThread = new LunThread();
        lunSaleThread = new LunSaleThread();
        olxSaleThread = new OlxSaleThread();
        //logger.info("Parsed date: {}", parseDate("09/01/2014 00:00:00", "MM/dd/yyyy"));
        //logger.info("formatted date: {}", formatDate(new Date(), "dd-MM-yyyy HH:mm:ss"));
        start();
    }

    public static String formatDate(Date date, String pattern){
        return  new SimpleDateFormat(pattern).format(date);
    }

    public static Date parseDate(String date, String pattern){
        // SimpleDateFormat df = new SimpleDateFormat(pattern);
        try{
            return  new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e){

        }
        return  null;
    }

    public void start(){
        service.scheduleAtFixedRate(lunSaleThread, 5, 120, TimeUnit.SECONDS);
        service.scheduleAtFixedRate(olxSaleThread, 0, 120, TimeUnit.SECONDS);
        //service.scheduleAtFixedRate(new EstThread(), 5, 120, TimeUnit.SECONDS);
        //service.scheduleAtFixedRate(olxThread, 10, 120, TimeUnit.SECONDS);
        //service.scheduleAtFixedRate(lgOlxThread, 5, 60, TimeUnit.SECONDS);

    }

    public void stop(){
        service.shutdownNow();
    }

    public static void openUrl(String url){
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e){
            logger.error("Can't open url: {} in browser", url );
        }
    }

    public static void main(String[] args) {
        App app = new App();
    }

    public static class EstThread implements Runnable {
        public static final String URL ="http://kiev.est.ua/nedvizhimost/snjat-kvartiru/rajon-obolonskij-40908/komnat-3/?sort=authenticity&submitted=1&price_till=15000&floor%5B0%5D=not-first&floor%5B1%5D=not-last&price_currency=UAH";
        final CommonDB db;
        private static final Logger logger = LoggerFactory.getLogger(EstThread.class);
        public EstThread() {
            db = new CommonDB("estdb");
        }

        @Override
        public void run() {
            logger.debug("Starting GET for url: " + URL);
            try {
                Document doc = Jsoup.connect(URL).get();
                Element paging = doc.select("div.paging__links").first();
                java.util.List<String> additionalUrls = new ArrayList<>();
                if (paging!=null){
                    logger.info("got pages");
                    Elements hrefs = paging.select("a[href]");
                    for (Element href : hrefs) {
                        String hrefText = href.text();
                        try{
                            int page = Integer.parseInt(hrefText);
                            additionalUrls.add(href.attr("href"));
                        }catch (NumberFormatException w){
                            //skip
                        }
                    }
                }
                logger.info("All pages: {}", additionalUrls.size()+1);
                parseDoc(doc);
                if (!additionalUrls.isEmpty()){
                    for (int i = 0; i < additionalUrls.size(); i++) {
                        String url = additionalUrls.get(i);
                        logger.info("Getting page {}", i+1);
                        doc = Jsoup.connect(url).get();
                        parseDoc(doc);
                    }
                }
            } catch (IOException e){
                logger.error("Can't get page from EST", e);
            }
        }

        private void parseDoc(Document doc) {
            Elements results = doc.select("div.eo-list-body").select("div.eo-item");
            logger.info("Got {} results", results.size());
            for (Element result : results) {
                try {
                    Long id = Long.valueOf(result.attr("data-record-id"));
                    Element headline = result.select("div.eo-item__headline").select("a[href]").first();
                    String link = null;
                    if (headline != null) {
                        link = headline.attr("href");
                    }
                    Ad ad = new Ad();

                    String address = result.select("div.eo-item__address").first().text();
                    String description = result.select("div.est-easy-html*").first().text();
                    ad.setProvId(id);
                    ad.setLink(link);
                    ad.setAddress(address);
                    ad.setDetails(description);
                    Elements infoElements = result.select("div.eo-item__main").select("span.eo-item__main-item");
                    ad.setPrice(infoElements.get(0).child(1).text());
                    if (infoElements.size()>1) {
                        ad.setArea(infoElements.get(1).child(1).text());
                    }
                    if (infoElements.size()>2) {
                        ad.setRoomNo(infoElements.get(2).child(1).text());
                    }
                    if (db.insertResult(ad)) {
                        logger.info("Find interesting ad: {}", ad);
                        openUrl(ad.getLink());
                    }
                }catch (Exception e){
                    logger.error("Can't parse item result", e);
                }
            }
        }
    }

    public static class LunThread implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(LunThread.class);
        LunDB db;
        private String url;

        public LunThread(String url) {
            this.url = url;
        }

        private LunThread(){

                //url = URLEncoder.encode("https://www.lun.ua/аренда-квартир-киев", "UTF-8");
                //url = URLEncoder.encode("https://www.lun.ua/продажа-квартир-киев", "UTF-8");
                //url = String.format(url + "?district=5&district=m27&roomCount=%1$s&roomCount=%2$s&priceMin=%3$s&priceMax=%4$s&currency=2",
                //        priceFrom, priceTo, roomsFrom, roomsTo);
                url = String.format("https://www.lun.ua/продажа-квартир-киев" + "?district=5&district=m27&roomCount=$s&roomCount=$s",
                                roomsFrom, roomsTo);

        }

        public void run() {
            db = new LunDB();
            try{
                logger.debug("Starting GET for url: " + url);
                //Document doc = Jsoup.connect("http://www.lun.ua/%D0%B0%D1%80%D0%B5%D0%BD%D0%B4%D0%B0-%D0%BA%D0%B2%D0%B0%D1%80%D1%82%D0%B8%D1%80-%D0%BA%D0%B8%D0%B5%D0%B2?district=5&district=m27&roomCount=3&roomCount=3&priceMax=12000&currency=2").get();
                Document doc = Jsoup.connect(url)
                        .validateTLSCertificates(false)
                        .followRedirects(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36")
                        .get();


                Elements results = doc.select("div.obj");
                logger.info("Got {} results", results.size());
                int i=0;
                for (Element result : results) {

                    Element favorite = result.select("div.obj-favourite").first();
                    if (favorite!=null){
                        String id = favorite.attr("ann_id");
                        Element infoBlock = result.select("div.obj-left").first();
                        Element linkEl = infoBlock.select("span.make-title-visited").first().children().select("a").first();
                        String link = linkEl.attr("abs:href");
                        linkEl.attr("href", link);
                        String address = linkEl.text();
                        Elements paramsEl = result.select("div.obj-params-col > p");
                        StringJoiner str = new StringJoiner(", ");
                        for (Element param : paramsEl) {
                            str.add(param.text());
                        }
                        String params = str.toString();

                        String details = result.select("div.obj-details > div").text();

                        String price = result.select("div.obj-right").first().select("span.obj-price").first().text();

                        Ad ad = new Ad();
                        ad.setProvId(Long.valueOf(id));
                        ad.setAddress(address);
                        ad.setLink(link);
                        ad.setDetails(details);
                        ad.setParams(params);
                        ad.setPrice(price);
                        logger.debug("Got ad: {}", ad);
                        if (match(ad.getAddress())){
                            if (db.insertResult(ad)){
                                logger.info("Got ad: {}", ad);
                                URI uri = createHtml(result, link, id);
                                openUrl(uri.toString());
                            }
                        }

                    }
                }
            } catch (IOException e){
                logger.error("Can't get lun page", e);
            }  finally {
                db.closeConnection();
            }
        }
        private URI createHtml(Element info, String link, String id){
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("lun_start.html").getFile());
            try {
                Document doc = Jsoup.parse(file, "utf-8");
                Element origLink = doc.select("a.orig_link").first();
                origLink.attr("href", link);
                Element infoBlock = doc.select("div.lun_original").first();
                infoBlock.appendChild(info);
                Element linkFrame = doc.select("#lun_content").first();
                linkFrame.attr("src", link );
                String newDoc = doc.html();
                File out = new File(String.format("tmp/lun_temp_%s.html", id));
                Writer writer = new OutputStreamWriter(
                        new FileOutputStream(out), "UTF-8");
                writer.write(newDoc);
                writer.close();
                return out.toURI();
            } catch (IOException e){
                logger.error("Can't open lun html file", e);
            }
            return null;
        }
        private boolean match(String src){
            String text = src.toLowerCase();
            if (text.contains("гер") && text.contains("стал")){
                return true;
            }
            if (text.contains("град") && text.contains("стал")){
                return true;
            }
            if (text.contains("мал") && text.contains("ског") && text.contains("нов")){
                return true;
            }
            if (text.contains("мошенк")){
                return true;
            }
            if (text.contains("оболонск")){
                return true;
            }
            return false;
        }
    }

    public static class OlxThread implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(OlxThread.class);
        DbUtils db;
        private final String url;
        private final boolean isNeedMatch;
        private int pages=1;

        public OlxThread(String url) {
            this.url = url;
            isNeedMatch = true;
        }
        public OlxThread(String url, boolean needMatch, int pages) {
            this.url = url;
            isNeedMatch = needMatch;
            this.pages=pages;
        }
        @Override
        public void run() {
            db = new DbUtils();

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();



                // Make the HTTP GET request, marshaling the response to a String
                try {
                    for (int i = 1; i <= pages; i++) {
                        logger.info("Getting page {}", i);
                        //String resultStr = restTemplate.getForObject(urlOlx, String.class, "");
                        String pagedUrl = url + "&page=" + i;
                        logger.info("Starting GET url: {}", pagedUrl);
                        Result result = restTemplate.getForObject(pagedUrl, Result.class, "");

                        //Result result = mapper.readValue(resultStr, Result.class);
                        if (result.getAds() != null) {
                            logger.info("Got result! Ads size: {}", result.getAds().size());
                            result.getAds().stream().filter(ads -> processAd(ads)).forEach(ads -> {
                                logger.info("Find interesting ad: {}", ads);
                                openUrl(ads.getUrl());
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

        private boolean match(String src){
            if (!isNeedMatch){
                return true;
            }
            String text = src.toLowerCase();
            if (text.contains("гер") && text.contains("стал")){
                return true;
            }
            if (text.contains("град") && text.contains("стал")){
                return true;
            }
            if (text.contains("мал") && text.contains("ског") && text.contains("нов")){
                return true;
            }
            if (text.contains("мошенк")){
                return true;
            }
            if (text.contains(", оболонск")){
                return true;
            }
            return false;
        }

        private boolean processAd(Ads ad){
            boolean result = false;
            if (ad.getTitle()!=null){
                if (match(ad.getTitle())){
                    if (db.insertResult(ad)){
                        result=true;
                    }
                }
            }
            if (ad.getDescription()!=null && !result){
                if (match(ad.getDescription())){
                    if (db.insertResult(ad)){
                        result=true;
                    }
                }
            }
            if (ad.getCity_label()!=null && !result){
                if (match(ad.getCity_label())){
                    if (db.insertResult(ad)){
                        result=true;
                    }
                }
            }
            return result;
        }
    }
}
