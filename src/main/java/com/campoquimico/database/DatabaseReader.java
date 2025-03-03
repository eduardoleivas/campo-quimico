package com.campoquimico.database;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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

    public boolean checkMoleculeExists(int moleculeId) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return false;
            }

            if(workbook.getSheetAt(moleculeId) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkSingleMolecule() {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return false;
            } else if (sheetCount == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
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

    public List<String> getMoleculeName(int moleculeId) {
        List<String> moleculeNames = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
    
            int sheetCount = workbook.getNumberOfSheets();
            if (sheetCount == 0) {
                System.out.println("The file must have at least one sheet.");
                return null;
            }
    
            XSSFSheet sheet = workbook.getSheetAt(moleculeId); // SHEET SELECTOR
    
            //READ A32 AND A33
            Row row32 = sheet.getRow(31); //ROW 32
            Row row33 = sheet.getRow(32); //ROW 33
    
            if (row32 != null) {
                Cell cell32 = row32.getCell(0); //COL A
                if (cell32 != null) {
                    moleculeNames.add(cell32.getStringCellValue());
                }
            }
    
            if (row33 != null) {
                Cell cell33 = row33.getCell(0); //COL A
                if (cell33 != null) {
                    moleculeNames.add(cell33.getStringCellValue());
                }
            }
    
        } catch (Exception e) {
            System.out.println("Error reading the file.");
        }
    
        return moleculeNames;
    }

    public int getMoleculeGamemode(int moleculeId) {
        int moleculeGamemode = -1; // Default value in case of error
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
    
            XSSFSheet sheet = workbook.getSheetAt(moleculeId); // SHEET SELECTOR
    
            // Retrieve cell A36 (row 36, col 0)
            Row row = sheet.getRow(35); // Row 36 (0-indexed)
            if (row != null) {
                Cell cell = row.getCell(0); // Column A
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    moleculeGamemode = (int) cell.getNumericCellValue();
                }
            }
    
            if (moleculeGamemode == -1) {
                System.out.println("Essa molécula não possui gamemode definido.");
            }
        } catch (Exception e) {
            System.out.println("Error reading the molecule gamemode: " + e.getMessage());
        }
        return moleculeGamemode;
    }

    public String getMoleculeTip(int moleculeId) {
        String moleculeTip = null;
        try (FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(moleculeId); // SHEET SELECTOR
            
            //CHECK TIP REGIONS
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.isInRange(28, 0)) { //ROW 29 COL A
                    Row row = sheet.getRow(region.getFirstRow());
                    Cell cell = row.getCell(region.getFirstColumn());
                    moleculeTip = cell.getStringCellValue();
                    break;
                }
            }

            if (moleculeTip == null) {
                System.out.println("Essa molécula não possúi dica.");
            }
        } catch (Exception e) {
            System.out.println("Error reading the molecule tip.");
        }
        return moleculeTip;
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

                        if (!value.equals("X")) { //UPDATES BOUNDING BOX OF THE CELL
                            if (col < leftmost) leftmost = col;
                            if (col > rightmost) rightmost = col;
                            if (row - 1 < topmost) topmost = row - 1;
                            if (row - 1 > bottommost) bottommost = row - 1;
                        }
                    }
                }
            }

            //EMPTY ERROR HANDLER
            if (leftmost > rightmost || topmost > bottommost) {
                return new String[0][0];
            }

            //CALCULATE MOLECULE DIMENSIONS
            int width = rightmost - leftmost + 1;
            int height = bottommost - topmost + 1;

            //CREATE MOLECULE-ONLY BOARD
            String[][] trimmedBoard = new String[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    trimmedBoard[i][j] = board[topmost + i][leftmost + j];
                }
            }

            Random random = new Random();
            int randomNumber = random.nextInt();
            if (randomNumber % 2 == 0) {
                return rotate90(trimmedBoard, height, width);
            }
            return trimmedBoard;

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return new String[0][0];
        }
    }

    private String[][] rotate90(String[][] matrix, int rows, int cols) {
        String[][] rotated = new String[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = matrix[i][j];
                switch(rotated[j][rows - 1 - i]) {
                    case "-":
                        rotated[j][rows - 1 - i] = "|";
                        break;
                    case "_":
                        rotated[j][rows - 1 - i] = "|";
                        break;
                    case "--":
                        rotated[j][rows - 1 - i] = "‖";
                        break;
                    case "__":
                        rotated[j][rows - 1 - i] = "‖";
                        break;
                    case "=":
                        rotated[j][rows - 1 - i] = "‖";
                        break;
                    case "---":
                        rotated[j][rows - 1 - i] = "⦀";
                        break;
                    case "___":
                        rotated[j][rows - 1 - i] = "⦀";
                        break;
                    case "≡":
                        rotated[j][rows - 1 - i] = "⦀";
                        break;
                    case "|":
                        rotated[j][rows - 1 - i] = "-";
                        break;
                    case "||":
                        rotated[j][rows - 1 - i] = "=";
                        break;
                    case "|||":
                        rotated[j][rows - 1 - i] = "≡";
                        break;
                    default:
                        break;
                }
            }
        }
        return rotated;
    }

    public void printMatrix(String[][] matrix) {
        for (String[] row : matrix) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public List<List<String>> getInfos(int moleculeId) {
        List<List<String>> atomInfos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(moleculeId);
            if (sheet == null) {
                System.out.println("Invalid molecule ID.");
                return atomInfos;
            }

            int startRow = 2; //Q3 = ROW INDEX 2
            int startCol = 16; //COLUMN Q
            int endCol = 22; //COLUMN W
            
            for (int rowIdx = startRow; rowIdx < sheet.getLastRowNum(); rowIdx++) {
                XSSFRow row = sheet.getRow(rowIdx);
                if (row == null || row.getCell(startCol) == null || row.getCell(startCol).toString().isEmpty()) {
                    break; //STOP IF EMPTY ROW IS FOUND
                }

                List<String> rowData = new ArrayList<>();
                for (int colIdx = startCol; colIdx <= endCol; colIdx++) {
                    XSSFCell cell = row.getCell(colIdx);
                    rowData.add(cell != null ? cell.toString() : "");
                }
                atomInfos.add(rowData);
            }
        } catch (Exception e) {
            System.out.println("Error reading atom info: " + e.getMessage());
        }
        return atomInfos;
    }

    public void printInfos(List<List<String>> atomInfos) {
        for (int i = 0; i < atomInfos.size(); i++) {
            System.out.println("ROW: " + (i + 1));
            List<String> row = atomInfos.get(i);
            StringBuilder rowOutput = new StringBuilder();
            for (int j = 0; j < row.size(); j++) {
                rowOutput.append(j).append(" - ").append(row.get(j));
                if (j < row.size() - 1) {
                    rowOutput.append(" | ");
                }
            }
            System.out.println(rowOutput);
        }
    }
}