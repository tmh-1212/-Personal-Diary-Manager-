/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package DiaryManager;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.*;

public class DiaryBackup {
    
    public static String createBackup(String entriesDirPath, String backupDirPath) throws IOException {
        Path entriesDir = Paths.get(entriesDirPath);
        if (!Files.exists(entriesDir) || !Files.isDirectory(entriesDir)) {
            throw new IOException("Entries directory not found: " + entriesDirPath);
        }
        
        // Create backup directory if it doesn't exist
        Path backupDir = Paths.get(backupDirPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }
        
        // Create backup filename with timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String backupFilename = "diary_backup_" + LocalDateTime.now().format(formatter) + ".zip";
        Path backupFile = backupDir.resolve(backupFilename);
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile.toFile()))) {
            Files.walk(entriesDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String entryName = entriesDir.relativize(file).toString();
                        zos.putNextEntry(new ZipEntry(entryName));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Error adding file to backup: " + file);
                    }
                });
        }
        
        return backupFile.toString();
    }
    
    public static void restoreBackup(String backupFilePath, String restoreDirPath) throws IOException {
        Path backupFile = Paths.get(backupFilePath);
        if (!Files.exists(backupFile)) {
            throw new IOException("Backup file not found: " + backupFilePath);
        }
        
        Path restoreDir = Paths.get(restoreDirPath);
        if (!Files.exists(restoreDir)) {
            Files.createDirectories(restoreDir);
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = restoreDir.resolve(entry.getName());
                
                // Create parent directories if they don't exist
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
}