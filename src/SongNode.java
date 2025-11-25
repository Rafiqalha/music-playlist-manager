public class SongNode {
    public Song data;
    public SongNode next;
    public SongNode previous;

    public SongNode(Song data) {
        this.data = data;
        this.next = null;
        this.previous = null;
    }

    public Song getData() {
        return data;
    }

    public SongNode getNext() {
        return next;
    }

    public void displayNode() {
        System.out.print(data.toString() + "; ");
    }
}