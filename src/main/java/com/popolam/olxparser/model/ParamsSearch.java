package com.popolam.olxparser.model;

/**
 * Created by p0p0lam on 15.10.2014.
 */
public class ParamsSearch {
    private Search search;

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public class Search{
        private int district_id;

        public int getDistrict_id() {
            return district_id;
        }

        public void setDistrict_id(int district_id) {
            this.district_id = district_id;
        }
    }
}
