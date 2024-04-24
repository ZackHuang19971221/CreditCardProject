import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.auth.Request;
import com.hotsauce.creditcard.io.manage.PosLinkManageData;
import com.hotsauce.creditcard.providers.POSLink;
import com.hotsauce.creditcard.providers.ProviderType;
import org.junit.Test;

import java.math.BigDecimal;

public class TestCreditCard {
    private final ProviderType providerType = ProviderType.POSLink;
    private final DeviceInfo deviceInfo = DeviceInfo.builder()
            .ip("192.168.1.110")
            .port(DeviceInfo.getDeviceDefaultPort(providerType))
            .timeOut(60000)
            .retryTime(0)
            .build();
    @Test
    public void testAuthAndVoidAuth() {
        CreditCard<PosLinkManageData> creditCard = new POSLink(deviceInfo);
        creditCard.setManagementData(new PosLinkManageData("TA5714099666","Hotsauce9!","887000001519","88700000151901"));
        Request request = new Request("123", BigDecimal.ONE);
        var a = creditCard.auth(request);
        a = creditCard.auth(request);
        System.out.println("123");
    }
    @Test
    public void testAuthAndCapture() {

    }
}
