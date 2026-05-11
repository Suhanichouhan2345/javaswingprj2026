package com.Reminder_Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ReminderAlert extends Thread {

    
    String reminderSenderEmail = "eventreminderapp11@gmail.com";
    String reminderAppPassword ="pbfx rjbe rtdd hwmc";
    // Database
    static final String DB_URL = "jdbc:mysql://localhost:3306/reminder_db";
    static final String USER = "root";
    static final String PASS = "Suhani22##";
    Connection con;

    public ReminderAlert() {
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("ReminderAlert Service Started ✅");
        } catch (Exception e) {
            System.out.println("Database Connection Failed in ReminderAlert");
            e.printStackTrace();
        }
        this.start(); // Thread start kar de
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkAndSendReminders();
                Thread.sleep(60000); // Har 1 minute me check karo
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void checkAndSendReminders() {
        try {
            LocalDateTime now = LocalDateTime.now(); // current time ordate 
            
            // Saare reminders fetch karo
            String query = "SELECT * FROM reminders";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String occasion = rs.getString("occasion");
                String eventDate = rs.getString("event_date");
                String eventTime = rs.getString("event_time");
                String recipientEmail = rs.getString("recipient_email");
                String description = rs.getString("description");
                String alertBefore = rs.getString("alert_before");

                // Event ka exact datetime banao
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime eventDateTime = LocalDateTime.parse(eventDate + " " + eventTime, formatter);

                // Alert time calculate karo
                LocalDateTime alertTime = calculateAlertTime(eventDateTime, alertBefore);

                // Check karo kya abhi alert bhejna hai
                long minutesDiff = ChronoUnit.MINUTES.between(now, alertTime);
      
                if (minutesDiff >= 0 && minutesDiff <= 1) {
                    if (!isAlertAlreadySent(id)) {
                        sendAlertEmail(id, title, occasion, eventDate, eventTime, recipientEmail, description, alertBefore);
                        markAlertAsSent(id);
                        System.out.println("Alert sent for: " + title + " to " + recipientEmail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocalDateTime calculateAlertTime(LocalDateTime eventDateTime, String alertBefore) {
        switch (alertBefore) {
            case "1 minute":
                return eventDateTime.minusMinutes(1);
            case "5 minutes":
                return eventDateTime.minusMinutes(5);
            case "10 minutes":
                return eventDateTime.minusMinutes(10);
            case "30 minutes":
                return eventDateTime.minusMinutes(30);
            case "1 hour":
                return eventDateTime.minusHours(1);
            case "1 day":
                return eventDateTime.minusDays(1);
            default:
                return eventDateTime.minusMinutes(1);
        }
    }

    void sendAlertEmail(int reminderId, String title, String occasion, String date, String time, 
                       String recipientEmail, String description, String alertBefore) {
    	try {
    	    String subject = "🔔 Reminder Alert: " + title + " in " + alertBefore;

    	    String body = "Dear User,\n\n" +
    	                 "This is a reminder notification for your upcoming event.\n\n" +
    	                 "━━━━━━━━━━━━━━━━━━━━━━\n" +
    	                 "📌 Event: " + title + "\n" +
    	                 "🎉 Occasion: " + occasion + "\n" +
    	                 "📅 Date: " + date + "\n" +
    	                 "⏰ Time: " + time + "\n" +
    	                 "📝 Description: " + description + "\n" +
    	                 "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
    	                 "Your event will begin in " + alertBefore + ".\n\n" +
    	                 "Please make the necessary preparations.\n\n" +
    	                 "Best Regards,\n" +
    	                 "ReminderApp Team";

    	    EmailSender.sendEmail(
    	        reminderSenderEmail,
    	        reminderAppPassword,
    	        recipientEmail,
    	        subject,
    	        body
    	    );

    	} catch (Exception e) {
    	    System.out.println("Failed to send alert email for ID: " + reminderId);
    	    e.printStackTrace();
    	}
    }

    boolean isAlertAlreadySent(int reminderId) {
        try {
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    void markAlertAsSent(int reminderId) {
       
    }
}