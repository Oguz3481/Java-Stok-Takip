package stoktakip;
import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PersonelIslemleri implements VeriEkleme, VeriGuncelleme, VeriListeleme {
    private static Connection conn;

    public PersonelIslemleri() {
        baglantiKur();
    }

    private void baglantiKur() {
        try {
            String url = "jdbc:sqlserver://DESKTOP-6BEGMBC:1433;databaseName=Stok_Takip;user=oguz;password=1234;encrypt=true;trustServerCertificate=true";
            conn = DriverManager.getConnection(url);
            System.out.println("✅ Bağlantı başarılı!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1️⃣ Ekleme Komutu
    @Override
    public void ekleme(String ad, String soyad, String pozisyon, String email, String parola, String rol) {
        try {
            if (!rol.equals("Yönetici") && !rol.equals("Personel")) {
                System.out.println("❌ HATA: Rol sadece 'Yönetici' veya 'Personel' olabilir!");
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
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Personel başarıyla eklendi!");
            } else {
                System.out.println("❌ Personel eklenemedi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2️⃣ Güncelleme Komutu
    @Override
    public void guncelleme(int id, String yeniVeri) {
        try {
            String sql = "UPDATE Personel SET pozisyon = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, yeniVeri);
            ps.setInt(2, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Personel güncellendi!");
            } else {
                System.out.println("❌ Güncelleme başarısız!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3️⃣ Silme Komutu
    @Override
    public void silme(int id) {
        try {
            String sql = "DELETE FROM Personel WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Personel başarıyla silindi!");
            } else {
                System.out.println("❌ Silme işlemi başarısız!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
public void listeleme() {
    try {
        String sql = "SELECT * FROM Personel";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("📌 Personel Listesi:");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + 
                               ", Ad: " + rs.getString("ad") + 
                               ", Soyad: " + rs.getString("soyad") + 
                               ", Pozisyon: " + rs.getString("pozisyon") + 
                               ", Email: " + rs.getString("email") + 
                               ", Rol: " + rs.getString("rol"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    // 4️⃣ Listeleme Komutu
   @Override
public void listeleme(JTable tablo) {
    try {
        String sql = "SELECT * FROM Personel";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        DefaultTableModel model = (DefaultTableModel) tablo.getModel();
        model.setRowCount(0); // Tabloyu temizle

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
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}