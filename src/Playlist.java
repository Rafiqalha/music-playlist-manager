public class Playlist {
    private SongNode first;
    private SongNode last;
    private SongNode currentSong;

    public Playlist() {
        this.first = null;
        this.last = null;
        this.currentSong = null;
    }

    public boolean isEmpty() {
        return (first == null);
    }

    public void addSong(Song song) {
        SongNode newNode = new SongNode(song);

        if (isEmpty()) {
            first = newNode;
            currentSong = newNode;
        } else {
            last.next = newNode;
            newNode.previous = last;
        }
        last = newNode;
        System.out.println("Menambahkan: " + song);
    }

    public Song play() {
        if (currentSong != null) {
            System.out.println("Memutar: " + currentSong.data);
            return currentSong.data;
        }
        return null;
    }

    public Song next() {
        if (currentSong != null && currentSong.next != null) {
            currentSong = currentSong.next;
            System.out.println("Pindah ke: " + currentSong.data);
            return currentSong.data;
        }
        System.out.println("Sudah di akhir playlist.");
        return null;
    }

    public Song previous() {
        if (currentSong != null && currentSong.previous != null) {
            currentSong = currentSong.previous;
            System.out.println("Pindah ke: " + currentSong.data);
            return currentSong.data;
        }
        System.out.println("Sudah di awal playlist.");
        return null;
    }

    public void displayPlaylist() {
        System.out.println("--- Isi Playlist ---");
        SongNode current = first;
        while (current != null) {
            current.displayNode();
            current = current.next;
        }
        System.out.println("\n--------------------");
    }

    public void setCurrentSong(Song song) {
        // Cari node yang sesuai di linked list
        SongNode current = first;
        while (current != null) {
            if (current.data == song) {
                this.currentSong = current; // Ditemukan! Set 'currentSong'
                System.out.println("Playlist diset ke: " + song.toString());
                return;
            }
            current = current.next;
        }
        System.err.println("Error: Lagu tidak ditemukan di playlist.");
    }

    public SongNode getHead() {
        return first;
        // PENTING: Cek variabel di atas class ini.
        // Kalau namanya 'head', ganti jadi 'return head;'
        // Kalau namanya 'first', pakai 'return first;'
    }
}