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

package com.librethinking.rte;

import com.librethinking.rte.transactions.ReconciledTransaction;
import com.librethinking.rte.transactions.Transaction;
import com.librethinking.rte.transactions.comparators.DateTransactionComparator;
import com.librethinking.rte.transactions.comparators.DefaultTransactionComparator;
import com.librethinking.rte.util.BankXLSXReader;
import com.librethinking.rte.util.ConcileXLSXWriter;
import com.librethinking.rte.util.TravelXLSXReader;
import com.librethinking.rte.util.XLSXReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class Reconciler {

    private final String BASE_DIR = System.getProperty("user.dir") + "/src/main/resources/test/files";
    private final String TRAVEL_WB_FILE = BASE_DIR + "/YourExpensesFile.xlsx";
    private final String BANK_WB_FILE = BASE_DIR + "/BankFile.xlsx";
    private final String OUTPUT_WB_FILE = BASE_DIR + "/ResultFile.xlsx";
    private final double EPSILON = 3.0;
    private final int MAXIMUM_PAST_DAYS = 3;
    private final int MAXIMUM_FUTURE_DAYS = 7;

    private XLSXReader travelReader, bankReader;

    private void loadTravelWB(final Comparator<Transaction> condition, final Transaction given) {
        final Comparator<Transaction> cond = condition == null ? new DefaultTransactionComparator() : condition;
        final Transaction givenT = given == null ? new Transaction() : given;
        travelReader = new TravelXLSXReader();
        bankReader = new BankXLSXReader();
        
        ((TravelXLSXReader) travelReader).setMethodFilter("Credit Card 1");

        travelReader.loadWorkbook(TRAVEL_WB_FILE, 0, cond, givenT);
        bankReader.loadWorkbook(BANK_WB_FILE, 0, cond, givenT);

        int travelSize = travelReader.getTransactions().size();
        int bankSize = bankReader.getTransactions().size();
        System.out.println("Travel Transactions: " + travelSize);
        System.out.println("First: " + travelReader.getTransactions().get(0));
        System.out.println("Last: " + travelReader.getTransactions().get(travelReader.getTransactions().size() - 1));
        System.out.println("--------------------------------------------------------------");
        System.out.println("BANK Transactions: " + bankSize);
        System.out.println("First: " + bankReader.getTransactions().get(0));
        System.out.println("Last: " + bankReader.getTransactions().get(bankReader.getTransactions().size() - 1));
    }

    /**
     * 0 - OK 1 - Number of transactions differ 2 - Not reconciled
     *
     * @param condition
     * @return
     */
    // TODO: refactor
    public int reconcile(final Comparator<Transaction> condition, final Transaction given) throws IOException {
        loadTravelWB(condition, given);
        List<Transaction> travelTransactions = travelReader.getTransactions();
        List<Transaction> bankTransactions = bankReader.getTransactions();
        if (travelTransactions.size() != bankTransactions.size()) {
            System.out.println("Transactions differ. Travel: " + travelTransactions.size() + ", BANK: " + bankTransactions.size());
        }

        // TODO: this N^2, make more efficient
        // TODO: maybe use a comparator
        List<Transaction> reconciled = new ArrayList<>();
        boolean notReconciled = false;
        for (Transaction tt : travelTransactions) {
            ReconciledTransaction rt = new ReconciledTransaction(tt);
            for (Transaction bt : bankTransactions) {
                if (tt.getAmount() <= bt.getAmount() + EPSILON && tt.getAmount() >= bt.getAmount() - EPSILON) {
                    

                    // NOT (If BANK date is not older than Travel date or if it to far away in the future)
                    if (!tooOld(bt.getDate(), tt.getDate()) && !tooNew(bt.getDate(), tt.getDate())) {
                        rt.addMatch(bt);
                        bt.reconcile();
                        tt.reconcile();
                    }

                }
            }
            if (rt.getMatches().isEmpty()) {
                notReconciled = true;
            } else {
                reconciled.add(rt);
            }
        }

        ConcileXLSXWriter output = new ConcileXLSXWriter(OUTPUT_WB_FILE);
        output.writeReconciled(reconciled, "Reconciled");
        output.writeUnreconciled(bankTransactions, "BANKUnreconciled");
        output.writeUnreconciled(travelTransactions, "TravelUnreconciled");
        output.close();
        
        if (notReconciled) {
            System.out.println("Couldn't reconcile all transactions.");
            printTransactions("----------RECONCILED---------", reconciled);
            printTransactions("----------NOT RECONCILED - Travel ---------", travelTransactions, false);
            printTransactions("----------NOT RECONCILED - Bank ---------", bankTransactions, false);
            return 2;
        }

        System.out.println("Reconciled successfully");
        printTransactions("----------RECONCILED---------", reconciled);
//        //TODO: export to excel
        return 0;
    }
    
    private boolean tooOld(final Date bankDate, final Date travelDate) {
        int daysDiff = Math.abs(Days.daysBetween(new DateTime(bankDate), new DateTime(travelDate)).getDays());
        return bankDate.compareTo(travelDate) < 0 && daysDiff > MAXIMUM_PAST_DAYS;
    }
    
    private boolean tooNew(final Date bankDate, final Date travelDate) {
        int daysDiff = Math.abs(Days.daysBetween(new DateTime(bankDate), new DateTime(travelDate)).getDays());
        return bankDate.compareTo(travelDate) > 0 && daysDiff > MAXIMUM_FUTURE_DAYS;
    }

    private void printTransactions(final String message, final List<Transaction> transactions) {
        System.out.println(message);
        System.out.println(StringUtils.join(transactions, "\n"));
    }
    
    // TODO
    private List<Transaction> selectTransactions(final String message, final List<Transaction> transactions) {
        System.out.println(message);
        for (Transaction t : transactions) {
            if (t instanceof ReconciledTransaction) {
                ReconciledTransaction rt = (ReconciledTransaction) t;
                Collection<Transaction> matches = rt.getMatches();
                if (matches.size() > 1) {
                    System.out.println("Please select one: ");
                    
                }
            }
        }
        System.out.println(StringUtils.join(transactions, "\n"));
        return null;
    }

    private void printTransactions(final String message, final List<Transaction> transactions, final boolean reconciled) {
        System.out.println(message);
        for (Transaction t : transactions) {
            if (t.isReconciled() == reconciled) {
                System.out.println(t);
            }
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        Reconciler rec = new Reconciler();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Transaction given = new Transaction();
        given.setDate(sdf.parse("01/08/2015"));
        rec.reconcile(new DateTransactionComparator(), given);
    }
}
