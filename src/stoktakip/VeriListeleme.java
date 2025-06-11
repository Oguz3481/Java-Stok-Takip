package stoktakip;
import javax.swing.JTable;

public interface VeriListeleme {
    void listeleme(); // Konsola yazan hâli
    void listeleme(JTable tablo); // JTable'ı dolduran hâli
}