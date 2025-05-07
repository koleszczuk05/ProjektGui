package ProjektGUI;

// To wrzucić do pakietu z typami filmów. 
public class Comedy extends Gatunek {
    public Comedy(String title, int nwm) {
        super(title, nwm);
        this.genre = GENRE.COMEDY;
    }
}
