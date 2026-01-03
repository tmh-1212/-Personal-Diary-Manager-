package com.diary;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.*;

public class DiaryApp extends Application {
    
    private VBox entriesContainer;
    private TextArea editorArea;
    private Label dateLabel;
    private Label timeLabel;
    private Label entryTitleLabel;
    private GridPane calendarGrid;
    private Label monthYearLabel;
    private LocalDate currentDate = LocalDate.now();
    private List<DiaryEntry> entries = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: #f5f8fa;");
        
        // ===== LEFT PANEL - ENTRIES LIST =====
        VBox leftPanel = createLeftPanel();
        mainPane.setLeft(leftPanel);
        
        // ===== CENTER PANEL - CALENDAR =====
        VBox centerPanel = createCenterPanel();
        mainPane.setCenter(centerPanel);
        
        // ===== RIGHT PANEL - EDITOR =====
        VBox rightPanel = createRightPanel();
        mainPane.setRight(rightPanel);
        
        // Add sample entries
        addSampleEntries();
        
        // Scene setup
        Scene scene = new Scene(mainPane, 1000, 600);
        primaryStage.setTitle("My Diary");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(350);
        leftPanel.setPadding(new Insets(25, 20, 20, 20));
        leftPanel.setStyle("-fx-background-color: white;");
        
        // Title
        Label title = new Label("Entries");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-padding: 8 15;");
        searchField.setPrefHeight(35);
        
        // Entries container with scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        entriesContainer = new VBox(10);
        entriesContainer.setPadding(new Insets(5, 0, 0, 0));
        
        scrollPane.setContent(entriesContainer);
        
        leftPanel.getChildren().addAll(title, searchField, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return leftPanel;
    }
    
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(20);
        centerPanel.setPadding(new Insets(25, 20, 20, 20));
        centerPanel.setPrefWidth(300);
        
        // App title
        Label appTitle = new Label("My Diary");
        appTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        appTitle.setTextFill(Color.web("#2c3e50"));
        
        // Calendar section
        VBox calendarSection = new VBox(15);
        
        // Month navigation
        HBox monthNav = new HBox(10);
        monthNav.setAlignment(Pos.CENTER);
        
        Button prevBtn = new Button("‹");
        prevBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #495057;");
        prevBtn.setOnAction(e -> changeMonth(-1));
        
        monthYearLabel = new Label("October 2024");
        monthYearLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        monthYearLabel.setTextFill(Color.web("#2c3e50"));
        
        Button nextBtn = new Button("›");
        nextBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #495057;");
        nextBtn.setOnAction(e -> changeMonth(1));
        
        monthNav.getChildren().addAll(prevBtn, monthYearLabel, nextBtn);
        
        // Calendar grid
        calendarGrid = new GridPane();
        calendarGrid.setHgap(8);
        calendarGrid.setVgap(8);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e9ecef;");
        
        updateCalendar();
        
        calendarSection.getChildren().addAll(monthNav, calendarGrid);
        
        centerPanel.getChildren().addAll(appTitle, calendarSection);
        
