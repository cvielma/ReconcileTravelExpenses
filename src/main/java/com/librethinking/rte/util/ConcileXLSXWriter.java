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

import com.librethinking.rte.transactions.ReconciledTransaction;
import com.librethinking.rte.transactions.Transaction;
import com.librethinking.rte.transactions.TravelTransaction;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Christian Vielma <cvielma@librethinking.com>
 */
public class ConcileXLSXWriter {

    private Workbook wb;
    private String wb_name;
    private static final int START_ROW = 3;
    private static final int FULL_TRANS_START_CELL = 0;
    private static final int SUB_TRANS_START_CELL = 1;
    private final CellStyle DEFAULT_STYLE;
    private final CellStyle BEST_MATCH_STYLE;
    private final CellStyle MATCH_STYLE;
    private final CellStyle DATE_STYLE;

    public ConcileXLSXWriter(final String wb_name) {
        this.wb = new XSSFWorkbook();
        this.wb_name = wb_name;

        Font font = wb.createFont();
        font.setColor(IndexedColors.BLACK.index);
        DEFAULT_STYLE = wb.createCellStyle();
        DEFAULT_STYLE.setFont(font);

        font = wb.createFont();
        font.setColor(IndexedColors.BLUE.index);
        BEST_MATCH_STYLE = wb.createCellStyle();
        BEST_MATCH_STYLE.setFont(font);

        font = wb.createFont();
        font.setColor(IndexedColors.RED.index);
        MATCH_STYLE = wb.createCellStyle();
        MATCH_STYLE.setFont(font);

        DATE_STYLE = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        DATE_STYLE.setDataFormat(
                createHelper.createDataFormat().getFormat("m/d/yy"));
    }

    public void writeReconciled(final List<Transaction> transactions, final String sheetName) {
        Sheet sheet = wb.createSheet(sheetName);
        int currRow = START_ROW;
        for (Transaction trans : transactions) {
            if (trans instanceof ReconciledTransaction) {
                ReconciledTransaction rec = (ReconciledTransaction) trans;
                Row row = sheet.createRow(++currRow);
                writeFullTransaction(rec, row, FULL_TRANS_START_CELL);
                //TODO: set style with setRowStyle
                for (Cell cell : row) {
                    cell.setCellStyle(DEFAULT_STYLE);
                }
                row.getCell(FULL_TRANS_START_CELL).setCellStyle(DATE_STYLE); //TODO: see a way to include it in the style

                Collection<Transaction> matches = rec.getMatches();
                boolean first = true;
                if (matches != null || matches.size() > 0) {
                    for (Transaction match : matches) {
                        row = sheet.createRow(++currRow);
                        writeBasicTransaction(match, row, SUB_TRANS_START_CELL);
                        if (first) {
                            for (Cell cell : row) {
                                cell.setCellStyle(BEST_MATCH_STYLE);
                            }
                            first = false;
                        } else {
                            for (Cell cell : row) {
                                cell.setCellStyle(MATCH_STYLE);
                            }
                        }
                        row.getCell(SUB_TRANS_START_CELL).setCellStyle(DATE_STYLE); //TODO: see a way to include it in the style
                    }
                }
            } else {
                Row row = sheet.createRow(++currRow);
                writeBasicTransaction(trans, row, FULL_TRANS_START_CELL);
                row.setRowStyle(DEFAULT_STYLE);
            }
        }
    }

    private void writeFullTransaction(final Transaction transaction, final Row row, final int startCell) {
        if (transaction != null) {
            int currCell = startCell;
            if (transaction instanceof ReconciledTransaction
                    && ((ReconciledTransaction) transaction).getSelf() instanceof TravelTransaction) {
                ReconciledTransaction rec = (ReconciledTransaction) transaction;
                TravelTransaction travel = (TravelTransaction) rec.getSelf();
                Cell cell = row.createCell(currCell++);
                cell.setCellValue(travel.getDate());
                cell.setCellStyle(DATE_STYLE);

                row.createCell(currCell++).setCellValue(travel.getDescription());
                row.createCell(currCell++).setCellValue(travel.getCategory());
                row.createCell(currCell++).setCellValue(travel.getMethod());
                row.createCell(currCell++).setCellValue(travel.getLocalAmount());
                row.createCell(currCell++).setCellValue(travel.getConversionRate());
                row.createCell(currCell++).setCellValue(travel.getAmount());
                row.createCell(currCell++).setCellValue(travel.getCity());
                row.createCell(currCell++).setCellValue(travel.getCountry());
                row.createCell(currCell++).setCellValue(travel.getId());

            } else {
                writeBasicTransaction(transaction, row, startCell);
            }
        }
    }

    private void writeBasicTransaction(final Transaction transaction, final Row row, final int startCell) {
        if (transaction != null) {
            int currCell = startCell;
            Cell cell = row.createCell(currCell++);
            cell.setCellValue(transaction.getDate());
            cell.setCellStyle(DATE_STYLE);
            row.createCell(currCell++).setCellValue(transaction.getId());
            row.createCell(currCell++).setCellValue(transaction.getDescription());
            row.createCell(currCell++).setCellValue(transaction.getAmount());
        }
    }

    public void writeUnreconciled(final List<Transaction> transactions, final String sheetName) {
        Sheet sheet = wb.createSheet(sheetName);
        int currRow = START_ROW;
        for (Transaction transaction : transactions) {
            if (!transaction.isReconciled()) {
                Row row = sheet.createRow(++currRow);
                if (transaction instanceof TravelTransaction) {
                    writeFullTransaction(transaction, row, FULL_TRANS_START_CELL);
                } else {
                    writeBasicTransaction(transaction, row, FULL_TRANS_START_CELL);
                }
            }
        }
    }

    public void close() throws FileNotFoundException, IOException {
        FileOutputStream fileOut = new FileOutputStream(wb_name);
        wb.write(fileOut);
        fileOut.close();
    }
}
