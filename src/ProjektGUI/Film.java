package ProjektGUI;

public abstract class Film {
    String title;
    int how_many;
    GENRE genre;

    public Film(String title, int how_many) {
        this.title = title;
        this.how_many = how_many;
    }
}
