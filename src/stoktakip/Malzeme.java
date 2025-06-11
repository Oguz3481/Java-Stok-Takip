package stoktakip;

public class Malzeme extends MalzemeBase {
    // Encapsulation - private alanlar
    private int id;
    private String isim;
    private String kategori;
    private int stokMiktari;
    private double birimFiyat;
    private String tedarikci;

    // Static kullanım
    private static int toplamMalzemeSayisi = 0;

    // Constructor
    public Malzeme(int id, String isim, String kategori, int stokMiktari, double birimFiyat, String tedarikci) {
        this.id = id;
        this.isim = isim;
        this.kategori = kategori;
        this.stokMiktari = stokMiktari;
        this.birimFiyat = birimFiyat;
        this.tedarikci = tedarikci;
        toplamMalzemeSayisi++;
    }

    public Malzeme(String isim, String kategori, int stokMiktari, double birimFiyat, String tedarikci) {
        this(0, isim, kategori, stokMiktari, birimFiyat, tedarikci);
    }
    
    @Override
    public void malzemeBilgisiGoster() {
        System.out.println("Malzeme: " + isim + ", Kategori: " + kategori +
            ", Stok: " + stokMiktari + ", Fiyat: " + birimFiyat + ", Tedarikçi: " + tedarikci);
    }

    // Getter ve Setterlar (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsim() { return isim; }
    public void setIsim(String isim) { this.isim = isim; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public int getStokMiktari() { return stokMiktari; }
    public void setStokMiktari(int stokMiktari) { this.stokMiktari = stokMiktari; }

    public double getBirimFiyat() { return birimFiyat; }
    public void setBirimFiyat(double birimFiyat) { this.birimFiyat = birimFiyat; }

    public String getTedarikci() { return tedarikci; }
    public void setTedarikci(String tedarikci) { this.tedarikci = tedarikci; }

    // Static metotlar
    public static void malzemeEkle() {
        toplamMalzemeSayisi++;
    }

    public static int getToplamMalzemeSayisi() {
        return toplamMalzemeSayisi;
    }

    // Method Overloading örneği
    public void guncelle(String yeniIsim) {
        this.isim = yeniIsim;
    }

    public void guncelle(String yeniIsim, int yeniStokMiktari) {
        this.isim = yeniIsim;
        this.stokMiktari = yeniStokMiktari;
    }

    // Inner Class (Stok hareketi bilgisi)
    public class StokHareketi {
        private String hareketTuru;
        private int miktar;

        public StokHareketi(String hareketTuru, int miktar) {
            this.hareketTuru = hareketTuru;
            this.miktar = miktar;
        }

        public void hareketBilgisiYazdir() {
            System.out.println("Hareket: " + hareketTuru + ", Miktar: " + miktar);
        }

        public String getHareketTuru() { return hareketTuru; }
        public int getMiktar() { return miktar; }
    }
}
