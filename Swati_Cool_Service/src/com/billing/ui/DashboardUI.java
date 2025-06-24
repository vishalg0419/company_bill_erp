package com.billing.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardUI extends JFrame {

    private JTable billTable;
    private DefaultTableModel tableModel;
    private JLabel dateTimeLabel;

    public DashboardUI() {
        setTitle("Swati Cool Services - Billing Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === HEADER PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(new Color(240, 248, 255)); // Light Blue background

        JLabel companyTitle = new JLabel("SWATI COOL SERVICES - BILLING SYSTEM", SwingConstants.CENTER);
        companyTitle.setFont(new Font("Arial", Font.BOLD, 24));
        companyTitle.setForeground(new Color(0, 102, 204));

        dateTimeLabel = new JLabel(getCurrentDateTime(), SwingConstants.RIGHT);
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Timer timer = new Timer(1000, e -> dateTimeLabel.setText(getCurrentDateTime()));
        timer.start();

        topPanel.add(companyTitle, BorderLayout.NORTH);
        topPanel.add(dateTimeLabel, BorderLayout.EAST);
     

        // === BUTTON PANEL ===
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton createBillBtn = new JButton("➕ Create Bill");
        JButton createEstimateBtn = new JButton("➕ Create Estimate");
        JButton reportBtn = new JButton("📝  Report");

        for (JButton btn : new JButton[] { createBillBtn, createEstimateBtn, reportBtn }) {
            btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            btn.setBackground(new Color(0, 153, 255));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        actionPanel.add(createBillBtn);
        actionPanel.add(createEstimateBtn);
        actionPanel.add(reportBtn);
      
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        // === TABLE SETUP ===
        String[] columns = {"BIll ID", "Bill No", "Customer Name", "Date", "Amount", "Edit", "Delete", "Menu" };
        tableModel = new DefaultTableModel(columns, 0);
        billTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column >= 4;
            }

            @Override
        
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                Font font;
                if (column == 4) {
                    font = new Font("Segoe UI", Font.BOLD, 14); // Amount column
                } else {
                    font = new Font("Segoe UI Emoji", Font.PLAIN, 14); // All others
                }

                comp.setFont(font);
                ((JComponent) comp).setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

                return comp;
            }


            @Override
            public int getRowHeight() {
                return 45;
            }
        };

        TableColumnModel columnModel = billTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // Bill ID
        columnModel.getColumn(1).setPreferredWidth(40);  // Bill No
        columnModel.getColumn(2).setPreferredWidth(350); // Customer Name
        columnModel.getColumn(3).setPreferredWidth(150); // Date
        columnModel.getColumn(4).setPreferredWidth(130); // Amount
        columnModel.getColumn(5).setPreferredWidth(50);  // Edit
        columnModel.getColumn(6).setPreferredWidth(50);  // Delete
        columnModel.getColumn(7).setPreferredWidth(40);  // Menu

        billTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("✏️", new Color(0, 200, 0)));
        billTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "edit", "✏️", new Color(0, 200, 0)));

        billTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("🗑", new Color(255, 69, 58)));
        billTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "delete", "🗑", new Color(255, 69, 58)));

        billTable.getColumn("Menu").setCellRenderer(new ButtonRenderer("⋮", new Color(200, 200, 200)));
        billTable.getColumn("Menu").setCellEditor(new ButtonEditor(new JCheckBox(), "menu", "⋮", new Color(200, 200, 200)));

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("\uD83D\uDCCB Bill Records"));
        add(scrollPane, BorderLayout.CENTER);

        loadBillsFromDatabase();

        createBillBtn.addActionListener(e -> new BillUI());
        reportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Report feature coming soon."));
        setVisible(true);
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("dd MMM yyyy - HH:mm:ss").format(new Date());
    }

    private void loadBillsFromDatabase() {
        try (Connection conn = com.billing.DBConnection.getConnection()) {
            String query = "SELECT bill_id,bill_no, customer_name, bill_date, total_amount FROM bills ORDER BY bill_id DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
            	 int billId = rs.getInt("bill_id");
                int billNo = rs.getInt("bill_no");
                String name = rs.getString("customer_name");
                Date date = rs.getDate("bill_date");
                int total = rs.getInt("total_amount");

                tableModel.addRow(new Object[] { billId,billNo, name, date.toString(), "\u20B9" + total, "✏️", "🗑", "︙" });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String symbol, Color bgColor) {
            setText(symbol);
            setFont(new Font("Segoe UI Emoji  " , Font.BOLD, 18));
            setBackground(bgColor);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
    	private String action;
    	private String symbol;
    	private JButton button;
    	private int selectedRow;

    	public ButtonEditor(JCheckBox checkBox, String action, String symbol, Color bgColor) {
    		super(checkBox);
    		this.action = action;
    		this.symbol = symbol;

    		button = new JButton(symbol); // set icon permanently
    		button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
    		button.setBackground(bgColor);
    		button.setFocusPainted(false);
    		button.addActionListener(e -> fireEditingStopped());
    	}

    	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    		selectedRow = row;
    		return button;
    	}

    	public Object getCellEditorValue() {
    		int billid = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

    		if (action.equals("edit")) {
    			JOptionPane.showMessageDialog(null, "Editing Bill No: " + billid);
    		} else if (action.equals("delete")) {
    			int confirm = JOptionPane.showConfirmDialog(null,
    					"Are you sure you want to delete bill #" + billid + "?", "Confirm Delete",
    					JOptionPane.YES_NO_OPTION);
    			if (confirm == JOptionPane.YES_OPTION) {
    				try (Connection conn = com.billing.DBConnection.getConnection()) {
    					PreparedStatement ps1 = conn.prepareStatement(
    							"DELETE FROM bill_items WHERE bill_id = ?");
    					ps1.setInt(1, billid);
    					ps1.executeUpdate();

    					PreparedStatement ps2 = conn.prepareStatement("DELETE FROM bills WHERE bill_id = ?");
    					ps2.setInt(1, billid);
    					ps2.executeUpdate();

    					tableModel.removeRow(selectedRow);
    					JOptionPane.showMessageDialog(null, "Bill deleted successfully.");
    				} catch (Exception ex) {
    					ex.printStackTrace();
    					JOptionPane.showMessageDialog(null, "Error deleting bill: " + ex.getMessage());
    				}
    			}
    		} else if (action.equals("menu")) {
    			JPopupMenu popup = new JPopupMenu();
    			JMenuItem view = new JMenuItem("👁 View Bill");
    			JMenuItem download = new JMenuItem("⬇ Download PDF");
    			JMenuItem share = new JMenuItem("🔗 Share");

    			view.addActionListener(e -> JOptionPane.showMessageDialog(null, "Viewing Bill ID: " + billid));
    			download.addActionListener(e -> JOptionPane.showMessageDialog(null, "Downloading PDF for Bill ID: " + billid));
    			share.addActionListener(e -> JOptionPane.showMessageDialog(null, "Sharing Bill ID: " + billid));

    			popup.add(view);
    			popup.add(download);
    			popup.add(share);

    			popup.show(button, button.getWidth() / 2, button.getHeight() / 2);
    		}
    		return action;
    	}
    }

    public static void main(String[] args) {
    new DashboardUI();
    }
}
