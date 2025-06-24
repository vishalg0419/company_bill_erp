package com.billing.ui;

import com.billing.DBConnection;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class EditBillUI extends JFrame {
	private JTable itemTable;
	private DefaultTableModel tableModel;
	private JLabel totalLabel;
	private JTextField billField, nameField, addressField;
	private JDateChooser dateChooser;
	private int totalAmount = 0;
	private int billId;

	private DashboardUI dashboard;

	public EditBillUI(int billId, DashboardUI dashboard) {
		this.dashboard = dashboard;
		this.billId = billId;

		setTitle("Edit Bill - Swati Cool Services");
		setSize(800, 900);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);

		JLabel companyName = new JLabel("SWATI COOL SERVICES", SwingConstants.CENTER);
		companyName.setFont(new Font("Arial", Font.BOLD, 20));
		companyName.setBounds(200, 20, 400, 30);
		add(companyName);

		JLabel companyTag = new JLabel("WINDOWS & SPLIT AIRCONDITIONERS MAINTENANCE & SERVICES", SwingConstants.CENTER);
		companyTag.setFont(new Font("Arial", Font.PLAIN, 14));
		companyTag.setBounds(100, 50, 600, 25);
		add(companyTag);

		JLabel companyAddress = new JLabel(
				"Pandit Lal Tiwari Road, Durga Chawl, Sanjay Nagar, Kandivali(W), Mumbai-400 067.",
				SwingConstants.CENTER);
		companyAddress.setFont(new Font("Arial", Font.PLAIN, 12));
		companyAddress.setBounds(80, 75, 650, 20);
		add(companyAddress);

		add(new JSeparator()).setBounds(30, 100, 720, 2);

		JLabel noLabel = new JLabel("Bill No.:");
		noLabel.setBounds(50, 120, 80, 20);
		add(noLabel);

		billField = new JTextField();
		billField.setBounds(130, 120, 200, 22);
		add(billField);

		JLabel nameLabel = new JLabel("Mr./Mrs.:");
		nameLabel.setBounds(50, 155, 80, 20);
		add(nameLabel);

		nameField = new JTextField();
		nameField.setBounds(130, 155, 300, 22);
		add(nameField);

		JLabel addressLabel = new JLabel("Customer Address:");
		addressLabel.setBounds(50, 190, 150, 20);
		add(addressLabel);

		addressField = new JTextField();
		addressField.setBounds(130, 211, 400, 60);
		add(addressField);

		JLabel dateLabel = new JLabel("Date:");
		dateLabel.setBounds(550, 120, 50, 20);
		add(dateLabel);

		dateChooser = new JDateChooser();
		dateChooser.setBounds(600, 120, 120, 22);
		add(dateChooser);

		add(new JSeparator()).setBounds(30, 290, 720, 2);

		String[] columns = { "SR.NO.", "PARTICULARS", "RATE", "QTY", "AMOUNT", "✎", "🗑" };
		tableModel = new DefaultTableModel(null, columns);
		itemTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return column == 5 || column == 6;
			}
		};

		itemTable.getColumn("✎").setCellRenderer(new ButtonRenderer("✎"));
		itemTable.getColumn("✎").setCellEditor(new ButtonEditor(new JCheckBox(), "edit"));
		itemTable.getColumn("🗑").setCellRenderer(new ButtonRenderer("🗑"));
		itemTable.getColumn("🗑").setCellEditor(new ButtonEditor(new JCheckBox(), "delete"));

		JScrollPane tableScroll = new JScrollPane(itemTable);
		tableScroll.setBounds(30, 310, 720, 200);
		add(tableScroll);

		JButton addItem = new JButton("Add Item");
		addItem.setBounds(320, 520, 150, 30);
		add(addItem);

		totalLabel = new JLabel("TOTAL: ₹0", SwingConstants.RIGHT);
		totalLabel.setBounds(530, 560, 200, 30);
		add(totalLabel);

		JButton updateBtn = new JButton("Update Bill");
		updateBtn.setBounds(320, 770, 160, 35);
		add(updateBtn);

		addItem.addActionListener(e -> showItemDialog());
		updateBtn.addActionListener(e -> submit());

		loadBillDetails(billId);

		setVisible(true);
	}

	private void loadBillDetails(int billId) {
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM bills WHERE bill_id = ?");
			ps.setInt(1, billId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				billField.setText(String.valueOf(rs.getInt("bill_no")));
				nameField.setText(rs.getString("customer_name"));
				addressField.setText(rs.getString("customer_address"));
				dateChooser.setDate(rs.getDate("bill_date"));
				totalAmount = rs.getInt("total_amount");
				totalLabel.setText("TOTAL: ₹" + totalAmount);
			}

			PreparedStatement itemStmt = conn.prepareStatement("SELECT * FROM bill_items WHERE bill_id = ?");
			itemStmt.setInt(1, billId);
			ResultSet items = itemStmt.executeQuery();
			int sr = 1;
			while (items.next()) {
				tableModel.addRow(new Object[] { String.valueOf(sr++), items.getString("particulars"),
						items.getInt("rate"), items.getInt("quantity"), items.getInt("amount"), "✎", "🗑" });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void submit() {
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);
			int billNo = Integer.parseInt(billField.getText());
			String customerName = nameField.getText();
			String customerAddress = addressField.getText();
			String billDate = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());

			PreparedStatement ps = conn.prepareStatement(
					"UPDATE bills SET bill_no=?, customer_name=?, customer_address=?, bill_date=?, total_amount=? WHERE bill_id = ?");
			ps.setInt(1, billNo);
			ps.setString(2, customerName);
			ps.setString(3, customerAddress);
			ps.setDate(4, java.sql.Date.valueOf(billDate));
			ps.setInt(5, totalAmount);
			ps.setInt(6, billId);
			ps.executeUpdate();

			PreparedStatement deleteItems = conn.prepareStatement("DELETE FROM bill_items WHERE bill_id = ?");
			deleteItems.setInt(1, billId);
			deleteItems.executeUpdate();

			PreparedStatement insertItem = conn.prepareStatement(
					"INSERT INTO bill_items (bill_id, particulars, rate, quantity, amount) VALUES (?, ?, ?, ?, ?)");
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				insertItem.setInt(1, billId);
				insertItem.setString(2, tableModel.getValueAt(i, 1).toString());
				insertItem.setInt(3, Integer.parseInt(tableModel.getValueAt(i, 2).toString()));
				insertItem.setInt(4, Integer.parseInt(tableModel.getValueAt(i, 3).toString()));
				insertItem.setInt(5, Integer.parseInt(tableModel.getValueAt(i, 4).toString()));
				insertItem.addBatch();
			}
			insertItem.executeBatch();
			conn.commit();

			JOptionPane.showMessageDialog(this, "Bill updated successfully!");
			dashboard.refreshTable();
			dispose(); // Close EditBillUI

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to update bill: " + ex.getMessage());
		}
	}

	private void showItemDialog() {
		JTextField particulars = new JTextField();
		JTextField rate = new JTextField();
		JTextField qty = new JTextField();

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Particulars:"));
		panel.add(particulars);
		panel.add(new JLabel("Rate:"));
		panel.add(rate);
		panel.add(new JLabel("Quantity:"));
		panel.add(qty);

		int result = JOptionPane.showConfirmDialog(this, panel, "Add Item", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				int r = Integer.parseInt(rate.getText());
				int q = Integer.parseInt(qty.getText());
				int amt = r * q;
				totalAmount += amt;
				totalLabel.setText("TOTAL: ₹" + totalAmount);
				String sr = String.valueOf(tableModel.getRowCount() + 1);
				tableModel.addRow(new Object[] { sr, particulars.getText(), r, q, amt, "✎", "🗑" });
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Invalid input!");
			}
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {
		public ButtonRenderer(String symbol) {
			setText(symbol);
			setFont(new Font("Arial", Font.BOLD, 12));
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private String action;
		private JButton button;
		private int editingRow;

		public ButtonEditor(JCheckBox checkBox, String action) {
			super(checkBox);
			this.action = action;
			button = new JButton(action.equals("edit") ? "✎" : "🗑");
			button.addActionListener(e -> fireEditingStopped());
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			editingRow = row;
			return button;
		}

		public Object getCellEditorValue() {
			if ("edit".equals(action)) {
				editRow(editingRow);
			} else {
				deleteRow(editingRow);
			}
			return null;
		}

		private void editRow(int row) {
			JTextField partField = new JTextField(tableModel.getValueAt(row, 1).toString());
			JTextField rateField = new JTextField(tableModel.getValueAt(row, 2).toString());
			JTextField qtyField = new JTextField(tableModel.getValueAt(row, 3).toString());

			JPanel panel = new JPanel(new GridLayout(3, 2));
			panel.add(new JLabel("Particulars:"));
			panel.add(partField);
			panel.add(new JLabel("Rate:"));
			panel.add(rateField);
			panel.add(new JLabel("Quantity:"));
			panel.add(qtyField);

			int result = JOptionPane.showConfirmDialog(null, panel, "Edit Item", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				try {
					int rate = Integer.parseInt(rateField.getText());
					int qty = Integer.parseInt(qtyField.getText());
					int amt = rate * qty;

					totalAmount -= Integer.parseInt(tableModel.getValueAt(row, 4).toString());
					totalAmount += amt;
					totalLabel.setText("TOTAL: ₹" + totalAmount);

					tableModel.setValueAt(partField.getText(), row, 1);
					tableModel.setValueAt(rate, row, 2);
					tableModel.setValueAt(qty, row, 3);
					tableModel.setValueAt(amt, row, 4);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid input");
				}
			}
		}

		private void deleteRow(int row) {
			totalAmount -= Integer.parseInt(tableModel.getValueAt(row, 4).toString());
			tableModel.removeRow(row);
			totalLabel.setText("TOTAL: ₹" + totalAmount);
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				tableModel.setValueAt(String.valueOf(i + 1), i, 0);
			}
		}
	}

}
