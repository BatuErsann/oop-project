package iki;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private Map<Integer, JPanel> panels = new HashMap<>();
    private JTable tablesag;
    private int soltablerow;

    public MainFrame() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Brand");
        readDataFromFile("db/brands.txt", model);

        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    System.out.println("soltablerow:" + row);
                    Object selectedBrand = table.getValueAt(row, 0);
                    String filePath = "db/" + selectedBrand.toString().toLowerCase() + ".txt";

                    System.out.println(filePath);
                    DefaultTableModel model2 = new DefaultTableModel();
                    model2.addColumn("Model");
                    readDataFromFile(filePath, model2);

                    tablesag.setModel(model2);

                    soltablerow = row;
                }
            }
        });
        getContentPane().setLayout(null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(12, 38, 201, 612);
        getContentPane().add(scrollPane);

        JButton btnEdit = new JButton("edit");
        btnEdit.setBounds(1039, 36, 169, 36);
        getContentPane().add(btnEdit);

        tablesag = new JTable();
        tablesag.setBounds(257, 38, 201, 304);
        tablesag.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Object selectedBrand = table.getValueAt(soltablerow, 0);
                int modelRow = tablesag.rowAtPoint(e.getPoint());
                System.out.println(modelRow);
                if (modelRow >= 0) {
                    showModelDetails(selectedBrand.toString(), tablesag.getValueAt(modelRow, 0).toString());
                }
            }
        });
        getContentPane().add(tablesag);
        
        JLabel lblNewLabel = new JLabel("Batu Auto");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
        lblNewLabel.setBounds(494, 40, 360, 30);
        getContentPane().add(lblNewLabel);

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                UserLoginPanel userLogin = new UserLoginPanel();
                userLogin.setVisible(true);

                // Close the MainFrame
                dispose();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showModels(int row) {
        Object selectedBrand = table.getValueAt(row, 0);
        String filePath = "db/" + selectedBrand.toString().toLowerCase() + ".txt";

        System.out.println(filePath);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Model");
        readDataFromFile(filePath, model);

        tablesag.setModel(model);

        JScrollPane modelsScrollPane = new JScrollPane(tablesag);
        modelsScrollPane.setBounds(220, 50, 200, 300);
        getContentPane().add(modelsScrollPane);

        tablesag.setVisible(true);
    }

    private void showModelDetails(String brand, String model) {
        String filePath = "db/" + brand.toLowerCase() + model.toLowerCase() + ".txt";

        System.out.println(filePath);
        JPanel panel = new JPanel();
        panel.setBounds(220, 400, 1000, 200);
        panel.setBackground(Color.BLUE);

        DefaultTableModel modelDetails = new DefaultTableModel();
        modelDetails.addColumn("Detail");
        readDataFromFile(filePath, modelDetails);

        JTable detailsTable = new JTable(modelDetails);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        getContentPane().add(panel);
        detailsTable.setVisible(true);

        panel.revalidate();
        panel.repaint();
    }

    private void editButtonClicked(int row) {
        if (panels.containsKey(row)) {
            JPanel panel = panels.get(row);
            panel.setVisible(true);
            return;
        }

        Object selectedBrand = table.getValueAt(row, 0);
        String filePath = "db/" + selectedBrand.toString().toLowerCase() + ".txt";

        JPanel panel = new JPanel();
        panel.setBounds(220, 50, 1000, 500);
        panel.setBackground(getColorForRow(row));

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Brand");
        readDataFromFile(filePath, model);

        JTable newTable = new JTable(model);
        newTable.setPreferredScrollableViewportSize(new Dimension(1000, 500));

        JScrollPane newScrollPane = new JScrollPane(newTable);
        panel.add(newScrollPane, BorderLayout.CENTER);

        getContentPane().add(panel);
        newTable.setVisible(true);

        panel.revalidate();
        panel.repaint();

        panels.put(row, panel);
    }

    private Color getColorForRow(int row) {
        if (row == 0) {
            return Color.RED;
        } else if (row == 1) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }

    private void readDataFromFile(String filePath, DefaultTableModel model) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                model.addRow(new Object[]{line.trim()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
