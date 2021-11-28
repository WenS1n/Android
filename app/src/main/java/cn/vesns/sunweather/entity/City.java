package cn.vesns.sunweather.entity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {

    private String id;

    private String cityName;

    private String getAdm2;

    private String getAdm1;

    private String getCountry;



    public City() {
    }

    public City(String id, String cityName, String getAdm2, String getAdm1, String getCountry) {
        this.id = id;
        this.cityName = cityName;
        this.getAdm2 = getAdm2;
        this.getAdm1 = getAdm1;
        this.getCountry = getCountry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getGetAdm2() {
        return getAdm2;
    }

    public void setGetAdm2(String getAdm2) {
        this.getAdm2 = getAdm2;
    }

    public String getGetAdm1() {
        return getAdm1;
    }

    public void setGetAdm1(String getAdm1) {
        this.getAdm1 = getAdm1;
    }

    public String getGetCountry() {
        return getCountry;
    }

    public void setGetCountry(String getCountry) {
        this.getCountry = getCountry;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", getAdm2='" + getAdm2 + '\'' +
                ", getAdm1='" + getAdm1 + '\'' +
                ", getCountry='" + getCountry + '\'' +
                '}';
    }
}
