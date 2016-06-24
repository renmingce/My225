package com.example.administrator.imageloader.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/6/22.
 */
@Table(name = "history_url")
public class HistoryUrl {
   @Column(name="_id",isId = true)
    private  int id;
   @Column(name="url")
    private  String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HistoryUrl(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public HistoryUrl() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryUrl that = (HistoryUrl) o;

        return url.equals(that.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
