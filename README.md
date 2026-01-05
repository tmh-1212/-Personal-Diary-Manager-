📓 My Diary - A Modern Desktop Diary Application

🎯 Overview
My Diary is a feature-rich desktop diary application built with JavaFX that provides users with an intuitive interface for recording daily thoughts, organizing entries by categories, and tracking writing habits through a visual calendar. The application combines modern UI design with robust functionality to create a seamless journaling experience.

Key Objectives:

Provide a distraction-free writing environment

Enable efficient organization of thoughts and memories

Offer visual insights into writing habits

Ensure data privacy with local storage

Support personalization through user profiles

✨ Features
Core Features
📝 Rich Text Editor - Clean, distraction-free writing environment

🗂️ Smart Organization - Categorize entries (Personal, Work, Health, Travel, Ideas)

📅 Interactive Calendar - Visual representation of entries by date

🔍 Advanced Search - Full-text search across all entries

⭐ Favorites System - Mark important entries for quick access

🌓 Dark/Light Theme - Toggle between themes for comfortable writing

👤 User Profiles - Personalized profiles with photos and statistics

⏰ Real-time Clock - Live updating time with seconds precision

Advanced Features
📊 Writing Statistics - Track entries count and writing streaks

🎨 Custom Categories - Five built-in categories for organization

🔔 Visual Indicators - Calendar dots show days with entries

💾 Auto-save Drafts - Never lose your thoughts mid-writing

📱 Responsive Design - Adapts to different window sizes

🎯 Quick Actions - One-click entry creation and editing

🎨 Screenshots
(Application Interface Layout)

text
┌─────────────────────────────────────────────────────────────┐
│ 📓 My Diary                     [Search...] +New 🌙          │
├───────┬──────────────────────┬──────────────────────┬───────┤
│ 👤     │                      │ 📄 Morning Thoughts   │ Tue,  │
│ User  │     JAN 2026         │ 📄 Project Update     │ Jan 6 │
│ 4 ent │   S M T W T F S      │ 📄 Gratitude          │ 2:30  │
│ 7 day │        1 2 3         │                      │       │
│ Edit  │   4   7 8 9 10       │                      │ Title:│
│ Profile│ 11 12 13 14 15 16 17│     Entries List     │ [     ]│
│       │ 18 19 20 21 22 23 24 │                      │       │
│ 📁    │ 25 26 27 28 29 30 31 │                      │ Editor│
│ Folders│                      │                      │ Area  │
│ • All │                      │                      │       │
│ • Fav │                      │                      │       │
│ • Pers│                      │                      │ Delete│
│ • Work│                      │                      │ Save  │
│ • Heal│                      │                      │       │
│ • Trav│                      │                      │       │
│ • Idea│                      │                      │       │
└───────┴──────────────────────┴──────────────────────┴───────┘
🏗️ Architecture & Design Choices
Three-Panel Layout Design
java
SplitPane mainContent = new SplitPane();
mainContent.setDividerPositions(0.25, 0.60);
Design Rationale:

Left Panel (25%): Navigation and overview (profile, calendar, folders)

Center Panel (35%): Entry list and previews

Right Panel (40%): Editor with ample writing space

Benefits: Familiar pattern used by modern productivity apps, optimal screen real estate usage

Object-Oriented Architecture
java
// Clear separation of concerns
class DiaryEntry { }    // Data model
class UserProfile { }   // User information
class DiaryApp { }      // Main controller
Design Principles Applied:

Single Responsibility: Each class has one clear purpose

Encapsulation: Internal data representation hidden from UI

Composition Over Inheritance: Flexible component structure

Open/Closed Principle: Extensible without modification

Event-Driven Programming Model
java
// Property listeners for real-time updates
titleField.textProperty().addListener((observable, oldValue, newValue) -> {
    if (!newValue.trim().isEmpty()) {
        createOrUpdateEntry();
    }
});
Advantages:

