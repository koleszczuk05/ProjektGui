package Projekt1Gui;

import java.util.LinkedList;

public class Client {
    String name;
    double d;
    boolean abonament;
    Wishlist wishlist;
    Basket basket;
    double sumzakup=0;


    public Client(String name, double d, boolean abonament) {
        this.name = name;
        this.d = d;
        this.abonament = abonament;
    }

    void add(Gatunek gatunek){
        Pricelist price_list = Pricelist.getPricelist();
        PriceListKey key = new PriceListKey(gatunek.genre, gatunek.tytul);
        PriceListValue value = price_list.getPriceListValue(key);
        if(value.a==0 && value.b==0 && value.c==0 && value.d==0){
            wishlist.add(new Produkt(gatunek.genre,gatunek.nwm,0,gatunek.tytul));
        }
        else if(value.c==0 && value.d==0){
            if(abonament){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
            }
        }
        else if(value.d==0){
            if(gatunek.nwm< value.c){
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
            }
            else{
                wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
            }
        }
        else{
            if(gatunek.nwm< value.c){
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, Math.min(value.b, value.d), gatunek.tytul));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
                }
            }
            else{
                if(abonament){
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.d, gatunek.tytul));
                }
                else{
                    wishlist.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
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
        LinkedList<Produkt> produkts = wishlist.getListazyczen();
        for(int i=0; i<produkts.size(); i++){
            if(produkts.get(i).price!=0){
                basket.add(wishlist.remove(produkts.get(i)));
                sumzakup+=produkts.get(i).price*produkts.get(i).ile;
            }
        }
    }

    void pay(String jakplaci,boolean autozwrot){

    }

}
class Wishlist{
    private LinkedList <Produkt> listazyczen;
    Pricelist price_list = Pricelist.getPricelist();
    Produkt remove(Produkt produkt){
        for (int i = 0; i < listazyczen.size(); i++) {
            if(listazyczen.get(i).tytul.equals(produkt.tytul)){
                return listazyczen.remove(i);
            }
        }
        return null;
    }
    void add(Produkt produkt){
        listazyczen.add(produkt);
    }

    public LinkedList<Produkt> getListazyczen() {
        return listazyczen;
    }
}
class Basket{
    private LinkedList <Produkt> koszykowalista;
    Pricelist price_list = Pricelist.getPricelist();
    Produkt remove(Gatunek gatunek){
        for (int i = 0; i < koszykowalista.size(); i++) {
            if(koszykowalista.get(i).tytul.equals(gatunek.tytul)){
                return koszykowalista.remove(i);
            }
        }
        return null;
    }
    void add(Produkt produkt){
        koszykowalista.add(produkt);
    }


}
class Produkt{
    GENRE genre;
    String tytul;
    int ile;
    double price;

    public Produkt(GENRE genre, int ile, double price, String tytul) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.tytul = tytul;
    }
}