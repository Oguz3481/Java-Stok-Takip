package stoktakip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
public class Form1 extends javax.swing.JFrame {
    private Connection conn; // Bağlantıyı sınıf seviyesinde tanımla

    public Form1() {
            initComponents();

        baglantiKur(); // SQL bağlantısını başlat
    }
private void baglantiKur() {
    try {
String url = "jdbc:sqlserver://DESKTOP-6BEGMBC\\SQLEXPRESS:1433;databaseName=Stok_Takip;user=oguz;password=1234;encrypt=true;trustServerCertificate=true";
        
        conn = DriverManager.getConnection(url);
        JOptionPane.showMessageDialog(this, "Bağlantı başarılı!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Bağlantı hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

private void veriCek() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Personel");
            
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("ad") + " " + rs.getString("soyad"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    
    public void kapatBaglanti() {
    try {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Bağlantı kapatıldı.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

     public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Form1().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
