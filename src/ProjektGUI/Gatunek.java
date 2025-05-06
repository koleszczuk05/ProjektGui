package ProjektGUI;
// To wrzucić do pakietu z typami filmów.
// Ta klasa się powinna nazywac Film.
public abstract class Gatunek {
    String title; // wszystko po angielsku
    int nwm; // zmien ta nazwe 
    GENRE genre;

    public Gatunek(String title, int nwm) {
        this.title = title;
        this.nwm = nwm;
    }
}
