package ProjektGUI;

import java.util.HashMap;
import java.util.Objects;

public class Pricelist {
    private static Pricelist price_list = null;
    private HashMap<PriceListKey, PriceListValue> CennikFilmow = new HashMap<PriceListKey, PriceListValue>();

    private Pricelist() {
    }

    public static Pricelist getPricelist() {
        if (price_list == null) {
            price_list = new Pricelist();
        }
        return price_list;
    }

    PriceListValue getPriceListValue(PriceListKey key) {
        return CennikFilmow.get(key);
    }

    void add(GENRE genre, String title, int a, int b, int c, int d) {
        CennikFilmow.put(new PriceListKey(genre, title), new PriceListValue(a, b, c, d));
    }

    void add(GENRE genre, String title, int a, int b, int c) {
        // add(G, title, a, b, c, 0); // Na przykład tak zamiast tego co niżej
        CennikFilmow.put(new PriceListKey(genre, title), new PriceListValue(a, b, c));

    }

    void add(GENRE genre, String title, int a, int b) {
        CennikFilmow.put(new PriceListKey(genre, title), new PriceListValue(a, b));
    }

    void add(GENRE genre, String title) {
        CennikFilmow.put(new PriceListKey(genre, title), new PriceListValue(0, 0, 0, 0));
    }

    void remove(GENRE genre, String title) {
        CennikFilmow.remove(new PriceListKey(genre, title));
    }

}

class PriceListValue {

    int brak_abonamentu_mniej_prog = 0;
    int brak_abonamentu_wiecej_prog = 0;
    int prog_urzadzen = 0;
    int ma_abonament = 0;

    public PriceListValue(int brak_abonamentu_mniej_prog, int brak_abonamentu_wiecej_prog, int prog_urzadzen, int ma_abonament) {
        this.brak_abonamentu_mniej_prog = brak_abonamentu_mniej_prog;
        this.brak_abonamentu_wiecej_prog = brak_abonamentu_wiecej_prog;
        this.prog_urzadzen = prog_urzadzen;
        this.ma_abonament = ma_abonament;
    }

    public PriceListValue(int brak_abonamentu_mniej_prog, int brak_abonamentu_wiecej_prog, int prog_urzadzen) {
        this(brak_abonamentu_mniej_prog, brak_abonamentu_wiecej_prog, prog_urzadzen, 0);
    }

    public PriceListValue(int brak_abonamentu_mniej_prog, int brak_abonamentu_wiecej_prog) {
        this(brak_abonamentu_mniej_prog, brak_abonamentu_wiecej_prog, 0, 0);
    }

    double getPrice(int a, int b, int c, int d, Client client, int ilosc) {

        boolean czy_abonament = client.isSubscription();

        if (a == 0 && b == 0 && c == 0 && d == 0) {
            return (double) 0;
        } else if (c == 0 && d == 0) {
            if (czy_abonament) {
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
                if (czy_abonament) {
                    return (double) Math.min(b, d);
                } else {
                    return (double) b;
                }
            } else {
                if (czy_abonament) {
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
