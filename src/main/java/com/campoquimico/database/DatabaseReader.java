package com.campoquimico.database;

import java.io.FileInputStream;
import java.util.Random;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.campoquimico.EnvVariables;

public class DatabaseReader {

    private String filePath;
    private static final Random random = new Random();

    public DatabaseReader(String filePath) {
        this.filePath = filePath;
    }

    public int getRandomMolecule() {
        int moleculeId = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return moleculeId;
            }
            moleculeId = random.nextInt(sheetCount);

        } catch (Exception e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        return moleculeId;
    }

    public String getMoleculeName(int moleculeId) {
        String moleculeName = null;
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return null;
            }

            XSSFSheet sheet = workbook.getSheetAt(moleculeId);//SHEET SELECTOR
            moleculeName = sheet.getSheetName();
        } catch (Exception e) {
            System.out.println("Error reading the file.");
        }

        return moleculeName;
    }

    public String[][] processMolecule(int moleculeId) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return null;
            }

            XSSFSheet sheet = workbook.getSheetAt(moleculeId);//SHEET SELECTOR
            String moleculeName = sheet.getSheetName();
            System.out.println("Molecule Name: " + moleculeName);

            //INITIALIZE EMPTY BOARD
            String[][] board = new String[EnvVariables.BOARD_SIZE][EnvVariables.BOARD_SIZE];
            for (int i = 0; i < EnvVariables.BOARD_SIZE; i++) {
                for (int j = 0; j < EnvVariables.BOARD_SIZE; j++) {
                    board[i][j] = " ";
                }
            }

            //LEFT AND TOP START AT MAX SO THEY CAN GET REDUCED
            int leftmost = EnvVariables.BOARD_SIZE, topmost = EnvVariables.BOARD_SIZE;
            //RIGHT AND BOTTOM START AT MIN SO THEY CAN GET INCREASED
            int rightmost = 0, bottommost = 0; 

            //READ EXCEL BOARD VALUES
            for (int row = 1; row <= EnvVariables.BOARD_SIZE; row++) {
                XSSFRow sheetRow = sheet.getRow(row);
                if (sheetRow != null) {
                    for (int col = 0; col < EnvVariables.BOARD_SIZE; col++) {
                        XSSFCell cell = sheetRow.getCell(col);
                        String value = (cell != null && !cell.toString().isEmpty()) ? cell.toString() : "X";
                        board[row - 1][col] = value;

                        if (!value.equals("X")) { // If cell is not empty, update bounding box
                            if (col < leftmost) leftmost = col;
                            if (col > rightmost) rightmost = col;
                            if (row - 1 < topmost) topmost = row - 1;
                            if (row - 1 > bottommost) bottommost = row - 1;
                        }
                    }
                }
            }

            // If no atoms were found, return an empty array
            if (leftmost > rightmost || topmost > bottommost) {
                return new String[0][0];
            }

            // Calculate trimmed board size
            int width = rightmost - leftmost + 1;
            int height = bottommost - topmost + 1;

            // Create trimmed board
            String[][] trimmedBoard = new String[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    trimmedBoard[i][j] = board[topmost + i][leftmost + j];
                }
            }

            return trimmedBoard;

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return new String[0][0]; // Return empty array in case of an error
        }
    }
}