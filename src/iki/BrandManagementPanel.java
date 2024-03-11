package iki;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BrandManagementPanel extends JFrame {
    private JTable brandTable;
    private DefaultTableModel tableModel;
    private static final String BRANDS_FILE = "db//brands.txt";

    public BrandManagementPanel() {
        super("Brand Management Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);

        JToolBar toolBar = new JToolBar();
        JButton backButton = new JButton("Geri");
        JButton forwardButton = new JButton("İleri");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Geri butonuna tıklandığında yapılacak işlemler
                System.out.println("Geri");
            }
        });

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // İleri butonuna tıklandığında yapılacak işlemler
                System.out.println("İleri");

                // İleri butonuna tıklandığında ModelManagementPanel'ı aç
                int selectedRow = brandTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedBrand = (String) tableModel.getValueAt(selectedRow, 0);
                    openModelManagementPanel(selectedBrand);
                }
            }
        });

        toolBar.add(backButton);
        toolBar.add(forwardButton);
        getContentPane().add(toolBar, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Brand"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        brandTable = new JTable(tableModel);
        brandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(brandTable);
        JButton deleteButton = new JButton("Sil");
        JButton addButton = new JButton("Ekle");
        JButton editButton = new JButton("Düzenle");
        deleteButton.addActionListener(e -> deleteBrand());
        addButton.addActionListener(e -> addBrand());
        editButton.addActionListener(e -> editBrand());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Brand elementine tıklanınca ModelManagementPanel'i aç
        brandTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = brandTable.getSelectedRow();
                if (selectedRow != -1 && e.getClickCount() == 2) {
                    // İki kez tıklandığında aç
                    String selectedBrand = (String) tableModel.getValueAt(selectedRow, 0);
                    openModelManagementPanel(selectedBrand);
                }
            }
        });
        getContentPane().add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);

        // New button to reopen the main frame
        JButton newButton = new JButton("çıkış yap");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // MainFrame örneği oluştur
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

                // Mevcut pencereyi kapat
                dispose();
            }
        });
        buttonPanel.add(newButton);

        setVisible(true);

        // Tabloyu otomatik olarak doldur
        showBrands();
    }

    private void closeWindow() {
        this.dispose();  // BrandManagementPanel penceresini kapat
    }

    private void showBrands() {
        tableModel.setRowCount(0); // Tabloyu temizle
        List<String> brands = readBrandsFromFile();
        for (String brand : brands) {
            tableModel.addRow(new Object[]{brand});
        }
    }

    private void addBrand() {
        String brand = JOptionPane.showInputDialog(this, "Eklemek istediğiniz markayı girin:");
        if (brand != null && !brand.isEmpty()) {
            List<String> brands = readBrandsFromFile();
            if (!brands.contains(brand)) {
                brands.add(brand);
                writeBrandsToFile(brands);
                createBrandFile(brand); // Brand dosyasını oluştur

                showBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Bu marka zaten var!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createBrandFile(String brand) {
        String brandFileName = "db//" + brand + ".txt";
        try {
            File brandFile = new File(brandFileName);
            if (brandFile.createNewFile()) {
                System.out.println(brandFileName + " dosyası oluşturuldu.");
            } else {
                System.err.println("Hata: " + brandFileName + " dosyası zaten mevcut.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Brand dosyası oluşturulurken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> readBrandsFromFile() {
        List<String> brands = new ArrayList<>();
        File brandsFile = new File(BRANDS_FILE);

        // Dosya varsa oku, yoksa oluştur
        if (brandsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(brandsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    brands.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Marka dosyası okunurken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        return brands;
    }

    private void writeBrandsToFile(List<String> brands) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(BRANDS_FILE)))) {
            for (String brand : brands) {
                writer.println(brand);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Marka dosyasına yazılırken bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBrand() {
        int selectedRow = brandTable.getSelectedRow();
        if (selectedRow != -1) {
            String brandToDelete = (String) tableModel.getValueAt(selectedRow, 0);
            List<String> brands = readBrandsFromFile();

            // Seçili markayı listeden kaldır
            brands.remove(brandToDelete);

            // Markaları dosyaya yaz
            writeBrandsToFile(brands);

            // selectedbrand.txt dosyasını sil
            String selectedBrandFileName = "db//" + brandToDelete + ".txt";
            File selectedBrandFile = new File(selectedBrandFileName);
            if (selectedBrandFile.exists()) {
                if (selectedBrandFile.delete()) {
                    System.out.println(selectedBrandFileName + " dosyası silindi.");
                } else {
                    System.err.println("Hata: " + selectedBrandFileName + " dosyası silinemedi.");
                }
            }

            // Tabloyu güncelle
            showBrands();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir marka seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editBrand() {
        int selectedRow = brandTable.getSelectedRow();
        if (selectedRow != -1) {
            String oldBrand = (String) tableModel.getValueAt(selectedRow, 0);
            String newBrand = JOptionPane.showInputDialog(this, "Yeni değeri girin:", oldBrand);
            if (newBrand != null && !newBrand.isEmpty()) {
                List<String> brands = readBrandsFromFile();
                int index = brands.indexOf(oldBrand);
                if (index != -1) {
                    // Dosya adını değiştir
                    String oldBrandFileName = "db//" + oldBrand + ".txt";
                    String newBrandFileName = "db//" + newBrand + ".txt";
                    File oldBrandFile = new File(oldBrandFileName);
                    File newBrandFile = new File(newBrandFileName);

                    if (oldBrandFile.renameTo(newBrandFile)) {
                        System.out.println(oldBrandFileName + " dosyası " + newBrandFileName + " olarak değiştirildi.");
                    } else {
                        System.err.println("Hata: " + oldBrandFileName + " dosyası " + newBrandFileName + " olarak değiştirilemedi.");
                    }

                    // Listeyi güncelle
                    brands.set(index, newBrand);

                    // Markaları dosyaya yaz
                    writeBrandsToFile(brands);

                    showBrands();
                } else {
                    JOptionPane.showMessageDialog(this, "Belirtilen marka bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir marka seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openModelManagementPanel(String selectedBrand) {
        // Bu sınıftan yeni bir ModelManagementPanel oluştur
        ModelManagementPanel modelManagementPanel = new ModelManagementPanel(selectedBrand);

        // Bu pencereyi kapat ve bellekten temizle
        dispose();

        // ModelManagementPanel penceresini göster
        modelManagementPanel.setVisible(true);

        // ModelManagementPanel penceresinin kapatıldığında bu pencereyi tekrar göster
        };
    
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BrandManagementPanel::new);
    }
}
