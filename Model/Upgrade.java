package Model;

import java.util.HashMap;

class Upgrade {

    static char CUR_DOLLAR = 'd';
    static char CUR_CREDIT = 'c';

    private static HashMap<String, Upgrade> stringMap = new HashMap<String, Upgrade>();
    private int rank;
    private int price;
    private char currency;

    Upgrade(int rank, int price, char currency)
    {
        this.rank = rank;
        this.price = price;
        this.currency = currency;
        String strVal = "" + rank + currency + price;

        stringMap.put(strVal, this);
    }

    int getRank(){return rank;}
    int getPrice(){return price;}
    char getCurrency(){return currency;}

    static Upgrade getFromString(String strVal)
    {
        return stringMap.getOrDefault(strVal, null);
    }

}

