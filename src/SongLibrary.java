/**
 * Class SongLibrary
 * Implementasi Hash Table dari Praktikum 8 (hal 86).
 * Disesuaikan untuk menyimpan object Song dengan key berupa String (title).
 * Menggunakan Open Addressing (Linear Probing) untuk collision.
 */
public class SongLibrary {
    private Song[] hashArray;
    private int arraySize;

    public SongLibrary(int size) {
        this.arraySize = size;
        this.hashArray = new Song[arraySize];
        // Kita tidak perlu inisialisasi nonItem/deleted marker
        // karena kita ikuti modul (hal 87) yang menggunakan 'null'
    }

    /**
     * Fungsi Hash (Praktikum 8, hal 93)
     * Disesuaikan untuk menerima key String (judul lagu).
     */
    public int hashFunc(String key) {
        // Gunakan hashCode() bawaan Java untuk String
        int hashVal = key.hashCode();
        
        // Pastikan nilainya positif
        hashVal = Math.abs(hashVal); 
        
        return hashVal % arraySize;
    }

    /**
     * Method insert (Praktikum 8, hal 87)
     * Disesuaikan untuk menyimpan object Song.
     */
    public void insert(Song song) {
        String key = song.getTitle();
        int hashVal = hashFunc(key);
        int startHashVal = hashVal; // Simpan hash asli

        // Linear Probing: cari sel kosong
        while (hashArray[hashVal] != null) {
            ++hashVal; // Geser ke sel berikutnya
            hashVal %= arraySize; // Wrap-around jika sudah di akhir

            // Tambahan: Cek jika table sudah penuh dan kembali ke awal
            if (hashVal == startHashVal) {
                System.out.println("Error: Hash Table penuh!");
                return;
            }
        }
        
        // Sel kosong ditemukan
        hashArray[hashVal] = song;
        System.out.println("LIBRARY: Menambahkan '" + key + "' di index " + hashVal);
    }

    /**
     * Method find (Praktikum 8, hal 88)
     * Disesuaikan untuk mencari berdasarkan String title.
     */
    public Song find(String key) {
        int hashVal = hashFunc(key);
        int startHashVal = hashVal; // Simpan hash asli

        while (hashArray[hashVal] != null) {
            // Cek apakah key-nya cocok
            if (hashArray[hashVal].getTitle().equals(key)) {
                return hashArray[hashVal]; // Ditemukan!
            }
            
            ++hashVal; // Lanjut probing
            hashVal %= arraySize; // Wrap-around

            // Jika sudah berputar penuh dan tidak ketemu
            if (hashVal == startHashVal) {
                return null; // Tidak ada
            }
        }
        
        return null; // Selnya kosong, berarti tidak ada
    }

    /**
     * Method delete (Praktikum 8, hal 87)
     * Disesuaikan untuk menghapus berdasarkan String title.
     * PENTING: Implementasi 'delete' di modul ini (menggunakan null)
     * sebenarnya punya kelemahan, tapi kita ikuti agar sesuai modul.
     */
    public Song delete(String key) {
        int hashVal = hashFunc(key);
        int startHashVal = hashVal; // Simpan hash asli

        while (hashArray[hashVal] != null) {
            if (hashArray[hashVal].getTitle().equals(key)) {
                Song temp = hashArray[hashVal];
                hashArray[hashVal] = null; // Hapus (set ke null)
                System.out.println("LIBRARY: Menghapus '" + key + "'");
                return temp;
            }

            ++hashVal;
            hashVal %= arraySize;

            if (hashVal == startHashVal) {
                return null; // Tidak ketemu
            }
        }
        return null; // Tidak ketemu
    }

    // Nanti kita bisa tambahkan method displayTable() jika perlu
}