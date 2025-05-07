package ProjektGUI;//👿👿👿👿👿👿👿👿👿
import java.util.HashMap;
import java.util.Objects;

public class Pricelist {
    private static Pricelist price_list = null;
    private Pricelist(){}

    public static Pricelist getPricelist(){
        if(price_list==null){
            price_list = new Pricelist();
        }
        return price_list;
    }

    private HashMap<PriceListKey,PriceListValue> pom= new HashMap<PriceListKey,PriceListValue>();



    PriceListValue getPriceListValue(PriceListKey key){
        return pom.get(key);
    }

    void add(GENRE G, String title, int a, int b, int c, int d ){
        pom.put(new PriceListKey(G,title),new PriceListValue(a,b,c,d));
    }
    void add(GENRE G, String title, int a, int b, int c){
        pom.put(new PriceListKey(G,title),new PriceListValue(a,b,c));

    }
    void add(GENRE G, String title, int a, int b){
        pom.put(new PriceListKey(G,title),new PriceListValue(a,b));
    }
    void add(GENRE G, String title){
        pom.put(new PriceListKey(G,title),new PriceListValue(0,0,0,0));
    }
    void remove(GENRE G, String title){
        pom.remove(new PriceListKey(G,title));
    }

}

class PriceListValue{
    int a = 0;
    int b = 0;
    int c = 0;
    int d = 0;

    public PriceListValue(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public PriceListValue(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public PriceListValue(int a, int b) {
        this.a = a;
        this.b = b;
    }
    double getprice(int a, int b, int c, int d){
        return a*b*c*d;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceListKey that = (PriceListKey) o;
        return genre == that.genre && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre, title);
    }
}
