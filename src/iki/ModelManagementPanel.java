package iki;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModelManagementPanel extends JFrame {
    private JTable modelTable;
    private DefaultTableModel tableModel;
    private static final String MODELS_FOLDER = "db//";
    private static final String BRANDS_FILE = "db//brands.txt";
    private int currentBrandIndex = -1;

    public ModelManagementPanel(String selectedBrand) {
        super("Model Yönetim Paneli - " + selectedBrand);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);

        JToolBar toolBar = new JToolBar();
        JButton backButton = new JButton("Geri");
        JButton forwardButton = new JButton("İleri");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Geri butonuna tıklandığında yapılacak işlemler
                openBrandManagementPanel(); 
            }
        });

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // İleri butonuna tıklandığında yapılacak işlemler
                System.out.println("İleri");
                openDetailManagementPanel(selectedBrand, null);
            }
        });

        toolBar.add(backButton);
        toolBar.add(forwardButton);
        getContentPane().add(toolBar, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Model"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hücre düzenlemeyi devre dışı bırak
            }
        };
        modelTable = new JTable(tableModel);
        modelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(modelTable);
        JButton deleteButton = new JButton("Sil");
        JButton addButton = new JButton("Ekle");
        JButton editButton = new JButton("Düzenle");
        deleteButton.addActionListener(e -> deleteModel(selectedBrand));
        addButton.addActionListener(e -> addModel(selectedBrand));
        editButton.addActionListener(e -> editModel(selectedBrand));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Model elementine çift tıklanınca DetailManagementPanel'i aç
        modelTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Çift tıklama kontrolü
                    int selectedRow = modelTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String selectedModel = (String) tableModel.getValueAt(selectedRow, 0);
                        openDetailManagementPanel(selectedBrand, selectedModel);
                    }
                }
            }
        });
        
        getContentPane().add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);

        // Tabloyu otomatik olarak doldur
        showModels(selectedBrand);
    }

    private void openBrandManagementPanel() {
        // Yeni bir BrandManagementPanel oluştur
        BrandManagementPanel brandManagementPanel = new BrandManagementPanel();

        // Mevcut pencereyi gizle (kapatma)
        this.setVisible(false);

        // BrandManagementPanel penceresini göster
        brandManagementPanel.setVisible(true);

      
        };
   

    private void openDetailManagementPanel(String selectedBrand, String selectedModel) {
        // Bu sınıftan yeni bir DetailManagementPanel oluştur
        DetailManagementPanel detailManagementPanel = new DetailManagementPanel(selectedBrand, selectedModel);

        // Bu pencereyi kapat
        dispose();

        // DetailManagementPanel penceresini göster
        detailManagementPanel.setVisible(true);
    }


    private void showModels(String selectedBrand) {
        String brandFileName = MODELS_FOLDER + selectedBrand + ".txt";
        if (brandFileName != null) {
            List<String> models = readModelsFromFile(brandFileName);
            tableModel.setRowCount(0); // Tabloyu temizle
            for (String model : models) {
                tableModel.addRow(new Object[]{model});
            }
        }
    }

    private void deleteModel(String selectedBrand) {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow != -1) {
            String modelToDelete = (String) tableModel.getValueAt(selectedRow, 0);
            String brandFileName = MODELS_FOLDER + selectedBrand + ".txt";
            List<String> models = readModelsFromFile(brandFileName);
            models.remove(modelToDelete);
            writeModelsToFile(brandFileName, models);

            // Silinen modelin detay dosyasını sil
            String modelDetailFileName = MODELS_FOLDER + selectedBrand + modelToDelete + ".txt";
            File modelDetailFile = new File(modelDetailFileName);
            if (modelDetailFile.exists()) {
                modelDetailFile.delete();
            }

            showModels(selectedBrand);
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir model seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addModel(String selectedBrand) {
        String model = JOptionPane.showInputDialog(this, "Eklemek istediğiniz modeli girin:");
        if (model != null && !model.isEmpty()) {
            String brandFileName = MODELS_FOLDER + selectedBrand + ".txt";
            List<String> models = readModelsFromFile(brandFileName);
            if (!models.contains(model)) {
                models.add(model);
                writeModelsToFile(brandFileName, models);

                // Create a new file for the entered brand and model
                String modelFileName = MODELS_FOLDER + selectedBrand + model + ".txt";
                try {
                    PrintWriter writer = new PrintWriter(new FileOutputStream(new File(modelFileName), true));
                    //  writer.println(selectedBrand + model);
                    // writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            showModels(selectedBrand);
        } else {
            JOptionPane.showMessageDialog(this, "Bu model zaten var!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editModel(String selectedBrand) {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow != -1) {
            String oldModel = (String) tableModel.getValueAt(selectedRow, 0);
            String newModel = JOptionPane.showInputDialog(this, "Yeni değeri girin:", oldModel);
            if (newModel != null && !newModel.isEmpty()) {
                String brandFileName = MODELS_FOLDER + selectedBrand + ".txt";
                File f = new File(brandFileName);
                List<String> models = null;
                if (f.exists())
                    models = readModelsFromFile(brandFileName);
                int index = models.indexOf(oldModel);
                if (index != -1) {
                    models.set(index, newModel);
                    writeModelsToFile(brandFileName, models);

                    // Rename the model file
                    String oldModelFileName = MODELS_FOLDER + selectedBrand + oldModel + ".txt";
                    String newModelFileName = MODELS_FOLDER + selectedBrand + newModel + ".txt";
                    File file = new File(oldModelFileName);
                    file.renameTo(new File(newModelFileName));

                    showModels(selectedBrand);
                } else {
                    JOptionPane.showMessageDialog(this, "Belirtilen model bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir model seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private List<String> readModelsFromFile(String filePath) {
        List<String> models = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                models.add(line);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Model dosyası okunurken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        return models;
    }

    private void writeModelsToFile(String filePath, List<String> models) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            for (String model : models) {
                writer.println(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Model dosyasına yazılırken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> readDetailsFromFile(String selectedBrand, String selectedModel) {
        List<String> details = new ArrayList<>();
        String filePath = MODELS_FOLDER + selectedBrand + selectedModel + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                details.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading details file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return details;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModelManagementPanel("OrnekMarka"));
    }
}
