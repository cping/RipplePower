package org.ripple.power.hft;

import java.util.Date;

public class Candle {
    double Open ;   
    double Close ;
    double High ; 
    double Low ;    
    long Time ;    
    Double Volume ;  
    Date date;
    double AdjClose;

    @Override
    public String toString() {
        return  Open +";" + Close +";" + High +";" + Low +";" + Volume;
    }

    public double getOpen() {
        return Open;
    }

    public double getClose() {
        return Close;
    }

    public double getHigh() {
        return High;
    }

    public double getLow() {
        return Low;
    }

    public Date getDate() {
        return date;
    }

    public long getTime() {
        return Time;
    }

    public double getVolume() {
        return Volume;
    }

    public double getAdjClose() {
        return AdjClose;
    }

    public void setOpen(double open) {
        Open = open;
    }

    public void setClose(double close) {
        Close = close;
    }

    public void setHigh(double high) {
        High = high;
    }

    public void setLow(double low) {
        Low = low;
    }

    public void setTime(long time) {
        Time = time;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public void setAdjClose(double adjClose) {
        AdjClose = adjClose;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
