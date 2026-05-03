package org.example.hostelsystem.ui;

import org.example.hostelsystem.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public MainDashboard() {
        setTitle("Hostel Management System - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initMenuBar();
        initComponents();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(this::logout);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Hostel Management System v1.0\nDeveloped for hostel operations management.",
            "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setPreferredSize(new Dimension(0, 50));

        JLabel welcomeLabel = new JLabel("  Welcome, " + AuthService.getCurrentUser().getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel roleLabel = new JLabel("Role: " + AuthService.getCurrentUser().getRole() + "  ");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        topPanel.add(roleLabel, BorderLayout.EAST);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));

        if (AuthService.isAdmin()) {
            tabbedPane.addTab("Rooms", new RoomManagementPanel());
            tabbedPane.addTab("Residents", new ResidentManagementPanel());
            tabbedPane.addTab("Bookings", new BookingManagementPanel());
            tabbedPane.addTab("Attendance", new AttendancePanel());
            tabbedPane.addTab("Mess & Food", new MessManagementPanel());
            tabbedPane.addTab("Leave Management", new LeaveManagementPanel());
            tabbedPane.addTab("Late Arrival", new LateArrivalManagementPanel());
            tabbedPane.addTab("Student ID", new StudentIdManagementPanel());
            tabbedPane.addTab("Notifications", new NotificationManagementPanel());
            tabbedPane.addTab("Users", new UserManagementPanel());
        } else if (AuthService.isWarden()) {
            tabbedPane.addTab("Rooms", new RoomManagementPanel());
            tabbedPane.addTab("Residents", new ResidentManagementPanel());
            tabbedPane.addTab("Bookings", new BookingManagementPanel());
            tabbedPane.addTab("Attendance", new AttendancePanel());
            tabbedPane.addTab("Mess & Food", new MessManagementPanel());
            tabbedPane.addTab("Leave Management", new LeaveManagementPanel());
            tabbedPane.addTab("Late Arrival", new LateArrivalManagementPanel());
            tabbedPane.addTab("Student ID", new StudentIdManagementPanel());
            tabbedPane.addTab("Notifications", new NotificationManagementPanel());
        } else if (AuthService.isStudent()) {
            tabbedPane.addTab("My Profile", new StudentProfilePanel());
            tabbedPane.addTab("Attendance", new AttendancePanel());
            tabbedPane.addTab("Mess & Food", new MessStudentPanel());
            tabbedPane.addTab("Leave Management", new LeaveStudentPanel());
            tabbedPane.addTab("Late Arrival", new LateArrivalStudentPanel());
            tabbedPane.addTab("Student ID", new StudentIdStudentPanel());
            tabbedPane.addTab("Notifications", new NotificationStudentPanel());
        }

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void logout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new AuthService().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
