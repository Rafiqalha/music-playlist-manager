import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class SongStorage {

    private static final String FILE_PATH = "playlist_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void savePlaylist(Playlist myPlaylist) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            int count = 0;
            SongNode current = myPlaylist.getHead();
            while (current != null) {
                count++;
                current = current.getNext();
            }
            Song[] tempArray = new Song[count];
            current = myPlaylist.getHead();
            int index = 0;
            while (current != null) {
                tempArray[index] = current.getData();
                index++;
                current = current.getNext();
            }
            gson.toJson(tempArray, writer);
            System.out.println("Berhasil menyimpan " + count + " lagu ke JSON.");

        } catch (IOException e) {
            System.err.println("Gagal menyimpan: " + e.getMessage());
        }
    }

    public static Song[] loadSongs() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return null;
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, Song[].class);
        } catch (IOException e) {
            System.err.println("Gagal membaca: " + e.getMessage());
            return null;
        }
    }
}