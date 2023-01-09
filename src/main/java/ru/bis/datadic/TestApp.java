package ru.bis.datadic;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.isRegularFile;

public class TestApp {
    Logger logger = LogManager.getLogger(TestApp.class);
    SchemeFileList schemes = new SchemeFileList();

    void test() {
        logger.log(Level.INFO, "0000: Spring application is alive.");

        String currentDirectory = System.getProperty("user.dir");
        logger.log(Level.INFO, "0001: Current directory: " + currentDirectory);

        SchemeFile schemeBase = new SchemeFile(currentDirectory + "\\target\\test1\\" + "cbr_ed_basetypes_v2018.3.0.xsd", schemes);
        schemes.add(schemeBase);
        schemeBase.log();

        System.out.println("==============================================================================");

        SchemeFile schemeLeaf = new SchemeFile(currentDirectory + "\\target\\test1\\" + "cbr_ed_leaftypes_v2022.4.1.xsd", schemes);
        schemes.add(schemeLeaf);
        schemeLeaf.log();

        System.out.println("==============================================================================");

        SchemeFile schemeObjects = new SchemeFile(currentDirectory + "\\target\\test1\\" + "cbr_ed_objects_v2022.4.1.xsd", schemes);
        schemes.add(schemeObjects);
        schemeObjects.log();

        System.out.println("==============================================================================");

        SchemeFile schemeED101 = new SchemeFile(currentDirectory + "\\target\\test1\\" + "cbr_ed101_v2022.4.1.xsd", schemes);
        schemes.add(schemeED101);
        schemeObjects.log();

        System.out.println("==============================================================================");

/*
        SchemeFile schemeTest = new SchemeFile(currentDirectory + "\\" + "SBC0_512.xsd", schemes);
        schemes.add(schemeTest);
        schemeTest.log();
*/
        schemes.checkRefTypes();

        System.out.println("==============================================================================");

//        schemeBase.writeFile(currentDirectory + "\\" + "base.xsd");
//        SAXExample.main();

    }

    void readDir(String relativePath) {
        String currentDirectory = System.getProperty("user.dir");
        logger.log(Level.INFO, "0001: Current directory: " + currentDirectory);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(currentDirectory + relativePath))) {
            for (Path path : directoryStream) {
                if (isRegularFile(path)) {
                    Path onlyFile = path.getFileName();
                    if (onlyFile.toString().lastIndexOf("xsd") > 0) { // Process only xsd files
                        logger.log(Level.INFO, "0002: Reading file: " + currentDirectory + relativePath + "\\" + onlyFile);
                        SchemeFile schemeFile = new SchemeFile(currentDirectory + relativePath + "\\" + onlyFile, schemes);
                        schemes.add(schemeFile);
                        schemeFile.log();
                    }
                    else {
                        logger.log(Level.WARN, "0003: Ignore file: " + currentDirectory + relativePath + "\\" + onlyFile);
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error("0002: Error while reading file " + e.getMessage());
        }
        schemes.checkRefTypes();
        writeXLS();
    }

    /**
     * Writes XLS
     */
    void writeXLS() {
        String currentDirectory = System.getProperty("user.dir");
        String fileName = currentDirectory + "\\" + "result.xlsx";
        logger.log(Level.INFO, "0011: Write result to: " + fileName);
        Workbook book = new XSSFWorkbook();
        for (Integer id : schemes.getMap().keySet()) {
            String sheetName = schemes.getMap().get(id).getFileName();
            Integer rowCount = 0;
            if (sheetName != null) { // Do not output base type list
                Sheet sheet = book.createSheet(sheetName.substring(sheetName.lastIndexOf("\\") + 1));
                sheet.setRowSumsBelow(false); // Set group header at the top of group
                // Header
                Row row = sheet.createRow(rowCount); // Numbers of rows and cells starts with 0
                Cell type = row.createCell(0);
                type.setCellValue("Type");
                Cell name = row.createCell(1);
                name.setCellValue("Name");
                Cell req = row.createCell(2);
                req.setCellValue("Req");
                Cell base = row.createCell(3);
                base.setCellValue("Base type");
                Cell desc = row.createCell(4);
                desc.setCellValue("Description");
                Cell cons = row.createCell(5);
                cons.setCellValue("Constraints");
                rowCount++;
                // Output simple types
                rowCount = schemes.getMap().get(id).getSimpleList().outXLS(rowCount, sheet);
                // Output complex types
                rowCount = schemes.getMap().get(id).getComplexList().outXLS(rowCount, sheet);
                // Output elements
                rowCount = schemes.getMap().get(id).getElementList().outXLS(rowCount, sheet);
                // Set size
                for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);
            }
        }
        try {
            book.write(new FileOutputStream(fileName)); // Write to a file
            book.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
