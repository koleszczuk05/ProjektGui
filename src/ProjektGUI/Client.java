package ProjektGUI;

import java.util.LinkedList;

/*
 * W ogólności można sie jeszcze zastanowić nad obsługą wyjątków. Zapytaj kolegów czy trzeba to zrobić. 
 * Na przykład co jezeli przypiszesz ujemny balans klientowi albo dodasz dwa razy ten sam film do cennika lub koszyka
 * Czy jezeli dodasz ten sam film do koszyka to powinna sie zwiekszac liczba jego kopii czy to jest bład.
 */
public class Client {
    // Wszystko po angielsku.
    public String name;
    // balans też powinien być prywatny.
    double balans; // zmien ta nazwe na cos co mowi co to jest. BTW jak już coś zmienisz to mozesz
                   // usunac komentarz. -> balance, angielski
    private boolean abonament; // -> subscription. Wszystko po angielsku.
    private double sum_zakup = 0;

    private Wishlist wishlist;
    private Basket basket;

    public Client(String name, double balans, boolean abonament) {
        this.name = name;
        this.balans = balans;
        this.abonament = abonament;
        wishlist = new Wishlist(this);
        basket = new Basket(this);
    }

    public boolean isAbonament() { // -> hasAbonament?
        return abonament;
    }

    void add(Gatunek gatunek) {
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(gatunek.genre, gatunek.title));

        if (value == null) {
            wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, null, gatunek.title));
            return;
        }

        // Moze da sie skrocic tą ifologie. Na koniec
        // Tutaj to castowanie na double wszedzie jest troche denertujące. Jakby w
        // PriceListValue były double to by nie było tego problemu.
        if (value.a == 0 && value.b == 0 && value.c == 0 && value.d == 0) {
            wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) 0, gatunek.title)); // W ogólności stałych
                                                                                              // liczbowych nigdy nie
                                                                                              // trzeba castować. One
                                                                                              // sie automatycznie
                                                                                              // castują.
        } else if (value.c == 0 && value.d == 0) {
            if (abonament) {
                wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.b, gatunek.title));
            } else {
                wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.a, gatunek.title));
            }
        } else if (value.d == 0) {
            if (gatunek.nwm > value.c) {
                wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.b, gatunek.title));
            } else {
                wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.a, gatunek.title));
            }
        } else {
            if (gatunek.nwm > value.c) {
                if (abonament) {
                    wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) Math.min(value.b, value.d),
                            gatunek.title));
                } else {
                    wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.b, gatunek.title));
                }
            } else {
                if (abonament) {
                    wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.d, gatunek.title));
                } else {
                    wishlist.add(new Produkt(gatunek.genre, gatunek.nwm, (double) value.a, gatunek.title));
                }
            }
        }
    }

    void returnVOD(GENRE genre, String nazwa, int ilezwraca) { // ilezwraca -> ile_zwraca. nazwa -> name
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(genre, nazwa));
        LinkedList<Produkt> temp1 = basket.getKoszykowa_lista(); // -> basket
        LinkedList<Produkt> temp2 = basket.getPozaplaceniu(); // -> bought_products

        double cena = 0;
        for (int i = 0; i < temp1.size(); ++i) {
            /*
             * To w
             * To w ogolności jest kwadratowe, a mogło by być w czasie stałym jakbyś używał
             * HashSeta zamiast linked listy.
             * Wtedy wystarczy zrobić i to działa w czasie stałym:
             * akt = basket.get(new PriceListValue(genre, name));
             * (...) tutaj sobie coś liczysz
             * bought_products.get(new PriceListValue(genre, name));
             * 
             */
            Produkt akt = temp1.get(i);
            if (genre == akt.genre && nazwa.equals(akt.tytul)) {
                double x = value.getprice(value.a, value.b, value.c, value.d, this, akt.ile) * akt.ile;
                double y = value.getprice(value.a, value.b, value.c, value.d, this, ilezwraca);
                cena = x - y;
                balans += cena;
                for (int j = 0; j < temp2.size(); j++) {
                    Produkt akt2 = temp2.get(j);
                    if (genre == akt2.genre && nazwa.equals(akt2.tytul)) {
                        temp2.get(j).ile += ilezwraca;
                        return;
                    }
                }
                temp2.add(new Produkt(akt.genre, ilezwraca, y, akt.tytul));
                break;
            }
        }

    }

    Wishlist getWishlist() {
        return wishlist;
    }

    Basket getBasket() {
        return basket;
    }

    void pack() {
        // Tutaj tak samo, gdyby wishlist to był HashSet to usuwanie byłoby O(n) a nie
        // O(n^2).
        LinkedList<Produkt> products_copy = new LinkedList<>(wishlist.getListaZyczen());
        for (Produkt product : products_copy) {
            if (product.price != null) {
                Produkt delete = wishlist.remove(product);
                if (delete != null) {
                    basket.add(delete);
                    sum_zakup += product.price * product.ile;
                }
            }
        }
    }

    void pay(Payform jakplaci, boolean autozwrot) { // Nazwy po angielsku i camelCasem.
        if (jakplaci == Payform.CARD) {
            sum_zakup *= 1.01;
        }
        if (sum_zakup > balans) {
            if (autozwrot) {
                double temp = balans;
                LinkedList<Produkt> products = basket.getKoszykowa_lista();
                LinkedList<Produkt> templist = basket.getPozaplaceniu(); // To tez by mogłaby być lepsza nazwa mówiąca
                                                                         // co to jest.
                for (int i = 0; i < products.size(); i++) {
                    Produkt product = products.get(i);
                    if (product.price * product.ile <= temp) {
                        temp -= product.price * product.ile;
                    } else {
                        for (int j = product.ile - 1; j >= 1; --j) {
                            Pricelist price_list = Pricelist.getPricelist();
                            PriceListValue value = price_list
                                    .getPriceListValue(new PriceListKey(product.genre, product.tytul));
                            double akt = value.getprice(value.a, value.b, value.c, value.d, this, j);
                            double cenacozostanie = value.getprice(value.a, value.b, value.c, value.d, this, i - j); // nazwa
                                                                                                                     // po
                                                                                                                     // angielsku
                                                                                                                     // i
                                                                                                                     // snake_case.
                            if (akt * j <= temp) {
                                temp -= akt * j;
                                templist.add(
                                        new Produkt(product.genre, product.ile - j, cenacozostanie, product.tytul));
                                products.get(i).ile = j; // Masz już zmienną product wyzej, nie musisz znowu wywoływac
                                                         // liniowego geta
                                products.get(i).price = akt; // Masz już zmienną product wyzej, nie musisz znowu
                                                             // wywoływac liniowego geta
                                break;
                            }
                        }
                    }
                }
                balans = temp;
            }
        } else {
            balans -= sum_zakup;
        }
        wishlist.empty = true;
        basket.empty = true;
    }

    double getWallet() {
        return balans;
    }

}

