
package com.popolam.olxparser.model;

import java.util.List;

public class Params{
   	private Number category;
   	private Number city;

   	private ParamsSearch params;
   	private Number region;
   	private Number shopId;
   	private Number subregion;


    public Number getCategory(){
		return this.category;
	}
	public void setCategory(Number category){
		this.category = category;
	}
 	public Number getCity(){
		return this.city;
	}
	public void setCity(Number city){
		this.city = city;
	}

    public ParamsSearch getParams() {
        return params;
    }

    public void setParams(ParamsSearch params) {
        this.params = params;
    }

    public Number getRegion(){
		return this.region;
	}
	public void setRegion(Number region){
		this.region = region;
	}
 	public Number getShopId(){
		return this.shopId;
	}
	public void setShopId(Number shopId){
		this.shopId = shopId;
	}
 	public Number getSubregion(){
		return this.subregion;
	}
	public void setSubregion(Number subregion){
		this.subregion = subregion;
	}
}
