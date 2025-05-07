package ProjektGUI;
// To wrzucić do pakietu z typami filmów. 
public class Action extends Gatunek{
    public Action(String title, int nwm) {
        super(title, nwm);
        this.genre=GENRE.ACTION;
    }
}
