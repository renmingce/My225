package com.example.administrator.imageloader.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/21.
 */
public class ImageBean implements Serializable{

    private  String url;
    private  boolean checked;

    public ImageBean(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "url='" + url + '\'' +
                ", checked=" + checked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageBean imageBean = (ImageBean) o;

        return url.equals(imageBean.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
