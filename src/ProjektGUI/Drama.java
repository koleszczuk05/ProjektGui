package ProjektGUI;

public class Drama extends Film {
    public Drama(String title, int nwm) {
        super(title, nwm);
        this.genre = GENRE.DRAMA;
    }
}