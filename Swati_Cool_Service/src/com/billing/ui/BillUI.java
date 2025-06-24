package com.billing.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.text.SimpleDateFormat;
import com.billing.*;

import com.toedter.calendar.JDateChooser; // Import this at the top

public class BillUI extends JFrame {
	private JTable itemTable;
	private DefaultTableModel tableModel;
	private JLabel totalLabel;
	private int totalAmount = 0;

	public BillUI() {
		setTitle("Billing Application");
		setSize(800, 900);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null); // Absolute layout

		// Company Name
		JLabel companyName = new JLabel("SWATI COOL SERVICES", SwingConstants.CENTER);
		companyName.setFont(new Font("Arial", Font.BOLD, 20));
		companyName.setBounds(200, 20, 400, 30);
		add(companyName);

		// Tagline
		JLabel companyTag = new JLabel("WINDOWS & SPLIT AIRCONDITIONERS MAINTENANCE & SERVICES", SwingConstants.CENTER);
		companyTag.setFont(new Font("Arial", Font.PLAIN, 14));
		companyTag.setBounds(100, 50, 600, 25);
		add(companyTag);

		// Address
		JLabel companyAddress = new JLabel(
				"Pandit Lal Tiwari Road, Durga Chawl, Sanjay Nagar, Kandivali(W), Mumbai-400 067.",
				SwingConstants.CENTER);
		companyAddress.setFont(new Font("Arial", Font.PLAIN, 12));
		companyAddress.setBounds(80, 75, 650, 20);
		add(companyAddress);

		// Separator
		JSeparator separator1 = new JSeparator();
		separator1.setBounds(30, 100, 720, 2);
		add(separator1);

		// Bill No.
		JLabel noLabel = new JLabel("Bill No.:");
		noLabel.setFont(new Font("Arial", Font.BOLD, 14));
		noLabel.setBounds(50, 120, 80, 20);
		add(noLabel);

		JTextField billField = new JTextField();
		billField.setFont(new Font("Arial", Font.PLAIN, 14));
		billField.setBounds(130, 120, 200, 22);
		add(billField);

		// Name
		JLabel nameLabel = new JLabel("Mr./Mrs.:");
		nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		nameLabel.setBounds(50, 155, 80, 20);
		add(nameLabel);

		JTextField nameField = new JTextField();
		nameField.setFont(new Font("Arial", Font.PLAIN, 14));
		nameField.setBounds(130, 155, 300, 22);
		add(nameField);

		// Address
		JLabel addressLabel = new JLabel("Customer Address:");
		addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
		addressLabel.setBounds(50, 190, 150, 20);
		add(addressLabel);

		JTextField addressField = new JTextField();
		addressField.setFont(new Font("Arial", Font.PLAIN, 14));
		addressField.setBounds(130, 211, 400, 60);
		add(addressField);

		JLabel dateLabel = new JLabel("Date:");
		dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
		dateLabel.setBounds(550, 120, 50, 20);
		add(dateLabel);

		// Calendar date chooser
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setBounds(600, 120, 120, 22);
		add(dateChooser);

		// Second separator
		JSeparator separator2 = new JSeparator();
		separator2.setBounds(30, 290, 720, 2);
		add(separator2);

