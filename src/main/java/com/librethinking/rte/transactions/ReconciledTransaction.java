/*
 * Copyright 2015 Christian Vielma <cvielma@librethinking.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.librethinking.rte.transactions;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class ReconciledTransaction extends Transaction {
    private Transaction self;
    private TreeSet<Transaction> matches;
    
    public ReconciledTransaction(final Transaction self){
        super();
        this.self = self;
        this.matches = new TreeSet<>();
    }
    
    public void addMatch(final Transaction match) {
        double priority = getPriority(match);
        this.matches.add(new TransactionPriority(priority, match));
    }
    
    public Collection<Transaction> getMatches() {
        return matches;
    }
    
    public Transaction getSelf() {
        return self;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.self.toString());
        for (Transaction t : this.matches) {
            sb.append("\n\t\t- ");
            sb.append(t.toString());
        }
        return sb.toString();
    }

    private double getPriority(Transaction match) {
        int daysDiff = Days.daysBetween(new DateTime(match.getDate()), new DateTime(self.getDate())).getDays();
        double amountDiff = match.getAmount() - self.getAmount();
        double factor = 0.1;
        return Math.abs(daysDiff) * Math.abs(amountDiff) +
                Math.abs(amountDiff) +
                Math.abs(daysDiff) * factor;
    }
}


//TODO: maybe not the cleanest
class TransactionPriority extends Transaction implements Comparable {
    private double priority;
    private Transaction self;

    public TransactionPriority() {
    }

    public TransactionPriority(double priority, Transaction transaction) {
        this.priority = priority;
        this.self = transaction;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public Transaction getSelf() {
        return self;
    }

    public void setSelf(Transaction transaction) {
        this.self = transaction;
    }

    @Override
    public String toString() {
        return "Priority: " + priority + ", " + self.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reconcile() {
        self.reconcile(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isReconciled() {
        return self.isReconciled(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAmount(double amount) {
        self.setAmount(amount); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAmount() {
        return self.getAmount(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDescription(String description) {
        self.setDescription(description); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        return self.getDescription(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDate(Date date) {
        self.setDate(date); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getDate() {
        return self.getDate(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(String id) {
        self.setId(id); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        return self.getId(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public int compareTo(Object o) {
        if (o instanceof TransactionPriority && o != null) {
            return Double.compare(this.getPriority() - ((TransactionPriority) o).getPriority(), 0.0);
        } else {
            return -1;
        }
    }
    
    
}