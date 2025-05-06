package ProjektGUI;

import java.util.LinkedList;

public class Client {
    // Wszystko po angielsku.
    private String name;
    double balans; // zmien ta nazwe na cos co mowi co to jest
    private boolean abonament;
    private Wishlist wishlist;
    private Basket basket;
    private double sum_zakup=0;


    public Client(String name, double balans, boolean abonament) {
        this.name = name;
        this.balans = balans;
        this.abonament = abonament;
        wishlist = new Wishlist();
        basket = new Basket();
    }

    void add(Gatunek gatunek){
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(gatunek.genre, gatunek.title));

        
        // Moze da sie skrocic tą ifologie. Na koniec
        if(value.a==0 && value.b==0 && value.c==0 && value.d==0){
            wishlist.add(new Produkt(gatunek.genre,gatunek.nwm,0,gatunek.title));
        }
        else if(value.c==0 && value.d==0){
            if(abonament){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.title));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.title));
            }
        }
        else if(value.d==0){
            if(gatunek.nwm< value.c){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.title));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.title));
            }
        }
        else{
            if(gatunek.nwm< value.c){
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, Math.min(value.b, value.d), gatunek.title));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.title));
                }
            }
            else{
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.d, gatunek.title));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.title));
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
        LinkedList<Produkt> products = wishlist.getListaZyczen();

        for(int i=0; i<products.size(); i++){
            // To jest kwadratowe, potencjalnie na koniec mozna pomyslec czego uzyc zamiast LinkedListy zeby bylo szybkie.
            Produkt product = products.get(i);
            if(product.price!=0){
                basket.add(wishlist.remove(product));
                sum_zakup+=product.price * product.ile; // sum_zakup po polsku
            }
        }
    }

    void pay(Payform jakplaci, boolean autozwrot){
        if(jakplaci==Payform.CARD){
            sum_zakup*=1.01;
        }
        if(sum_zakup>balans) {
            if (!autozwrot) {
                double temp = balans;
                LinkedList<Produkt> products = basket.getKoszykowa_lista();
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
            else{
                wishlist.clear();
                basket.clear();
            }
        }
        else{
            balans -= sum_zakup;
        }
    }

    double getWallet(){
        return balans;
    }


}

// Klasa Wishlist i Basket robia dokładnie to samo. To powinno być wydzielone do klasy abstrakcyjnej typu ProductList czy cos
class Wishlist{
    private LinkedList <Produkt> lista_zyczen; // snake case pls
    Pricelist price_list = Pricelist.getPricelist(); // to chyba nie jest tu potrzebne
    Produkt remove(Produkt produkt){
        for (int i = 0; i < lista_zyczen.size(); i++) {
            if(lista_zyczen.get(i).tytul.equals(produkt.tytul)){
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
    void clear(){
        lista_zyczen.removeAll(lista_zyczen);
    }
}
class Basket{
    private LinkedList <Produkt> koszykowa_lista;
    Pricelist price_list = Pricelist.getPricelist(); // to chyba nie jest tu potrzebne
    
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

    void clear(){
        koszykowa_lista.removeAll(koszykowa_lista);
    }
}
class Produkt{
    GENRE genre;
    String tytul; // po angielsku
    int ile;
    double price;

    public Produkt(GENRE genre, int ile, double price, String tytul) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.tytul = tytul;
    }
}