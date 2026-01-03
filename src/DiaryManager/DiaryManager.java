
package DiaryManager;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DiaryManager {
    private static final String ENTRIES_DIR = "entries";
    private static final String CONFIG_FILE = "diary_config.ser";
    private static final String BACKUP_DIR = "backups";
    private static DiaryConfig config;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        initializeApplication();
        
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    writeEntry();
                    break;
                case "2":
                    readEntry();
                    break;
                case "3":
                    listEntries();
                    break;
                case "4":
                    searchEntries();
                    break;
                case "5":
                    deleteEntry();
                    break;
                case "6":
                    createBackup();
                    break;
                case "7":
                    restoreBackup();
                    break;
                case "8":
                    clearAllEntries();
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye! Your diary has been saved.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        saveConfig();
        scanner.close();
    }
    
    private static void initializeApplication() {
        try {
            // Create directories if they don't exist
            Files.createDirectories(Paths.get(ENTRIES_DIR));
            Files.createDirectories(Paths.get(BACKUP_DIR));
            
            // Load configuration
            loadConfig();
            
            System.out.println("====================================");
            System.out.println("     PERSONAL DIARY MANAGER");
            System.out.println("====================================");
            System.out.println("Total entries: " + config.getTotalEntries());
            if (config.getLastBackupDate() != null) {
                System.out.println("Last backup: " + config.getLastBackupDate());
            }
            System.out.println();
            
        } catch (IOException e) {
            System.err.println("Error initializing application: " + e.getMessage());
            config = new DiaryConfig();
        }
    }
    
    private static void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(configPath))) {
                config = (DiaryConfig) ois.readObject();
                System.out.println("Configuration loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading config, starting fresh: " + e.getMessage());
                config = new DiaryConfig();
            }
        } else {
            config = new DiaryConfig();
        }
    }
    
    private static void saveConfig() {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(CONFIG_FILE)))) {
            oos.writeObject(config);
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Write New Entry");
        System.out.println("2. Read an Entry");
        System.out.println("3. List All Entries");
        System.out.println("4. Search Entries");
        System.out.println("5. Delete an Entry");
        System.out.println("6. Create Backup");
        System.out.println("7. Restore from Backup");
        System.out.println("8. Clear All Entries");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
    }
    
    private static void writeEntry() {
        System.out.println("\n=== WRITE NEW ENTRY ===");
        System.out.println("Type your entry (enter 'END' on a new line to finish):");
        
        StringBuilder content = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            content.append(line).append("\n");
        }
        
        if (content.length() > 0) {
            LocalDateTime now = LocalDateTime.now();
            DiaryEntry entry = new DiaryEntry(now, content.toString().trim());
            
            Path entryFile = Paths.get(ENTRIES_DIR, entry.getFilename());
            
            try (BufferedWriter writer = Files.newBufferedWriter(entryFile, 
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                writer.write(content.toString().trim());
                config.addDiaryFile(entry.getFilename());
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                System.out.println("Entry saved successfully!");
                System.out.println("Filename: " + entry.getFilename());
                System.out.println("Timestamp: " + now.format(formatter));
            } catch (IOException e) {
                System.err.println("Error saving entry: " + e.getMessage());
            }
        } else {
            System.out.println("Entry was empty, not saved.");
        }
    }
    
    private static void listEntries() {
        System.out.println("\n=== ALL DIARY ENTRIES ===");
        
        List<String> files = getDiaryFiles();
        if (files.isEmpty()) {
            System.out.println("No entries found.");
            return;
        }
        
        for (int i = 0; i < files.size(); i++) {
            try {
                Path filePath = Paths.get(ENTRIES_DIR, files.get(i));
                String firstLine = Files.lines(filePath)
                    .findFirst()
                    .orElse("");
                System.out.printf("%d. %s - %s%n", i + 1, files.get(i).replace(".txt", ""), 
                    firstLine.substring(0, Math.min(firstLine.length(), 50)));
            } catch (IOException e) {
                System.out.printf("%d. %s - [Error reading file]%n", i + 1, files.get(i));
            }
        }
    }
    
    private static void readEntry() {
        List<String> files = getDiaryFiles();
        if (files.isEmpty()) {
            System.out.println("No entries found.");
            return;
        }
        
        System.out.println("\n=== READ ENTRY ===");
        for (int i = 0; i < files.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, files.get(i));
        }
        
        System.out.print("Select entry number to read: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > files.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            String selectedFile = files.get(choice - 1);
            Path filePath = Paths.get(ENTRIES_DIR, selectedFile);
            
            System.out.println("\n=== " + selectedFile.replace(".txt", "") + " ===");
            System.out.println("=".repeat(50));
            
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
            
            System.out.println("=".repeat(50));
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private static void searchEntries() {
        System.out.println("\n=== SEARCH ENTRIES ===");
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }
        
        List<String> files = getDiaryFiles();
        List<String> results = new ArrayList<>();
        
        System.out.println("\nSearching for: \"" + keyword + "\"");
        System.out.println("=".repeat(50));
        
        for (String filename : files) {
            try {
                Path filePath = Paths.get(ENTRIES_DIR, filename);
                String content = Files.lines(filePath)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining("\n"));
                
                if (content.contains(keyword)) {
                    results.add(filename);
                    
                    // Show preview
                    String preview = Files.lines(filePath)
                        .limit(3)
                        .collect(Collectors.joining("\n"));
                    
                    System.out.println("Found in: " + filename);
                    System.out.println("Preview:");
                    System.out.println(preview.substring(0, Math.min(preview.length(), 100)) + "...");
                    System.out.println("-".repeat(50));
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + filename);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("No entries found containing: \"" + keyword + "\"");
        } else {
            System.out.println("Total matches: " + results.size());
        }
    }
    
    private static void deleteEntry() {
        List<String> files = getDiaryFiles();
        if (files.isEmpty()) {
            System.out.println("No entries to delete.");
            return;
        }
        
        System.out.println("\n=== DELETE ENTRY ===");
        for (int i = 0; i < files.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, files.get(i));
        }
        
        System.out.print("Select entry number to delete (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                return;
            }
            
            if (choice < 1 || choice > files.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            String selectedFile = files.get(choice - 1);
            System.out.print("Are you sure you want to delete \"" + selectedFile + "\"? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("yes")) {
                try {
                    Files.delete(Paths.get(ENTRIES_DIR, selectedFile));
                    config.removeDiaryFile(selectedFile);
                    System.out.println("Entry deleted successfully.");
                } catch (IOException e) {
                    System.err.println("Error deleting file: " + e.getMessage());
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private static void createBackup() {
        System.out.println("\n=== CREATE BACKUP ===");
        try {
            String backupFile = DiaryBackup.createBackup(ENTRIES_DIR, BACKUP_DIR);
            config.setLastBackupDate(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            System.out.println("Backup created successfully!");
            System.out.println("Backup file: " + backupFile);
            System.out.println("Entries backed up: " + config.getTotalEntries());
            
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }
    
    private static void restoreBackup() {
        System.out.println("\n=== RESTORE FROM BACKUP ===");
        
        try {
            List<Path> backups = Files.list(Paths.get(BACKUP_DIR))
                .filter(path -> path.toString().endsWith(".zip"))
                .sorted()
                .collect(Collectors.toList());
            
            if (backups.isEmpty()) {
                System.out.println("No backup files found.");
                return;
            }
            
            System.out.println("Available backups:");
            for (int i = 0; i < backups.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, backups.get(i).getFileName());
            }
            
            System.out.print("Select backup to restore (or 0 to cancel): ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 0) return;
            
            if (choice < 1 || choice > backups.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            Path selectedBackup = backups.get(choice - 1);
            System.out.print("This will overwrite current entries. Continue? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("yes")) {
                // Clear current entries
                Files.list(Paths.get(ENTRIES_DIR))
                    .forEach(path -> {
                        try { Files.delete(path); } catch (IOException e) {}
                    });
                config.clear();
                
                // Restore from backup
                DiaryBackup.restoreBackup(selectedBackup.toString(), ENTRIES_DIR);
                
                // Reload entries into config
                Files.list(Paths.get(ENTRIES_DIR))
                    .forEach(path -> config.addDiaryFile(path.getFileName().toString()));
                
                System.out.println("Backup restored successfully!");
                System.out.println("Entries restored: " + config.getTotalEntries());
            } else {
                System.out.println("Restore cancelled.");
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error restoring backup: " + e.getMessage());
        }
    }
    
    private static void clearAllEntries() {
        System.out.println("\n=== CLEAR ALL ENTRIES ===");
        System.out.print("WARNING: This will delete ALL diary entries. Continue? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("yes")) {
            try {
                Files.list(Paths.get(ENTRIES_DIR))
                    .forEach(path -> {
                        try { Files.delete(path); } catch (IOException e) {}
                    });
                config.clear();
                System.out.println("All entries have been deleted.");
            } catch (IOException e) {
                System.err.println("Error clearing entries: " + e.getMessage());
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }
    
    private static List<String> getDiaryFiles() {
        try {
            return Files.list(Paths.get(ENTRIES_DIR))
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(name -> name.startsWith("diary_") && name.endsWith(".txt"))
                .sorted(Comparator.reverseOrder()) // Show newest first
                .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}