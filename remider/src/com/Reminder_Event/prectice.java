package com.Reminder_Event;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class prectice extends JFrame implements ActionListener {

    // --- COMPONENTS ---
    JTextField nameField;
    JButton addBtn, clearBtn;
    JList<String> myList;
    DefaultListModel<String> listModel;
    JLabel headingLabel;

    // --- COLORS ---
    Color bgDark   = new Color(30,  30,  50);
    Color purple   = new Color(140, 82,  255);
    Color green    = new Color(0,   200, 100);
    Color red      = new Color(255, 80,  80);
    Color cardBg   = new Color(45,  48,  65);

    // --- FONT ---
    Font myFont = new Font("Segoe UI", Font.BOLD, 14);

    prectice() {
        // Window setup
        setTitle("Mini Reminder");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(bgDark);

        // Heading
        headingLabel = new JLabel("Mera Reminder App");
        headingLabel.setBounds(130, 20, 300, 40);
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(headingLabel);

        // Text field — naam daalne ke liye
        nameField = new JTextField();
        nameField.setBounds(50, 80, 280, 40);
        nameField.setBackground(cardBg);
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setFont(myFont);
        add(nameField);

        // Add button — green
        addBtn = new JButton("Add");
        addBtn.setBounds(350, 80, 100, 40);
        addBtn.setBackground(green);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(myFont);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(this);
        add(addBtn);

        // List — items yahan dikhenge
        listModel = new DefaultListModel<>();
        myList = new JList<>(listModel);
        myList.setBackground(cardBg);
        myList.setForeground(Color.WHITE);
        myList.setFont(myFont);
        myList.setSelectionBackground(purple);

        JScrollPane scroll = new JScrollPane(myList);
        scroll.setBounds(50, 140, 400, 200);
        add(scroll);

        // Clear button — red
        clearBtn = new JButton("Clear List");
        clearBtn.setBounds(150, 360, 180, 40);
        clearBtn.setBackground(red);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(myFont);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(this);
        add(clearBtn);

        setVisible(true);
    }

    // Button click hone pe yeh chalta hai
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addBtn) {
            String text = nameField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement("→ " + text);
                nameField.setText(""); 
            } else {
                JOptionPane.showMessageDialog(this, "Kuch likho pehle!");
            }
        }

        if (e.getSource() == clearBtn) {
            listModel.clear();  
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new prectice());
    }
}
