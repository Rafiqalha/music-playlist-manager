import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class SongStorage {

    private static final String FILE_PATH = "playlist_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // MENYIMPAN: Dari Linked List (Playlist) -> JSON
    public static void savePlaylist(Playlist myPlaylist) {
        try (Writer writer = new FileWriter(FILE_PATH)) {

            // 1. Hitung dulu jumlah lagu di Linked List (Manual)
            int count = 0;
            SongNode current = myPlaylist.getHead(); // Pastikan ada getter getHead() di Playlist.java
            while (current != null) {
                count++;
                current = current.getNext();
            }

            // 2. Pindahkan Linked List ke Array Biasa (Song[])
            // Ini dilakukan karena GSON butuh wadah yang jelas untuk disimpan
            Song[] tempArray = new Song[count];
            current = myPlaylist.getHead();
            int index = 0;
            while (current != null) {
                tempArray[index] = current.getData();
                index++;
                current = current.getNext();
            }

            // 3. Serahkan Array ke GSON untuk ditulis ke File
            gson.toJson(tempArray, writer);
            System.out.println("Berhasil menyimpan " + count + " lagu ke JSON.");

        } catch (IOException e) {
            System.err.println("Gagal menyimpan: " + e.getMessage());
        }
    }

    // MEMBACA: Dari JSON -> Array Lagu
    public static Song[] loadSongs() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return null; // Kalau file belum ada

        try (Reader reader = new FileReader(FILE_PATH)) {
            // GSON baca JSON dan ubah langsung jadi Array Song[]
            // Kita TIDAK pakai ArrayList, jadi aman dari aturan dosen
            return gson.fromJson(reader, Song[].class);
        } catch (IOException e) {
            System.err.println("Gagal membaca: " + e.getMessage());
            return null;
        }
    }
}