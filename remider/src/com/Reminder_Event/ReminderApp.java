package com.Reminder_Event;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class ReminderApp extends JFrame implements ActionListener {

  
    String reminderSenderEmail = "eventreminderapp11@gmail.com";
    String reminderAppPassword = "pbfx rjbe rtdd hwmc";

    // Database
    static final String DB_URL = "jdbc:mysql://localhost:3306/reminder_db";
    static final String USER = "root";
    static final String PASS = "Suhani22##";
    Connection con;

    // User data
    int userId;
    String userName;
    boolean isGuest;

    // UI Components
    JTextField titleField, occasionField, recipientEmailField;
    JTextArea descArea;
    JComboBox<String> alertBox;
    JComboBox<String> dayBox, monthBox, yearBox, hourBox, minuteBox, amPmBox;
    JButton addBtn, updateBtn, deleteBtn, logoutBtn;
    //sidebar
    JButton allNotesBtn, todayBtn, upcomingBtn, calendarBtn, deleteNoteBtn, exportBtn;
    JTable notesTable;
    DefaultTableModel tableModel;
    JLabel welcomeLabel;

    // Colors
    Color card = new Color(32, 34, 48);
    Color field = new Color(45, 48, 65);
    Color purple = new Color(140, 82, 255);
    Color green = new Color(0, 220, 130);
    Color blue = new Color(0, 170, 255);
    Color red = new Color(255, 70, 70);

    Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    Font boldFont = new Font("Segoe UI", Font.BOLD, 15);
    Font titleFont = new Font("Segoe UI", Font.BOLD, 28);

    public ReminderApp(int userId, String userName, boolean isGuest) {
        this.userId = userId;
        this.userName = userName;
        this.isGuest = isGuest;

        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!");
            e.printStackTrace();
        }

        setTitle("Reminder App - " + userName);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Sidebar
        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 200, 720);
        sidebar.setBackground(new Color(25, 27, 40));
        mainPanel.add(sidebar);

        JLabel logo = new JLabel("REMINDER");
        logo.setBounds(20, 20, 160, 30);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidebar.add(logo);

        JLabel subtitle = new JLabel("Manage your events easily");
        subtitle.setBounds(20, 50, 160, 20);
        subtitle.setForeground(new Color(150,150,170));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sidebar.add(subtitle);

        // Sidebar Buttons
        allNotesBtn = sidebarButton("All Notes", purple, 100);
        allNotesBtn.addActionListener(this);
        sidebar.add(allNotesBtn);

        todayBtn = sidebarButton("Today", green, 150);
        todayBtn.addActionListener(this);
        sidebar.add(todayBtn);

        upcomingBtn = sidebarButton("Upcoming", blue, 200);
        upcomingBtn.addActionListener(this);
        sidebar.add(upcomingBtn);

        calendarBtn = sidebarButton("📅 Calender", blue, 250);
        calendarBtn.addActionListener(this);
        sidebar.add(calendarBtn);

        deleteNoteBtn = sidebarButton("Delete Note", red, 300);
        deleteNoteBtn.addActionListener(this);
        sidebar.add(deleteNoteBtn);

        exportBtn = sidebarButton("Export Notes", purple, 350);
        exportBtn.addActionListener(this);
        sidebar.add(exportBtn);

        // Main Content
        welcomeLabel = new JLabel("Welcome Back, " + userName );
        welcomeLabel.setBounds(220, 20, 400, 35);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(titleFont);
        mainPanel.add(welcomeLabel);

        // Form Panel
        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(220, 70, 750, 600);
        formPanel.setBackground(card);
        formPanel.setBorder(new LineBorder(new Color(90,90,140), 2, true));
        mainPanel.add(formPanel);

        // DATE SELECTION
        JLabel dateLabel = createLabel("Event Date:");
        dateLabel.setBounds(20, 20, 100, 25);
        formPanel.add(dateLabel);

        String[] days = new String[31];
        for(int i = 1; i <= 31; i++) days[i-1] = String.valueOf(i);
        dayBox = new JComboBox<>(days);
        dayBox.setBounds(20, 45, 60, 30);
        styleComboBox(dayBox);
        formPanel.add(dayBox);

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        monthBox = new JComboBox<>(months);
        monthBox.setBounds(90, 45, 80, 30);
        styleComboBox(monthBox);
        formPanel.add(monthBox);

        String[] years = {"2024","2025","2026","2027","2028"};
        yearBox = new JComboBox<>(years);
        yearBox.setBounds(180, 45, 80, 30);
        styleComboBox(yearBox);
        formPanel.add(yearBox);

        // TIME SELECTION - AM/PM
        JLabel timeLabel = createLabel("Event Time:");
        timeLabel.setBounds(20, 85, 100, 25);
        formPanel.add(timeLabel);

        String[] hours = new String[12];
        for(int i = 1; i <= 12; i++) hours[i-1] = String.valueOf(i);
        hourBox = new JComboBox<>(hours);
        hourBox.setBounds(20, 110, 50, 30);
        styleComboBox(hourBox);
        formPanel.add(hourBox);

        JLabel colonLabel = new JLabel(":");
        colonLabel.setBounds(75, 110, 10, 30);
        colonLabel.setForeground(Color.WHITE);
        colonLabel.setFont(boldFont);
        formPanel.add(colonLabel);

        String[] minutes = new String[60];
        for(int i = 0; i < 60; i++) minutes[i] = String.format("%02d", i);
        minuteBox = new JComboBox<>(minutes);
        minuteBox.setBounds(90, 110, 50, 30);
        styleComboBox(minuteBox);
        formPanel.add(minuteBox);

        String[] amPm = {"AM", "PM"};
        amPmBox = new JComboBox<>(amPm);
        amPmBox.setBounds(150, 110, 60, 30);
        styleComboBox(amPmBox);
        formPanel.add(amPmBox);

        // TABLE - *** ID HATA DIYA ***
        String[] columns = {"Title", "Date", "Time", "Email"}; // ID nahi hai
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table read-only
            }
        };
        notesTable = new JTable(tableModel);
        notesTable.setBackground(field);
        notesTable.setForeground(Color.WHITE);
        notesTable.setSelectionBackground(purple);
        notesTable.setFont(mainFont);
        notesTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(notesTable);
        scrollPane.setBounds(20, 160, 350, 380);
        scrollPane.getViewport().setBackground(field);
        formPanel.add(scrollPane);

        // Form Fields
        JLabel titleLabel = createLabel("Title:");
        titleLabel.setBounds(390, 20, 100, 25);
        formPanel.add(titleLabel);

        titleField = modernTextField();
        titleField.setBounds(390, 45, 340, 35);
        formPanel.add(titleField);

        JLabel occLabel = createLabel("Occasion:");
        occLabel.setBounds(390, 90, 100, 25);
        formPanel.add(occLabel);

        occasionField = modernTextField();
        occasionField.setBounds(390, 115, 340, 35);
        formPanel.add(occasionField);

        JLabel emailLabel = createLabel("Recipient Email:");
        emailLabel.setBounds(390, 160, 150, 25);
        formPanel.add(emailLabel);

        recipientEmailField = modernTextField();
        recipientEmailField.setBounds(390, 185, 340, 35);
        formPanel.add(recipientEmailField);

        JLabel alertLabel = createLabel("Alert Before Event:");
        alertLabel.setBounds(390, 230, 150, 25);
        formPanel.add(alertLabel);

        String[] alertOptions = {"1 minute", "5 minutes", "10 minutes", "30 minutes", "1 hour", "1 day"};
        alertBox = new JComboBox<>(alertOptions);
        alertBox.setBounds(390, 255, 340, 35);
        styleComboBox(alertBox);
        formPanel.add(alertBox);

        JLabel descLabel = createLabel("Description:");
        descLabel.setBounds(390, 300, 100, 25);
        formPanel.add(descLabel);

        descArea = new JTextArea();
        descArea.setBackground(field);
        descArea.setForeground(Color.WHITE);
        descArea.setCaretColor(Color.WHITE);
        descArea.setFont(mainFont);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBounds(390, 325, 340, 100);
        formPanel.add(descScroll);

        // Buttons
        addBtn = modernButton("+ Add Reminder", green);
        addBtn.setBounds(390, 440, 160, 40);
        addBtn.addActionListener(this);
        formPanel.add(addBtn);

        updateBtn = modernButton("Update", blue);
        updateBtn.setBounds(570, 440, 160, 40);
        updateBtn.addActionListener(this);
        formPanel.add(updateBtn);

        deleteBtn = modernButton("Delete", red);
        deleteBtn.setBounds(390, 490, 160, 40);
        deleteBtn.addActionListener(this);
        formPanel.add(deleteBtn);

        logoutBtn = modernButton("Logout", purple);
        logoutBtn.setBounds(570, 490, 160, 40);
        logoutBtn.addActionListener(this);
        formPanel.add(logoutBtn);

        // Set current date as default
        LocalDate today = LocalDate.now();
        dayBox.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        monthBox.setSelectedIndex(today.getMonthValue() - 1);
        yearBox.setSelectedItem(String.valueOf(today.getYear()));

        loadNotes();
        setVisible(true);

        // Background Alert Service
        new ReminderAlert();
    }

    void loadNotes() {
        try {
            tableModel.setRowCount(0);
            String query = isGuest?
                "SELECT * FROM reminders WHERE user_id=0 ORDER BY event_date, event_time" :
                "SELECT * FROM reminders WHERE user_id=? ORDER BY event_date, event_time";
            PreparedStatement pstmt = con.prepareStatement(query);
            if(!isGuest) pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {
                    // *** ID HATA DIYA - AB NAHI DIKHEGA ***
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("recipient_email")
                };
                tableModel.addRow(row);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void loadTodayNotes() {
        try {
            tableModel.setRowCount(0);
            String query = isGuest?
                "SELECT * FROM reminders WHERE user_id=0 AND event_date = CURDATE() ORDER BY event_time" :
                "SELECT * FROM reminders WHERE user_id=? AND event_date = CURDATE() ORDER BY event_time";
            PreparedStatement pstmt = con.prepareStatement(query);
            if(!isGuest) pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("recipient_email")
                };
                tableModel.addRow(row);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void loadUpcomingNotes() {
        try {
            tableModel.setRowCount(0);
            String query = isGuest?
                "SELECT * FROM reminders WHERE user_id=0 AND event_date > CURDATE() ORDER BY event_date, event_time" :
                "SELECT * FROM reminders WHERE user_id=? AND event_date > CURDATE() ORDER BY event_date, event_time";
            PreparedStatement pstmt = con.prepareStatement(query);
            if(!isGuest) pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("recipient_email")
                };
                tableModel.addRow(row);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if(src == allNotesBtn) {
            loadNotes();
        }
        else if(src == todayBtn) {
            loadTodayNotes();
        }
        else if(src == upcomingBtn) {
            loadUpcomingNotes();
        }
        else if(src == calendarBtn) {
            new CalendarView(this, con).setVisible(true);
        }
        else if(src == deleteNoteBtn) {
            deleteBtn.doClick();
        }
        else if(src == exportBtn) {
            ExportNotes.exportToFile(con, this);
        }

        else if(src == addBtn) {
            String title = titleField.getText().trim();
            String occasion = occasionField.getText().trim();
            String email = recipientEmailField.getText().trim();
            String desc = descArea.getText().trim();
            String alert = (String) alertBox.getSelectedItem();

            if(title.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title aur Email jaruri hai!");
                return;
            }

            String date = yearBox.getSelectedItem() + "-" +
                         String.format("%02d", monthBox.getSelectedIndex() + 1) + "-" +
                         String.format("%02d", Integer.parseInt((String)dayBox.getSelectedItem()));

            // 12-Hour ko 24-Hour me convert
            int hour = Integer.parseInt((String)hourBox.getSelectedItem());
            String amPm = (String)amPmBox.getSelectedItem();
            String minute = (String)minuteBox.getSelectedItem();

            if(amPm.equals("PM") && hour!= 12) {
                hour += 12;
            } else if(amPm.equals("AM") && hour == 12) {
                hour = 0;
            }

            String time = String.format("%02d:%s:00", hour, minute);

            try {
                PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO reminders(user_id,title,occasion,event_date,event_time,recipient_email,description,alert_before) VALUES(?,?,?,?,?,?,?,?)"
                );
                pstmt.setInt(1, isGuest? 0 : userId);
                pstmt.setString(2, title);
                pstmt.setString(3, occasion);
                pstmt.setString(4, date);
                pstmt.setString(5, time);
                pstmt.setString(6, email);
                pstmt.setString(7, desc);
                pstmt.setString(8, alert);
                pstmt.executeUpdate();

                // Confirmation email
                String displayTime = hourBox.getSelectedItem() + ":" + minute + " " + amPm;
                EmailSender.sendEmail(
                    reminderSenderEmail,
                    reminderAppPassword,
                    email,
                    "✅ Reminder Set: " + title,
                    "Hi,\n\nReminder successfully set:\n\n" +
                    "📌 Event: " + title + "\n" +
                    "🎉 Occasion: " + occasion + "\n" +
                    "📅 Date: " + date + "\n" +
                    "⏰ Time: " + displayTime + "\n" +
                    "🔔 Alert: " + alert + " before event\n\n" +
                    "Note: " + desc + "\n\n" +
                    "We'll notify you " + alert + " before the event.\n\n" +
                    "Thanks,\nReminderApp Team"
                );

                JOptionPane.showMessageDialog(this, "Reminder Added! Confirmation email sent.");
                loadNotes();
                clearFields();

            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

// databases delete
        else if(src == deleteBtn) {
            int row = notesTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "Pehle ek note select karo");
                return;
            }

            String title = (String) tableModel.getValueAt(row, 0);
            String date = (String) tableModel.getValueAt(row, 1);
            String time = (String) tableModel.getValueAt(row, 2);

            try {
                // Title + Date + Time se unique record delete karo
                PreparedStatement pstmt = con.prepareStatement(
                    "DELETE FROM reminders WHERE title=? AND event_date=? AND event_time=? AND user_id=?"
                );
                pstmt.setString(1, title);
                pstmt.setString(2, date);
                pstmt.setString(3, time);
                pstmt.setInt(4, isGuest? 0 : userId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Note Deleted!");
                loadNotes();
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        else if(src == logoutBtn) {
            dispose();
            new LoginPage();
        }
    }

    void clearFields() {
        titleField.setText("");
        occasionField.setText("");
        recipientEmailField.setText("");
        descArea.setText("");
    }

    JButton sidebarButton(String text, Color color, int yPos) {
        JButton btn = new JButton(text);
        btn.setBounds(10, yPos, 180, 40);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(boldFont);
        return btn;
    }

    void styleComboBox(JComboBox<String> box) {
        box.setBackground(field);
        box.setForeground(Color.WHITE);
        box.setFont(mainFont);
    }

    JButton modernButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(boldFont);
        return btn;
    }

    JTextField modernTextField() {
        JTextField fieldBox = new JTextField();
        fieldBox.setBackground(field);
        fieldBox.setForeground(Color.WHITE);
        fieldBox.setCaretColor(Color.WHITE);
        fieldBox.setFont(mainFont);
        fieldBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(90,90,140), 1, true),
                new EmptyBorder(5,10,5,10)
        ));
        return fieldBox;
    }

    JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(boldFont);
        return label;
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color color1 = new Color(15,15,25);
            Color color2 = new Color(70,35,120);
            GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0,0,width,height);
        }

    }
}