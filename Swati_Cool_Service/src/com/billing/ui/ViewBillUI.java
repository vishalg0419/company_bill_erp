package com.billing.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

import com.billing.DBConnection;

public class ViewBillUI extends JFrame {

    public ViewBillUI(int billId) {
        setTitle("View Bill");
        setSize(820, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // --- Header ---
        JLabel title = new JLabel("SWATI COOL SERVICES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));
        title.setBounds(210, 20, 400, 30);
        add(title);

        JLabel subTitle = new JLabel("WINDOWS & SPLIT AIRCONDITIONERS MAINTENANCE & SERVICES", SwingConstants.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setBounds(80, 55, 660, 20);
        add(subTitle);

        JLabel address = new JLabel("Pandit Lal Tiwari Road, Durga Chawl, Sanjay Nagar, Kandivali(W), Mumbai-400 067.", SwingConstants.CENTER);
        address.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        address.setBounds(80, 75, 660, 20);
        add(address);

        add(new JSeparator()).setBounds(30, 100, 760, 2);

        // --- Customer Info Layout ---
        JLabel lblBillNo = new JLabel();
        lblBillNo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBillNo.setBounds(50, 115, 300, 20);
        add(lblBillNo);

        JLabel lblDate = new JLabel("", SwingConstants.RIGHT);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setBounds(480, 115, 280, 20);
        add(lblDate);

        JLabel lblCustomer = new JLabel();
        lblCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCustomer.setBounds(50, 145, 700, 20);
        add(lblCustomer);

        JLabel lblAddress = new JLabel();
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAddress.setBounds(50, 170, 700, 20);
        add(lblAddress);

        // --- Item Table ---
        String[] cols = { "SR.NO.", "PARTICULARS", "RATE", "QTY", "AMOUNT" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(40);
        table.setEnabled(false);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(70);
        cm.getColumn(1).setPreferredWidth(380);
        cm.getColumn(2).setPreferredWidth(110);
        cm.getColumn(3).setPreferredWidth(80);
        cm.getColumn(4).setPreferredWidth(120);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        cm.getColumn(0).setCellRenderer(center);
        cm.getColumn(1).setCellRenderer(left);
        cm.getColumn(2).setCellRenderer(center);
        cm.getColumn(3).setCellRenderer(center);
        cm.getColumn(4).setCellRenderer(center);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(30, 210, 760, 335);
        pane.setBorder(BorderFactory.createTitledBorder("Bill Items"));
        add(pane);

        // --- Total & Footer ---
        JLabel totalLabel = new JLabel("TOTAL: ₹0", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setBounds(490, 560, 260, 30);
        add(totalLabel);

        JLabel sign = new JLabel("Receiver's Signature: ____________________", SwingConstants.LEFT);
        sign.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sign.setBounds(50, 625, 400, 30);
        add(sign);

        JLabel prepared = new JLabel("Prepared By: RAM SAHAY GUPTA", SwingConstants.LEFT);
        prepared.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prepared.setBounds(50, 655, 300, 30);
        add(prepared);

        // --- Load DB ---
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bills WHERE bill_id = ?");
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int billNo = rs.getInt("bill_no");
                String name = rs.getString("customer_name");
                String addr = rs.getString("customer_address");
                String date = rs.getDate("bill_date").toString();
                int total = rs.getInt("total_amount");

                lblBillNo.setText("Bill No: " + billNo);
                lblDate.setText("Date: " + date);
                lblCustomer.setText("Name: " + name);
                lblAddress.setText("Address: " + addr);
                totalLabel.setText("TOTAL: ₹" + total);
            }

            PreparedStatement psi = conn.prepareStatement("SELECT * FROM bill_items WHERE bill_id = ?");
            psi.setInt(1, billId);
            ResultSet ri = psi.executeQuery();
            int sr = 1;
            while (ri.next()) {
                model.addRow(new Object[]{
                        sr++,
                        ri.getString("particulars"),
                        ri.getInt("rate"),
                        ri.getInt("quantity"),
                        ri.getInt("amount")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load bill: " + ex.getMessage());
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewBillUI(1));
    }
}
