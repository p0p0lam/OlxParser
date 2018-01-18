
package com.popolam.olxparser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;
public class Result{
   	private List<Ads> ads;
   	private Number ads_on_page;
   	private String category_id;
   	private String next_page_url;
   	private Number page;
    @JsonIgnore
   	private Params params;
   	private TopHeaderLabels top_header_labels;
   	private Integer total_ads;
   	private Integer total_pages;
   	private String view;

 	public List<Ads> getAds(){
		return this.ads;
	}
	public void setAds(List<Ads> ads){
		this.ads = ads;
	}
 	public Number getAds_on_page(){
		return this.ads_on_page;
	}
	public void setAds_on_page(Number ads_on_page){
		this.ads_on_page = ads_on_page;
	}
 	public String getCategory_id(){
		return this.category_id;
	}
	public void setCategory_id(String category_id){
		this.category_id = category_id;
	}
 	public String getNext_page_url(){
		return this.next_page_url;
	}
	public void setNext_page_url(String next_page_url){
		this.next_page_url = next_page_url;
	}
 	public Number getPage(){
		return this.page;
	}
	public void setPage(Number page){
		this.page = page;
	}
 	public Params getParams(){
		return this.params;
	}
	public void setParams(Params params){
		this.params = params;
	}
 	public TopHeaderLabels getTop_header_labels(){
		return this.top_header_labels;
	}
	public void setTop_header_labels(TopHeaderLabels top_header_labels){
		this.top_header_labels = top_header_labels;
	}
 	public Number getTotal_ads(){
		return this.total_ads;
	}

    public void setTotal_ads(Integer total_ads) {
        this.total_ads = total_ads;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }

    public String getView(){
		return this.view;
	}
	public void setView(String view){
		this.view = view;
	}
}
