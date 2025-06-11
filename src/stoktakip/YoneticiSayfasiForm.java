package stoktakip;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.sql.Connection;

public class YoneticiSayfasiForm extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel model;

    public YoneticiSayfasiForm() {
    initComponents();
    baglantiKur();

    // tblUrunler için model
    DefaultTableModel urunModel = new DefaultTableModel(
        new Object[][]{},
    new String[]{"UrunAdi", "StokAdedi", "Kategori", "Fiyat", "Tedarikci"}
    ) {
        boolean[] canEdit = new boolean[] {false, false};
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    };
    tblUrunler.setModel(urunModel);

    // Kullanıcı tablosu için model
    model = new DefaultTableModel();
    model.setColumnIdentifiers(new String[]{"ID", "Ad", "Soyad", "Pozisyon", "Email", "Rol"});
    jTable1.setModel(model);

    urunleriGetir();
    listeleKullanicilar();

    // Selection listener
    jTable1.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                tfAd.setText(model.getValueAt(selectedRow, 1).toString());
                tfSoyad.setText(model.getValueAt(selectedRow, 2).toString());
                tfPozisyon.setText(model.getValueAt(selectedRow, 3).toString());
                tfEmail.setText(model.getValueAt(selectedRow, 4).toString());
                cbRol.setSelectedItem(model.getValueAt(selectedRow, 5).toString());
                tfParola.setText("");
            }
        }
    });
}
public void urunleriGetir() {
    DefaultTableModel model = (DefaultTableModel) tblUrunler.getModel();
    model.setRowCount(0);  // önce temizle

    String sql = "SELECT isim AS UrunAdi, stok_miktari AS StokAdedi,kategori AS Kategori,birim_fiyat AS Fiyat,tedarikci AS Tedarikci FROM Malzeme";


    try {
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while(rs.next()) {
            String urunAdi = rs.getString("UrunAdi");
            int stokAdedi = rs.getInt("StokAdedi");
            String kategori=rs.getString("Kategori");
            int fiyat=rs.getInt("Fiyat");
            String tedarik=rs.getString("Tedarikci");

            model.addRow(new Object[]{urunAdi, stokAdedi,kategori,fiyat,tedarik});
        }

        rs.close();
        pst.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void baglantiKur() {
        try {
            String url = "jdbc:sqlserver://DESKTOP-6BEGMBC:1433;databaseName=Stok_Takip;encrypt=true;trustServerCertificate=true";
            String user = "oguz";
            String password = "1234";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Veritabanına bağlandı.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listeleKullanicilar() {
        try {
            model.setRowCount(0);
            String filtre = txtAra.getText().trim();
            String sql;
            PreparedStatement ps;

            if (filtre.isEmpty()) {
                sql = "SELECT id, ad, soyad, pozisyon, email, rol FROM Personel";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT id, ad, soyad, pozisyon, email, rol FROM Personel WHERE ad LIKE ? OR soyad LIKE ?";
ps = conn.prepareStatement(sql);
ps.setString(1, "%" + filtre + "%");
ps.setString(2, "%" + filtre + "%");

            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               Object[] row = {
    rs.getInt("id"),
    rs.getString("ad"),
    rs.getString("soyad"),
    rs.getString("pozisyon"),
    rs.getString("email"),
    rs.getString("rol")
};

                model.addRow(row);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kullanıcıları listeleme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
private void kullaniciEkle() {
        try {
            String ad = tfAd.getText().trim();
            String soyad = tfSoyad.getText().trim();
            String pozisyon = tfPozisyon.getText().trim();
            String email = tfEmail.getText().trim();
            String parola = tfParola.getText().trim();
            String rol = cbRol.getSelectedItem().toString();

            if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || parola.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm zorunlu alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO Personel (ad, soyad, pozisyon, email, parola, rol) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ad);
            ps.setString(2, soyad);
            ps.setString(3, pozisyon);
            ps.setString(4, email);
            ps.setString(5, parola);
            ps.setString(6, rol);


            int sonuc = ps.executeUpdate();
            ps.close();

            if (sonuc > 0) {
                JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla eklendi.");
                listeleKullanicilar();
                temizleForm();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kullanıcı ekleme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
private void kullaniciSil() {
    try {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir kullanıcı seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Seçili kullanıcıyı silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Personel WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int sonuc = ps.executeUpdate();
        ps.close();

        if (sonuc > 0) {
            JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla silindi.");
            listeleKullanicilar();
            temizleForm();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Kullanıcı silme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

    private void kullaniciGuncelle() {
    try {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir kullanıcı seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String ad = tfAd.getText().trim();
        String soyad = tfSoyad.getText().trim();
        String pozisyon = tfPozisyon.getText().trim();
        String email = tfEmail.getText().trim();
        String rol = cbRol.getSelectedItem().toString();
        String parola = tfParola.getText().trim();

        if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen zorunlu alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql;
        PreparedStatement ps;

        if (parola.isEmpty()) {
            sql = "UPDATE Personel SET ad = ?, soyad = ?, pozisyon = ?, email = ?, rol = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ad);
            ps.setString(2, soyad);
            ps.setString(3, pozisyon);
            ps.setString(4, email);
            ps.setString(5, rol);
            ps.setInt(6, id);
        } else {
            sql = "UPDATE Personel SET ad = ?, soyad = ?, pozisyon = ?, email = ?, parola = ?, rol = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ad);
            ps.setString(2, soyad);
            ps.setString(3, pozisyon);
            ps.setString(4, email);
            ps.setString(5, parola);
            ps.setString(6, rol);
            ps.setInt(7, id);
        }

        int sonuc = ps.executeUpdate();
        ps.close();

        if (sonuc > 0) {
            JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla güncellendi.");
            listeleKullanicilar();
            temizleForm();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Kullanıcı güncelleme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
  private void urunEkle() {
    try {
        String urunAdi = txtUrunAdi.getText().trim();
        String stokStr = txtStokAdedi.getText().trim();
        String kategori = txtKategori.getText().trim();
        String fiyatStr = txtFiyat.getText().trim();
        String tedarikci = txtTedarikci.getText().trim();

        if (urunAdi.isEmpty() || stokStr.isEmpty() || kategori.isEmpty() || fiyatStr.isEmpty() || tedarikci.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stokAdedi = Integer.parseInt(stokStr);
        int fiyat = Integer.parseInt(fiyatStr);

        String sql = "INSERT INTO Malzeme (isim, stok_miktari, kategori, birim_fiyat, tedarikci) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, urunAdi);
        ps.setInt(2, stokAdedi);
        ps.setString(3, kategori);
        ps.setInt(4, fiyat);
        ps.setString(5, tedarikci);

        int sonuc = ps.executeUpdate();
        ps.close();

        if (sonuc > 0) {
            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi.");
            urunleriGetir();

            // Alanları temizle
            txtUrunAdi.setText("");
            txtStokAdedi.setText("");
            txtKategori.setText("");
            txtFiyat.setText("");
            txtTedarikci.setText("");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Stok adedi ve fiyat sayısal olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ürün ekleme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}


// Ürün Güncelle
private void urunGuncelle() {
    try {
        int selectedRow = tblUrunler.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir ürün seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String urunAdi = txtUrunAdi.getText().trim();
        String stokStr = txtStokAdedi.getText().trim();
        String kategori = txtKategori.getText().trim();
        String fiyatStr = txtFiyat.getText().trim();
        String tedarikci = txtTedarikci.getText().trim();

        if (urunAdi.isEmpty() || stokStr.isEmpty() || kategori.isEmpty() || fiyatStr.isEmpty() || tedarikci.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stokAdedi = Integer.parseInt(stokStr);
        int fiyat = Integer.parseInt(fiyatStr);

        // Güncellenecek ürünün eski adını al (veya id kullanıyorsan id'yi al)
        String urunAdiTablo = tblUrunler.getValueAt(selectedRow, 0).toString();

        String sql = "UPDATE Malzeme SET isim = ?, stok_miktari = ?, kategori = ?, birim_fiyat = ?, tedarikci = ? WHERE isim = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, urunAdi);
        ps.setInt(2, stokAdedi);
        ps.setString(3, kategori);
        ps.setInt(4, fiyat);
        ps.setString(5, tedarikci);
        ps.setString(6, urunAdiTablo);

        int sonuc = ps.executeUpdate();
        ps.close();

        if (sonuc > 0) {
            JOptionPane.showMessageDialog(this, "Ürün başarıyla güncellendi.");
            urunleriGetir();
            temizleUrunForm();
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Stok adedi ve fiyat sayısal olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ürün güncelleme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}


// Ürün Sil
private void urunSil() {
    try {
        int selectedRow = tblUrunler.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir ürün seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String urunAdi = tblUrunler.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Seçili ürünün stok adedini 1 azaltmak istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String stokSorgu = "SELECT stok_miktari FROM Malzeme WHERE isim = ?";
        PreparedStatement ps1 = conn.prepareStatement(stokSorgu);
        ps1.setString(1, urunAdi);
        ResultSet rs = ps1.executeQuery();

        if (rs.next()) {
            int stok = rs.getInt("stok_miktari");
            rs.close();
            ps1.close();

            if (stok > 1) {
                String updateSql = "UPDATE Malzeme SET stok_miktari = stok_miktari - 1 WHERE isim = ?";
                PreparedStatement ps2 = conn.prepareStatement(updateSql);
                ps2.setString(1, urunAdi);
                int sonuc = ps2.executeUpdate();
                ps2.close();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Ürünün stok adedi 1 azaltıldı.");
                }
            } else {
                String deleteSql = "DELETE FROM Malzeme WHERE isim = ?";
                PreparedStatement ps3 = conn.prepareStatement(deleteSql);
                ps3.setString(1, urunAdi);
                int sonuc = ps3.executeUpdate();
                ps3.close();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Ürün stok adedi 0 olduğu için veritabanından silindi.");
                }
            }

            urunleriGetir();
            temizleUrunForm();

        } else {
            JOptionPane.showMessageDialog(this, "Ürün bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            rs.close();
            ps1.close();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ürün stok azaltma/silme hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}




    private void temizleForm() {
        tfAd.setText("");
        tfSoyad.setText("");
        tfPozisyon.setText("");
        tfEmail.setText("");
        tfParola.setText("");
        cbRol.setSelectedIndex(0);
    }
private void temizleUrunForm() {
    txtUrunAdi.setText("");
    txtStokAdedi.setText("");
    txtKategori.setText("");
    txtFiyat.setText("");
    txtTedarikci.setText("");
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        JButton1 = new javax.swing.JButton();
        txtAra = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tfAd = new javax.swing.JTextField();
        tfSoyad = new javax.swing.JTextField();
        tfPozisyon = new javax.swing.JTextField();
        tfEmail = new javax.swing.JTextField();
        tfParola = new javax.swing.JTextField();
        cbRol = new javax.swing.JComboBox<>();
        btnEkle = new javax.swing.JButton();
        btnGuncelle1 = new javax.swing.JButton();
        btnSil = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblUrunler = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtUrunAdi = new javax.swing.JTextField();
        txtStokAdedi = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtKategori = new javax.swing.JTextField();
        txtTedarikci = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtFiyat = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        JButton1.setBackground(new java.awt.Color(52, 152, 219));
        JButton1.setForeground(new java.awt.Color(255, 255, 255));
        JButton1.setText("Listele");
        JButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Ada Göre Filtrele:");

        jLabel2.setText("Rol:");

        jLabel3.setText("Soyad:");

        jLabel4.setText("Pozisyon:");

        jLabel5.setText("Email:");

        jLabel6.setText("Parola:");

        jLabel7.setText("Ad:");

        cbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Personel","Yönetici"}));

        btnEkle.setBackground(new java.awt.Color(52, 152, 219));
        btnEkle.setForeground(new java.awt.Color(255, 255, 255));
        btnEkle.setText("Ekle");
        btnEkle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEkleActionPerformed(evt);
            }
        });

        btnGuncelle1.setBackground(new java.awt.Color(52, 152, 219));
        btnGuncelle1.setForeground(new java.awt.Color(255, 255, 255));
        btnGuncelle1.setText("Güncelle");
        btnGuncelle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuncelle1ActionPerformed(evt);
            }
        });

        btnSil.setBackground(new java.awt.Color(52, 152, 219));
        btnSil.setForeground(new java.awt.Color(255, 255, 255));
        btnSil.setText("Sil");
        btnSil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSilActionPerformed(evt);
            }
        });

        jLabel8.setText("YÖNETİCİ SAYFASI");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAra, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfAd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfSoyad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfParola, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbRol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfPozisyon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(JButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEkle, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuncelle1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSil, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtAra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tfAd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tfSoyad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tfPozisyon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tfParola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addGap(112, 112, 112)))
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuncelle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEkle, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(192, 192, 192))
        );

        tblUrunler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "UrunAdi", "StokAdedi", "Kategori", "Fiyat", "Tedarikci"
            }
        ));
        tblUrunler.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUrunlerMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblUrunler);

        jLabel9.setText("Ürün Adı:");

        jLabel10.setText("Stok Adeti:");

        jButton2.setBackground(new java.awt.Color(52, 152, 219));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Listele");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(52, 152, 219));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Güncelle");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(52, 152, 219));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Ekle");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(52, 152, 219));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Sil");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel11.setText("Kategori:");

        jLabel12.setText("Fiyat:");

        jLabel13.setText("Tedarikçi:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel10))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtUrunAdi, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtStokAdedi, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTedarikci, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFiyat, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(434, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtUrunAdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtStokAdedi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txtFiyat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtTedarikci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton1ActionPerformed
      listeleKullanicilar();
    }//GEN-LAST:event_JButton1ActionPerformed

    private void btnEkleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEkleActionPerformed
        kullaniciEkle();
    }//GEN-LAST:event_btnEkleActionPerformed

    private void btnSilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSilActionPerformed
      kullaniciSil();
    }//GEN-LAST:event_btnSilActionPerformed

    private void btnGuncelle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuncelle1ActionPerformed
       kullaniciGuncelle(); 
    }//GEN-LAST:event_btnGuncelle1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        urunleriGetir();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        urunEkle();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        urunGuncelle();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       urunSil();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void tblUrunlerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUrunlerMouseClicked
        int selectedRow = tblUrunler.getSelectedRow();
    if (selectedRow != -1) {
        txtUrunAdi.setText(tblUrunler.getValueAt(selectedRow, 0).toString());
        txtStokAdedi.setText(tblUrunler.getValueAt(selectedRow, 1).toString());
        txtKategori.setText(tblUrunler.getValueAt(selectedRow, 2).toString());
        txtFiyat.setText(tblUrunler.getValueAt(selectedRow, 3).toString());
        txtTedarikci.setText(tblUrunler.getValueAt(selectedRow, 4).toString());
    }
    }//GEN-LAST:event_tblUrunlerMouseClicked

   
     public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new YoneticiSayfasiForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButton1;
    private javax.swing.JButton btnEkle;
    private javax.swing.JButton btnGuncelle1;
    private javax.swing.JButton btnSil;
    private javax.swing.JComboBox<String> cbRol;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblUrunler;
    private javax.swing.JTextField tfAd;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfParola;
    private javax.swing.JTextField tfPozisyon;
    private javax.swing.JTextField tfSoyad;
    private javax.swing.JTextField txtAra;
    private javax.swing.JTextField txtFiyat;
    private javax.swing.JTextField txtKategori;
    private javax.swing.JTextField txtStokAdedi;
    private javax.swing.JTextField txtTedarikci;
    private javax.swing.JTextField txtUrunAdi;
    // End of variables declaration//GEN-END:variables
}
