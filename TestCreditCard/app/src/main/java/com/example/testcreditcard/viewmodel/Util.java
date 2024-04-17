package com.example.testcreditcard.viewmodel;

import com.hotsauce.creditcard.CreditCardType;

import java.util.InputMismatchException;

public class Util {
    public static class CreditCard{
        public static final String PosLink = "PosLink";
        public static final String SPIN = "SPIN";
        public static final String Ingenico = "Ingenico";
    }
    public static com.hotsauce.creditcard.CreditCardType GetCreditCardType(String value)
    {
        switch (value)
        {
            case CreditCard.PosLink:
                return CreditCardType.POSLink;
            case CreditCard.SPIN:
                return CreditCardType.SPIN;
            case CreditCard.Ingenico:
                return CreditCardType.Ingenico;
        }
        throw  new InputMismatchException();
    }
}
