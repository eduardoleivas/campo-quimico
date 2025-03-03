package com.campoquimico.handlers.resourceHandlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceHandler {
    
    public String getResourcePath(String resource) {
        try (InputStream input = getClass().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalArgumentException("Resource not found: " + resource);
            }

            File tempFile = File.createTempFile("temp", resource.substring(resource.lastIndexOf('.')));
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return tempFile.toURI().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resource, e);
        }
    }

    public String getExcelFilePath(String resource) {
        try (InputStream input = getClass().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalArgumentException("Excel file not found: " + resource);
            }
    
            // Create a temporary .xlsx file
            File tempFile = File.createTempFile("tempExcel", ".xlsx");
            tempFile.deleteOnExit();
    
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
    
            // Return the absolute path for POI
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Excel file: " + resource, e);
        }
    }
}