// Klasa Wishlist i Basket robia dokładnie to samo. To powinno być wydzielone do
// klasy abstrakcyjnej typu ProductList czy cos

/*
 * 1) Klasa WishList i Basket robią dokładnie to samo. Lepiej by było zrobić
 * klase abstrakcyjną typu ProductSet, w której będzie cała funkcjonalność
 * remove, add itp.
 * 2) Naprawde nalegam zeby LinkedLista zmieniła się w HashSet. Wtedy uprości
 * sie bardzo kod w returnVOD i wszystko będzie o 2 rzędy wielkości szybsze.
 * 3) Wishlist i Basket i ich nadklasa abstrakcyjna powinny byc w innym pliku a
 * nie w kliencie.
 */

class Wishlist {
    Client client;
    boolean empty = false; // Tutaj w ogólności mozna by było dodać komentarz, bo ta zmienna empty pełni
                           // jakąś kluczową rolę w działaniu programu a troche nie jest intuicyjne co ona
                           // robi.
    private LinkedList<Produkt> lista_zyczen = new LinkedList<Produkt>();

    Wishlist(Client client) {
        this.client = client;
    }

    Produkt remove(Produkt produkt) {
        // lista_zyczen.remove(produkt); // tu można zrobic po prostu tak .
        for (int i = 0; i < lista_zyczen.size(); i++) {
            Produkt temp = lista_zyczen.get(i);
            if (temp.tytul.equals(produkt.tytul) && produkt.genre == temp.genre) {
                return lista_zyczen.remove(i);
            }
        }
        return null;
    }

    void add(Produkt produkt) {
        lista_zyczen.add(produkt);
    }

    public LinkedList<Produkt> getListaZyczen() {
        return lista_zyczen;
    }

    @Override
    public String toString() {
        String temp = client.name + "\n";
        if (empty) {
            temp += " --- pusto";
            return temp;
        }
        for (int i = 0; i < lista_zyczen.size(); i++) {
            temp += lista_zyczen.get(i).toString() + "\n";
        }
        return temp;
    }
}

class Basket {
    Client client;
    boolean empty = false;
    // Atrubuty w jednym miejscu.
    private LinkedList<Produkt> koszykowa_lista = new LinkedList<Produkt>();
    private LinkedList<Produkt> pozaplaceniu = new LinkedList<Produkt>(); // snake_case

    public Basket(Client client) {
        this.client = client;
    }

    public LinkedList<Produkt> getPozaplaceniu() {
        return pozaplaceniu;
    }

    Produkt remove(Gatunek gatunek) {
        // Tu mozna zrobić to samo co napisałem w Wishliscie zamiast tego fora.
        for (int i = 0; i < koszykowa_lista.size(); i++) {
            if (koszykowa_lista.get(i).tytul.equals(gatunek.title)) {
                return koszykowa_lista.remove(i);
            }
        }
        return null;
    }

    void add(Produkt produkt) {
        koszykowa_lista.add(produkt);
    }

    public LinkedList<Produkt> getKoszykowa_lista() { // camelCase
        return koszykowa_lista;
    }

    public String toString() {
        String temp = client.name + "\n";
        if (!pozaplaceniu.isEmpty()) {
            for (int i = 0; i < pozaplaceniu.size(); i++) {
                temp += pozaplaceniu.get(i).toString() + "\n";
            }
            return temp;
        }
        if (empty) {
            temp += " --- pusto";
            return temp;
        }
        for (int i = 0; i < koszykowa_lista.size(); i++) {
            temp += koszykowa_lista.get(i).toString() + "\n";
        }
        return temp;
    }
}

// To też powinno wylądować w oddzielnym pliku albo pliku z WishListem i
// Basketem.
class Produkt {
    GENRE genre;
    String tytul; // po angielsku -> title
    int ile;
    Double price = null;

    public Produkt(GENRE genre, int ile, Double price, String tytul) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.tytul = tytul; // Po angielsku
    }

    @Override
    public String toString() {
        // Na koniec, użyj tu StringBuilderów
        String ret = "";
        switch (genre) {
            case DRAMA ->
                ret += tytul + ", typ: obyczaj, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case ACTION ->
                ret += tytul + ", typ: sensacja, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case MUSICAL ->
                ret += tytul + ", typ: muzyczny, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case COMEDY ->
                ret += tytul + ", typ: komedia, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
        }
        return ret;
    }
}