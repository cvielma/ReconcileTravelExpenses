/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.librethinking.rte.util;

import com.librethinking.rte.transactions.Transaction;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public abstract class XLSXReader {

    private XSSFWorkbook workbook;
    private List<Transaction> transactions;

    // TODO: overload method to have different inputs
    public void loadWorkbook(final String filename, final int sheetNum) {
        final File myFile = new File(filename);
        try {
            final FileInputStream fis = new FileInputStream(myFile);
            // Finds the workbook instance for XLSX file
            workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            transactions = loadTransactions(sheet);
        } catch (final IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

//        Read more
//        : http://java67.blogspot.com/2014/09/how-to-read-write-xlsx-file-in-java-apache-poi-example.html#ixzz3i2CGDVgs
    }

    // Condition should consider that all rows that are greater or equal to given will be included
    public void loadWorkbook(final String filename, final int sheetNum, final Comparator<Transaction> condition, final Transaction given) {
        final File myFile = new File(filename);
        try {
            final FileInputStream fis = new FileInputStream(myFile);
            // Finds the workbook instance for XLSX file
            workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            transactions = loadTransactions(sheet, condition, given);
        } catch (final IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

//        Read more
//        : http://java67.blogspot.com/2014/09/how-to-read-write-xlsx-file-in-java-apache-poi-example.html#ixzz3i2CGDVgs
    }

    protected abstract List<Transaction> loadTransactions(final XSSFSheet sheet);
    
    protected abstract List<Transaction> loadTransactions(final XSSFSheet sheet, final Comparator<Transaction> condition, final Transaction given);

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
