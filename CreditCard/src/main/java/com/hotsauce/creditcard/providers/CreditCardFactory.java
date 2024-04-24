package com.hotsauce.creditcard;

import com.hotsauce.creditcard.io.DeviceInfo;
import lombok.NonNull;

public class CreditCardFactory {
    public  static ICreditCard createInstance(@NonNull ProviderType type,@NonNull DeviceInfo deviceInfo)
    {
        ICreditCard instance = null;
        switch (type){
            case Developer:
                //instance = new com.hotsauce.creditcard.providers.Developer();
                break;
            case SPIN :
                //instance = new com.hotsauce.creditcard.providers.SPIN();
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

