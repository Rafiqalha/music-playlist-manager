public class SongStack {
    private Song[] stackArray;
    private int maxSize;
    private int top;

    public SongStack(int size) {
        maxSize = size;
        stackArray = new Song[maxSize];
        top = -1;
    }

    public void push(Song song) {
        if (isFull()) {
            System.err.println("Stack Penuh! Tidak bisa push " + song.getTitle());
        } else {
            stackArray[++top] = song;
            System.out.println("HISTORY: Push " + song.getTitle());
        }
    }

    public Song pop() {
        if (isEmpty()) {
            System.err.println("Stack Kosong!");
            return null;
        } else {
            return stackArray[top--]; 
        }
    }

    public Song peek() {
        if (isEmpty()) {
            return null;
        }
        return stackArray[top];
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == maxSize - 1);
    }
}