		// Table setup
		String[] columns = { "SR.NO.", "PARTICULARS", "RATE", "QTY", "AMOUNT", "✎", "🗑" };
		tableModel = new DefaultTableModel(null, columns);
		itemTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return column == 5 || column == 6; // Only edit/delete columns
			}
		};

		itemTable.getColumn("✎").setCellRenderer(new ButtonRenderer("✎"));
		itemTable.getColumn("✎").setCellEditor(new ButtonEditor(new JCheckBox(), "edit"));
		itemTable.getColumn("🗑").setCellRenderer(new ButtonRenderer("🗑"));
		itemTable.getColumn("🗑").setCellEditor(new ButtonEditor(new JCheckBox(), "delete"));

		JScrollPane tableScroll = new JScrollPane(itemTable);
		tableScroll.setBounds(30, 310, 720, 200);
		add(tableScroll);

		// Add Item Button (Single Popup)
		JButton addRowButton = new JButton("Add Item");
		addRowButton.setBounds(320, 520, 150, 30);
		addRowButton.addActionListener(e -> showItemDialog());
		add(addRowButton);

		// Total
		totalLabel = new JLabel("TOTAL: ₹0", SwingConstants.RIGHT);
		totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
		totalLabel.setBounds(530, 560, 200, 30);
		add(totalLabel);

		// Signature
		JLabel receiverSignature = new JLabel("Receiver's Signature: ____________________________");
		receiverSignature.setBounds(50, 660, 400, 30);
		add(receiverSignature);

		JLabel preparedBy = new JLabel("Prepared By: RAM SAHAY GUPTA");
		preparedBy.setBounds(50, 690, 300, 30);
		add(preparedBy);

		// Bottom Buttons
		JButton clearBtn = new JButton("Clear");
		clearBtn.setBounds(100, 770, 120, 35);
		add(clearBtn);

		JButton submitBtn = new JButton("Submit");
		submitBtn.setBounds(240, 770, 120, 35);
		add(submitBtn);

		JButton pdfBtn = new JButton("Generate PDF");
		pdfBtn.setBounds(380, 770, 140, 35);
		add(pdfBtn);

		JButton excelBtn = new JButton("Generate Excel");
		excelBtn.setBounds(540, 770, 160, 35);
		add(excelBtn);

		setVisible(true);

		clearBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the form?",
					"Confirm Clear", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				billField.setText("");
				nameField.setText("");
				addressField.setText("");
				dateChooser.setDate(null);
				tableModel.setRowCount(0);
				totalAmount = 0;
				totalLabel.setText("TOTAL: ₹0");
				JOptionPane.showMessageDialog(this, "Form cleared!");
			}
		});

		submitBtn.addActionListener(e->{
			submit(submitBtn, billField, nameField, addressField, dateChooser);
		});

	}

	private void submit(JButton submitBtn, JTextField billField, JTextField nameField, JTextField addressField, JDateChooser dateChooser) {
	    if (nameField.getText().isEmpty() || addressField.getText().isEmpty()
	            || dateChooser.getDate() == null || tableModel.getRowCount() == 0) {
	        JOptionPane.showMessageDialog(this,
	                "Please fill all fields and add at least one item before submitting.", "Warning",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    int billNo=1;
	    try {
	        billNo = Integer.parseInt(billField.getText());
	    } catch (NumberFormatException e) {
	       // JOptionPane.showMessageDialog(this, "Invalid Bill No. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
	    }

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String customerName = nameField.getText();
	    String customerAddress = addressField.getText();
	    String billDate = sdf.format(dateChooser.getDate());

	    StringBuilder summary = new StringBuilder();
	    summary.append("Bill No: ").append(billNo).append("\nCustomer: ").append(customerName).append("\nAddress: ")
	            .append(customerAddress).append("\nDate: ").append(billDate).append("\n\nItems:\n");

	    for (int i = 0; i < tableModel.getRowCount(); i++) {
	        summary.append(tableModel.getValueAt(i, 1)).append(" - ₹").append(tableModel.getValueAt(i, 4))
	                .append("\n");
	    }

	    summary.append("\nTotal: ₹").append(totalAmount);

	    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit the bill?",
	            "Confirm Submit", JOptionPane.YES_NO_OPTION);

	    if (confirm == JOptionPane.YES_OPTION) {
	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false); // Begin transaction

	            // Insert into `bills`
	            String insertBillSQL = "INSERT INTO bills (bill_no, customer_name, customer_address, bill_date, total_amount) VALUES (?, ?, ?, ?, ?)";
	            PreparedStatement psBill = conn.prepareStatement(insertBillSQL, Statement.RETURN_GENERATED_KEYS);
	            psBill.setInt(1, billNo);
	            psBill.setString(2, customerName);
	            psBill.setString(3, customerAddress);
	            psBill.setDate(4, java.sql.Date.valueOf(billDate));
	            psBill.setInt(5, totalAmount);
	            psBill.executeUpdate();

	            ResultSet rs = psBill.getGeneratedKeys();
	            rs.next();
	            int billId = rs.getInt(1);

	            // Insert into `bill_items`
	            String insertItemSQL = "INSERT INTO bill_items (bill_id, particulars, rate, quantity, amount) VALUES (?, ?, ?, ?, ?)";
	            PreparedStatement psItem = conn.prepareStatement(insertItemSQL);

	            for (int i = 0; i < tableModel.getRowCount(); i++) {
	                psItem.setInt(1, billId);
	                psItem.setString(2, tableModel.getValueAt(i, 1).toString());
	                psItem.setInt(3, Integer.parseInt(tableModel.getValueAt(i, 2).toString()));
	                psItem.setInt(4, Integer.parseInt(tableModel.getValueAt(i, 3).toString()));
	                psItem.setInt(5, Integer.parseInt(tableModel.getValueAt(i, 4).toString()));
	                psItem.addBatch();
	            }

	            psItem.executeBatch();
	            conn.commit();

	            JOptionPane.showMessageDialog(this, "Bill submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    
	    PDFGenerator.generateBillPDF(
	    	    String.valueOf(billNo),
	    	    customerName,
	    	    customerAddress,
	    	    billDate,
	    	    totalAmount,
	    	    tableModel
	    	);

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
			String part = tableModel.getValueAt(row, 1).toString();
			String rate = tableModel.getValueAt(row, 2).toString();
			String qty = tableModel.getValueAt(row, 3).toString();

			JTextField partField = new JTextField(part);
			JTextField rateField = new JTextField(rate);
			JTextField qtyField = new JTextField(qty);

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
					int newRate = Integer.parseInt(rateField.getText());
					int newQty = Integer.parseInt(qtyField.getText());
					int newAmt = newRate * newQty;

					totalAmount -= Integer.parseInt(tableModel.getValueAt(row, 4).toString());
					totalAmount += newAmt;
					totalLabel.setText("TOTAL: ₹" + totalAmount);

					tableModel.setValueAt(partField.getText(), row, 1);
					tableModel.setValueAt(newRate, row, 2);
					tableModel.setValueAt(newQty, row, 3);
					tableModel.setValueAt(newAmt, row, 4);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid input");
				}
			}
		}

		private void deleteRow(int row) {
			int amt = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
			totalAmount -= amt;
			totalLabel.setText("TOTAL: ₹" + totalAmount);
			tableModel.removeRow(row);
			// update SR.NO.
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				tableModel.setValueAt(String.valueOf(i + 1), i, 0);
			}
		}
	}

	private void addItemRow() {
		String srNo = String.valueOf(tableModel.getRowCount() + 1);
		String particulars = JOptionPane.showInputDialog(this, "Enter Item Name:");
		String rateStr = JOptionPane.showInputDialog(this, "Enter Rate:");
		String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity:");

		try {
			int rate = Integer.parseInt(rateStr);
			int qty = Integer.parseInt(qtyStr);
			int amount = rate * qty;

			totalAmount += amount;
			totalLabel.setText("TOTAL: ₹" + totalAmount);

			tableModel.addRow(new Object[] { srNo, particulars, rate, qty, amount });
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
		}
	}

	public static void main(String[] args) {
		new BillUI();
	}
}
