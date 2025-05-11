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


abstract class ProductList {
    Client client;
    boolean empty = false;
    protected LinkedList<Produkt> products = new LinkedList<>();

    ProductList(Client client) {
        this.client = client;
    }

    void add(Produkt produkt) {
        products.add(produkt);
    }

    Produkt removeByTitleAndGenre(String title, GENRE genre) {
        for (int i = 0; i < products.size(); i++) {
            Produkt temp = products.get(i);
            if (temp.title.equals(title) && temp.genre == genre) {
                return products.remove(i);
            }
        }
        return null;
    }

    LinkedList<Produkt> getProducts() {
        return products;
    }

    public String toString() {
        String temp = client.name;
        if (empty || products.isEmpty()) {
            temp += " --- pusto";
        } else {
            temp += "\n";
            for (Produkt produkt : products) {
                temp += produkt.toString() + "\n";
            }
        }
        return temp;
    }
}

class Wishlist extends ProductList {

    Wishlist(Client client) {
        super(client);
    }

    Produkt remove(Produkt produkt) {
        return removeByTitleAndGenre(produkt.title, produkt.genre);
    }

    public LinkedList<Produkt> getListaZyczen() {
        return getProducts();
    }
}


class Basket extends ProductList {
    private LinkedList<Produkt> pozaplaceniu = new LinkedList<>();

    Basket(Client client) {
        super(client);
    }

    Produkt remove(Film film) {
        return removeByTitleAndGenre(film.title, film.genre);
    }

    LinkedList<Produkt> getKoszykowa_lista() {
        return getProducts();
    }

    LinkedList<Produkt> getPozaplaceniu() {
        return pozaplaceniu;
    }

    @Override
    public String toString() {
        String temp = client.name;
        if (!pozaplaceniu.isEmpty()) {
            temp += "\n";
            for (Produkt produkt : pozaplaceniu) {
                temp += produkt.toString() + "\n";
            }
            return temp;
        }
        return super.toString();
    }
}