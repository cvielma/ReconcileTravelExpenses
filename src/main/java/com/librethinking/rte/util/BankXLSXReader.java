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
import com.librethinking.rte.transactions.comparators.DefaultTransactionComparator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class BankXLSXReader extends XLSXReader {

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
            Cell idCell = row.getCell(1);
            Cell descCell = row.getCell(2);
            Cell amountCell = row.getCell(4);

            if (!foundFirstRow) {
                foundFirstRow = true;
            } else {
                if (dateCell != null && idCell != null && descCell != null && amountCell != null) {
                    Transaction currTransaction = new Transaction();

                    // TODO: make it reusable
                    String string = dateCell.getStringCellValue();
                    DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                    Date date = null;

                    try {
                        date = format.parse(string);
                        //
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }

                    currTransaction.setDate(date);
                    currTransaction.setId("" + (row.getRowNum() + 1));
                    currTransaction.setDescription(descCell.getStringCellValue());
                    currTransaction.setAmount(amountCell.getNumericCellValue() * -1); // Because BANK has the amout the other way
                    if (condition.compare(given, currTransaction) <= 0) {
                        transactions.add(currTransaction);
                    }
                }
            }
        }
        return transactions;
    }

}
