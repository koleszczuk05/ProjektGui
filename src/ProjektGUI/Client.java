package ProjektGUI;

import java.util.LinkedList;

public class Client {
    public String name;
    double balance;
    private boolean subscription;
    private double sum_zakup = 0;

    private Wishlist wishlist;
    private Basket basket;

    public Client(String name, double balance, boolean subscription) {
        this.name = name;
        this.balance = balance;
        this.subscription = subscription;
        wishlist = new Wishlist(this);
        basket = new Basket(this);
    }

    public boolean isSubscription() { // -> hasAbonament?
        return subscription;
    }

    void add(Film film) {
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(film.genre, film.title));

        if (value == null) {
            wishlist.add(new Produkt(film.genre, film.how_many, null, film.title));
            return;
        }

        if (value.brak_abonamentu_mniej_prog == 0 && value.brak_abonamentu_wiecej_prog == 0 && value.prog_urzadzen == 0 && value.ma_abonament == 0) {
            wishlist.add(new Produkt(film.genre, film.how_many, (double) 0, film.title));
        } else if (value.prog_urzadzen == 0 && value.ma_abonament == 0) {
            if (subscription) {
                wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_wiecej_prog, film.title));
            } else {
                wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_mniej_prog, film.title));
            }
        } else if (value.ma_abonament == 0) {
            if (film.how_many > value.prog_urzadzen) {
                wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_wiecej_prog, film.title));
            } else {
                wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_mniej_prog, film.title));
            }
        } else {
            if (film.how_many > value.prog_urzadzen) {
                if (subscription) {
                    wishlist.add(new Produkt(film.genre, film.how_many, (double) Math.min(value.brak_abonamentu_wiecej_prog, value.ma_abonament),
                            film.title));
                } else {
                    wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_wiecej_prog, film.title));
                }
            } else {
                if (subscription) {
                    wishlist.add(new Produkt(film.genre, film.how_many, (double) value.ma_abonament, film.title));
                } else {
                    wishlist.add(new Produkt(film.genre, film.how_many, (double) value.brak_abonamentu_mniej_prog, film.title));
                }
            }
        }
    }

    void returnVOD(GENRE genre, String name, int ile_zwraca) {
        Pricelist price_list = Pricelist.getPricelist();
        PriceListValue value = price_list.getPriceListValue(new PriceListKey(genre, name));
        LinkedList<Produkt> temp1 = basket.getKoszykowa_lista();
        LinkedList<Produkt> temp2 = basket.getPozaplaceniu();

        double cena = 0;
        for (int i = 0; i < temp1.size(); ++i) {
            //hash set optymalizuje
            Produkt akt = temp1.get(i);
            if (genre == akt.genre && name.equals(akt.title)) {
                double x = value.getPrice(value.brak_abonamentu_mniej_prog, value.brak_abonamentu_wiecej_prog, value.prog_urzadzen, value.ma_abonament, this, akt.ile) * akt.ile;
                double y = value.getPrice(value.brak_abonamentu_mniej_prog, value.brak_abonamentu_wiecej_prog, value.prog_urzadzen, value.ma_abonament, this, ile_zwraca);
                cena = x - y;
                balance += cena;
                for (int j = 0; j < temp2.size(); j++) {
                    Produkt akt2 = temp2.get(j);
                    if (genre == akt2.genre && name.equals(akt2.title)) {
                        temp2.get(j).ile += ile_zwraca;
                        return;
                    }
                }
                temp2.add(new Produkt(akt.genre, ile_zwraca, y, akt.title));
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

    void pay(Payform payform, boolean chechreturn) {
        if (payform == Payform.CARD) {
            sum_zakup *= 1.01;
        }
        if (sum_zakup > balance) {
            if (chechreturn) {
                double temp = balance;
                LinkedList<Produkt> products = basket.getKoszykowa_lista();
                LinkedList<Produkt> templist = basket.getPozaplaceniu();
                for (int i = 0; i < products.size(); i++) {
                    Produkt product = products.get(i);
                    if (product.price * product.ile <= temp) {
                        temp -= product.price * product.ile;
                    } else {
                        for (int j = product.ile - 1; j >= 1; --j) {
                            Pricelist price_list = Pricelist.getPricelist();
                            PriceListValue value = price_list
                                    .getPriceListValue(new PriceListKey(product.genre, product.title));
                            double akt = value.getPrice(value.brak_abonamentu_mniej_prog, value.brak_abonamentu_wiecej_prog, value.prog_urzadzen, value.ma_abonament, this, j);
                            double cenacozostanie = value.getPrice(value.brak_abonamentu_mniej_prog, value.brak_abonamentu_wiecej_prog, value.prog_urzadzen, value.ma_abonament, this, i - j);
                            if (akt * j <= temp) {
                                temp -= akt * j;
                                templist.add(
                                        new Produkt(product.genre, product.ile - j, cenacozostanie, product.title));
                                products.get(i).ile = j;
                                products.get(i).price = akt;
                                break;
                            }
                        }
                    }
                }
                balance = temp;
            }
        } else {
            balance -= sum_zakup;
        }
        wishlist.empty = true;
        basket.empty = true;
    }

    double getWallet() {
        return balance;
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
    boolean empty = false;
    private LinkedList<Produkt> lista_zyczen = new LinkedList<Produkt>();

    Wishlist(Client client) {
        this.client = client;
    }

    Produkt remove(Produkt produkt) {
        for (int i = 0; i < lista_zyczen.size(); i++) {
            Produkt temp = lista_zyczen.get(i);
            if (temp.title.equals(produkt.title) && produkt.genre == temp.genre) {
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
        String temp = client.name;
        if (empty || lista_zyczen.size()==0) {
            temp += " --- pusto";
            return temp;
        }
        temp += "\n";
        for (int i = 0; i < lista_zyczen.size(); i++) {
            temp += lista_zyczen.get(i).toString() + "\n";
        }
        return temp;
    }
}

class Basket {
    Client client;
    boolean empty = false;
    private LinkedList<Produkt> koszykowa_lista = new LinkedList<Produkt>();
    private LinkedList<Produkt> pozaplaceniu = new LinkedList<Produkt>(); // snake_case

    public Basket(Client client) {
        this.client = client;
    }

    public LinkedList<Produkt> getPozaplaceniu() {
        return pozaplaceniu;
    }

    Produkt remove(Film film) {
        for (int i = 0; i < koszykowa_lista.size(); i++) {
            if (koszykowa_lista.get(i).title.equals(film.title)) {
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
        String temp = client.name;
        if (!pozaplaceniu.isEmpty()) {
            temp += "\n";
            for (int i = 0; i < pozaplaceniu.size(); i++) {
                temp += pozaplaceniu.get(i).toString() + "\n";
            }
            return temp;
        }
        if (empty) {
            temp += " --- pusto";
            return temp;
        }
        temp += "\n";
        for (int i = 0; i < koszykowa_lista.size(); i++) {
            temp += koszykowa_lista.get(i).toString() + "\n";
        }
        return temp;
    }
}

class Produkt {
    GENRE genre;
    String title;
    int ile;
    Double price = null;

    public Produkt(GENRE genre, int ile, Double price, String title) {
        this.genre = genre;
        this.ile = ile;
        this.price = price;
        this.title = title;
    }

    @Override
    public String toString() {
        String ret = "";
        switch (genre) {
            case DRAMA ->
                ret += title + ", typ: obyczaj, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case ACTION ->
                ret += title + ", typ: sensacja, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case MUSICAL ->
                ret += title + ", typ: muzyczny, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
            case COMEDY ->
                ret += title + ", typ: komedia, ile: " + ile + " urzadzenia, cena " + (price == null ? "brak" : price);
        }
        return ret;
    }
}