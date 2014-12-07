package com.gipsyz.safe.dto;

import java.util.List;

/**
 * Created by batman on 07/12/2014.
 */
public class BeaconList {
    private int num_objects;
    private List<SimpleBeacon> objects;
    private int page;
    private int total_pages;

    public int getNum_objects() {
        return num_objects;
    }

    public void setNum_objects(int num_objects) {
        this.num_objects = num_objects;
    }

    public List<SimpleBeacon> getObjets() {
        return objects;
    }

    public void setObjets(List<SimpleBeacon> objets) {
        this.objects = objets;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}
