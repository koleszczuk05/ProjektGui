package ProjektGUI;

// To wrzucić do pakietu z typami filmów. To jest nadal aktualne.
public class Musical extends Gatunek {
    public Musical(String title, int nwm) {
        super(title, nwm);
        this.genre = GENRE.MUSICAL;
    }
}
