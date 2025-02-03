package com.campoquimico.database;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.campoquimico.EnvVariables;


public class DatabaseReader {

    private String filePath;

    public DatabaseReader(String filePath) {
        this.filePath = filePath;
    }

    public void processMolecule() {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheets.");
                return;
            }

            XSSFSheet sheet = workbook.getSheetAt(1); //SHEET SELECTOR
            String moleculeName = sheet.getSheetName();
            System.out.println("Molecule Name: " + moleculeName);

            String[][] board = new String[EnvVariables.BOARD_SIZE][EnvVariables.BOARD_SIZE]; //BOARD-SIZE
            int leftmost = EnvVariables.BOARD_SIZE, rightmost = 0, topmost = EnvVariables.BOARD_SIZE, bottommost = 0;

            //READ EXCEL BOARD VALUES
            for (int row = 1; row <= EnvVariables.BOARD_SIZE; row++) { // ROW 2 -> 16
                XSSFRow sheetRow = sheet.getRow(row);
                if (sheetRow != null) {
                    for (int col = 0; col <= (EnvVariables.BOARD_SIZE - 1); col++) { //COL A(1st) -> O(15th)
                        XSSFCell cell = sheetRow.getCell(col);
                        String value = (cell != null && !cell.toString().isEmpty()) ? cell.toString() : "  ";
                        board[row - 1][col] = value;

                        if (!value.equals("  ")) {
                            if (col < leftmost) leftmost = col;
                            if (col > rightmost) rightmost = col;
                            if (row - 1 < topmost) topmost = row - 1;
                            if (row - 1 > bottommost) bottommost = row - 1;
                        }
                    }
                }
            }

            int width = rightmost - leftmost + 1;
            int height = bottommost - topmost + 1;
            System.out.println("Width: " + width);
            System.out.println("Height: " + height);

            // Print the molecule exactly as drawn
            System.out.println("Molecule Structure:");
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    System.out.print(board[i][j] + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
