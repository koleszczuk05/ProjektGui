package ProjektGUI;

import java.time.Period;
import java.util.LinkedList;

public class Client {
    // Wszystko po angielsku.
    public String name;
    double balans; // zmien ta nazwe na cos co mowi co to jest
    private boolean abonament;
    private Wishlist wishlist;
    private Basket basket;
    private double sum_zakup=0;


    public Client(String name, double balans, boolean abonament) {
        this.name = name;
        this.balans = balans;
        this.abonament = abonament;
        wishlist = new Wishlist(this);
        basket = new Basket(this);
    }

    void add(Gatunek gatunek){
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(gatunek.genre, gatunek.title));

        if (value == null){
            wishlist.add(new Produkt(gatunek.genre, gatunek.nwm,null, gatunek.title ));
            return;
        }

        // Moze da sie skrocic tą ifologie. Na koniec
        //hhgh
        if(value.a==0 && value.b==0 && value.c==0 && value.d==0){
            wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) 0,gatunek.title));
        }
        else if(value.c==0 && value.d==0){
            if(abonament){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.b, gatunek.title));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.a, gatunek.title));
            }
        }
        else if(value.d==0){
            if(gatunek.nwm > value.c){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.b, gatunek.title));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.a, gatunek.title));
            }
        }
        else{
            if(gatunek.nwm > value.c){
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) Math.min(value.b, value.d), gatunek.title));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.b, gatunek.title));
                }
            }
            else{
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.d, gatunek.title));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, (double) value.a, gatunek.title));
                }
            }
        }
    }

    Wishlist getWishlist(){
        return wishlist;
    }

    Basket getBasket(){
        return basket;
    }

    void pack(){
        LinkedList<Produkt> products_copy = new LinkedList<>(wishlist.getListaZyczen());
        for(Produkt product : products_copy){
            if(product.price != null){
                Produkt delete = wishlist.remove(product);
                if(delete!=null){
                    basket.add(delete);
                    sum_zakup+=product.price*product.ile;
                }
            }
        }
    }

    void pay(Payform jakplaci, boolean autozwrot){
        if(jakplaci==Payform.CARD){
            sum_zakup*=1.01;
        }
        if(sum_zakup>balans) {
            if (autozwrot) {
                double temp = balans;
                LinkedList<Produkt> products = basket.getKoszykowa_lista();
                LinkedList<Produkt> templist = basket.getPozaplaceniu();
                for (int i = 0; i < products.size(); i++) {
                    Produkt product = products.get(i);
                    if (product.price * product.ile <= temp) {
                        temp -= product.price * product.ile;
                    } else {
                        for (int j = product.ile - 1; j >= 1; --j) {
                            if (product.price * j <= temp) {
                                temp -= product.price * j;
                                products.get(i).ile = j;


                                break;
                            }
                        }
                    }
                }
                balans = temp;
            }
        }
        else{
            balans -= sum_zakup;
        }
        wishlist.empty=true;
        basket.empty=true;
    }

    double getWallet(){
        return balans;
    }


}

// Klasa Wishlist i Basket robia dokładnie to samo. To powinno być wydzielone do klasy abstrakcyjnej typu ProductList czy cos
class Wishlist{
    Client client;
    boolean empty=false;
    private LinkedList <Produkt> lista_zyczen = new LinkedList<Produkt>(); // snake case pls
    //Pricelist price_list = Pricelist.getPricelist(); // to chyba nie jest tu potrzebne

    Wishlist(Client client){
        this.client = client;
    }


    Produkt remove(Produkt produkt){
        for (int i = 0; i < lista_zyczen.size(); i++) {
            Produkt temp = lista_zyczen.get(i);
            if(temp.tytul.equals(produkt.tytul) && produkt.genre==temp.genre){
                return lista_zyczen.remove(i);
            }
        }
        return null;
    }
    
    void add(Produkt produkt){
        lista_zyczen.add(produkt);
    }

    public LinkedList<Produkt> getListaZyczen() {
        return lista_zyczen;
    }

    @Override
    public String toString() {
        String temp= client.name  + "\n";
        if(empty){
            temp+=" --- pusto";
            return temp;
        }
        for(int i=0; i<lista_zyczen.size(); i++){
            temp+=lista_zyczen.get(i).toString()+"\n";
        }
        return temp;
    }
}
class Basket{
    Client client;
    boolean empty=false;
    public Basket(Client client) {
        this.client = client;
    }

    private LinkedList <Produkt> koszykowa_lista = new LinkedList<Produkt>();
    private LinkedList <Produkt> pozaplaceniu = new LinkedList<Produkt>();
    //Pricelist price_list = Pricelist.getPricelist(); // to chyba nie jest tu potrzebne


    public LinkedList<Produkt> getPozaplaceniu() {
        return pozaplaceniu;
    }

    Produkt remove(Gatunek gatunek){
        for (int i = 0; i < koszykowa_lista.size(); i++) {
            if(koszykowa_lista.get(i).tytul.equals(gatunek.title)){
                return koszykowa_lista.remove(i);
            }
        }
        return null;
    }
    
    void add(Produkt produkt){
        koszykowa_lista.add(produkt);
    }

    public LinkedList<Produkt> getKoszykowa_lista() {
        return koszykowa_lista;
    }

    public String toString() {
        String temp= client.name + "\n";
        if(empty){
            temp+=" --- pusto";
            return temp;
        }
        for(int i=0; i<koszykowa_lista.size(); i++){
            temp+=koszykowa_lista.get(i).toString()+"\n";
        }
        return temp;
    }
}
class Produkt{
    GENRE genre;
    String tytul; // po angielsku
    int ile;
    Double price = null;

    public Produkt(GENRE genre, int ile, Double price, String tytul) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.tytul = tytul;
    }

    @Override
    public String toString() {
        // Na koniec, użyj tu StringBuilderów
        String ret = "";
        switch (genre){
            case DRAMA -> ret+=tytul + ", typ: obyczaj, ile: "+ ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case ACTION -> ret+=tytul + ", typ: sensacja, ile: "+ ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case MUSICAL -> ret+=tytul + ", typ: muzyczny, ile: "+ ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case COMEDY ->  ret+=tytul + ", typ: komedia, ile: "+ ile + " urzadzenia, cena " + (price == null ? "brak" : price);
        }
        return ret;
    }
}