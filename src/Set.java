public class Set {
    int index;
    Line[] lines; //holds the sets belongs to cache

    //Initializes set structure by the given size
    public void initLine(int size) {
        lines = new Line[size];
        for (int i = 0; i < size; i++) {
            lines[i] = new Line();
        }
    }

}
