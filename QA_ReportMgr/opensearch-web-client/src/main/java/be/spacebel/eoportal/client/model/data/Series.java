/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.model.data;

import java.io.Serializable;
import java.util.List;

/**
 * This class contains all configurations of series search
 *
 * @author mng
 */
public class Series extends Dataset implements Serializable {    
    private List<String> seriesMenuList;
    private String menuOptionRegex;

    public Series() {
    }    

    public boolean isSeriesMenu() {
        return true;
    }   

    public List<String> getSeriesMenuList() {
        return seriesMenuList;
    }

    public void setSeriesMenuList(List<String> seriesMenuList) {
        this.seriesMenuList = seriesMenuList;
    }

    public String getMenuOptionRegex() {
        return menuOptionRegex;
    }

    public void setMenuOptionRegex(String menuOptionRegex) {
        this.menuOptionRegex = menuOptionRegex;
    }

}
