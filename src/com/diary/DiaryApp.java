package com.diary;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DiaryApp extends Application {
    
    private VBox entriesContainer;
    private TextArea editorArea;
    private Label dateLabel;
    private Label timeLabel;
    private TextField titleField;
    private GridPane calendarGrid;
    private Label monthYearLabel;
    private LocalDate currentDate = LocalDate.now();
    private LocalDate selectedCalendarDate = LocalDate.now();
    private List<DiaryEntry> entries = new ArrayList<>();
    private boolean darkMode = false;
    private Stage primaryStage;
    private DiaryEntry selectedEntry = null;
    private DiaryEntry currentDraftEntry = null; // Track draft entries
    
    // UI Components
    private ComboBox<String> categoryCombo;
    
    // User Profile
    private UserProfile currentUser = new UserProfile();
    
    // Profile components
    private Circle profilePhotoCircle;
    private ImageView profileImageView;
    private Label userNameLabel;
    private Label userStatsLabel;
    
    // Search components
    private TextField searchField;
    private Label resultsCountLabel;
    
    // Timeline for updating time
    private Timeline timeUpdater;
    
    // Theme colors
    private Color primaryLight = Color.web("#2c3e50");
    private Color backgroundLight = Color.web("#f5f8fa");
    private Color panelLight = Color.web("#ffffff");
    private Color borderLight = Color.web("#e9ecef");
    private Color textLight = Color.web("#212529");
    private Color mutedLight = Color.web("#6c757d");
    private Color accentLight = Color.web("#007bff");
    private Color successLight = Color.web("#28a745");
    private Color warningLight = Color.web("#ffc107");
    private Color dangerLight = Color.web("#dc3545");
    
    private Color primaryDark = Color.web("#e0e0e0");
    private Color backgroundDark = Color.web("#1a1a1a");
    private Color panelDark = Color.web("#2d2d2d");
    private Color borderDark = Color.web("#404040");
    private Color textDark = Color.web("#ffffff");
    private Color mutedDark = Color.web("#909090");
    private Color accentDark = Color.web("#4dabf7");
    private Color successDark = Color.web("#20c997");
    private Color dangerDark = Color.web("#fa5252");
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        setupDefaultUserProfile();
        
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: " + toHex(backgroundLight) + ";");
        
        HBox topPanel = createTopPanel();
        mainPane.setTop(topPanel);
        
        // Main content with split pane
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPositions(0.25, 0.60); // Better distribution
        
        // Left panel - Calendar, profile and folders
        VBox leftPanel = createLeftPanel();
        
        // Center panel - Entries
        VBox centerPanel = createCenterPanel();
        
        // Right panel - Editor
        VBox rightPanel = createRightPanel();
        
        mainContent.getItems().addAll(leftPanel, centerPanel, rightPanel);
        
        mainPane.setCenter(mainContent);
        
        addSampleEntries();
        
        // Start time updater
        startTimeUpdater();
        
        Scene scene = new Scene(mainPane, 1200, 700); // Increased size for profile
        primaryStage.setTitle("My Diary");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setupDefaultUserProfile() {
        currentUser.setName("Tesfamikael Hailu");
        currentUser.setEmail("tesfamikealhailu@gmail.com");
        currentUser.setBio("Keeping memories alive through words.");
        currentUser.setJoinDate(LocalDate.of(2023, 1, 15));
        currentUser.setTotalEntries(entries.size());
        currentUser.setStreakDays(7);
        currentUser.setFavoriteCategory("Personal");
        // Default profile photo (emoji as fallback)
        currentUser.setProfilePhotoUrl(null);
    }
    
    private void startTimeUpdater() {
        // Update time every second
        timeUpdater = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateCurrentTime();
        }));
        timeUpdater.setCycleCount(Animation.INDEFINITE);
        timeUpdater.play();
    }
    
    private void updateCurrentTime() {
        if (timeLabel != null) {
            // Get current time with seconds
            LocalDateTime now = LocalDateTime.now();
            String timeString = now.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
            timeLabel.setText(timeString);
            
            // Update date if it's a new day and no entry is selected
            if (selectedEntry == null && currentDraftEntry == null) {
                String dateString = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
                dateLabel.setText(dateString);
            }
        }
    }
    
    private HBox createTopPanel() {
        HBox topPanel = new HBox(8);
        topPanel.setPadding(new Insets(8));
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.setStyle("-fx-background-color: " + toHex(panelLight) + ";");
        
        // App icon and title
        HBox titleBox = new HBox(6);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label appIcon = new Label("📓");
        appIcon.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label appTitle = new Label("My Diary");
        appTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        appTitle.setTextFill(primaryLight);
        
        titleBox.getChildren().addAll(appIcon, appTitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.setPrefWidth(180);
        searchField.setStyle(
            "-fx-background-color: #ffffff; -fx-text-fill: #212529; " +
            "-fx-background-radius: 12; -fx-padding: 4 12; -fx-font-size: 12;"
        );
        searchField.setOnAction(e -> performSearch());
        
        // Quick actions
        HBox quickActions = new HBox(6);
        quickActions.setAlignment(Pos.CENTER);
        
        Button newEntryBtn = new Button("+ New Entry");
        newEntryBtn.setStyle(
            "-fx-background-color: " + toHex(accentLight) + "; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 6 14; -fx-font-size: 12;"
        );
        newEntryBtn.setTooltip(new Tooltip("Create new entry"));
        newEntryBtn.setOnAction(e -> createNewEntry());
        
        // Theme toggle
        ToggleButton themeToggle = new ToggleButton(darkMode ? "☀️" : "🌙");
        themeToggle.setStyle(
            "-fx-background-color: #f1f3f5; -fx-text-fill: #495057; " +
            "-fx-background-radius: 12; -fx-padding: 4 8; -fx-min-width: 40;"
        );
        
        themeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            darkMode = newValue;
            applyThemeToAll();
        });
        
        quickActions.getChildren().addAll(newEntryBtn, searchField, themeToggle);
        
        topPanel.getChildren().addAll(titleBox, spacer, quickActions);
        return topPanel;
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(12);
        leftPanel.setPadding(new Insets(12));
        leftPanel.setPrefWidth(220);
        
        // ===== PROFILE PANEL =====
        VBox profilePanel = createProfilePanel();
        
        // ===== CALENDAR PANEL =====
        VBox calendarPanel = createCompactCalendarPanel();
        
        // ===== FOLDERS PANEL =====
        VBox foldersPanel = createCompactFoldersPanel();
        
        leftPanel.getChildren().addAll(profilePanel, calendarPanel, foldersPanel);
        VBox.setVgrow(foldersPanel, Priority.ALWAYS);
        
        return leftPanel;
    }
    
    private VBox createProfilePanel() {
        VBox profilePanel = new VBox(8);
        profilePanel.setPadding(new Insets(12));
        profilePanel.setStyle(
            "-fx-background-color: #ffffff; -fx-background-radius: 8; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1;"
        );
        
        // Profile photo
        StackPane photoContainer = new StackPane();
        photoContainer.setAlignment(Pos.CENTER);
        
        profilePhotoCircle = new Circle(40);
        profilePhotoCircle.setFill(Color.web("#e9ecef"));
        profilePhotoCircle.setStroke(accentLight);
        profilePhotoCircle.setStrokeWidth(2);
        
        profileImageView = new ImageView();
        profileImageView.setFitWidth(70);
        profileImageView.setFitHeight(70);
        profileImageView.setPreserveRatio(true);
        profileImageView.setClip(new Circle(35));
        
        // Set default profile photo (emoji)
        Label defaultPhoto = new Label("👤");
        defaultPhoto.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        StackPane.setAlignment(defaultPhoto, Pos.CENTER);
        
        photoContainer.getChildren().addAll(profilePhotoCircle, profileImageView, defaultPhoto);
        
        // User name
        userNameLabel = new Label(currentUser.getName());
        userNameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        userNameLabel.setTextFill(primaryLight);
        userNameLabel.setAlignment(Pos.CENTER);
        
        // User stats
        userStatsLabel = new Label(currentUser.getTotalEntries() + " entries • " + currentUser.getStreakDays() + " day streak");
        userStatsLabel.setFont(Font.font("Segoe UI", 10));
        userStatsLabel.setTextFill(mutedLight);
        userStatsLabel.setAlignment(Pos.CENTER);
        
        // Edit profile button
        Button editProfileBtn = new Button("Edit Profile");
        editProfileBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + toHex(accentLight) + "; " +
            "-fx-border-color: " + toHex(accentLight) + "; -fx-border-width: 1; " +
            "-fx-border-radius: 15; -fx-padding: 4 12; -fx-font-size: 11;"
        );
        editProfileBtn.setOnAction(e -> showEditProfileDialog());
        
        VBox profileInfo = new VBox(4);
        profileInfo.setAlignment(Pos.CENTER);
        profileInfo.getChildren().addAll(userNameLabel, userStatsLabel, editProfileBtn);
        
        profilePanel.getChildren().addAll(photoContainer, profileInfo);
        
        // Update profile display
        updateProfileDisplay();
        
        return profilePanel;
    }
    
    private VBox createCompactCalendarPanel() {
        VBox calendarPanel = new VBox(6);
        calendarPanel.setPadding(new Insets(12));
        calendarPanel.setStyle(
            "-fx-background-color: #f8f9fa; -fx-background-radius: 6; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 6; -fx-border-width: 1;"
        );
        
        // Calendar header
        HBox calendarHeader = new HBox(4);
        calendarHeader.setAlignment(Pos.CENTER);
        calendarHeader.setPadding(new Insets(0, 0, 6, 0));
        
        Button prevMonthBtn = new Button("◀");
        prevMonthBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 11; -fx-padding: 1 4;");
        prevMonthBtn.setOnAction(e -> navigateCalendar(-1));
        
        monthYearLabel = new Label();
        monthYearLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        monthYearLabel.setTextFill(primaryLight);
        
        Button nextMonthBtn = new Button("▶");
        nextMonthBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 11; -fx-padding: 1 4;");
        nextMonthBtn.setOnAction(e -> navigateCalendar(1));
        
        calendarHeader.getChildren().addAll(prevMonthBtn, monthYearLabel, nextMonthBtn);
        
        // Days of week headers
        GridPane daysHeader = new GridPane();
        daysHeader.setHgap(1);
        
        String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            dayLabel.setTextFill(mutedLight);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefWidth(20);
            daysHeader.add(dayLabel, i, 0);
        }
        
        // Calendar grid
        calendarGrid = new GridPane();
        calendarGrid.setHgap(1);
        calendarGrid.setVgap(1);
        
        updateCalendar();
        
        calendarPanel.getChildren().addAll(calendarHeader, daysHeader, calendarGrid);
        
        return calendarPanel;
    }
    
    private VBox createCompactFoldersPanel() {
        VBox foldersPanel = new VBox(6);
        foldersPanel.setPadding(new Insets(12));
        foldersPanel.setStyle(
            "-fx-background-color: #f8f9fa; -fx-background-radius: 6; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 6; -fx-border-width: 1;"
        );
        
        Label folderTitle = new Label("📁 Folders");
        folderTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        folderTitle.setTextFill(primaryLight);
        
        // Folder list
        VBox folderList = new VBox(2);
        
        String[] folderNames = {"All Entries", "Favorites", "Personal", "Work", "Health", "Travel", "Ideas"};
        for (String folderName : folderNames) {
            HBox folderItem = new HBox(4);
            folderItem.setAlignment(Pos.CENTER_LEFT);
            folderItem.setPadding(new Insets(6, 8, 6, 8));
            folderItem.setStyle("-fx-cursor: hand;");
            
            folderItem.setOnMouseEntered(e -> folderItem.setStyle(
                "-fx-background-color: #e9ecef; -fx-cursor: hand; -fx-background-radius: 4;"
            ));
            folderItem.setOnMouseExited(e -> folderItem.setStyle(
                "-fx-background-color: transparent; -fx-cursor: hand;"
            ));
            
            Label folderIcon = new Label(getFolderIcon(folderName));
            folderIcon.setFont(Font.font("Arial", 12));
            
            Label folderLabel = new Label(folderName);
            folderLabel.setFont(Font.font("Segoe UI", 12));
            folderLabel.setTextFill(primaryLight);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Entry count
            int count = entries.stream()
                .filter(e -> e.getCategory().equals(folderName) || 
                          (folderName.equals("All Entries")) ||
                          (folderName.equals("Favorites") && e.isFavorite()))
                .collect(Collectors.toList()).size();
            
            Label countBadge = new Label(String.valueOf(count));
            countBadge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            countBadge.setPadding(new Insets(1, 6, 1, 6));
            countBadge.setStyle(
                "-fx-background-color: " + toHex(accentLight) + "; " +
                "-fx-text-fill: white; -fx-background-radius: 8;"
            );
            countBadge.setVisible(count > 0);
            
            folderItem.getChildren().addAll(folderIcon, folderLabel, spacer, countBadge);
            
            final String currentFolder = folderName;
            folderItem.setOnMouseClicked(e -> filterByFolder(currentFolder));
            
            folderList.getChildren().add(folderItem);
        }
        
        foldersPanel.getChildren().addAll(folderTitle, folderList);
        
        return foldersPanel;
    }
    
    private String getFolderIcon(String folderName) {
        switch (folderName) {
            case "All Entries": return "📄";
            case "Favorites": return "⭐";
            case "Personal": return "👤";
            case "Work": return "💼";
            case "Health": return "🏥";
            case "Travel": return "✈️";
            case "Ideas": return "💡";
            default: return "📁";
        }
    }
    
    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(6);
        centerPanel.setPadding(new Insets(12));
        centerPanel.setPrefWidth(300);
        
        // Entries header
        HBox entriesHeader = new HBox(6);
        entriesHeader.setAlignment(Pos.CENTER_LEFT);
        entriesHeader.setPadding(new Insets(0, 0, 8, 0));
        
        Label entriesTitle = new Label("Recent Entries");
        entriesTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        entriesTitle.setTextFill(primaryLight);
        
        resultsCountLabel = new Label(entries.size() + " entries");
        resultsCountLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 11));
        resultsCountLabel.setTextFill(mutedLight);
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        entriesHeader.getChildren().addAll(entriesTitle, resultsCountLabel);
        
        // Entries list
        VBox entriesListPanel = new VBox();
        entriesListPanel.setStyle(
            "-fx-background-color: #ffffff; -fx-background-radius: 8; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1;"
        );
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        entriesContainer = new VBox(4);
        entriesContainer.setPadding(new Insets(6));
        
        scrollPane.setContent(entriesContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        entriesListPanel.getChildren().add(scrollPane);
        
        centerPanel.getChildren().addAll(entriesHeader, entriesListPanel);
        VBox.setVgrow(entriesListPanel, Priority.ALWAYS);
        
        return centerPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(6);
        rightPanel.setPadding(new Insets(12));
        rightPanel.setPrefWidth(350);
        
        // Editor header
        VBox editorHeader = new VBox(2);
        editorHeader.setPadding(new Insets(0, 0, 8, 0));
        
        dateLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        dateLabel.setTextFill(primaryLight);
        
        timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a")));
        timeLabel.setFont(Font.font("Segoe UI", 11));
        timeLabel.setTextFill(mutedLight);
        
        editorHeader.getChildren().addAll(dateLabel, timeLabel);
        
        // Editor panel
        VBox editorPanel = new VBox(6);
        editorPanel.setStyle(
            "-fx-background-color: #ffffff; -fx-background-radius: 8; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1;"
        );
        
        // Title and category
        HBox titleBox = new HBox(6);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(12, 12, 6, 12));
        
        titleField = new TextField();
        titleField.setPromptText("Entry title...");
        titleField.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleField.setStyle(
            "-fx-background-color: transparent; -fx-border-color: transparent; " +
            "-fx-text-fill: #212529; -fx-padding: 4;"
        );
        HBox.setHgrow(titleField, Priority.ALWAYS);
        
        // Title field listener - creates draft entry when typing starts
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                if (selectedEntry == null && currentDraftEntry == null) {
                    // Create new draft entry with EXACT current time
                    LocalDateTime now = LocalDateTime.now();
                    String exactTime = now.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
                    LocalDate today = now.toLocalDate();
                    
                    currentDraftEntry = new DiaryEntry(
                        today,
                        exactTime,
                        newValue,
                        "",
                        categoryCombo.getValue(),
                        false
                    );
                    
                    // Enable editor
                    editorArea.setDisable(false);
                    Platform.runLater(() -> editorArea.requestFocus());
                    
                } else if (selectedEntry != null) {
                    // Update existing entry
                    selectedEntry.setTitle(newValue);
                    performSearch();
                } else if (currentDraftEntry != null) {
                    // Update draft entry title
                    currentDraftEntry.setTitle(newValue);
                }
            }
        });
        
        // Category selector
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Personal", "Work", "Health", "Travel", "Ideas");
        categoryCombo.setValue("Personal");
        categoryCombo.setPrefWidth(90);
        categoryCombo.setStyle(
            "-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; " +
            "-fx-text-fill: #495057; -fx-font-size: 12;"
        );
        categoryCombo.setOnAction(e -> {
            if (currentDraftEntry != null) {
                currentDraftEntry.setCategory(categoryCombo.getValue());
            } else if (selectedEntry != null) {
                selectedEntry.setCategory(categoryCombo.getValue());
                performSearch();
            }
        });
        
        titleBox.getChildren().addAll(titleField, categoryCombo);
        
        // Editor area
        editorArea = new TextArea();
        editorArea.setWrapText(true);
        editorArea.setPromptText("Start writing here...\n\nTips:\n• Write freely about your thoughts\n• Use categories to organize\n• Add tags with #symbol");
        editorArea.setDisable(true);
        editorArea.setStyle(
            "-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif; " +
            "-fx-control-inner-background: #ffffff; -fx-text-fill: #212529; -fx-padding: 10;"
        );
        VBox.setVgrow(editorArea, Priority.ALWAYS);
        
        // Editor area listener - updates content
        editorArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentDraftEntry != null) {
                currentDraftEntry.setContent(newValue);
            } else if (selectedEntry != null) {
                selectedEntry.setContent(newValue);
            }
        });
        
        // Editor toolbar
        HBox editorToolbar = new HBox(8);
        editorToolbar.setPadding(new Insets(8, 12, 12, 12));
        editorToolbar.setAlignment(Pos.CENTER_RIGHT);
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(
            "-fx-background-color: " + toHex(dangerLight) + "; -fx-text-fill: white; " +
            "-fx-padding: 6 16; -fx-font-size: 12; -fx-background-radius: 4;"
        );
        deleteBtn.setOnAction(e -> deleteSelectedEntry());
        
        Button saveBtn = new Button("Save Entry");
        saveBtn.setStyle(
            "-fx-background-color: " + toHex(successLight) + "; -fx-text-fill: white; " +
            "-fx-padding: 6 20; -fx-font-size: 12; -fx-background-radius: 4; -fx-font-weight: bold;"
        );
        saveBtn.setOnAction(e -> saveEntry());
        
        editorToolbar.getChildren().addAll(deleteBtn, saveBtn);
        
        editorPanel.getChildren().addAll(titleBox, editorArea, editorToolbar);
        
        rightPanel.getChildren().addAll(editorHeader, editorPanel);
        VBox.setVgrow(editorPanel, Priority.ALWAYS);
        
        return rightPanel;
    }
    
    private void showEditProfileDialog() {
        Dialog<UserProfile> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the profile form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        
        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("Your name");
        
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Email address");
        
        TextArea bioField = new TextArea(currentUser.getBio());
        bioField.setPromptText("Write a short bio about yourself");
        bioField.setWrapText(true);
        bioField.setPrefRowCount(3);
        
        Button changePhotoBtn = new Button("Change Profile Photo");
        changePhotoBtn.setStyle(
            "-fx-background-color: " + toHex(accentLight) + "; -fx-text-fill: white; " +
            "-fx-padding: 6 12; -fx-font-size: 12;"
        );
        
        Label currentPhotoLabel = new Label("Current photo will be updated after saving");
        currentPhotoLabel.setFont(Font.font("Segoe UI", 10));
        currentPhotoLabel.setTextFill(mutedLight);
        
        changePhotoBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Photo");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                currentUser.setProfilePhotoUrl(selectedFile.toURI().toString());
                currentPhotoLabel.setText("Photo selected: " + selectedFile.getName());
            }
        });
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Bio:"), 0, 2);
        grid.add(bioField, 1, 2);
        grid.add(changePhotoBtn, 0, 3, 2, 1);
        grid.add(currentPhotoLabel, 0, 4, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        Platform.runLater(nameField::requestFocus);
        
        // Convert the result to a UserProfile when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                currentUser.setName(nameField.getText());
                currentUser.setEmail(emailField.getText());
                currentUser.setBio(bioField.getText());
                return currentUser;
            }
            return null;
        });
        
        Optional<UserProfile> result = dialog.showAndWait();
        
        result.ifPresent(profile -> {
            // Update profile display
            updateProfileDisplay();
            showAlert("Profile Updated", "Your profile has been updated successfully!");
        });
    }
    
    private void updateProfileDisplay() {
        if (userNameLabel != null) {
            userNameLabel.setText(currentUser.getName());
        }
        
        if (userStatsLabel != null) {
            userStatsLabel.setText(currentUser.getTotalEntries() + " entries • " + 
                currentUser.getStreakDays() + " day streak");
        }
        
        // Update profile photo if available
        if (currentUser.getProfilePhotoUrl() != null && !currentUser.getProfilePhotoUrl().isEmpty()) {
            try {
                Image profileImage = new Image(currentUser.getProfilePhotoUrl(), true);
                profileImageView.setImage(profileImage);
            } catch (Exception e) {
                // If image loading fails, keep the default emoji
                System.err.println("Failed to load profile image: " + e.getMessage());
            }
        }
    }
    
    // ===== CALENDAR METHODS =====
    
    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        
        YearMonth yearMonth = YearMonth.from(currentDate);
        monthYearLabel.setText(yearMonth.getMonth().toString().substring(0, 3) + " " + yearMonth.getYear());
        
        LocalDate firstOfMonth = yearMonth.atDay(1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        
        int daysInMonth = yearMonth.lengthOfMonth();
        int startColumn = firstDayOfWeek.getValue() % 7;
        
        // Fill calendar grid
        int row = 0;
        int col = startColumn;
        
        // Current month days
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(22, 22);
            
            boolean hasEntries = entries.stream().anyMatch(e -> e.getDate().equals(date));
            
            if (date.equals(LocalDate.now())) {
                dayLabel.setStyle(
                    "-fx-background-color: " + toHex(accentLight) + "; " +
                    "-fx-text-fill: white; -fx-background-radius: 11; -fx-cursor: hand;"
                );
            } else if (date.equals(selectedCalendarDate)) {
                dayLabel.setStyle(
                    "-fx-background-color: " + toHex(warningLight) + "; " +
                    "-fx-text-fill: white; -fx-background-radius: 11; -fx-cursor: hand;"
                );
            } else if (hasEntries) {
                dayLabel.setStyle(
                    "-fx-background-color: #e9ecef; -fx-text-fill: " + toHex(accentLight) + "; " +
                    "-fx-background-radius: 11; -fx-cursor: hand;"
                );
            } else {
                dayLabel.setStyle("-fx-cursor: hand;");
                dayLabel.setTextFill(primaryLight);
            }
            
            if (hasEntries) {
                StackPane dayContainer = new StackPane();
                dayContainer.setAlignment(Pos.CENTER);
                
                Circle dot = new Circle(2);
                dot.setFill(accentLight);
                dot.setTranslateY(6);
                
                dayContainer.getChildren().addAll(dayLabel, dot);
                calendarGrid.add(dayContainer, col, row);
            } else {
                calendarGrid.add(dayLabel, col, row);
            }
            
            final LocalDate clickDate = date;
            dayLabel.setOnMouseClicked(e -> filterEntriesByDate(clickDate));
            
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }
    
    private void navigateCalendar(int months) {
        currentDate = currentDate.plusMonths(months);
        updateCalendar();
    }
    
    private void filterEntriesByDate(LocalDate date) {
        selectedCalendarDate = date;
        
        List<DiaryEntry> filteredEntries = entries.stream()
            .filter(entry -> entry.getDate().equals(date))
            .collect(Collectors.toList());
        
        displayFilteredEntries(filteredEntries);
        resultsCountLabel.setText(filteredEntries.size() + " entries");
        
        updateCalendar();
        
        if (!filteredEntries.isEmpty()) {
            selectedEntry = filteredEntries.get(0);
            loadEntryForEditing(selectedEntry);
        } else {
            // Prepare for new entry with EXACT current time
            LocalDateTime now = LocalDateTime.now();
            
            // Clear editor for new entry
            titleField.setText("");
            titleField.setPromptText("Title for " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
            editorArea.clear();
            editorArea.setDisable(true);
            
            // Clear any draft
            currentDraftEntry = null;
            selectedEntry = null;
            
            // Show exact date and time
            dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            timeLabel.setText(now.format(DateTimeFormatter.ofPattern("h:mm:ss a")));
            
            Platform.runLater(() -> titleField.requestFocus());
        }
    }
    
    // ===== ENTRY CREATION =====
    
    private void createNewEntry() {
        // Get EXACT current time
        LocalDateTime now = LocalDateTime.now();
        String exactTime = now.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
        String exactDate = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        
        // Reset selection and draft
        selectedEntry = null;
        currentDraftEntry = null;
        
        // Clear fields and show EXACT time
        titleField.setText("");
        titleField.setPromptText("Enter entry title...");
        editorArea.clear();
        editorArea.setDisable(true);
        
        // Show EXACT date and time
        dateLabel.setText(exactDate);
        timeLabel.setText(exactTime);
        
        categoryCombo.setValue("Personal");
        
        Platform.runLater(() -> titleField.requestFocus());
    }
    
    // ===== ENTRY METHODS =====
    
    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<DiaryEntry> filteredEntries = entries.stream()
            .filter(entry -> searchText.isEmpty() ||
                entry.getTitle().toLowerCase().contains(searchText) ||
                entry.getContent().toLowerCase().contains(searchText))
            .collect(Collectors.toList());
        
        resultsCountLabel.setText(filteredEntries.size() + " entries");
        displayFilteredEntries(filteredEntries);
    }
    
    private void displayFilteredEntries(List<DiaryEntry> filteredEntries) {
        entriesContainer.getChildren().clear();
        
        if (filteredEntries.isEmpty()) {
            Label noResults = new Label("No entries found");
            noResults.setFont(Font.font("Segoe UI", 12));
            noResults.setTextFill(mutedLight);
            noResults.setPadding(new Insets(20));
            noResults.setAlignment(Pos.CENTER);
            entriesContainer.getChildren().add(noResults);
            return;
        }
        
        // Sort entries by date (newest first)
        filteredEntries.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
        
        for (DiaryEntry entry : filteredEntries) {
            VBox entryCard = createCompactEntryCard(entry);
            entriesContainer.getChildren().add(entryCard);
        }
    }
    
    private VBox createCompactEntryCard(DiaryEntry entry) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(10));
        card.setStyle(
            "-fx-background-color: " + (entry.equals(selectedEntry) ? "#e7f5ff" : "#ffffff") + "; " +
            "-fx-background-radius: 6; -fx-border-color: #e9ecef; " +
            "-fx-border-radius: 6; -fx-border-width: 1; -fx-cursor: hand;"
        );
        
        // Header
        HBox header = new HBox(4);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label timeLabel = new Label(entry.getTime());
        timeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        timeLabel.setTextFill(mutedLight);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label categoryBadge = new Label(entry.getCategory());
        categoryBadge.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 9));
        categoryBadge.setPadding(new Insets(2, 6, 2, 6));
        categoryBadge.setStyle(
            "-fx-background-color: " + getCategoryColor(entry.getCategory()) + "; " +
            "-fx-text-fill: white; -fx-background-radius: 8;"
        );
        
        header.getChildren().addAll(timeLabel, spacer, categoryBadge);
        
        // Title
        Label titleLabel = new Label(entry.getTitle());
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(primaryLight);
        titleLabel.setWrapText(true);
        
        // Preview
        String preview = entry.getContent();
        if (preview.length() > 80) {
            preview = preview.substring(0, 77) + "...";
        }
        Label previewLabel = new Label(preview);
        previewLabel.setFont(Font.font("Segoe UI", 11));
        previewLabel.setTextFill(mutedLight);
        previewLabel.setWrapText(true);
        
        // Date and favorite
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label(entry.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        dateLabel.setFont(Font.font("Segoe UI", 9));
        dateLabel.setTextFill(mutedLight);
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        Label favoriteIcon = new Label(entry.isFavorite() ? "★" : "☆");
        favoriteIcon.setFont(Font.font("Arial", 12));
        favoriteIcon.setTextFill(entry.isFavorite() ? Color.GOLD : mutedLight);
        favoriteIcon.setCursor(javafx.scene.Cursor.HAND);
        favoriteIcon.setOnMouseClicked(e -> {
            entry.setFavorite(!entry.isFavorite());
            displayFilteredEntries(entries);
        });
        
        footer.getChildren().addAll(dateLabel, footerSpacer, favoriteIcon);
        
        card.getChildren().addAll(header, titleLabel, previewLabel, footer);
        
        // Click handler
        card.setOnMouseClicked(e -> {
            selectedEntry = entry;
            currentDraftEntry = null;
            loadEntryForEditing(entry);
            displayFilteredEntries(entries);
        });
        
        return card;
    }
    
    private void filterByFolder(String folderName) {
        List<DiaryEntry> filtered;
        
        if (folderName.equals("All Entries")) {
            filtered = new ArrayList<>(entries);
        } else if (folderName.equals("Favorites")) {
            filtered = entries.stream()
                .filter(DiaryEntry::isFavorite)
                .collect(Collectors.toList());
        } else {
            filtered = entries.stream()
                .filter(e -> e.getCategory().equals(folderName))
                .collect(Collectors.toList());
        }
        
        displayFilteredEntries(filtered);
        resultsCountLabel.setText(filtered.size() + " entries");
        
        if (!filtered.isEmpty()) {
            selectedEntry = filtered.get(0);
            currentDraftEntry = null;
            loadEntryForEditing(selectedEntry);
        } else {
            // Clear current entry and prepare for new one
            selectedEntry = null;
            currentDraftEntry = null;
            titleField.setText("");
            titleField.setPromptText("Enter entry title...");
            editorArea.clear();
            editorArea.setDisable(true);
        }
    }
    
    private void saveEntry() {
        // Save draft entry if exists
        if (currentDraftEntry != null) {
            // Add draft to entries list
            entries.add(currentDraftEntry);
            selectedEntry = currentDraftEntry;
            currentDraftEntry = null;
            
            // Update UI
            currentUser.setTotalEntries(entries.size());
            updateProfileDisplay();
            
            // Update calendar
            updateCalendar();
            
            // Refresh entries list
            performSearch();
            
            // Focus on editor for more editing
            Platform.runLater(() -> editorArea.requestFocus());
            
            showAlert("Saved", "New entry created and saved!");
            return;
        }
        
        if (selectedEntry == null) {
            showAlert("No Entry", "Please create or select an entry first.");
            return;
        }
        
        // Update existing entry with EXACT current time when saving
        LocalDateTime now = LocalDateTime.now();
        String exactTime = now.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
        LocalDate today = now.toLocalDate();
        
        selectedEntry.setTitle(titleField.getText());
        selectedEntry.setContent(editorArea.getText());
        selectedEntry.setCategory(categoryCombo.getValue());
        selectedEntry.setTime(exactTime);
        selectedEntry.setDate(today); // Update date to today
        
        // Update display with exact time
        dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        timeLabel.setText(exactTime);
        
        currentUser.setTotalEntries(entries.size());
        updateProfileDisplay();
        
        // Update calendar
        updateCalendar();
        
        // Refresh entries list
        performSearch();
        
        showAlert("Saved", "Entry updated successfully at " + exactTime + "!");
    }
    
    private void deleteSelectedEntry() {
        // If we have a draft entry, just clear it
        if (currentDraftEntry != null) {
            createNewEntry(); // This will clear everything
            return;
        }
        
        if (selectedEntry == null) {
            showAlert("No Selection", "Please select an entry to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Entry");
        alert.setHeaderText("Delete '" + selectedEntry.getTitle() + "'?");
        alert.setContentText("This cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            entries.remove(selectedEntry);
            selectedEntry = null;
            currentDraftEntry = null;
            
            currentUser.setTotalEntries(entries.size());
            updateProfileDisplay();
            
            // Clear editor and show EXACT current time
            LocalDateTime now = LocalDateTime.now();
            titleField.setText("");
            titleField.setPromptText("Enter entry title...");
            editorArea.clear();
            editorArea.setDisable(true);
            dateLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            timeLabel.setText(now.format(DateTimeFormatter.ofPattern("h:mm:ss a")));
            
            if (!entries.isEmpty()) {
                selectedEntry = entries.get(0);
                loadEntryForEditing(selectedEntry);
            }
            
            // Update calendar
            updateCalendar();
            
            // Refresh entries list
            performSearch();
        }
    }
    
    private void loadEntryForEditing(DiaryEntry entry) {
        selectedEntry = entry;
        currentDraftEntry = null; // Clear any draft
        
        titleField.setText(entry.getTitle());
        dateLabel.setText(entry.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        timeLabel.setText(entry.getTime());
        editorArea.setText(entry.getContent());
        editorArea.setDisable(false);
        categoryCombo.setValue(entry.getCategory());
        
        selectedCalendarDate = entry.getDate();
        currentDate = entry.getDate();
        updateCalendar();
        
        // Focus on editor for immediate editing
        Platform.runLater(() -> editorArea.requestFocus());
    }
    
    // ===== HELPER METHODS =====
    
    private void addSampleEntries() {
        // Use EXACT time formatting for sample entries
        LocalDateTime now = LocalDateTime.now();
        
        entries.add(new DiaryEntry(now.toLocalDate(), 
            now.minusHours(2).format(DateTimeFormatter.ofPattern("h:mm:ss a")), 
            "Morning Thoughts", 
            "Starting the day with coffee and planning my tasks. Feeling productive today!", 
            "Personal", true));
        
        entries.add(new DiaryEntry(now.toLocalDate(), 
            now.minusHours(1).format(DateTimeFormatter.ofPattern("h:mm:ss a")), 
            "Project Update", 
            "Made significant progress on the new feature. Need to write unit tests tomorrow.", 
            "Work", false));
        
        entries.add(new DiaryEntry(LocalDate.of(2024, 10, 25), 
            "19:00:00", 
            "Gratitude", 
            "Grateful for family, health, and new opportunities. Life is beautiful!", 
            "Personal", true));
        
        currentUser.setTotalEntries(entries.size());
        
        performSearch();
        if (!entries.isEmpty()) {
            selectedEntry = entries.get(0);
            loadEntryForEditing(selectedEntry);
        }
        updateCalendar();
    }
    
    private String getCategoryColor(String category) {
        switch (category) {
            case "Personal": return "#007bff";
            case "Work": return "#28a745";
            case "Health": return "#dc3545";
            case "Travel": return "#17a2b8";
            case "Ideas": return "#6f42c1";
            default: return "#6c757d";
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
    
    private void applyThemeToAll() {
        BorderPane mainPane = (BorderPane) primaryStage.getScene().getRoot();
        Color bgColor = darkMode ? backgroundDark : backgroundLight;
        mainPane.setStyle("-fx-background-color: " + toHex(bgColor) + ";");
        
        if (monthYearLabel != null) {
            monthYearLabel.setTextFill(darkMode ? primaryDark : primaryLight);
        }
        
        primaryStage.setTitle("My Diary" + (darkMode ? " - Dark Mode" : " - Light Mode"));
    }
    
    @Override
    public void stop() {
        // Stop the time updater when application closes
        if (timeUpdater != null) {
            timeUpdater.stop();
        }
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
    private String category;
    private boolean favorite;
    
    public DiaryEntry(LocalDate date, String time, String title, String content, String category, boolean favorite) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.content = content;
        this.category = category;
        this.favorite = favorite;
    }
    
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public boolean isFavorite() { return favorite; }
    
    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCategory(String category) { this.category = category; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}

class UserProfile {
    private String name;
    private String email;
    private String bio;
    private LocalDate joinDate;
    private int totalEntries;
    private int streakDays;
    private String favoriteCategory;
    private String profilePhotoUrl;
    
    public UserProfile() {
        this.totalEntries = 0;
        this.streakDays = 0;
        this.joinDate = LocalDate.now();
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    
    public int getTotalEntries() { return totalEntries; }
    public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
    
    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }
    
    public String getFavoriteCategory() { return favoriteCategory; }
    public void setFavoriteCategory(String favoriteCategory) { this.favoriteCategory = favoriteCategory; }
    
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
}