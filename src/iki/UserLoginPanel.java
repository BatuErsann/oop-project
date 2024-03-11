package iki;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class UserLoginPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public UserLoginPanel() {
        super("Kullanıcı Giriş Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);

        JPanel panel = new JPanel();

        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setBounds(0, 10, 143, 40);
        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(0, 62, 143, 40);

        usernameField = new JTextField();
        usernameField.setBounds(143, 11, 143, 40);
        passwordField = new JPasswordField();
        passwordField.setBounds(143, 63, 143, 40);

        JButton loginButton = new JButton("Giriş");
        loginButton.setBounds(143, 113, 143, 40);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                if (isValidUser(username, password)) {
                    // Giriş Başarılı!
                    closeWindow(); // UserLoginPanel'i kapat

                    // BrandManagementPanel'i oluştur ve göster
                    SwingUtilities.invokeLater(() -> {
                        BrandManagementPanel bm = new BrandManagementPanel();
                        bm.setVisible(true);
                    });
                } /*else {
                    JOptionPane.showMessageDialog(UserLoginPanel.this, "Geçersiz Kullanıcı Adı veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                }*/
            }
        });

        panel.setLayout(null);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        JLabel label = new JLabel();
        label.setBounds(0, 81, 143, 40);
        panel.add(label); // Boş bir etiket ekleyerek düzeni düzeltiyoruz.
        panel.add(loginButton);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void closeWindow() {
        this.dispose();  // UserLoginPanel penceresini kapat
    }

    private boolean isValidUser(String username, char[] password) {
        boolean isValid = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storedUsername = parts[0];
                String storedPassword = parts[1];

                if (username.equals(storedUsername) && new String(password).equals(storedPassword)) {
                    isValid = true;
                    break;  // Geçerli bir kullanıcı bulunduğunda döngüden çık
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!isValid) {
            // Geçerli bir kullanıcı bulunamadığında hata mesajını göster
            JOptionPane.showMessageDialog(UserLoginPanel.this, "Geçersiz Kullanıcı Adı veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        return isValid;
    }



  /*  private boolean registerUser(String username, char[] password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storedUsername = parts[0];

                if (username.equals(storedUsername)) {
                    return false; // Kullanıcı zaten mevcut
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("admin.txt", true))) {
            writer.write(username + "," + new String(password) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
    */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserLoginPanel();
            }
        });
    }
}
