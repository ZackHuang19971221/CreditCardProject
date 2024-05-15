package com.hotsauce.creditcard.util.creditcard;

public class CreditCardUtil {
    //https://en.wikipedia.org/wiki/Payment_card_number#:~:text=The%20parts%20of%20the%20number%20are%20as%20follows%3A,check%20digit%20calculated%20using%20the%20Luhn%20algorithm%20%5B4%5D
    public enum CardIssuers {
        UNKNOWN,
        MASTER,
        VISA,
        JCB,
        DINER_CLUB,
        AMERICAN_EXPRESS,
        DISCOVER,
        CHINA_UNION_PAY
    }
    public static CardIssuers getCardIssuers(String bin) {
        try {
            if(bin == null) {
                return CardIssuers.UNKNOWN;
            }
            if(bin.length() == 4 || bin.length() == 6 || bin.length() == 8){
                int one = Integer.parseInt(bin.substring(0,1));
                int two = Integer.parseInt(bin.substring(0,2));
                int three = Integer.parseInt(bin.substring(0,3));
                int four = Integer.parseInt(bin.substring(0,4));
                Integer five;
                Integer six;
                if(bin.length() == 6) {
                    five = Integer.parseInt(bin.substring(0,5));
                    six = Integer.parseInt(bin.substring(0,6));
                }
                Integer seven;
                Integer eight;
                if(bin.length() == 8) {
                    seven = Integer.parseInt(bin.substring(0,7));
                    eight = Integer.parseInt(bin.substring(0,8));
                }
                if(one == 4) {
                    return CardIssuers.VISA;
                }
                if(two >= 51 && 55 >= two) {
                    return CardIssuers.MASTER;
                }
                if(four >= 3528 && 3589 >= four) {
                    return CardIssuers.JCB;
                }
                if(two == 34 || two == 37) {
                    return CardIssuers.AMERICAN_EXPRESS;
                }
                if(two == 62) {
                    return CardIssuers.CHINA_UNION_PAY;
                }
            }
        }catch (Exception ignored) {
            return CardIssuers.UNKNOWN;
        }
        return CardIssuers.UNKNOWN;
    }
    public static String getPartialCardNumber(String bin,String pan,String checkDigit) {
        CardIssuers cardIssuers = getCardIssuers(bin);
        if(pan == null){pan = "";}
        if(checkDigit == null){checkDigit = "x";}
        String partialCardNumber = "xxxxxxxxxxxxxxxxxxx";
        try{
            switch (cardIssuers) {
                case MASTER:
                case JCB:
                    partialCardNumber = bin.substring(0,6) + padRight(pan,'x',9) + checkDigit;
                    break;
                case VISA:
                    partialCardNumber = bin.substring(0,6) + padRight(pan,'x',6) + checkDigit;
                    break;
                case AMERICAN_EXPRESS:
                    partialCardNumber = bin.substring(0,4) + padRight(pan,'x',10) + checkDigit;
                    break;
                case  CHINA_UNION_PAY:
                    partialCardNumber = bin + pan + checkDigit;
                    break;
            }
        }catch (Exception ignored) {}
        return partialCardNumber;
    }

    private static String padRight(String s, char c, int length) {
        if (s.length() >= length) {
            return s;
        }
        StringBuilder paddedString = new StringBuilder(s);
        while (paddedString.length() < length) {
            paddedString.append(c);
        }
        return paddedString.toString();
    }
}