Responsive UI: Immediate feedback to user actions

Decoupled Components: UI elements don't directly manipulate data

Maintainable: Clear flow of data and events

Scalable: Easy to add new event handlers

Draft System Implementation
java
private DiaryEntry currentDraftEntry = null;
Why This Design?

Prevents Data Loss: Users must explicitly save entries

Better UX: Clear distinction between editing and creating

Flexibility: Users can abandon drafts without consequences

Performance: Only final entries are added to the main list

Real-time Time Management
java
private Timeline timeUpdater = new Timeline(
    new KeyFrame(Duration.seconds(1), event -> {
        updateCurrentTime();
    })
);
Purpose:

Accuracy: Entries timestamped to the exact second

Context: Writers see the exact moment of creation

Atmosphere: Live clock creates a "present moment" feeling

Consistency: Time updates even during long writing sessions

Color Scheme Selection
java
// Light theme (professional, calm)
private Color primaryLight = Color.web("#2c3e50");     // Dark blue-gray
private Color accentLight = Color.web("#007bff");      // Bright blue
private Color backgroundLight = Color.web("#f5f8fa");  // Off-white

// Dark theme (eye-friendly, modern)
private Color primaryDark = Color.web("#e0e0e0");      // Light gray
private Color backgroundDark = Color.web("#1a1a1a");    // Near-black
Design Considerations:

Accessibility: WCAG-compliant contrast ratios

Readability: Optimized for long writing sessions

Aesthetics: Professional yet inviting appearance

Thematic Consistency: Colors reinforce the diary/journal theme

Data Persistence Strategy
Current Implementation:

In-Memory Storage: Entries stored in ArrayList during session

Serialization Ready: DiaryEntry class is serializable

Future-Proof Design: Easy to add file/database persistence

Why This Approach?

Simplicity: No external dependencies

Performance: Fast in-memory operations

Flexibility: Can easily switch to file/database storage

Learning Focus: Demonstrates core Java concepts clearly


# 3. Run the application
java -cp out com.diary.DiaryApp
Method 3: Using IDE (Recommended)
Open in IntelliJ IDEA or Eclipse

Set JavaFX SDK path in project settings

Run DiaryApp.main()
📖 User Guide
Getting Started
Launch the application

Explore sample entries to understand the interface

Familiarize yourself with the three-panel layout

Try creating your first entry

Creating Your First Entry
Method 1: Quick Start (Recommended)
text
1. Click the blue "+ New Entry" button (top-right)
2. Type any title in the "Entry title..." field
3. Notice the editor area automatically enables
4. Write your diary content in the text area
5. Select a category from dropdown
6. Click the green "Save Entry" button
7. View your entry in the center panel
Method 2: Direct Typing
text
1. Simply start typing in the title field
2. The system automatically creates a new entry
3. Continue with steps 4-7 above
Method 3: From Calendar
text
1. Click on any date in the calendar
2. If no entries exist, it prepares a new entry
3. Start typing a title
Working with Entries
Editing Existing Entries
text
1. Click on any entry card in the center panel
2. The entry loads in the right panel editor
3. Make changes to title, content, or category
4. Click "Save Entry" to update
Deleting Entries
text
1. Select the entry (click on it)
2. Click the red "Delete" button
3. Confirm deletion in the pop-up dialog
4. Entry is permanently removed
Marking Favorites
text
1. Look at any entry in the center panel
2. Click the star icon (☆) at bottom-right
3. Empty star (☆) = Not favorite
4. Filled star (★) = Favorite (yellow color)
5. Favorites appear in the "Favorites" folder
Organizing Your Diary
Using Categories
Personal: Private thoughts, reflections, daily experiences

Work: Professional notes, meetings, projects

Health: Fitness, wellness, medical information

Travel: Trip plans, experiences, memories

Ideas: Creative thoughts, inventions, plans