        return centerPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(25, 25, 25, 25));
        rightPanel.setPrefWidth(600);
        
        // Entry header
        VBox headerBox = new VBox(5);
        
        HBox dateTimeBox = new HBox(15);
        dateTimeBox.setAlignment(Pos.CENTER_LEFT);
        
        dateLabel = new Label("Tuesday, October 26");
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        dateLabel.setTextFill(Color.web("#6c757d"));
        
        timeLabel = new Label("8:15 AM");
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        timeLabel.setTextFill(Color.web("#6c757d"));
        
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);
        
        entryTitleLabel = new Label("Morning Reflections");
        entryTitleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        entryTitleLabel.setTextFill(Color.web("#2c3e50"));
        
        headerBox.getChildren().addAll(dateTimeBox, entryTitleLabel);
        
        // Editor area
        editorArea = new TextArea();
        editorArea.setWrapText(true);
        editorArea.setStyle("""
            -fx-font-size: 16px;
            -fx-font-family: 'Georgia', serif;
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-control-inner-background: white;
            -fx-padding: 15 0 0 0;
        """);
        editorArea.setPrefHeight(400);
        
        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        saveBtn.setOnAction(e -> saveEntry());
        
        Button newBtn = new Button("New Entry");
        newBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        newBtn.setOnAction(e -> createNewEntry());
        
        toolbar.getChildren().addAll(saveBtn, newBtn);
        
        rightPanel.getChildren().addAll(headerBox, editorArea, toolbar);
        VBox.setVgrow(editorArea, Priority.ALWAYS);
        
        return rightPanel;
    }
    
    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        
        // Day headers
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            dayLabel.setTextFill(Color.web("#868e96"));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(35, 35);
            calendarGrid.add(dayLabel, i, 0);
        }
        
        // Get first day of month
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0
        
        // Fill calendar
        int row = 1;
        int col = dayOfWeek;
        
        for (int day = 1; day <= currentDate.lengthOfMonth(); day++) {
            StackPane cell = createCalendarCell(day);
            calendarGrid.add(cell, col, row);
            
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
        
        // Update month label
        monthYearLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
    }
    
    private StackPane createCalendarCell(int day) {
        StackPane cell = new StackPane();
        cell.setPrefSize(35, 35);
        
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        
        // Highlight current day
        LocalDate cellDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
        if (cellDate.equals(LocalDate.now())) {
            cell.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 17.5;");
        } else {
            cell.setStyle("-fx-background-color: transparent;");
        }
        
        // Mark days with entries (example: 1, 8, 14, 25, 26 have entries)
        if (hasEntriesOnDay(day)) {
            Label dot = new Label("•");
            dot.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            dot.setTextFill(Color.web("#28a745"));
            dot.setTranslateY(8);
            cell.getChildren().addAll(dayLabel, dot);
        } else {
            cell.getChildren().add(dayLabel);
        }
        
        // Click handler
        cell.setOnMouseClicked(e -> {
            selectDate(day);
        });
        
        // Hover effect
        cell.setOnMouseEntered(e -> {
            if (!cellDate.equals(LocalDate.now())) {
                cell.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 17.5;");
            }
        });
        
        cell.setOnMouseExited(e -> {
            if (!cellDate.equals(LocalDate.now())) {
                cell.setStyle("-fx-background-color: transparent;");
            }
        });
        
        return cell;
    }
    
    private boolean hasEntriesOnDay(int day) {
        // Example: certain days have entries
        return day == 1 || day == 8 || day == 14 || day == 25 || day == 26;
    }
    
    private void selectDate(int day) {
        LocalDate selectedDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
        dateLabel.setText(selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        
        // Load entries for selected date
        loadEntriesForDate(selectedDate);
    }
    
    private void loadEntriesForDate(LocalDate date) {
        // Filter entries for this date
        List<DiaryEntry> dateEntries = new ArrayList<>();
        for (DiaryEntry entry : entries) {
            if (entry.getDate().equals(date)) {
                dateEntries.add(entry);
            }
        }
        
        // Display entries
        if (!dateEntries.isEmpty()) {
            DiaryEntry firstEntry = dateEntries.get(0);
            entryTitleLabel.setText(firstEntry.getTitle());
            timeLabel.setText(firstEntry.getTime());
            editorArea.setText(firstEntry.getContent());
        } else {
            entryTitleLabel.setText("New Entry");
            timeLabel.setText("Now");
            editorArea.clear();
        }
    }
    
    private void addSampleEntries() {
        // Sample entry 1
        DiaryEntry entry1 = new DiaryEntry(
            LocalDate.of(2024, 10, 26),
            "8:15 AM",
            "Morning Reflections",
            "Today started with a quiet coffee on the porch. The air was crisp, and the leaves are finally starting to turn that brilliant shade of orange. It's my favorite time of year. I feel a sense of calm and opportunity washing over me..."
        );
        entries.add(entry1);
        
        // Sample entry 2
        DiaryEntry entry2 = new DiaryEntry(
            LocalDate.of(2024, 10, 26),
            "1:30 PM",
            "A Productive Afternoon",
            "Managed to finally tackle that project I've been putting off. The feeling of checking it off my list is incredible. It's amazing how much you can get done when you just focus for a couple of hours without distractions."
        );
        entries.add(entry2);
        
        // Sample entry 3 (from previous day)
        DiaryEntry entry3 = new DiaryEntry(
            LocalDate.of(2024, 10, 25),
            "7:00 PM",
            "Gratitude Journal",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
        );
        entries.add(entry3);
        
        // Update entries list display
        updateEntriesList();
        
        // Load today's entries
        selectDate(26);
    }
    
    private void updateEntriesList() {
        entriesContainer.getChildren().clear();
        
        // Group entries by date
        Map<LocalDate, List<DiaryEntry>> entriesByDate = new TreeMap<>(Collections.reverseOrder());
        for (DiaryEntry entry : entries) {
            entriesByDate.computeIfAbsent(entry.getDate(), k -> new ArrayList<>()).add(entry);
        }
        
        // Display grouped entries
        for (Map.Entry<LocalDate, List<DiaryEntry>> dateEntry : entriesByDate.entrySet()) {
            LocalDate date = dateEntry.getKey();
            List<DiaryEntry> dateEntries = dateEntry.getValue();
            
            // Date header
            TextFlow dateHeader = createDateHeader(date);
            entriesContainer.getChildren().add(dateHeader);
            
            // Entries for this date
            for (DiaryEntry entry : dateEntries) {
                VBox entryPreview = createEntryPreview(entry);
                entriesContainer.getChildren().add(entryPreview);
            }
        }
    }
    
    private TextFlow createDateHeader(LocalDate date) {
        TextFlow textFlow = new TextFlow();
        
        Text dateText = new Text(date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        dateText.setFill(Color.web("#2c3e50"));
        
        // Add separator line after date
        textFlow.getChildren().add(dateText);
        
        return textFlow;
    }
    
    private VBox createEntryPreview(DiaryEntry entry) {
        VBox preview = new VBox(5);
        preview.setPadding(new Insets(10, 0, 10, 0));
        preview.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Time
        Label timeLabel = new Label(entry.getTime());
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        timeLabel.setTextFill(Color.web("#6c757d"));
        timeLabel.setMinWidth(70);
        
        // Title with bullet
        HBox titleBox = new HBox(5);
        
        Text bullet = new Text("• ");
        bullet.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        bullet.setFill(Color.web("#495057"));
        
        Text title = new Text(entry.getTitle());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setFill(Color.web("#212529"));
        
        titleBox.getChildren().addAll(bullet, title);
        
        header.getChildren().addAll(timeLabel, titleBox);
        
        // Preview text
        String previewText = entry.getContent();
        if (previewText.length() > 80) {
            previewText = previewText.substring(0, 80) + "...";
        }
        Label contentLabel = new Label(previewText);
        contentLabel.setFont(Font.font("Segoe UI", 13));
        contentLabel.setTextFill(Color.web("#495057"));
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(280);
        
        preview.getChildren().addAll(header, contentLabel);
        
        // Click handler
        preview.setOnMouseClicked(e -> {
            entryTitleLabel.setText(entry.getTitle());
            timeLabel.setText(entry.getTime());
            editorArea.setText(entry.getContent());
            dateLabel.setText(entry.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        });
        
        // Hover effect
        preview.setOnMouseEntered(e -> {
            preview.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-padding: 10 0 10 0;");
        });
        
        preview.setOnMouseExited(e -> {
            preview.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-padding: 10 0 10 0;");
        });
        
        return preview;
    }
    
    private void changeMonth(int delta) {
        currentDate = currentDate.plusMonths(delta);
        updateCalendar();
    }
    
    private void saveEntry() {
        String title = entryTitleLabel.getText();
        String content = editorArea.getText();
        String time = timeLabel.getText();
        
        // Parse date from dateLabel
        String dateStr = dateLabel.getText();
        LocalDate date = LocalDate.now(); // Default to today
        
        try {
            // Try to parse date (remove day of week)
            String[] parts = dateStr.split(", ");
            if (parts.length > 1) {
                dateStr = parts[1];
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MMMM d"));
                date = date.withYear(currentDate.getYear());
            }
        } catch (Exception e) {
            date = LocalDate.now();
        }
        
        // Create or update entry
        DiaryEntry newEntry = new DiaryEntry(date, time, title, content);
        
        // Check if entry already exists
        boolean exists = false;
        for (int i = 0; i < entries.size(); i++) {
            DiaryEntry existing = entries.get(i);
            if (existing.getDate().equals(date) && 
                existing.getTime().equals(time) && 
                existing.getTitle().equals(title)) {
                entries.set(i, newEntry);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            entries.add(newEntry);
        }
        
        updateEntriesList();
        
        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Entry saved successfully!");
        alert.show();
    }
    
    private void createNewEntry() {
        LocalDate today = LocalDate.now();
        dateLabel.setText(today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        timeLabel.setText("Now");
        entryTitleLabel.setText("New Entry");
        editorArea.clear();
        editorArea.setPromptText("Start writing your thoughts...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

class DiaryEntry {
    private LocalDate date;
    private String time;
    private String title;
    private String content;
    
    public DiaryEntry(LocalDate date, String time, String title, String content) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.content = content;
    }
    
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    
    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}