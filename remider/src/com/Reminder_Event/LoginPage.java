package com.Reminder_Event;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;
import java.util.Random;

public class LoginPage extends JFrame implements ActionListener {

    // DB config
    static final String DB_URL = "jdbc:mysql://localhost:3306/reminder_db";
    static final String USER = "root";
    static final String PASS = "Suhani22##";

    // email config (sender)
    String loginSenderEmail = "eventreminderapp11@gmail.com";
    String loginAppPassword = "pbfx rjbe rtdd hwmc";

    // UI fields
    JTextField emailField, nameField, otpField, signupEmailField;
    JPasswordField passField;
    JButton signupBtn, guestBtn, sendOtpBtn, verifyOtpBtn;

    JLabel titleLabel, emailLabel, passLabel, nameLabel, otpLabel;
    JPanel loginPanel, signupPanel;
    CardLayout cardLayout;
    JPanel mainPanel;

    Connection con;
    String generatedOTP = "";
    String tempEmail = "";

    // colors
    Color bg = new Color(18, 18, 28);
    Color card = new Color(32, 34, 48);
    Color field = new Color(45, 48, 65);
    Color purple = new Color(140, 82, 255);
    Color green = new Color(0, 220, 130);
    Color blue = new Color(0, 170, 255);

    Font mainFont = new Font("Segoe UI", Font.PLAIN, 15);
    Font boldFont = new Font("Segoe UI", Font.BOLD, 16);
    Font titleFont = new Font("Segoe UI", Font.BOLD, 32);

    LoginPage() {

        // DB connection
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed!");
        }

        // frame setup
        setTitle("ReminderApp - Login");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // background
        JPanel bgPanel = new GradientPanel();
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // title
        titleLabel = new JLabel("REMINDER APP");
        titleLabel.setBounds(0, 30, 500, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleFont);
        bgPanel.add(titleLabel);

        // subtitle
        JLabel subtitle = new JLabel("Login to manage your events");
        subtitle.setBounds(0, 70, 500, 25);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(180,180,200));
        subtitle.setFont(mainFont);
        bgPanel.add(subtitle);

        // main card
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(75, 120, 350, 380);
        mainPanel.setBackground(card);
        bgPanel.add(mainPanel);

        // login panel
        loginPanel = new JPanel(null);
        loginPanel.setBackground(card);
        createLoginPanel();
        mainPanel.add(loginPanel, "login");

        // signup panel
        signupPanel = new JPanel(null);
        signupPanel.setBackground(card);
        createSignupPanel();
        mainPanel.add(signupPanel, "signup");

        // guest button
        guestBtn = modernButton("Continue as Guest", blue);
        guestBtn.setBounds(150, 520, 200, 40);
        guestBtn.addActionListener(this);
        bgPanel.add(guestBtn);

        setVisible(true);
    }

    // LOGIN UI
    void createLoginPanel() {

        JLabel t = new JLabel("LOGIN WITH OTP");
        t.setBounds(0, 20, 350, 30);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(boldFont);
        loginPanel.add(t);

        emailLabel = createLabel("Email");
        emailLabel.setBounds(30, 70, 100, 20);
        loginPanel.add(emailLabel);

        emailField = modernTextField("");
        emailField.setBounds(30, 95, 290, 40);
        loginPanel.add(emailField);

        sendOtpBtn = modernButton("Send OTP", purple);
        sendOtpBtn.setBounds(30, 150, 290, 40);
        sendOtpBtn.addActionListener(this);
        loginPanel.add(sendOtpBtn);

        otpLabel = createLabel("OTP");
        otpLabel.setBounds(30, 210, 100, 20);
        loginPanel.add(otpLabel);

        otpField = modernTextField("");
        otpField.setBounds(30, 235, 290, 40);
        otpField.setEnabled(false);
        loginPanel.add(otpField);

        verifyOtpBtn = modernButton("Verify & Login", green);
        verifyOtpBtn.setBounds(30, 290, 290, 40);
        verifyOtpBtn.setEnabled(false);
        verifyOtpBtn.addActionListener(this);
        loginPanel.add(verifyOtpBtn);

        JButton switchBtn = new JButton("New User? Sign Up");
        switchBtn.setBounds(30, 340, 290, 30);
        switchBtn.setForeground(purple);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setBorderPainted(false);
        switchBtn.addActionListener(e -> cardLayout.show(mainPanel, "signup"));
        loginPanel.add(switchBtn);
    }

    // SIGNUP UI
    void createSignupPanel() {

        JLabel t = new JLabel("SIGN UP");
        t.setBounds(0, 20, 350, 30);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(boldFont);
        signupPanel.add(t);

        nameLabel = createLabel("Name");
        nameLabel.setBounds(30, 60, 100, 20);
        signupPanel.add(nameLabel);

        nameField = modernTextField("");
        nameField.setBounds(30, 85, 290, 40);
        signupPanel.add(nameField);

        emailLabel = createLabel("Email");
        emailLabel.setBounds(30, 135, 100, 20);
        signupPanel.add(emailLabel);

        signupEmailField = modernTextField("");
        signupEmailField.setBounds(30, 160, 290, 40);
        signupPanel.add(signupEmailField);

        passLabel = createLabel("Password");
        passLabel.setBounds(30, 210, 100, 20);
        signupPanel.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(30, 235, 290, 40);
        passField.setBackground(field);
        passField.setForeground(Color.WHITE);
        signupPanel.add(passField);

        signupBtn = modernButton("Create Account", green);
        signupBtn.setBounds(30, 290, 290, 40);
        signupBtn.addActionListener(this);
        signupPanel.add(signupBtn);

        JButton switchBtn = new JButton("Already have account? Login");
        switchBtn.setBounds(30, 340, 290, 30);
        switchBtn.setForeground(purple);
        switchBtn.setContentAreaFilled(false);
        switchBtn.setBorderPainted(false);
        switchBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        signupPanel.add(switchBtn);
    }

    // actions
    public void actionPerformed(ActionEvent e) {

        // send otp
        if (e.getSource() == sendOtpBtn) {
            String email = emailField.getText();

            if (!email.contains("@")) return;

            try {
                Random r = new Random();
                generatedOTP = String.format("%06d", r.nextInt(999999));
                tempEmail = email;

                otpField.setEnabled(true);
                verifyOtpBtn.setEnabled(true);

                JOptionPane.showMessageDialog(this, "OTP Sent!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // verify otp
        if (e.getSource() == verifyOtpBtn) {
            if (otpField.getText().equals(generatedOTP)) {
                JOptionPane.showMessageDialog(this, "Login Success");
            } else {
                JOptionPane.showMessageDialog(this, "Wrong OTP");
            }
        }

        // guest
        if (e.getSource() == guestBtn) {
            dispose();
            new ReminderApp(0, "Guest", true);
        }
    }

    // UI helpers
    JButton modernButton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    JTextField modernTextField(String t) {
        JTextField f = new JTextField(t);
        f.setBackground(field);
        f.setForeground(Color.WHITE);
        return f;
    }

    JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        return l;
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}

// gradient background
class GradientPanel extends JPanel {
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0,
                new Color(15,15,25),
                getWidth(), getHeight(),
                new Color(70,35,120));
        g2.setPaint(gp);
        g2.fillRect(0,0,getWidth(),getHeight());
    }
}