Filtering Entries
text
1. Click any folder name in left panel
2. Center panel shows filtered entries
3. Number badges show entry count in each folder
4. Click "All Entries" to see everything
Search Functionality
text
1. Type in the search box at top-right
2. Press Enter or wait for auto-search
3. Search looks through:
   - Entry titles
   - Entry content
   - Entry categories
4. Results update instantly
Profile Management
Editing Your Profile
text
1. Click "Edit Profile" button below your name
2. Update your information:
   - Name and email
   - Bio about yourself
3. Click "Change Profile Photo" to upload image
4. Select image file (PNG, JPG, JPEG, GIF)
5. Click "Save" to update
Understanding Statistics
Total Entries: Count of all diary entries

Streak Days: Consecutive days with entries

Profile Photo: Personalizes your experience

Favorite Category: Most used category

Calendar Features
Navigating Calendar
text
◀ Button: Previous month
▶ Button: Next month
Today's Date: Highlighted in blue
Dates with Entries: Blue dot indicator
Click Date: View/create entries for that day
Calendar Indicators
Blue Highlight: Today's date

Blue Dot: Day has one or more entries

Clickable: All dates can be clicked

Month Navigation: Easily browse different months

Theme Management
Switching Themes
text
1. Click the 🌙/☀️ button at top-right
2. Moon icon = Switch to dark mode
3. Sun icon = Switch to light mode
4. Theme changes instantly
5. Preference saved between sessions
Theme Benefits
Light Theme: Daytime use, bright environments

Dark Theme: Nighttime use, reduces eye strain

Accessibility: Good contrast for readability

Personal Preference: Choose what works for you

Advanced Tips
Efficient Writing Workflow
Use keyboard shortcuts where available

Create entry templates for recurring topics

Use categories consistently for better organization

Regularly favorite important entries

Review calendar to maintain writing habits

Data Management
Regular backups: Consider exporting important entries

Category consistency: Helps with future searches

Entry titles: Make them descriptive for easy searching

Regular reviews: Periodically review old entries

💻 Development Guide
Project Setup for Developers
1. Environment Setup
bash
# Required Tools
- Java Development Kit (JDK 11+)
- JavaFX SDK
- IDE (IntelliJ IDEA, Eclipse, or VS Code)
- Git (for version control)
2. Importing into IDE
IntelliJ IDEA:

text
1. File → Open → Select project folder
2. Configure SDK: File → Project Structure → SDK
3. Add JavaFX: Run → Edit Configurations → VM Options
   --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
Eclipse:

text
1. File → Import → Existing Projects into Workspace
2. Configure Build Path: Add JavaFX libraries
3. Run Configuration: Add VM arguments for JavaFX
Code Architecture
Main Components
java
// 1. DiaryApp (Main Controller)
// Responsibilities: UI setup, event handling, application flow
// Key Methods: start(), createNewEntry(), saveEntry(), performSearch()

// 2. DiaryEntry (Data Model)
// Responsibilities: Store entry data, serialization support
// Key Fields: date, time, title, content, category, favorite

// 3. UserProfile (User Data)
// Responsibilities: Store user information, statistics
// Key Fields: name, email, bio, joinDate, statistics
UI Component Structure
text
DiaryApp (BorderPane)
├── Top Panel (HBox)
│   ├── Title Area
│   ├── Search Field
│   └── Action Buttons
├── Center Area (SplitPane)
│   ├── Left Panel (VBox)
│   │   ├── Profile Panel
│   │   ├── Calendar Panel
│   │   └── Folders Panel
│   ├── Center Panel (VBox)
│   │   ├── Entries Header
│   │   └── Entries List (ScrollPane)
│   └── Right Panel (VBox)
│       ├── Editor Header
│       ├── Editor Area
│       └── Editor Toolbar
Extending the Application
Adding New Features
1. New Category Type:

java
// In createRightPanel() method
categoryCombo.getItems().addAll("Personal", "Work", "Health", "Travel", "Ideas", "NewCategory");

