/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package DiaryManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiaryConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> diaryFiles;
    private String lastBackupDate;
    private int totalEntries;
    
    public DiaryConfig() {
        this.diaryFiles = new ArrayList<>();
    }
    
    public void addDiaryFile(String filename) {
        if (!diaryFiles.contains(filename)) {
            diaryFiles.add(filename);
            totalEntries = diaryFiles.size();
        }
    }
    
    public void removeDiaryFile(String filename) {
        diaryFiles.remove(filename);
        totalEntries = diaryFiles.size();
    }
    
    public List<String> getDiaryFiles() { return diaryFiles; }
    public String getLastBackupDate() { return lastBackupDate; }
    public void setLastBackupDate(String lastBackupDate) { this.lastBackupDate = lastBackupDate; }
    public int getTotalEntries() { return totalEntries; }
    
    public void clear() {
        diaryFiles.clear();
        totalEntries = 0;
        lastBackupDate = null;
    }
}