package Projekt1Gui;

import java.util.LinkedList;

public class Client {
    String name;
    double d;
    boolean abonament;
    Wishlist wishlist;
    Basket basket;


    public Client(String name, double d, boolean abonament) {
        this.name = name;
        this.d = d;
        this.abonament = abonament;
    }

    void add(Gatunek gatunek){
        PriceList price_list = PriceList.getPricelist();
        PriceListKey key = new PriceListKey(gatunek.genre, gatunek.tytul);
        PriceListValue value = price_list.getPriceListValue(key);
        if(value.a==0 && value.b==0 && value.c==0 && value.d==0){
            wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm,0,gatunek.tytul));
        }
        else if(value.c==0 && value.d==0){
            if(abonament){
                wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
            }
            else{
                wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
            }
        }
        else if(value.d==0){
            if(gatunek.nwm< value.c){
                wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
            }
            else{
                wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
            }
        }
        else{
            if(gatunek.nwm< value.c){
                if(abonament){
                    wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, Math.min(value.b, value.d), gatunek.tytul));
                }
                else{
                    wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.b, gatunek.tytul));
                }
            }
            else{
                if(abonament){
                    wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.d, gatunek.tytul));
                }
                else{
                    wishlist.listazyczen.add(new Produkt(gatunek.genre,gatunek.nwm, value.a, gatunek.tytul));
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

    }
}
class Wishlist{
    private LinkedList <Produkt> listazyczen;
    PriceList price_list = PriceList.getPricelist();
    Produkt remove(Gatunek gatunek){
        for (int i = 0; i < listazyczen.size(); i++) {
            if(listazyczen.get(i).tytul.equals(gatunek.tytul)){
                return listazyczen.remove(i);
            }
        }
        return null;
    }
}
class Basket{
    LinkedList <Gatunek> koszykowalista;

}
class Produkt{
    Genre genre;
    String tytul;
    int ile;
    double price;

    public Produkt(Genre genre, int ile, double price, String tytul) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.tytul = tytul;
    }
}