// In getCategoryColor() method
case "NewCategory": return "#your-color";
2. Additional Statistics:

java
// In UserProfile class
private int monthlyEntries;
private Map<String, Integer> categoryCounts;

// Add getters/setters and update logic
3. Export Functionality:

java
private void exportEntriesToFile(List<DiaryEntry> entries, String format) {
    // Implement PDF, TXT, or HTML export
}
Modifying UI Components
Changing Layout Proportions:

java
// In start() method
mainContent.setDividerPositions(0.20, 0.65); // Adjust as needed
Adding New UI Elements:

java
// Example: Add tags to entry cards
HBox tagsContainer = new HBox(4);
tagsContainer.getChildren().addAll(tag1, tag2, tag3);
card.getChildren().add(tagsContainer);
Testing the Application
Manual Testing Checklist
Entry Creation: All three methods work

Editing: Existing entries can be modified

Deletion: Entries can be deleted with confirmation

Search: Finds entries by title and content

Filtering: Folder filtering works correctly

Theme Switching: Light/dark modes work

Profile Editing: Profile updates save correctly

Calendar Navigation: Month navigation works

Time Updates: Real-time clock functions

Responsive Design: Window resizing works

Automated Testing (Future Enhancement)
java
// Example test structure
public class DiaryAppTest {
    @Test
    public void testEntryCreation() {
        // Test draft creation
        // Test title validation
        // Test category assignment
    }
    
    @Test 
    public void testSearchFunctionality() {
        // Test search by title
        // Test search by content
        // Test empty search
    }
}
Performance Considerations
Memory Management
Entry Previews: Limited to 80 characters for performance

Lazy Loading: Only visible entries are rendered

Stream Operations: Efficient filtering and searching

Event Listeners: Properly managed to prevent memory leaks

UI Performance
CSS Styling: Efficient selectors and minimal reflows

Layout Caching: Reuse UI components where possible

Event Delegation: Efficient event handling

Background Operations: Time updates don't block UI

Common Development Tasks
Debugging Tips
Check Console Output: System.err.println() for debugging

JavaFX Scene Builder: Visual UI design and debugging

Property Listeners: Verify they're firing correctly

Thread Safety: Ensure UI updates happen on JavaFX thread

Code Style Guidelines
Naming: camelCase variables, PascalCase classes

Indentation: 4 spaces, no tabs

Comments: Javadoc for public methods

Line Length: Maximum 100 characters

Error Handling: Meaningful error messages

📁 Project Structure
Source Code Organization
text
src/com/diary/
├── DiaryApp.java          # Main application class (1200+ lines)
│   ├── UI Setup Methods
│   ├── Event Handlers
│   ├── Business Logic
│   └── Helper Methods
├── DiaryEntry.java        # Entry data model (50 lines)
│   ├── Data Fields
│   ├── Getters/Setters
│   └── Serializable
└── UserProfile.java       # User profile model (60 lines)
    ├── User Information
    ├── Statistics
    └── Profile Photo
Method Categories in DiaryApp.java
UI Creation Methods
createTopPanel(): Creates header with search and actions

createLeftPanel(): Builds navigation panel

createCenterPanel(): Creates entries list display

createRightPanel(): Builds editor interface

createProfilePanel(): User profile display

createCompactCalendarPanel(): Calendar UI

createCompactFoldersPanel(): Folder navigation

createCompactEntryCard(): Individual entry display

Business Logic Methods
createNewEntry(): Initializes new entry creation

saveEntry(): Saves/updates entries

deleteSelectedEntry(): Removes entries

loadEntryForEditing(): Loads entry into editor

performSearch(): Executes search functionality

filterByFolder(): Filters by category/folder

filterEntriesByDate(): Filters by calendar date

Helper Methods
updateCalendar(): Refreshes calendar display

updateProfileDisplay(): Updates user profile UI

showAlert(): Displays message dialogs

