package ProjektGUI;

// To wrzucić do pakietu z typami filmów. 
public class Drama extends Gatunek {
    public Drama(String title, int nwm) {
        super(title, nwm);
        this.genre = GENRE.DRAMA;
    }
}