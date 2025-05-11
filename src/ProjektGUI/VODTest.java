package ProjektGUI;

import java.util.LinkedList;

import static ProjektGUI.GENRE.*;
import static ProjektGUI.Payform.*;

public class VODTest {

    // cena programów danego gatunku z koszyka
    static double price(Basket b, GENRE g) {
        LinkedList<Produkt> produkts = b.getKoszykowa_lista();
        double cena = 0;
        for (int i = 0; i < produkts.size(); i++) {
            Produkt temp = produkts.get(i);
            if (g == temp.genre) {
                cena += temp.ile * temp.price;
            }
        }
        return cena;
    }

    public static void main(String[] args) {

        // cennik
        Pricelist cennik = Pricelist.getPricelist();

        // dodawanie nowych cen do cennika
        cennik.add(MUSICAL, "Król lew", 12, 6, 3, 5); // metoda przyjmująca 6 parametrów
        // (kolejność, typy i oznaczenie parametrów są obowiązkowe, podane argumenty
        // wywołania są przykładowe):
        // "próg" urządzeń: 3
        // jeśli klient ma abonament: 5 zł/urządzenie (z dowolną liczbą urządzeń)
        // jeśli klient nie ma abonamentu: do 3 urządzeń za 12 zł/urządzenie,
        // w przeciwnym przypadku (wpp.) 6 zł/urządzenie

        cennik.add(DRAMA, "Król Lear", 15, 10, 2); // metoda przyjmująca 5 parametrów
        // (kolejność, typy i oznaczenie parametrów są obowiązkowe, podane argumenty
        // wywołania są przykładowe):
        // "próg" urządzeń: 2
        // niezależnie od tego, czy klient posiada abonament czy nie
        // do 2 urządzeń za 15 zł/urządzenie, wpp. 10 zł/urządzenie

        cennik.add(COMEDY, "Królowa", 14, 7); // metoda przyjmująca 4 parametry
        // (kolejność, typy i oznaczenie parametrów są obowiązkowe, podane argumenty
        // wywołania są przykładowe):
        // 7 zł/urządzenie jeśli klient ma abonament, wpp. 14 zł/urządzenie

        cennik.add(COMEDY, "Król", 10, 5);

        cennik.add(ACTION, "Król Artur"); // metoda przyjmująca 2 parametry
        // (kolejność, typy i oznaczenie parametrów są obowiązkowe, podane argumenty
        // wywołania są przykładowe):
        // darmowy dostęp

        cennik.remove(COMEDY, "Król"); // metoda remove (do usunięcia ceny konkretnego programu) przyjmująca 2
                                       // parametry

        // Klient Kinoman deklaruje kwotę 60 zł na zamównienia; true oznacza, że klient
        // posiada abonament w serwisie
        Client kinoman = new Client("Kinoman", 60, true);

        // Klient Kinoman dodaje do listy życzeń różne programy:
        // "Król Lear" typu obyczajowego na 4 urządzeniach,
        // "Król Artur" typu sensacyjnego na 3 urządzeniach,
        // "Król lew" typu muzycznego na 2 urządzeniach,
        // "Korona" typu komediowego na 2 urządzeniach,
        kinoman.add(new Drama("Król Lear", 4));
        kinoman.add(new Action("Król Artur", 3));
        kinoman.add(new Musical("Król lew", 2));
        kinoman.add(new Comedy("Korona", 2));

        // Lista życzeń klienta Kinoman
        Wishlist listaKinomana = kinoman.getWishlist();

        System.out.println("Lista życzeń klienta " + listaKinomana);

        // Przed płaceniem, klient przepakuje programy z listy życzeń do koszyka.
        // Możliwe, że na liście życzeń są programy niemające ceny w cenniku,
        // w takim przypadku nie trafiłyby do koszyka
        Basket koszykKinomana = kinoman.getBasket();
        kinoman.pack();

        // Co jest na liście życzeń klienta Kinomana
        System.out.println("Po przepakowaniu, lista życzeń klienta " + kinoman.getWishlist());

        // Co jest w koszyku klienta Kinoman
        System.out.println("Po przepakowaniu, koszyk klienta " + koszykKinomana);

        // Ile wynosi cena wszystkich programów typu obyczajowego w koszyku klienta
        // Kinoman
        System.out.println("Progamy obyczajowe w koszyku klienta Kinoman kosztowały:  " + price(koszykKinomana, DRAMA));

        System.out.println();

        // Klient zapłaci...
        kinoman.pay(CARD, false); // płaci kartą płatniczą, prowizja 1%
        // true oznacza, że w przypadku braku środków aplikacja sam odłoży nadmiarowe
        // programy,
        // wpp. rezygnacja z płacenia razem z wyczyszczeniem koszyka i listy życzeń

        // Ile klientowi Kinoman zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi Kinoman zostało: " + kinoman.getWallet() + " zł");

        System.out.println();
        // Mogło klientowi zabraknąć srodków, wtedy opcjonalnie programy mogą być
        // odkładane,
        // wpp. koszyk jest pusty po zapłaceniu
        System.out.println("Po zapłaceniu, koszyk klienta " + kinoman.getBasket());
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykKinomana);
        System.out.println();

        // Teraz przychodzi klient Krytyk,
        // deklaruje 60 zł na zamówienia
        Client krytyk = new Client("Krytyk", 60, false);

        // Zamówił za dużo jak na tę kwotę
        krytyk.add(new Musical("Król lew", 2));
        krytyk.add(new Comedy("Królowa", 3));

        // Co klient Krytyk ma na swojej liście życzeń
        System.out.println("Lista życzeń klienta " + krytyk.getWishlist());

        Basket koszykKrytyka = krytyk.getBasket();
        krytyk.pack();

        // Co jest na liście życzeń klienta Krytyk
        System.out.println("Po przepakowaniu, lista życzeń klienta " + krytyk.getWishlist());
        System.out.println();

        // A co jest w koszyku klienta Krytyk
        System.out.println("Po przepakowaniu, koszyk klienta " + krytyk.getBasket());

        // klient Krytyk płaci
        krytyk.pay(TRANSFER, true); // płaci przelewem, bez prowizji

        // Ile klientowi Krytyk zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi Krytyk zostało: " + krytyk.getWallet() + " zł");
        System.out.println();

        // Co zostało w koszyku klienta Krytyk (za mało pieniędzy miał)
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykKrytyka);

        krytyk.returnVOD(COMEDY, "Królowa", 1); // zwrot (do koszyka) 1 urządzenia programu komediowego "Królowa" z
                                                // ostatniej transakcji

        // Ile klientowi krytyk zostało pieniędzy?
        System.out.println("Po zwrocie, klientowi krytyk zostało: " + krytyk.getWallet() + " zł");
        System.out.println();

        // Co zostało w koszyku klienta krytyk
        System.out.println("Po zwrocie, koszyk klienta " + koszykKrytyka);

    }
}