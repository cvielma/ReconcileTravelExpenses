/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.librethinking.rte.transactions;

import java.util.Date;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class TravelTransaction extends Transaction {

    private String category;
    private String method; //TODO: might be better as an enum?
    private double localAmount;
    private double conversionRate;
    private String city;
    private String country;

    public TravelTransaction() {
        super();
    }   
    
    public TravelTransaction(final String id, final Date date,
            final String description, final double amount,
            final String category, final String method,
            final double localAmount,
            final double conversionRate, final String city,
            final String country) {
        
        super(id, date, description, amount);
        this.category = category;
        this.method = method;
        this.localAmount = localAmount;
        this.conversionRate = conversionRate;
        this.city = city;
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(double localAmount) {
        this.localAmount = localAmount;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "TravelTransaction{id=" + this.getId() + ", date=" + this.getDate() 
                + ", description=" + this.getDescription() + ", amount=" + this.getAmount() 
                + ", category=" + category + ", method=" + method + ", localAmount=" + localAmount + ", conversionRate=" + conversionRate + ", city=" + city + ", country=" + country + '}';
    }
    
    

}
