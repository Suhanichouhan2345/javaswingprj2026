package com.Reminder_Event;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;  
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;

public class CalendarView extends JDialog {
    
    Connection con;
    YearMonth currentMonth;
    JPanel calendarPanel;
    JLabel monthLabel;
    ArrayList<LocalDate> noteDates;
    JFrame parentFrame;

    CalendarView(JFrame parent, Connection connection) {
        super(parent, "Calendar View", true);
        this.con = connection;
        this.parentFrame = parent;
        this.currentMonth = YearMonth.now();
        this.noteDates = new ArrayList<>();
        
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(32, 34, 48));
        
        loadNoteDates();
        createCalendarUI();
    }
    
    void loadNoteDates() {
        try {
            noteDates.clear();
            Statement stmt = con.createStatement();
            // *** TABLE NAME CHANGE: notes -> reminders ***
            // *** COLUMN CHANGE: note_date -> event_date ***
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT event_date FROM reminders");
            while (rs.next()) {
                noteDates.add(rs.getDate("event_date").toLocalDate());
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void createCalendarUI() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(32, 34, 48));
        topPanel.setLayout(new FlowLayout());
        
        JButton prevBtn = new JButton("<");
        prevBtn.setBackground(new Color(140, 82, 255));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFocusPainted(false);
        prevBtn.setBorderPainted(false);
        prevBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        monthLabel.setPreferredSize(new Dimension(250, 35));
        
        JButton nextBtn = new JButton(">");
        nextBtn.setBackground(new Color(140, 82, 255));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorderPainted(false);
        nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });
        
        topPanel.add(prevBtn);
        topPanel.add(monthLabel);
        topPanel.add(nextBtn);
        add(topPanel, BorderLayout.NORTH);
        
        calendarPanel = new JPanel();
        calendarPanel.setBackground(new Color(32, 34, 48));
        calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(calendarPanel, BorderLayout.CENTER);
        
        JPanel legendPanel = new JPanel();
        legendPanel.setBackground(new Color(32, 34, 48));
        legendPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel greenBox = new JLabel("  ");
        greenBox.setOpaque(true);
        greenBox.setBackground(new Color(0, 220, 130));
        greenBox.setPreferredSize(new Dimension(20, 20));
        JLabel greenText = new JLabel(" Reminders Available  ");
        greenText.setForeground(Color.WHITE);
        
        JLabel purpleBox = new JLabel("  ");
        purpleBox.setOpaque(true);
        purpleBox.setBackground(new Color(140, 82, 255));
        purpleBox.setPreferredSize(new Dimension(20, 20));
        JLabel purpleText = new JLabel(" Today  ");
        purpleText.setForeground(Color.WHITE);
        
        JLabel infoText = new JLabel(" | Click to View, Double-click to Add");
        infoText.setForeground(new Color(180, 180, 200));
        
        legendPanel.add(greenBox);
        legendPanel.add(greenText);
        legendPanel.add(purpleBox);
        legendPanel.add(purpleText);
        legendPanel.add(infoText);
        add(legendPanel, BorderLayout.SOUTH);
        
        updateCalendar();
    }
    
    void updateCalendar() {
        calendarPanel.removeAll();
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(new Color(180, 180, 200));
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calendarPanel.add(dayLabel);
        }
        
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < dayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }
        
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dayBtn.setFocusPainted(false);
            dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (noteDates.contains(date)) {
                dayBtn.setBackground(new Color(0, 220, 130));
                dayBtn.setForeground(Color.BLACK);
                dayBtn.setToolTipText("Reminders available - Click to view");
            } else {
                dayBtn.setBackground(new Color(45, 48, 65));
                dayBtn.setForeground(Color.WHITE);
                dayBtn.setToolTipText("Double click to add reminder");
            }
            
            if (date.equals(LocalDate.now())) {
                dayBtn.setBorder(BorderFactory.createLineBorder(new Color(140, 82, 255), 3));
            } else {
                dayBtn.setBorderPainted(false);
            }
            
            final LocalDate selectedDate = date;
            
            // Single click - View reminders
            dayBtn.addActionListener(e -> {
                showNotesForDate(selectedDate);
            });
            
            // Double click - Add reminder
            dayBtn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        openAddEditDialog(selectedDate);
                    }
                }
            });
            
            calendarPanel.add(dayBtn);
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    void showNotesForDate(LocalDate date) {
        try {
            // *** TABLE + COLUMNS CHANGE ***
            String sql = "SELECT * FROM reminders WHERE event_date = ? ORDER BY event_time ASC";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            StringBuilder notesText = new StringBuilder();
            notesText.append("📅 Date: ").append(date).append("\n\n");
            
            int count = 0;
            while (rs.next()) {
                count++;
                notesText.append("📌 Reminder #").append(count).append(" (ID: ").append(rs.getInt("id")).append(")\n");
                notesText.append("Title: ").append(rs.getString("title")).append("\n");
                notesText.append("Occasion: ").append(rs.getString("occasion")).append("\n");
                notesText.append("Time: ").append(rs.getTime("event_time")).append("\n");
                notesText.append("Email: ").append(rs.getString("recipient_email")).append("\n");
                notesText.append("Description: ").append(rs.getString("description")).append("\n");
                notesText.append("--------------------------------\n\n");
            }
            
            if (count == 0) {
                notesText.append("No reminders for this date.\nDouble-click to add one!");
            } else {
                notesText.append("Double-click on date to add new reminder");
            }
            
            JTextArea textArea = new JTextArea(notesText.toString());
            textArea.setEditable(false);
            textArea.setBackground(new Color(45, 48, 65));
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 350));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Reminders for " + date, JOptionPane.INFORMATION_MESSAGE);
            
            rs.close();
            pstmt.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reminders: " + e.getMessage());
        }
    }
    
    void openAddEditDialog(LocalDate date) {
        JDialog dialog = new JDialog(this, "Add Reminder - " + date, true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(32, 34, 48));
        
        JLabel dateLabel = new JLabel("Date: " + date);
        dateLabel.setBounds(20, 20, 400, 30);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dialog.add(dateLabel);
        
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(20, 70, 100, 25);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(titleLabel);
        
        JTextField titleField = new JTextField();
        titleField.setBounds(20, 95, 390, 35);
        titleField.setBackground(new Color(45, 48, 65));
        titleField.setForeground(Color.WHITE);
        titleField.setCaretColor(Color.WHITE);
        dialog.add(titleField);
        
        JLabel occasionLabel = new JLabel("Occasion:");
        occasionLabel.setBounds(20, 145, 100, 25);
        occasionLabel.setForeground(Color.WHITE);
        occasionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(occasionLabel);
        
        JTextField occasionField = new JTextField();
        occasionField.setBounds(20, 170, 390, 35);
        occasionField.setBackground(new Color(45, 48, 65));
        occasionField.setForeground(Color.WHITE);
        occasionField.setCaretColor(Color.WHITE);
        dialog.add(occasionField);

        JLabel emailLabel = new JLabel("Recipient Email:");
        emailLabel.setBounds(20, 220, 150, 25);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(emailLabel);
        
        JTextField emailField = new JTextField();
        emailField.setBounds(20, 245, 390, 35);
        emailField.setBackground(new Color(45, 48, 65));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        dialog.add(emailField);
        
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setBounds(20, 295, 100, 25);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(timeLabel);
        
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setBounds(20, 320, 150, 35);
        dialog.add(timeSpinner);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setBounds(20, 370, 120, 25);
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(descLabel);
        
        JTextArea descArea = new JTextArea();
        descArea.setBackground(new Color(45, 48, 65));
        descArea.setForeground(Color.WHITE);
        descArea.setCaretColor(Color.WHITE);
        descArea.setLineWrap(true);
        
        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setBounds(20, 395, 390, 60);
        dialog.add(scrollPane);
        
        JButton saveBtn = new JButton("Save Reminder");
        saveBtn.setBounds(20, 470, 180, 40);
        saveBtn.setBackground(new Color(0, 220, 130));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(saveBtn);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(230, 470, 180, 40);
        cancelBtn.setBackground(new Color(255, 80, 80));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialog.add(cancelBtn);
        
        saveBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String email = emailField.getText().trim();
                if (title.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title aur Email jaruri hai!");
                    return;
                }
                
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String selectedTime = timeFormat.format(timeSpinner.getValue());
                
                // *** INSERT INTO reminders NOT notes ***
                String sql = "INSERT INTO reminders(user_id,title,occasion,event_date,event_time,recipient_email,description,alert_before) VALUES(0,?,?,?,?,?,?,?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, title);
                pstmt.setString(2, occasionField.getText());
                pstmt.setDate(3, java.sql.Date.valueOf(date));
                pstmt.setTime(4, java.sql.Time.valueOf(selectedTime));
                pstmt.setString(5, email);
                pstmt.setString(6, descArea.getText());
                pstmt.setString(7, "1 minute"); // Default alert
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(dialog, "Reminder Added Successfully! ✅");
                dialog.dispose();
                loadNoteDates();
                updateCalendar();
                
                // *** FIXED: loadNotes() not loadNotesFromDB() ***
                if (parentFrame instanceof ReminderApp) {
                    ((ReminderApp) parentFrame).loadNotes();
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
}