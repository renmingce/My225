package com.example.administrator.imageloader.bean;

/**
 * Created by Administrator on 2016/6/21.
 */
public class WebLink {

    private  int icon;
    private  String url;
    private  String name;

    public WebLink(String name, int icon, String url) {
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    public WebLink() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WebLink{" +
                "icon=" + icon +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
