package stoktakip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MalzemeManager {
    private final String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Stok_Takip;user=oguz;password=1234;encrypt=true;trustServerCertificate=true;";

    public List<Malzeme> listele() {
        List<Malzeme> liste = new ArrayList<>();
        String sql = "SELECT * FROM Malzeme";
        try (Connection con = DriverManager.getConnection(connectionUrl);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Malzeme m = new Malzeme(
                    rs.getInt("id"),
                    rs.getString("isim"),
                    rs.getString("kategori"),
                    rs.getInt("stok_miktari"),
                    rs.getDouble("birim_fiyat"),
                    rs.getString("tedarikci")
                );
                liste.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public void ekle(Malzeme m) {
        String sql = "INSERT INTO Malzeme (isim, kategori, stok_miktari, birim_fiyat, tedarikci) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(connectionUrl);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getIsim());
            ps.setString(2, m.getKategori());
            ps.setInt(3, m.getStokMiktari());
            ps.setDouble(4, m.getBirimFiyat());
            ps.setString(5, m.getTedarikci());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guncelle(Malzeme m) {
        String sql = "UPDATE Malzeme SET isim = ?, kategori = ?, stok_miktari = ?, birim_fiyat = ?, tedarikci = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(connectionUrl);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getIsim());
            ps.setString(2, m.getKategori());
            ps.setInt(3, m.getStokMiktari());
            ps.setDouble(4, m.getBirimFiyat());
            ps.setString(5, m.getTedarikci());
            ps.setInt(6, m.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sil(int id) {
    String sql = "UPDATE Malzeme SET stok_miktari = stok_miktari - 1 WHERE id = ? AND stok_miktari > 0";
    try (Connection con = DriverManager.getConnection(connectionUrl);
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
