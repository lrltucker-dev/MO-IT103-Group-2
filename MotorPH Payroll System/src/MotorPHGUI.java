import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * MotorPH Payroll System — GUI Version
 * ----------------------------------------
 * Features:
 * Feature 1 — Display, View, and Add Employee Records (CSV-backed JTable)
 * Feature 2 — Salary Computation (Gross Pay, Deductions, Net Pay)
 * Feature 3 — Update and Delete Employee Records
 * Feature 4 — Payroll Summary (totals and averages)
 *
 * Rules:
 * - No OOP concepts (no custom classes, no constructors, no instance fields)
 * - All logic in static methods
 * - All file I/O through dedicated static methods
 * - Exception handling via JOptionPane
 */
public class MotorPHGUI {

    // ============================================================
    // FILE PATHS — update these to match your CSV locations
    // ============================================================
    static final String EMPLOYEE_FILE   = "Employee Details.csv";
    static final String ATTENDANCE_FILE = "Attendance Record.csv";

    // ============================================================
    // CSV COLUMN INDICES — Employee Details
    // ============================================================
    static final int COL_EMP_ID       = 0;
    static final int COL_LAST_NAME    = 1;
    static final int COL_FIRST_NAME   = 2;
    static final int COL_BIRTHDAY     = 3;
    static final int COL_ADDRESS      = 4;
    static final int COL_PHONE        = 5;
    static final int COL_SSS          = 6;
    static final int COL_PHILHEALTH   = 7;
    static final int COL_TIN          = 8;
    static final int COL_PAGIBIG      = 9;
    static final int COL_STATUS       = 10;
    static final int COL_POSITION     = 11;
    static final int COL_SUPERVISOR   = 12;
    static final int COL_BASIC_SAL    = 13;
    static final int COL_RICE         = 14;
    static final int COL_PHONE_ALLOW  = 15;
    static final int COL_CLOTHING     = 16;
    static final int COL_SEMI_MONTHLY = 17;
    static final int COL_HOURLY_RATE  = 18;

    // Attendance Record CSV columns
    static final int ATT_EMP_ID   = 0;
    static final int ATT_DATE     = 3;
    static final int ATT_TIME_IN  = 4;
    static final int ATT_TIME_OUT = 5;

    // ============================================================
    // TABLE COLUMN HEADERS shown in the JTable
    // ============================================================
    static final String[] TABLE_HEADERS = {
        "Emp #", "Last Name", "First Name", "Birthday",
        "SSS #", "PhilHealth #", "TIN", "Pag-IBIG #"
    };

    // Indices from the CSV that map to the table columns above
    static final int[] TABLE_COL_MAP = {
        COL_EMP_ID, COL_LAST_NAME, COL_FIRST_NAME, COL_BIRTHDAY,
        COL_SSS, COL_PHILHEALTH, COL_TIN, COL_PAGIBIG
    };

    // ============================================================
    // COLOR PALETTE
    // ============================================================
    static final Color COLOR_BG        = new Color(18, 24, 38);
    static final Color COLOR_PANEL     = new Color(28, 36, 54);
    static final Color COLOR_CARD      = new Color(36, 46, 68);
    static final Color COLOR_ACCENT    = new Color(59, 130, 246);
    static final Color COLOR_ACCENT2   = new Color(16, 185, 129);
    static final Color COLOR_DANGER    = new Color(239, 68, 68);
    static final Color COLOR_WARNING   = new Color(245, 158, 11);
    static final Color COLOR_TEXT      = new Color(226, 232, 240);
    static final Color COLOR_SUBTEXT   = new Color(148, 163, 184);
    static final Color COLOR_BORDER    = new Color(51, 65, 85);
    static final Color COLOR_ROW_ODD   = new Color(30, 41, 59);
    static final Color COLOR_ROW_EVEN  = new Color(36, 46, 68);
    static final Color COLOR_ROW_SEL   = new Color(59, 130, 246, 80);

    // ============================================================
    // SHARED STATE — kept as static fields (no OOP)
    // ============================================================
    static JFrame       mainFrame;
    static JTable       employeeTable;
    static DefaultTableModel tableModel;
    static JTabbedPane  tabbedPane;

    // Shared input fields (Feature 1 — Add Employee)
    static JTextField   fldEmpNumber, fldEmpName, fldLastName, fldFirstName;
    static JTextField   fldBirthday, fldAddress, fldPhone;
    static JTextField   fldSSS, fldPhilHealth, fldTIN, fldPagIbig;
    static JTextField   fldStatus, fldPosition, fldSupervisor;
    static JTextField   fldBasicSalary, fldRice, fldPhoneAllow, fldClothing;
    static JTextField   fldSemiMonthly, fldHourlyRate;

    // Feature 2 — Salary computation inputs
    static JTextField   fldPayEmpNumber, fldPayEmpName, fldPayCoverage;
    static JTextArea    areaPayResults;

    // Feature 3 — Update/Delete
    static JTextField   fldEditEmpNumber, fldEditLastName, fldEditFirstName;
    static JTextField   fldEditBirthday, fldEditSSS, fldEditPhilHealth;
    static JTextField   fldEditTIN, fldEditPagIbig, fldEditStatus;
    static JTextField   fldEditPosition, fldEditSupervisor, fldEditBasicSal;
    static JTextField   fldEditHourlyRate;

