
package com.popolam.olxparser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ads{
   	private Number business;
   	private String city_label;
   	private String created;
   	private String description;
   	private Number has_email;
   	private Number has_phone;
   	private Number has_skype;
   	private String header;
   	private Number hide_user_ads_button;
   	private Number highlighted;
   	private String id;
   	private String list_label;
   	private String list_label_ad;
   	private String list_label_small;
   	private String map_lat;
   	private String map_lon;
   	private Number map_radius;
   	private boolean map_show_detailed;
   	private String map_zoom;
    @JsonIgnore
   	private List<Params> params;
   	private String person;
   	private Photos photos;
   	private String preview_url;
   	private List<String> subtitle;
   	private String title;
   	private Number topAd;
   	private Number urgent;
   	private String url;
   	private String user_ads_id;
   	private String user_ads_url;
   	private String user_id;
   	private String user_label;

 	public Number getBusiness(){
		return this.business;
	}
	public void setBusiness(Number business){
		this.business = business;
	}
 	public String getCity_label(){
		return this.city_label;
	}
	public void setCity_label(String city_label){
		this.city_label = city_label;
	}
 	public String getCreated(){
		return this.created;
	}
	public void setCreated(String created){
		this.created = created;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public Number getHas_email(){
		return this.has_email;
	}
	public void setHas_email(Number has_email){
		this.has_email = has_email;
	}
 	public Number getHas_phone(){
		return this.has_phone;
	}
	public void setHas_phone(Number has_phone){
		this.has_phone = has_phone;
	}
 	public String getHeader(){
		return this.header;
	}
	public void setHeader(String header){
		this.header = header;
	}
 	public Number getHide_user_ads_button(){
		return this.hide_user_ads_button;
	}
	public void setHide_user_ads_button(Number hide_user_ads_button){
		this.hide_user_ads_button = hide_user_ads_button;
	}
 	public Number getHighlighted(){
		return this.highlighted;
	}
	public void setHighlighted(Number highlighted){
		this.highlighted = highlighted;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public String getList_label(){
		return this.list_label;
	}
	public void setList_label(String list_label){
		this.list_label = list_label;
	}
 	public String getList_label_ad(){
		return this.list_label_ad;
	}
	public void setList_label_ad(String list_label_ad){
		this.list_label_ad = list_label_ad;
	}
 	public String getMap_lat(){
		return this.map_lat;
	}
	public void setMap_lat(String map_lat){
		this.map_lat = map_lat;
	}
 	public String getMap_lon(){
		return this.map_lon;
	}
	public void setMap_lon(String map_lon){
		this.map_lon = map_lon;
	}
 	public Number getMap_radius(){
		return this.map_radius;
	}
	public void setMap_radius(Number map_radius){
		this.map_radius = map_radius;
	}
 	public boolean getMap_show_detailed(){
		return this.map_show_detailed;
	}
	public void setMap_show_detailed(boolean map_show_detailed){
		this.map_show_detailed = map_show_detailed;
	}
 	public String getMap_zoom(){
		return this.map_zoom;
	}
	public void setMap_zoom(String map_zoom){
		this.map_zoom = map_zoom;
	}
 	public List<Params> getParams(){
		return this.params;
	}
	public void setParams(List<Params> params){
		this.params = params;
	}
 	public String getPerson(){
		return this.person;
	}
	public void setPerson(String person){
		this.person = person;
	}

	public Photos getPhotos() {
		return photos;
	}

	public void setPhotos(Photos photos) {
		this.photos = photos;
	}

	public String getPreview_url(){
		return this.preview_url;
	}
	public void setPreview_url(String preview_url){
		this.preview_url = preview_url;
	}
 	public List<String> getSubtitle(){
		return this.subtitle;
	}
	public void setSubtitle(List<String> subtitle){
		this.subtitle = subtitle;
	}
 	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
 	public Number getTopAd(){
		return this.topAd;
	}
	public void setTopAd(Number topAd){
		this.topAd = topAd;
	}
 	public Number getUrgent(){
		return this.urgent;
	}
	public void setUrgent(Number urgent){
		this.urgent = urgent;
	}
 	public String getUrl(){
		return this.url;
	}
	public void setUrl(String url){
		this.url = url;
	}
 	public String getUser_ads_id(){
		return this.user_ads_id;
	}
	public void setUser_ads_id(String user_ads_id){
		this.user_ads_id = user_ads_id;
	}
 	public String getUser_ads_url(){
		return this.user_ads_url;
	}
	public void setUser_ads_url(String user_ads_url){
		this.user_ads_url = user_ads_url;
	}
 	public String getUser_id(){
		return this.user_id;
	}
	public void setUser_id(String user_id){
		this.user_id = user_id;
	}
 	public String getUser_label(){
		return this.user_label;
	}
	public void setUser_label(String user_label){
		this.user_label = user_label;
	}

    public Number getHas_skype() {
        return has_skype;
    }

    public void setHas_skype(Number has_skype) {
        this.has_skype = has_skype;
    }

    public boolean isMap_show_detailed() {
        return map_show_detailed;
    }

    public String getList_label_small() {
        return list_label_small;
    }

    public void setList_label_small(String list_label_small) {
        this.list_label_small = list_label_small;
    }

    @Override
    public String toString() {
        return "Ads{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", list_label='" + list_label + '\'' +
                '}';
    }
}