toHex(): Color conversion utility

getCategoryColor(): Maps categories to colors

getFolderIcon(): Folder emoji mapping

addSampleEntries(): Demo data initialization

Event Handlers
Title field listeners

Editor area listeners

Category combo handlers

Theme toggle listeners

Calendar navigation

Folder click handlers

Entry card click handlers

Data Flow Architecture
text
User Action → Event Handler → Business Logic → Data Update → UI Refresh
    ↓           ↓               ↓               ↓              ↓
Click Save → saveEntry() → Update Model → Save to List → Refresh Display
🛠️ Technical Details
Java & JavaFX Version Information
Java Version: Compatible with Java 8+

JavaFX Version: JavaFX 11+

Dependencies: Pure Java - no external libraries

Packaging: Can be bundled as JAR or native executable

Key Java Features Utilized
JavaFX Components Used
Layout: BorderPane, SplitPane, VBox, HBox, GridPane

Controls: Button, Label, TextField, TextArea, ComboBox, ToggleButton

Containers: ScrollPane, StackPane

Shapes: Circle for profile photo and calendar dots

Animations: Timeline for real-time updates

Java 8+ Features
Stream API: For filtering and searching entries

Lambda Expressions: Event handlers and callbacks

Method References: Cleaner code in stream operations

LocalDateTime API: Modern date/time handling

Optional: Safe null handling in dialogs

Design Patterns Implemented
1. Observer Pattern
java
// Property listeners throughout application
titleField.textProperty().addListener((obs, oldVal, newVal) -> {
    // React to changes
});
2. Factory Method Pattern
java
// Entry card creation
private VBox createCompactEntryCard(DiaryEntry entry) {
    // Factory method for creating uniform UI components
}
3. MVC-like Architecture
Model: DiaryEntry, UserProfile

View: JavaFX UI components

Controller: DiaryApp with event handlers

4. Strategy Pattern
java
// Different filtering strategies
filterByFolder(), filterEntriesByDate(), performSearch()
Performance Characteristics
Time Complexity
Entry Search: O(n) - linear search through entries

Filtering: O(n) - stream filtering operations

UI Updates: O(1) for individual component updates

Calendar Rendering: O(1) - fixed number of days

Memory Usage
Entry Storage: Each entry ~1KB in memory

UI Components: Recycled and reused where possible

Images: Profile photos loaded on demand

Caching: Limited caching for performance

Scalability Considerations
Current Limitations
Entry Count: Best performance with < 10,000 entries

Search Speed: Linear search may slow with many entries

Memory: All entries loaded into memory

Scalability Improvements
Pagination: Load entries in chunks

Database: Switch to SQLite for larger datasets

Indexed Search: Implement search indexes

Lazy Loading: Load entry content on demand

Security Considerations
Current Security Features
Local Storage: Data stays on user's machine

No Network: No external connections

File Uploads: Local file selection only

Input Validation: Basic validation on user input

Future Security Enhancements
Encryption: Password protection for entries

Backup Encryption: Secure backup files

Input Sanitization: Prevent injection attacks

Secure Deletion: Properly wipe deleted data

🤝 Contributing
How to Contribute
1. Reporting Issues
Bug Reports: Detailed steps to reproduce

Feature Requests: Clear description of desired functionality

UI/UX Suggestions: Screenshots or mockups helpful

2. Code Contributions
bash
# Fork and clone the repository
git clone https://github.com/yourusername/my-diary-app.git

# Create a feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git commit -m "Add: Description of changes"

# Push and create pull request
git push origin feature/your-feature-name
3. Documentation Improvements
README Updates: Clarify instructions or add examples

Code Comments: Improve documentation of complex logic

User Guides: Create tutorials or how-to articles

Areas Needing Contribution
High Priority
Data Persistence: File/database storage

Export Features: PDF, HTML, TXT export

Import Features: Import from other diary apps

Backup System: Automatic backup creation