    // Feature 4 — Summary
    static JTextArea    areaSummary;

    // ============================================================
    // MAIN — build and show the GUI
    // ============================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            buildAndShowGUI();
        });
    }

    // ============================================================
    // GUI CONSTRUCTION
    // ============================================================
    static void buildAndShowGUI() {
        mainFrame = new JFrame("MotorPH Payroll System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1100, 720);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().setBackground(COLOR_BG);
        mainFrame.setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = buildHeader();
        mainFrame.add(header, BorderLayout.NORTH);

        // --- Tabbed Pane ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BG);
        tabbedPane.setForeground(COLOR_TEXT);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        tabbedPane.addTab("📋  Employee Records",  buildFeature1Panel());
        tabbedPane.addTab("💰  Salary Computation", buildFeature2Panel());
        tabbedPane.addTab("✏️  Edit / Delete",       buildFeature3Panel());
        tabbedPane.addTab("📊  Payroll Summary",    buildFeature4Panel());

        styleTabPane(tabbedPane);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);

        // Load CSV data into the table on startup
        refreshTable();
    }

    // ============================================================
    // HEADER PANEL
    // ============================================================
    static JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, COLOR_ACCENT),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JLabel title = new JLabel("MotorPH Payroll System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT);

        JLabel subtitle = new JLabel("Employee Management & Salary Computation");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(COLOR_SUBTEXT);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);
        return header;
    }

    // ============================================================
    // FEATURE 1 — Employee Records (Display, View, Add)
    // ============================================================
    static JPanel buildFeature1Panel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- Table ---
        tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        employeeTable = new JTable(tableModel);
        styleTable(employeeTable);

        JScrollPane tableScroll = new JScrollPane(employeeTable);
        styleScrollPane(tableScroll);
        tableScroll.setPreferredSize(new Dimension(0, 320));

        // --- Table action buttons ---
        JButton btnRefresh = makeButton("🔄 Refresh", COLOR_ACCENT);
        JButton btnView    = makeButton("👁 View Details", COLOR_ACCENT2);

        btnRefresh.addActionListener(e -> refreshTable());

        btnView.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Please select an employee from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showEmployeeDetails(selectedRow);
        });

        JPanel tableButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tableButtons.setOpaque(false);
        tableButtons.add(btnRefresh);
        tableButtons.add(btnView);

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        tableTop.add(makeSectionLabel("Employee Records"), BorderLayout.WEST);
        tableTop.add(tableButtons, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);
        tablePanel.add(tableTop, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // --- Add Employee Form ---
        JPanel addForm = buildAddEmployeeForm();

        // --- Split layout ---
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, addForm);
        split.setOpaque(false);
        split.setDividerLocation(360);
        split.setDividerSize(6);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    static JPanel buildAddEmployeeForm() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(8, 0, 0, 0));
        wrapper.add(makeSectionLabel("Add New Employee"), BorderLayout.NORTH);

        // GridBagLayout with 4 columns: label | field | label | field
        // Each addFormField call adds label+field as 2 adjacent cells.
        // This guarantees fields always render at their natural height.
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_CARD);
        form.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(14, 14, 14, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;

        // Each call places label+field in 2 cells; alternates between left and right column pairs.
        // col=0,1 for even index fields; col=2,3 for odd index fields.
        // After every 2 fields, move to the next row.

        // Build fields using the helper that adds label+field to form with GBC
        fldEmpNumber   = addGBCField(form, "Employee Number *",    0, 0);
        fldLastName    = addGBCField(form, "Last Name *",           2, 0);
        fldFirstName   = addGBCField(form, "First Name *",          0, 1);
        fldBirthday    = addGBCField(form, "Birthday (MM/DD/YYYY)", 2, 1);
        fldAddress     = addGBCField(form, "Address",               0, 2);
        fldPhone       = addGBCField(form, "Phone Number",          2, 2);
        fldSSS         = addGBCField(form, "SSS Number *",          0, 3);
        fldPhilHealth  = addGBCField(form, "PhilHealth Number *",   2, 3);
        fldTIN         = addGBCField(form, "TIN *",                 0, 4);
        fldPagIbig     = addGBCField(form, "Pag-IBIG Number *",     2, 4);
        fldStatus      = addGBCField(form, "Status",                0, 5);
        fldPosition    = addGBCField(form, "Position",              2, 5);
        fldSupervisor  = addGBCField(form, "Immediate Supervisor",  0, 6);
        fldBasicSalary = addGBCField(form, "Basic Salary",          2, 6);
        fldRice        = addGBCField(form, "Rice Subsidy",          0, 7);
        fldPhoneAllow  = addGBCField(form, "Phone Allowance",       2, 7);
        fldClothing    = addGBCField(form, "Clothing Allowance",    0, 8);
        fldSemiMonthly = addGBCField(form, "Semi-Monthly Rate",     2, 8);
        fldHourlyRate  = addGBCField(form, "Hourly Rate *",         0, 9);

        // Wrap in scroll pane so it never collapses when window is small
        JScrollPane formScroll = new JScrollPane(form);
        styleScrollPane(formScroll);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton btnAdd   = makeButton("\u2795 Add Employee", COLOR_ACCENT2);
        JButton btnClear = makeButton("\uD83D\uDDD1 Clear Fields", COLOR_WARNING);

        btnAdd.addActionListener(e -> addEmployee());
        btnClear.addActionListener(e -> clearAddForm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnClear);
        btnRow.add(btnAdd);

        wrapper.add(formScroll, BorderLayout.CENTER);
        wrapper.add(btnRow, BorderLayout.SOUTH);
        return wrapper;
    }

    // Adds a label+field pair to a GridBagLayout panel at the specified grid position.
    static JTextField addGBCField(JPanel panel, String labelText, int col, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = row;

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(COLOR_SUBTEXT);
        gbc.gridx = col;
        gbc.weightx = 0;
        panel.add(lbl, gbc);

        JTextField fld = new JTextField(14);
        fld.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fld.setBackground(new Color(15, 23, 42));
        fld.setForeground(COLOR_TEXT);
        fld.setCaretColor(COLOR_TEXT);
        fld.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridx = col + 1;
        gbc.weightx = 1;
        panel.add(fld, gbc);

        return fld;
    }


    // ============================================================
    // FEATURE 2 — Salary Computation
    // ============================================================
    static JPanel buildFeature2Panel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Input card
        JPanel inputCard = new JPanel(new GridLayout(0, 2, 12, 10));
        inputCard.setBackground(COLOR_CARD);
        inputCard.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));

        fldPayEmpNumber = addFormField(inputCard, "Employee Number *");
        fldPayEmpName   = addFormField(inputCard, "Employee Name");
        fldPayCoverage  = addFormField(inputCard, "Pay Coverage (Month 6-12) *");

        // Auto-fill name when employee number is entered
        fldPayEmpNumber.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                autoFillEmployeeName(fldPayEmpNumber.getText().trim(), fldPayEmpName);
            }
        });

        JButton btnCompute = makeButton("💰 Compute Salary", COLOR_ACCENT);
        JButton btnClearPay = makeButton("🗑 Clear", COLOR_WARNING);

        btnCompute.addActionListener(e -> computeSalary());
        btnClearPay.addActionListener(e -> {
            fldPayEmpNumber.setText("");
            fldPayEmpName.setText("");
            fldPayCoverage.setText("");
            areaPayResults.setText("");
        });

        JPanel payBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        payBtnRow.setOpaque(false);
        payBtnRow.add(btnCompute);
        payBtnRow.add(btnClearPay);

        JPanel inputWrapper = new JPanel(new BorderLayout(0, 10));
        inputWrapper.setOpaque(false);
        inputWrapper.add(makeSectionLabel("Salary Computation"), BorderLayout.NORTH);
        inputWrapper.add(inputCard, BorderLayout.CENTER);
        inputWrapper.add(payBtnRow, BorderLayout.SOUTH);

        // Results area
        areaPayResults = new JTextArea();
        areaPayResults.setEditable(false);
        areaPayResults.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaPayResults.setBackground(COLOR_CARD);
        areaPayResults.setForeground(COLOR_TEXT);
        areaPayResults.setBorder(new EmptyBorder(12, 12, 12, 12));
        areaPayResults.setText("Computation results will appear here...");

        JScrollPane resultsScroll = new JScrollPane(areaPayResults);
        styleScrollPane(resultsScroll);

        JPanel resultsWrapper = new JPanel(new BorderLayout(0, 8));
        resultsWrapper.setOpaque(false);
        resultsWrapper.add(makeSectionLabel("Results"), BorderLayout.NORTH);
        resultsWrapper.add(resultsScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputWrapper, resultsWrapper);
        split.setOpaque(false);
        split.setDividerLocation(420);
        split.setDividerSize(6);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // FEATURE 3 — Edit / Delete Employee Records
    // ============================================================
    static JPanel buildFeature3Panel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel hint = new JLabel("Select a record from the Employee Records tab, then switch here to edit or delete it.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(COLOR_SUBTEXT);
        hint.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Edit form
        JPanel editForm = new JPanel(new GridLayout(0, 4, 10, 8));
        editForm.setBackground(COLOR_CARD);
        editForm.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(14, 14, 14, 14)
        ));

        fldEditEmpNumber  = addFormField(editForm, "Employee Number *");
        fldEditLastName   = addFormField(editForm, "Last Name *");
        fldEditFirstName  = addFormField(editForm, "First Name *");
        fldEditBirthday   = addFormField(editForm, "Birthday");
        fldEditSSS        = addFormField(editForm, "SSS Number");
        fldEditPhilHealth = addFormField(editForm, "PhilHealth Number");
        fldEditTIN        = addFormField(editForm, "TIN");
        fldEditPagIbig    = addFormField(editForm, "Pag-IBIG Number");
        fldEditStatus     = addFormField(editForm, "Status");
        fldEditPosition   = addFormField(editForm, "Position");
        fldEditSupervisor = addFormField(editForm, "Supervisor");
        fldEditBasicSal   = addFormField(editForm, "Basic Salary");
        fldEditHourlyRate = addFormField(editForm, "Hourly Rate");

        // Load selected button
        JButton btnLoad   = makeButton("📥 Load Selected Record", COLOR_ACCENT);
        JButton btnSave   = makeButton("💾 Save Changes", COLOR_ACCENT2);
        JButton btnDelete = makeButton("🗑 Delete Record", COLOR_DANGER);

        btnLoad.addActionListener(e -> loadSelectedIntoEditForm());
        btnSave.addActionListener(e -> saveEditedEmployee());
        btnDelete.addActionListener(e -> deleteSelectedEmployee());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnLoad);
        btnRow.add(btnSave);
        btnRow.add(btnDelete);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 10));
        formWrapper.setOpaque(false);
        formWrapper.add(makeSectionLabel("Edit Employee Record"), BorderLayout.NORTH);
        formWrapper.add(editForm, BorderLayout.CENTER);
        formWrapper.add(btnRow, BorderLayout.SOUTH);

        panel.add(hint, BorderLayout.NORTH);
        panel.add(formWrapper, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // FEATURE 4 — Payroll Summary
    // ============================================================
    static JPanel buildFeature4Panel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JButton btnGenerate = makeButton("📊 Generate Payroll Summary", COLOR_ACCENT);
        btnGenerate.addActionListener(e -> generateSummary());

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.setOpaque(false);
        topRow.add(btnGenerate);

        areaSummary = new JTextArea();
        areaSummary.setEditable(false);
        areaSummary.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaSummary.setBackground(COLOR_CARD);
        areaSummary.setForeground(COLOR_TEXT);
        areaSummary.setBorder(new EmptyBorder(14, 14, 14, 14));
        areaSummary.setText("Click 'Generate Payroll Summary' to view totals and averages.");

        JScrollPane summaryScroll = new JScrollPane(areaSummary);
        styleScrollPane(summaryScroll);

        JPanel summaryWrapper = new JPanel(new BorderLayout(0, 8));
        summaryWrapper.setOpaque(false);
        summaryWrapper.add(makeSectionLabel("Payroll Summary"), BorderLayout.NORTH);
        summaryWrapper.add(summaryScroll, BorderLayout.CENTER);

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(summaryWrapper, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // FEATURE 1 LOGIC — Add, View, Refresh
    // ============================================================

    // Refreshes the JTable by re-reading the CSV file.
    static void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            for (String[] row : employees) {
                if (row.length < 10) continue;
                Object[] tableRow = new Object[TABLE_HEADERS.length];
                for (int i = 0; i < TABLE_COL_MAP.length; i++) {
                    tableRow[i] = (TABLE_COL_MAP[i] < row.length) ? row[TABLE_COL_MAP[i]] : "";
                }
                tableModel.addRow(tableRow);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Could not load employee file:\n" + ex.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Shows a dialog with full details of the selected employee.
    static void showEmployeeDetails(int selectedRow) {
        try {
            String empId = tableModel.getValueAt(selectedRow, 0).toString();
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            String[] emp = findEmployee(employees, empId);

            if (emp == null) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee record not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] labels = {
                "Employee #", "Last Name", "First Name", "Birthday",
                "Address", "Phone", "SSS #", "PhilHealth #", "TIN",
                "Pag-IBIG #", "Status", "Position", "Supervisor",
                "Basic Salary", "Rice Subsidy", "Phone Allowance",
                "Clothing Allowance", "Semi-Monthly Rate", "Hourly Rate"
            };

            StringBuilder sb = new StringBuilder();
            sb.append("=".repeat(45)).append("\n");
            sb.append("  EMPLOYEE DETAILS\n");
            sb.append("=".repeat(45)).append("\n");
            for (int i = 0; i < labels.length && i < emp.length; i++) {
                sb.append(String.format("  %-22s: %s%n", labels[i], emp[i]));
            }
            sb.append("=".repeat(45));

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));
            area.setBackground(COLOR_CARD);
            area.setForeground(COLOR_TEXT);

            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(480, 420));

            JOptionPane.showMessageDialog(mainFrame, scroll,
                "Employee Details — " + empId, JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error reading employee data:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Validates and adds a new employee to the CSV file.
    static void addEmployee() {
        // --- Validation ---
        String empNumber  = fldEmpNumber.getText().trim();
        String lastName   = fldLastName.getText().trim();
        String firstName  = fldFirstName.getText().trim();
        String hourlyRate = fldHourlyRate.getText().trim();

        if (empNumber.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Employee Number, Last Name, and First Name are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(empNumber);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Employee Number must be a numeric value.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!hourlyRate.isEmpty()) {
            try {
                Double.parseDouble(hourlyRate);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Hourly Rate must be a numeric value.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // --- Check for duplicate employee number ---
        try {
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            if (findEmployee(employees, empNumber) != null) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee Number " + empNumber + " already exists.",
                    "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            // File may not exist yet — that's fine, we'll create it
        }

        // --- Build new CSV row ---
        String newRow = String.join(",",
            empNumber,
            lastName,
            firstName,
            fldBirthday.getText().trim(),
            fldAddress.getText().trim(),
            fldPhone.getText().trim(),
            fldSSS.getText().trim(),
            fldPhilHealth.getText().trim(),
            fldTIN.getText().trim(),
            fldPagIbig.getText().trim(),
            fldStatus.getText().trim(),
            fldPosition.getText().trim(),
            fldSupervisor.getText().trim(),
            fldBasicSalary.getText().trim(),
            fldRice.getText().trim(),
            fldPhoneAllow.getText().trim(),
            fldClothing.getText().trim(),
            fldSemiMonthly.getText().trim(),
            hourlyRate
        );

        // --- Append to CSV ---
        try {
            appendToFile(EMPLOYEE_FILE, newRow);
            refreshTable();
            clearAddForm();
            JOptionPane.showMessageDialog(mainFrame,
                "Employee " + empNumber + " added successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Failed to save employee:\n" + ex.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void clearAddForm() {
        JTextField[] fields = {
            fldEmpNumber, fldLastName, fldFirstName, fldBirthday,
            fldAddress, fldPhone, fldSSS, fldPhilHealth, fldTIN,
            fldPagIbig, fldStatus, fldPosition, fldSupervisor,
            fldBasicSalary, fldRice, fldPhoneAllow, fldClothing,
            fldSemiMonthly, fldHourlyRate
        };
        for (JTextField f : fields) f.setText("");
    }

    // ============================================================
    // FEATURE 2 LOGIC — Salary Computation
    // ============================================================

    static void computeSalary() {
        String empIdStr   = fldPayEmpNumber.getText().trim();
        String coverageStr = fldPayCoverage.getText().trim();

        // --- Validate inputs ---
        if (empIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Employee Number is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int coverageMonth;
        try {
            coverageMonth = Integer.parseInt(coverageStr);
            if (coverageMonth < 6 || coverageMonth > 12) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Pay Coverage must be a month number between 6 and 12\n(6=June, 7=July, ... 12=December).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String[][] employees  = loadEmployees(EMPLOYEE_FILE);
            String[][] attendance = loadAttendance(ATTENDANCE_FILE);

            String[] emp = findEmployee(employees, empIdStr);
            if (emp == null) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee number does not exist.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double hourlyRate = parseAmount(emp[COL_HOURLY_RATE]);
            int year = getYearFromAttendance(attendance, empIdStr);
            int lastDay = getLastDayOfMonth(coverageMonth, year);

            double firstHours  = computeGrossPay_Hours(attendance, empIdStr, coverageMonth, 1, 15);
            double secondHours = computeGrossPay_Hours(attendance, empIdStr, coverageMonth, 16, lastDay);

            double gross1 = computeGrossPay(firstHours, hourlyRate);
            double gross2 = computeGrossPay(secondHours, hourlyRate);
            double totalGross = gross1 + gross2;

            double sss        = computeSSS(totalGross);
            double philHealth = computePhilHealth(totalGross);
            double pagIbig    = computePagIBIG(totalGross);
            double totalDed   = computeDeductions(sss, philHealth, pagIbig);
            double taxable    = totalGross - totalDed;
            double tax        = computeWithholdingTax(taxable);
            double totalDedWithTax = totalDed + tax;
            double net1       = gross1;
            double net2       = computeNetPay(gross2, totalDedWithTax);

            String monthName = getMonthName(coverageMonth);

            StringBuilder sb = new StringBuilder();
            sb.append("=".repeat(50)).append("\n");
            sb.append(String.format("  PAYSLIP — %s%n", monthName.toUpperCase()));
            sb.append("=".repeat(50)).append("\n");
            sb.append(String.format("  Employee #    : %s%n", emp[COL_EMP_ID]));
            sb.append(String.format("  Name          : %s, %s%n", emp[COL_LAST_NAME], emp[COL_FIRST_NAME]));
            sb.append(String.format("  Hourly Rate   : %.2f%n", hourlyRate));
            sb.append("-".repeat(50)).append("\n");
            sb.append(String.format("  CUTOFF 1 — %s 1-15%n", monthName));
            sb.append(String.format("    Hours Worked : %.4f%n", firstHours));
            sb.append(String.format("    Gross Pay    : %.4f%n", gross1));
            sb.append(String.format("    Net Pay      : %.4f%n", net1));
            sb.append("-".repeat(50)).append("\n");
            sb.append(String.format("  CUTOFF 2 — %s 16-%d%n", monthName, lastDay));
            sb.append(String.format("    Hours Worked : %.4f%n", secondHours));
            sb.append(String.format("    Gross Pay    : %.4f%n", gross2));
            sb.append("-".repeat(50)).append("\n");
            sb.append(String.format("  MONTHLY GROSS  : %.4f%n", totalGross));
            sb.append("\n  DEDUCTIONS:\n");
            sb.append(String.format("    SSS              : %.4f%n", sss));
            sb.append(String.format("    PhilHealth       : %.4f%n", philHealth));
            sb.append(String.format("    Pag-IBIG         : %.4f%n", pagIbig));
            sb.append(String.format("    Withholding Tax  : %.4f%n", tax));
            sb.append(String.format("    Total Deductions : %.4f%n", totalDedWithTax));
            sb.append("-".repeat(50)).append("\n");
            sb.append(String.format("  NET PAY (Cutoff 2) : %.4f%n", net2));
            sb.append("=".repeat(50));

            areaPayResults.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error computing salary:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Auto-fills the Employee Name field when a valid employee number is entered.
    static void autoFillEmployeeName(String empId, JTextField nameField) {
        if (empId.isEmpty()) return;
        try {
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            String[] emp = findEmployee(employees, empId);
            if (emp != null) {
                nameField.setText(emp[COL_LAST_NAME] + ", " + emp[COL_FIRST_NAME]);
            }
        } catch (Exception ex) {
            // Silently ignore — name field is optional
        }
    }

    // ============================================================
    // FEATURE 3 LOGIC — Update and Delete
    // ============================================================

    // Loads the row selected in the JTable into the edit form fields.
    static void loadSelectedIntoEditForm() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame,
                "Please select an employee from the Employee Records tab first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String empId = tableModel.getValueAt(selectedRow, 0).toString();
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            String[] emp = findEmployee(employees, empId);

            if (emp == null) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee record could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            fldEditEmpNumber.setText(getField(emp, COL_EMP_ID));
            fldEditLastName.setText(getField(emp, COL_LAST_NAME));
            fldEditFirstName.setText(getField(emp, COL_FIRST_NAME));
            fldEditBirthday.setText(getField(emp, COL_BIRTHDAY));
            fldEditSSS.setText(getField(emp, COL_SSS));
            fldEditPhilHealth.setText(getField(emp, COL_PHILHEALTH));
            fldEditTIN.setText(getField(emp, COL_TIN));
            fldEditPagIbig.setText(getField(emp, COL_PAGIBIG));
            fldEditStatus.setText(getField(emp, COL_STATUS));
            fldEditPosition.setText(getField(emp, COL_POSITION));
            fldEditSupervisor.setText(getField(emp, COL_SUPERVISOR));
            fldEditBasicSal.setText(getField(emp, COL_BASIC_SAL));
            fldEditHourlyRate.setText(getField(emp, COL_HOURLY_RATE));

            tabbedPane.setSelectedIndex(2); // Switch to Edit tab

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error loading record:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Saves changes to the selected employee back to the CSV file.
    static void saveEditedEmployee() {
        String empNumber = fldEditEmpNumber.getText().trim();
        String lastName  = fldEditLastName.getText().trim();
        String firstName = fldEditFirstName.getText().trim();

        if (empNumber.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Employee Number, Last Name, and First Name are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!fldEditHourlyRate.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(fldEditHourlyRate.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Hourly Rate must be a numeric value.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            boolean updated = false;

            StringBuilder newContent = new StringBuilder();
            newContent.append(getCSVHeader()).append("\n");

            for (String[] emp : employees) {
                if (emp[COL_EMP_ID].equals(empNumber)) {
                    // Write the updated row
                    newContent.append(buildUpdatedRow(emp)).append("\n");
                    updated = true;
                } else {
                    newContent.append(String.join(",", emp)).append("\n");
                }
            }

            if (!updated) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee not found. Nothing was updated.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            writeFile(EMPLOYEE_FILE, newContent.toString().trim());
            refreshTable();
            JOptionPane.showMessageDialog(mainFrame,
                "Employee " + empNumber + " updated successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Failed to save changes:\n" + ex.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Builds a CSV row from the edit form fields.
    static String buildUpdatedRow(String[] original) {
        String[] updated = Arrays.copyOf(original, Math.max(original.length, 19));
        updated[COL_EMP_ID]     = fldEditEmpNumber.getText().trim();
        updated[COL_LAST_NAME]  = fldEditLastName.getText().trim();
        updated[COL_FIRST_NAME] = fldEditFirstName.getText().trim();
        updated[COL_BIRTHDAY]   = fldEditBirthday.getText().trim();
        updated[COL_SSS]        = fldEditSSS.getText().trim();
        updated[COL_PHILHEALTH] = fldEditPhilHealth.getText().trim();
        updated[COL_TIN]        = fldEditTIN.getText().trim();
        updated[COL_PAGIBIG]    = fldEditPagIbig.getText().trim();
        updated[COL_STATUS]     = fldEditStatus.getText().trim();
        updated[COL_POSITION]   = fldEditPosition.getText().trim();
        updated[COL_SUPERVISOR] = fldEditSupervisor.getText().trim();
        updated[COL_BASIC_SAL]  = fldEditBasicSal.getText().trim();
        updated[COL_HOURLY_RATE] = fldEditHourlyRate.getText().trim();
        return String.join(",", updated);
    }

    // Deletes the selected employee from the CSV after confirmation.
    static void deleteSelectedEmployee() {
        String empNumber = fldEditEmpNumber.getText().trim();

        if (empNumber.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Please load an employee record first using 'Load Selected Record'.",
                "No Record Loaded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Are you sure you want to delete Employee #" + empNumber + "?\nThis cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String[][] employees = loadEmployees(EMPLOYEE_FILE);
            boolean deleted = false;

            StringBuilder newContent = new StringBuilder();
            newContent.append(getCSVHeader()).append("\n");

            for (String[] emp : employees) {
                if (emp[COL_EMP_ID].equals(empNumber)) {
                    deleted = true; // Skip this row — it's being deleted
                } else {
                    newContent.append(String.join(",", emp)).append("\n");
                }
            }

            if (!deleted) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Employee not found. Nothing was deleted.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            writeFile(EMPLOYEE_FILE, newContent.toString().trim());
            refreshTable();

            // Clear edit form
            JTextField[] editFields = {
                fldEditEmpNumber, fldEditLastName, fldEditFirstName, fldEditBirthday,
                fldEditSSS, fldEditPhilHealth, fldEditTIN, fldEditPagIbig,
                fldEditStatus, fldEditPosition, fldEditSupervisor,
                fldEditBasicSal, fldEditHourlyRate
            };
            for (JTextField f : editFields) f.setText("");

            JOptionPane.showMessageDialog(mainFrame,
                "Employee " + empNumber + " deleted successfully.",
                "Deleted", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Failed to delete employee:\n" + ex.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // FEATURE 4 LOGIC — Payroll Summary
    // ============================================================
    static void generateSummary() {
        try {
            String[][] employees  = loadEmployees(EMPLOYEE_FILE);
            String[][] attendance = loadAttendance(ATTENDANCE_FILE);

            if (employees.length == 0) {
                JOptionPane.showMessageDialog(mainFrame,
                    "No employee data is loaded. Please check your CSV file.",
                    "No Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int totalEmployees  = employees.length;
            double totalGross   = 0;
            double totalDed     = 0;
            double totalNet     = 0;

            for (String[] emp : employees) {
                double hourlyRate = parseAmount(emp[COL_HOURLY_RATE]);
                int year = getYearFromAttendance(attendance, emp[COL_EMP_ID]);

                // Sum across all months June-December
                for (int month = 6; month <= 12; month++) {
                    int lastDay = getLastDayOfMonth(month, year);
                    double h1 = computeGrossPay_Hours(attendance, emp[COL_EMP_ID], month, 1, 15);
                    double h2 = computeGrossPay_Hours(attendance, emp[COL_EMP_ID], month, 16, lastDay);
                    double g  = computeGrossPay(h1 + h2, hourlyRate);
                    double s  = computeSSS(g);
                    double ph = computePhilHealth(g);
                    double pi = computePagIBIG(g);
                    double d  = computeDeductions(s, ph, pi);
                    double tx = computeWithholdingTax(g - d);
                    double n  = computeNetPay(g, d + tx);

                    totalGross += g;
                    totalDed   += d + tx;
                    totalNet   += n;
                }
            }

            double avgNet = (totalEmployees > 0) ? totalNet / totalEmployees : 0;

            StringBuilder sb = new StringBuilder();
            sb.append("=".repeat(50)).append("\n");
            sb.append("  MOTORPH PAYROLL SUMMARY (June - December)\n");
            sb.append("=".repeat(50)).append("\n");
            sb.append(String.format("  Total Employees     : %d%n", totalEmployees));
            sb.append(String.format("  Total Gross Pay     : %.4f%n", totalGross));
            sb.append(String.format("  Total Deductions    : %.4f%n", totalDed));
            sb.append(String.format("  Total Net Pay       : %.4f%n", totalNet));
            sb.append(String.format("  Average Net Pay     : %.4f%n", avgNet));
            sb.append("=".repeat(50));

            areaSummary.setText(sb.toString());

            JOptionPane.showMessageDialog(mainFrame,
                "Summary generated successfully.",
                "Done", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error generating summary:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // SALARY COMPUTATION METHODS (Feature 2 — modular)
    // ============================================================

    // Calculates gross pay from hours and hourly rate.
    static double computeGrossPay(double hoursWorked, double hourlyRate) {
        return hoursWorked * hourlyRate;
    }

    // Calculates total hours worked for a given employee, month, and day range.
    static double computeGrossPay_Hours(
            String[][] attendance, String empId,
            int month, int startDay, int endDay) {

        double totalMinutes = 0;

        for (String[] row : attendance) {
            if (row.length <= ATT_TIME_OUT) continue;
            if (!row[ATT_EMP_ID].trim().equals(empId)) continue;

            String[] dateParts = row[ATT_DATE].split("/");
            if (dateParts.length < 2) continue;

            int recMonth = Integer.parseInt(dateParts[0].trim());
            int recDay   = Integer.parseInt(dateParts[1].trim());

            if (recMonth != month) continue;
            if (recDay < startDay || recDay > endDay) continue;

            int[] login  = parseTime(row[ATT_TIME_IN]);
            int[] logout = parseTime(row[ATT_TIME_OUT]);

            int loginMin  = login[0]  * 60 + login[1];
            int logoutMin = logout[0] * 60 + logout[1];

            if (loginMin <= 8 * 60 + 10) loginMin = 8 * 60;
            if (loginMin < 8 * 60)       loginMin = 8 * 60;
            if (logoutMin > 17 * 60)     logoutMin = 17 * 60;

            int worked = logoutMin - loginMin - 60;
            if (worked > 0) totalMinutes += worked;
        }

        return totalMinutes / 60.0;
    }

    static double computeSSS(double grossSalary) {
        if (grossSalary < 3250)   return 135.0;
        if (grossSalary >= 24750) return 1125.0;
        int bracket = (int) ((grossSalary - 3250) / 500);
        return 157.5 + bracket * 22.5;
    }

    static double computePhilHealth(double grossSalary) {
        if (grossSalary <= 10000) return 150.0;
        if (grossSalary >= 60000) return 900.0;
        return (grossSalary * 0.03) / 2.0;
    }

    static double computePagIBIG(double grossSalary) {
        double rate = (grossSalary <= 1500) ? 0.01 : 0.02;
        return Math.min(grossSalary * rate, 100.0);
    }

    static double computeDeductions(double sss, double philHealth, double pagIbig) {
        return sss + philHealth + pagIbig;
    }

    static double computeWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832)  return 0.0;
        if (taxableIncome <= 33332)  return (taxableIncome - 20833)  * 0.20;
        if (taxableIncome <= 66666)  return 2500.0   + (taxableIncome - 33333)  * 0.25;
        if (taxableIncome <= 166666) return 10833.0  + (taxableIncome - 66667)  * 0.30;
        if (taxableIncome <= 666666) return 40833.33 + (taxableIncome - 166667) * 0.32;
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    static double computeNetPay(double grossPay, double totalDeductions) {
        return grossPay - totalDeductions;
    }

    // ============================================================
    // CSV FILE HANDLING — separate methods for read / write
    // ============================================================

    static String[][] loadEmployees(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<String[]> rows = new ArrayList<>();
        String line;
        boolean header = true;

        while ((line = reader.readLine()) != null) {
            if (header) { header = false; continue; }
            String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (cols.length < 19) continue;
            for (int i = 0; i < cols.length; i++) {
                cols[i] = cols[i].replace("\"", "").trim();
            }
            cols[COL_HOURLY_RATE] = cols[COL_HOURLY_RATE].replace(",", "");
            rows.add(cols);
        }

        reader.close();
        return rows.toArray(new String[0][]);
    }

    static String[][] loadAttendance(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<String[]> rows = new ArrayList<>();
        String line;
        boolean header = true;

        while ((line = reader.readLine()) != null) {
            if (header) { header = false; continue; }
            String[] cols = line.split(",");
            if (cols.length < 6) continue;
            rows.add(cols);
        }

        reader.close();
        return rows.toArray(new String[0][]);
    }

    // Appends a single line to an existing file (or creates it).
    static void appendToFile(String filePath, String line) throws Exception {
        File file = new File(filePath);
        boolean needsHeader = !file.exists() || file.length() == 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (needsHeader) {
                writer.write(getCSVHeader());
                writer.newLine();
            }
            writer.write(line);
            writer.newLine();
        }
    }

    // Overwrites the entire file with new content (used for update/delete).
    static void writeFile(String filePath, String content) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(content);
            writer.newLine();
        }
    }

    // Returns the standard CSV header row.
    static String getCSVHeader() {
        return "Employee #,Last Name,First Name,Birthday,Address,Phone Number," +
               "SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position," +
               "Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance," +
               "Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================


    static String[] findEmployee(String[][] employees, String empId) {
        for (String[] emp : employees) {
            if (emp[COL_EMP_ID].trim().equals(empId)) return emp;
        }
        return null;
    }

    static double parseAmount(String value) {
        return Double.parseDouble(value.replace("\"", "").replace(",", "").trim());
    }

    static int[] parseTime(String time) {
        String[] p = time.trim().split(":");
        return new int[]{ Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()) };
    }

    static int getYearFromAttendance(String[][] attendance, String empId) {
        for (String[] row : attendance) {
            if (!row[ATT_EMP_ID].trim().equals(empId)) continue;
            String[] parts = row[ATT_DATE].split("/");
            if (parts.length >= 3) return Integer.parseInt(parts[2].trim());
        }
        return 2024;
    }

    static int getLastDayOfMonth(int month, int year) {
        if (month == 2) {
            boolean leap = (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
            return leap ? 29 : 28;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) return 30;
        return 31;
    }

    static String getMonthName(int month) {
        String[] names = {"","January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        return names[month];
    }

    static String getField(String[] arr, int idx) {
        return (idx < arr.length) ? arr[idx] : "";
    }

    // ============================================================
    // GUI HELPER METHODS — styling and component builders
    // ============================================================

    static JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COLOR_TEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    // Creates a labeled field pair and adds both to the parent panel.
    // Returns the JTextField for reference.
    static JTextField addFormField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(COLOR_SUBTEXT);

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(new Color(15, 23, 42));
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_TEXT);
        field.setBorder(new CompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));

        parent.add(label);
        parent.add(field);
        return field;
    }

    static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            Color original = bg;
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(original.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });

        return btn;
    }

    static void styleTable(JTable table) {
        table.setBackground(COLOR_ROW_ODD);
        table.setForeground(COLOR_TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(COLOR_ROW_SEL);
        table.setSelectionForeground(COLOR_TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_PANEL);
        header.setForeground(COLOR_SUBTEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDER));
        header.setReorderingAllowed(false);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? COLOR_ROW_SEL : (row % 2 == 0 ? COLOR_ROW_EVEN : COLOR_ROW_ODD));
                setForeground(COLOR_TEXT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    static void styleScrollPane(JScrollPane pane) {
        pane.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        pane.getViewport().setBackground(COLOR_ROW_ODD);
        pane.setBackground(COLOR_BG);
    }

    static void styleTabPane(JTabbedPane pane) {
        pane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            protected void installDefaults() {
                super.installDefaults();
                highlight = COLOR_ACCENT;
                lightHighlight = COLOR_PANEL;
                shadow = COLOR_BORDER;
                darkShadow = COLOR_BG;
                focus = COLOR_ACCENT;
            }
        });
    }
}