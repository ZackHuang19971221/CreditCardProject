package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import lombok.NonNull;

public class CreditCardFactory {
    public static CreditCard<?> createInstance(@NonNull ProviderType type, @NonNull DeviceInfo deviceInfo)
    {
        CreditCard<?> instance = null;
        switch (type){
            case Developer:
                //instance = new com.hotsauce.creditcard.providers.Developer();
                break;
            case Dejavoo:
                instance = new com.hotsauce.creditcard.providers.Dejavoo(deviceInfo);
                break;
            case POSLink:
                instance = new com.hotsauce.creditcard.providers.POSLink(deviceInfo);
                break;
            case Ingenico:
                //instance = new Ingenico();
            default:
        }
        return  instance;
    }
}

