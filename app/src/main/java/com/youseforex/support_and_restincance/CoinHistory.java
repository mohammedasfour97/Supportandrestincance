package com.youseforex.support_and_restincance;

public class CoinHistory {

    private String index , symbol , d1h4 , sr , price , date , time , notified;

    public String getIndex() {
        return index;
    }

    public CoinHistory( String symbol, String d1h4, String sr, String price, String date, String time) {
        this.symbol = symbol;
        this.d1h4 = d1h4;
        this.sr = sr;
        this.price = price;
        this.date = date;
        this.time = time;
    }

    public CoinHistory() {
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getD1h4() {
        return d1h4;
    }

    public void setD1h4(String d1h4) {
        this.d1h4 = d1h4;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotified() {
        return notified;
    }

    public void setNotified(String notified) {
        this.notified = notified;
    }
}
