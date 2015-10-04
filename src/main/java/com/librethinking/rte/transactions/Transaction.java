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
public class Transaction {

    private String id;
    private Date date;
    private String description;
    private double amount;
    private int reconciled;

    public Transaction() {
    }

    public Transaction(final String id, final Date date, final String description, final double amount) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.reconciled = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public boolean isReconciled() {
        return reconciled > 0;
    }

    public void reconcile() {
        this.reconciled++;
    }
    
    public void dereconcile() {
        this.reconciled--;
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", date=" + date + ", description=" + description + ", amount=" + amount + '}';
    }

}