Medium Priority
Rich Text Editing: Bold, italics, lists

Tags System: Flexible tagging beyond categories

Reminders: Daily writing prompts

Statistics Dashboard: Writing analytics

Low Priority
Themes: Additional color schemes

Font Options: Custom font selection

Print Support: Direct printing of entries

Spell Check: Built-in spell checking

Code Review Process
What We Look For
Code Quality: Clean, readable, well-commented

Functionality: Works as described

Performance: Efficient algorithms and data structures

Testing: Includes or updates tests

Documentation: Updated README and comments

Review Checklist
Code follows project style guidelines

No breaking changes to existing functionality

New features are properly documented

Performance impact is considered

Security implications are addressed

📄 License
MIT License
text
Copyright (c) 2024 Tesfamikael Hailu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
Third-Party Resources
JavaFX: Open-source GUI framework (GPL with Classpath Exception)

Icons: Emoji characters (Unicode standard)

Color Schemes: Designed for accessibility and aesthetics

Attribution Requirements
If you use this code in your projects:

Include the license: Copy the MIT license text

Give credit: Mention the original author

No warranty: Clearly state no warranties provided

Modifications: Note any changes made

🔮 Future Roadmap
Short-term Goals (Next 3 Months)
Data Persistence: Save entries to file

Export Functionality: Basic text export

Improved Search: Case-insensitive, partial matches

Entry Sorting: Sort by date, title, category

Medium-term Goals (3-6 Months)
Cloud Sync: Optional cloud backup

Mobile Companion: Basic mobile app

Rich Text: Basic formatting options

Statistics: Enhanced writing analytics

Long-term Vision (6+ Months)
AI Features: Writing suggestions, sentiment analysis

Collaboration: Shared diaries with permissions

Multimedia: Photo and audio attachments

Plugins: Extensible plugin system

🙏 Acknowledgments
Credits
Developer: Tesfamikael Hailu

Email: tesfamikealhailu@gmail.com

Inspiration: Traditional diary keeping meets modern technology

Testing: All users who provided feedback

Special Thanks
JavaFX Community: For an excellent GUI framework

OpenJDK Contributors: For maintaining Java

Early Testers: For bug reports and suggestions

Educators: For teaching Java and software design principles

Learning Resources
This project demonstrates:

JavaFX UI Development: Modern desktop application design

Object-Oriented Design: Clean architecture and separation of concerns

Event-Driven Programming: Responsive user interfaces

Software Engineering: From requirements to implementation

📞 Support & Contact
Getting Help
GitHub Issues: For bug reports and feature requests

Email: tesfamikealhailu@gmail.com

Documentation: This README and code comments

Community
Share Your Experience: How you use the diary

Feature Suggestions: What would make it better for you

Bug Reports: Help improve stability

Code Contributions: Make it your own

Stay Updated
Watch Repository: Get notifications of updates

Star Project: Show your support

Fork & Customize: Create your own version

Share: Tell others about the project

🎯 Final Notes
Project Philosophy
This diary application was built with several key principles in mind:

Simplicity: Do one thing well - diary keeping

Privacy: Your data stays on your computer

Usability: Intuitive interface requiring minimal learning

Quality: Clean code and thoughtful design

Learning: Demonstrates real-world Java application development

For Users
Whether you're journaling for mental health, recording memories, or organizing thoughts, this application provides a dedicated space for your writing. The combination of organization tools and a clean writing interface makes it suitable for both casual diarists and dedicated journal keepers.

For Developers
This project serves as a comprehensive example of a real-world JavaFX application. It demonstrates proper architecture, event handling, UI design, and software engineering principles. The code is structured to be readable, maintainable, and extensible.

For Educators
This application can be used as a teaching tool for:

JavaFX Programming: Complete desktop application example

Software Design: Object-oriented principles in practice

UI/UX Design: User-centered interface design

Project Structure: Organizing medium-sized Java projects

