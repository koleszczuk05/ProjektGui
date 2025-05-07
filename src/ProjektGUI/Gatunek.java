package ProjektGUI;

// To wrzucić do pakietu z typami filmów. To jest nadal aktualne.
// Ta klasa się powinna nazywac Film. To jest nadal aktualne.
public abstract class Gatunek {
    String title;
    int nwm; // zmien ta nazwe na coś co mówi czym to jest. Po angielsku.
    GENRE genre;

    public Gatunek(String title, int nwm) {
        this.title = title;
        this.nwm = nwm;
    }
}
