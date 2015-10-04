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
package com.librethinking.rte.util;

import com.librethinking.rte.transactions.Transaction;
import com.librethinking.rte.transactions.TravelTransaction;
import com.librethinking.rte.transactions.comparators.DefaultTransactionComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class TravelXLSXReader extends XLSXReader {

    private final String DEFAULT_FILTER = "Credit Card";
    private String FILTER = null;
    
    @Override
    protected List<Transaction> loadTransactions(XSSFSheet sheet) {
        return loadTransactions(sheet, new DefaultTransactionComparator(), new Transaction());
    }

    @Override
    protected List<Transaction> loadTransactions(final XSSFSheet sheet, final Comparator<Transaction> condition, final Transaction given) {
        List<Transaction> transactions = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        boolean foundFirstRow = false;

        // Traversing over each row of XLSX file
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // TODO: can we make it more dynamic/reusable?
            Cell dateCell = row.getCell(0);
            Cell descCell = row.getCell(1);
            Cell catCell = row.getCell(2);
            Cell methodCell = row.getCell(3);
            Cell localAmCell = row.getCell(4);
            Cell convCell = row.getCell(5);
            Cell amountCell = row.getCell(6);
            Cell cityCell = row.getCell(7);
            Cell countryCell = row.getCell(8);

            if (dateCell != null && descCell != null && amountCell != null) {

                if (!foundFirstRow || dateCell.getDateCellValue() == null) {
                    foundFirstRow = true;
                } else {
                    TravelTransaction currTransaction = new TravelTransaction();
                    currTransaction.setId("" + (row.getRowNum() + 1));
                    currTransaction.setDate(dateCell.getDateCellValue());
                    currTransaction.setDescription(descCell.getStringCellValue());
                    currTransaction.setCategory(catCell == null ? "" : catCell.getStringCellValue());
                    currTransaction.setMethod(methodCell == null ? "" : methodCell.getStringCellValue());
                    currTransaction.setLocalAmount(localAmCell == null ? 0.0 : localAmCell.getNumericCellValue());
                    currTransaction.setConversionRate(convCell == null ? 1.0 : convCell.getNumericCellValue());
                    currTransaction.setAmount(amountCell.getNumericCellValue());
                    currTransaction.setCity(cityCell == null ? "" : cityCell.getStringCellValue());
                    currTransaction.setCountry(countryCell == null ? "" : countryCell.getStringCellValue());
                    
                    if (condition.compare(given, currTransaction) <= 0 && getMethodFilter().equals(currTransaction.getMethod())) {
                        transactions.add(currTransaction);
                    }
                }
            }
        }
        return transactions;
    }
    
    public void setMethodFilter(final String filter) {
        this.FILTER = filter;
    }
    
    public String getMethodFilter() {
        return this.FILTER == null ? this.DEFAULT_FILTER : this.FILTER;
    }
}
