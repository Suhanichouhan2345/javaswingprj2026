package com.Reminder_Event;

import javax.swing.*;
import java.io.FileWriter;
import java.sql.*;

public class ExportNotes {
    
    public static void exportToFile(Connection con, JFrame parent) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Notes Export Karo");
            fileChooser.setSelectedFile(new java.io.File("MyReminders.txt"));
            
            int userSelection = fileChooser.showSaveDialog(parent);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                
                // *** YAHAN CHANGE KIYA - reminders table se data le raha ***
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM reminders ORDER BY event_date ASC, event_time ASC");
                
                FileWriter writer = new FileWriter(fileToSave);
                writer.write("===== MY REMINDER NOTES =====\n\n");
                
                int count = 1;
                while (rs.next()) {
                    writer.write("Note #" + count + "\n");
                    writer.write("Title: " + rs.getString("title") + "\n");
                    writer.write("Occasion: " + rs.getString("occasion") + "\n");
                    writer.write("Date: " + rs.getString("event_date") + "\n");
                    writer.write("Time: " + rs.getString("event_time") + "\n");
                    writer.write("Email: " + rs.getString("recipient_email") + "\n");
                    writer.write("Alert: " + rs.getString("alert_before") + "\n");
                    writer.write("Description: " + rs.getString("description") + "\n");
                    writer.write("----------------------------\n\n");
                    count++;
                }
                
                writer.close();
                rs.close();
                stmt.close();
                
                JOptionPane.showMessageDialog(parent, "Export Done! ✅\nFile saved: " + fileToSave.getAbsolutePath());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Export Error: " + e.getMessage());
        }
    }
}