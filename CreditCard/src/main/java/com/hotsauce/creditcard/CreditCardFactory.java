package com.hotsauce.creditcard;

import com.hotsauce.creditcard.providers.Ingenico;

public class CreditCardFactory {
    public  static ICreditCard CreateInstance(CreditCardType type)
    {
        ICreditCard Instance = null;
        switch (type){
            case Developer:
                Instance = new com.hotsauce.creditcard.providers.Developer();
                break;
            case SPIN :
                Instance = new com.hotsauce.creditcard.providers.SPIN();
                break;
            case POSLink:
                Instance = new com.hotsauce.creditcard.providers.POSLink();
                break;
            case Ingenico:
                Instance = new Ingenico();
            default:
        }
        return  Instance;
    }
}

