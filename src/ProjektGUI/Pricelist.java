package ProjektGUI;//👿👿👿👿👿👿👿👿👿 // we to usuń xd

import java.util.HashMap;
import java.util.Objects;

public class Pricelist {
    // Ogólnie ładnie jest jeżeli wszystkie atrybuty sa na górze
    private static Pricelist price_list = null;
    private HashMap<PriceListKey, PriceListValue> pom = new HashMap<PriceListKey, PriceListValue>(); // Zmien nazwe pom
                                                                                                     // na coś co mówi
                                                                                                     // co to jest.

    private Pricelist() {
    }

    public static Pricelist getPricelist() {
        if (price_list == null) {
            price_list = new Pricelist();
        }
        return price_list;
    }

    PriceListValue getPriceListValue(PriceListKey key) {
        return pom.get(key);
    }

    // Tu tak samo jak w konstruktorach PriceListValue mozna wywołac po prostu add z
    // wieksza liczba parametrów jezeli jakis brakuje.
    // Zmien te krypto nazwy G, a, b, c, d na cos to mowi co to jest. Co tego
    // nazywanie zmiennych dużą literą to chyba też zły pomysł. Nazwałbym to po
    // prostu `genre`.
    void add(GENRE G, String title, int a, int b, int c, int d) {
        pom.put(new PriceListKey(G, title), new PriceListValue(a, b, c, d));
    }

    void add(GENRE G, String title, int a, int b, int c) {
        // add(G, title, a, b, c, 0); // Na przykład tak zamiast tego co niżej
        pom.put(new PriceListKey(G, title), new PriceListValue(a, b, c));

    }

    void add(GENRE G, String title, int a, int b) {
        pom.put(new PriceListKey(G, title), new PriceListValue(a, b));
    }

    void add(GENRE G, String title) {
        pom.put(new PriceListKey(G, title), new PriceListValue(0, 0, 0, 0));
    }

    void remove(GENRE G, String title) {
        pom.remove(new PriceListKey(G, title));
    }

}

class PriceListValue {
    // Moim zdaniem niektóre z tych rzeczy powinny być dużymi Doublami. W szczególności
    // cena którąkolwiek literką ona jest. Nie musiałbyś castować wszystkiego w
    // Client.
    int a = 0; // Koniecznie zmień te nazwy na coś co mówi czym one są, bo za chuja nie
               // wiadomo.
    int b = 0;
    int c = 0;
    int d = 0;

    // Tutaj moim zdaniem fajniej by było używać tylko tego konstruktora z 4
    // parametrami a w innych konstruktorach używać go i przekazać mu domyślne
    // parametry.
    public PriceListValue(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public PriceListValue(int a, int b, int c) {
        this(a, b, c, 0); // O tak, zamiast powtarzać kod. Wtedy wywołujesz istniejący konstruktor z
                          // wieksza liczba argumentów.
        // this.a = a;
        // this.b = b;
        // this.c = c;
    }

    public PriceListValue(int a, int b) {
        this(a, b, 0, 0);

        // this.a = a;
        // this.b = b;
    }

    double getprice(int a, int b, int c, int d, Client client, int ilosc) { // Nazwy metod camelCasem -> getprice ->
                                                                            // getPrice.
        boolean czyabonament = client.isAbonament(); // Nazwy zmiennych snake_casem -> czyabonament -> czy_abonament.

        if (a == 0 && b == 0 && c == 0 && d == 0) {
            return (double) 0; // Jestem w 99% przekonany ze nie trzeba tutaj castować wszystkiego na double.
                               // To się scastuje automatycznie.
        } else if (c == 0 && d == 0) {
            if (czyabonament) {
                return (double) b;
            } else {
                return (double) a;
            }
        } else if (d == 0) {
            if (ilosc > c) {
                return (double) b;
            } else {
                return (double) a;
            }
        } else {
            if (ilosc > c) {
                if (czyabonament) {
                    return (double) Math.min(b, d); // Tylko tutaj ten cast może być wymagany.
                } else {
                    return (double) b;
                }
            } else {
                if (czyabonament) {
                    return (double) d;
                } else {
                    return (double) a;
                }
            }
        }
    }
}

class PriceListKey {
    GENRE genre;
    String title;

    public PriceListKey(GENRE genre, String title) {
        this.genre = genre;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PriceListKey that = (PriceListKey) o;
        return genre == that.genre && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre, title);
    }
}
