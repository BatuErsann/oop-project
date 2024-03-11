package iki;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DetailManagementPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private JTextField firstDetailTextField;
    private JTextArea secondDetailTextField;
    private static final String DETAILS_FOLDER = "db//";
    private String selectedBrand;
    private String selectedModel;

    public void setSelectedBrand(String selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    public void setSelectedModel(String selectedModel) {
        this.selectedModel = selectedModel;
    }

    public DetailManagementPanel(String selectedBrand, String selectedModel) {
        super("Detay Yönetim Paneli - " + selectedBrand + " - " + selectedModel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);

        JToolBar toolBar = new JToolBar();
        JButton backButton = new JButton("Geri");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openModelManagementPanel();
            }
        });

        toolBar.add(backButton);
        getContentPane().add(toolBar, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Detay"}, 0);
        detailTable = new JTable(tableModel);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(detailTable);
        JButton deleteButton = new JButton("Sil");
        JButton addButton = new JButton("Ekle");
        JButton editButton = new JButton("Düzenle");
        deleteButton.addActionListener(e -> deleteDetail());
        addButton.addActionListener(e -> addDetail());
        editButton.addActionListener(e -> editDetail());

        firstDetailTextField = new JTextField(20);
        secondDetailTextField = new JTextArea(1, 30);
        secondDetailTextField.setTabSize(20);

        // secondDetailTextField için KeyListener ekleyin
        secondDetailTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume(); // Enter tuşu olayını tüket, böylece yeni satır eklenmez
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Fiyat: "));
        inputPanel.add(firstDetailTextField);
        inputPanel.add(new JLabel("Donanım Paketi"));
        inputPanel.add(new JScrollPane(secondDetailTextField));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
        setVisible(true);

        this.selectedBrand = selectedBrand;
        this.selectedModel = selectedModel;

        showDetails();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openModelManagementPanel() {
        // Bu sınıftan yeni bir ModelManagementPanel oluştur
        ModelManagementPanel modelManagementPanel = new ModelManagementPanel(selectedBrand);

        // Bu pencereyi gizle (kapatma)
        DetailManagementPanel.this.dispose();

        // ModelManagementPanel penceresini göster
        modelManagementPanel.setVisible(true);
    }


    private void addDetail() {
        String firstDetail = firstDetailTextField.getText();
        String secondDetail = secondDetailTextField.getText();

        if (!firstDetail.isEmpty() && !secondDetail.isEmpty()) {
            saveDetailToFile(firstDetail, secondDetail);
            showDetails();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen detayları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteDetail() {
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow != -1) {
            String detailToDelete = (String) tableModel.getValueAt(selectedRow, 0);
            ArrayList<String> details = readDetailsFromFile();
            details.remove(detailToDelete);
            writeDetailsToFile(details);
            showDetails();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir detay seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editDetail() {
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow != -1) {
            String oldDetail = (String) tableModel.getValueAt(selectedRow, 0);
            String newDetail = JOptionPane.showInputDialog(this, "Yeni değeri girin:", oldDetail);

            if (newDetail != null && !newDetail.isEmpty()) {
                ArrayList<String> details = readDetailsFromFile();
                int index = details.indexOf(oldDetail);
                if (index != -1) {
                    details.set(index, newDetail);
                    writeDetailsToFile(details);
                    showDetails();
                } else {
                    JOptionPane.showMessageDialog(this, "Belirtilen detay bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir detay seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void writeDetailsToFile(ArrayList<String> details) {
        String detailFileName = DETAILS_FOLDER + selectedBrand + selectedModel + ".txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(detailFileName)))) {
            for (String detail : details) {
                writer.println(detail);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Detaylar dosyasına yazılırken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDetailToFile(String firstDetail, String secondDetail) {
        String detailFileName = DETAILS_FOLDER + selectedBrand + selectedModel + ".txt";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(detailFileName, true)))) {
            writer.println("Fiyat: " + firstDetail);
            writer.println("Donanım Paketi: " + secondDetail);
            JOptionPane.showMessageDialog(this, "Detaylar başarıyla kaydedildi!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Detaylar kaydedilirken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ArrayList<String> readDetailsFromFile() {
        ArrayList<String> details = new ArrayList<>();
        String detailFileName = DETAILS_FOLDER + selectedBrand + selectedModel + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(detailFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                details.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Detaylar dosyası okunurken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        return details;
    }

    private void showDetails() {
        String detailFileName = DETAILS_FOLDER + selectedBrand + selectedModel + ".txt";
        ArrayList<String> details = readDetailsFromFile();
        tableModel.setRowCount(0);
        for (String detail : details) {
            tableModel.addRow(new Object[]{detail});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DetailManagementPanel("ExampleBrand", "ExampleModel"));
    }
}
