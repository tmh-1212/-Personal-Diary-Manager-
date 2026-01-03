/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package DiaryManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filename;
    private LocalDateTime timestamp;
    private String content;
    
    public DiaryEntry(LocalDateTime timestamp, String content) {
        this.timestamp = timestamp;
        this.content = content;
        this.filename = generateFilename(timestamp);
    }
    
    private String generateFilename(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        return "diary_" + timestamp.format(formatter) + ".txt";
    }
    
    public String getFilename() { return filename; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Override
    public String toString() {
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(displayFormatter) + " - " + 
               content.substring(0, Math.min(content.length(), 50)) + 
               (content.length() > 50 ? "..." : "");
    }
}