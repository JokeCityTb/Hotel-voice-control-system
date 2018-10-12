package com.eningqu.emeeting.bean;

public class TranslateInfo {

    public  static final int TYPE_RECEIVE = 1;
    public  static final int TYPE_SEND = 2;
    private String translate_old;
    private String translate_other;
    private String date;
    private String url;
    private int type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TranslateInfo() {
    }

    public TranslateInfo(String tran_old, String tran_other, String mdate, String murl, int mtype) {
        translate_old = tran_old;
        translate_other = tran_other;
        date = mdate;
        url = murl;
        type = mtype;
    }

    public void setTranslate_old(String translate_old) {
        this.translate_old = translate_old;
    }

    public void setTranslate_other(String translate_other) {
        this.translate_other = translate_other;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTranslate_other() {
        return translate_other;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public String getTranslate_old() {
        return translate_old;
    }
}
