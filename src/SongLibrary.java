public class SongLibrary {
    private Song[] hashArray;
    private int arraySize;

    public SongLibrary(int size) {
        this.arraySize = size;
        this.hashArray = new Song[arraySize];
    }

    public int hashFunc(String key) {
        int hashVal = key.hashCode();
        hashVal = Math.abs(hashVal);
        return hashVal % arraySize;
    }

    public void insert(Song song) {
        String key = song.getTitle();
        int hashVal = hashFunc(key);
        int startHashVal = hashVal;

        // Linear Probing: cari sel kosong
        while (hashArray[hashVal] != null) {
            ++hashVal;
            hashVal %= arraySize;
            if (hashVal == startHashVal) {
                System.out.println("Error: Hash Table penuh!");
                return;
            }
        }
        hashArray[hashVal] = song;
        System.out.println("LIBRARY: Menambahkan '" + key + "' di index " + hashVal);
    }

    public Song find(String key) {
        int hashVal = hashFunc(key);
        int startHashVal = hashVal;

        while (hashArray[hashVal] != null) {
            if (hashArray[hashVal].getTitle().equals(key)) {
                return hashArray[hashVal];
            }
            ++hashVal;
            hashVal %= arraySize;
            if (hashVal == startHashVal) {
                return null;
            }
        }
        return null;
    }

    public Song delete(String key) {
        int hashVal = hashFunc(key);
        int startHashVal = hashVal; 

        while (hashArray[hashVal] != null) {
            if (hashArray[hashVal].getTitle().equals(key)) {
                Song temp = hashArray[hashVal];
                hashArray[hashVal] = null; 
                System.out.println("LIBRARY: Menghapus '" + key + "'");
                return temp;
            }

            ++hashVal;
            hashVal %= arraySize;

            if (hashVal == startHashVal) {
                return null;
            }
        }
        return null;
    }
}