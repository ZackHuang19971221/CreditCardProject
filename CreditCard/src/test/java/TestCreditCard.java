import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.auth.Request;
import com.hotsauce.creditcard.io.auth.Response;
import com.hotsauce.creditcard.io.manage.PosLinkManageData;
import com.hotsauce.creditcard.providers.POSLink;
import com.hotsauce.creditcard.providers.ProviderType;
import com.hotsauce.creditcard.util.IDataReadCallBack;
import org.junit.Test;

import java.math.BigDecimal;

public class TestCreditCard {
    private final ProviderType providerType = ProviderType.POSLink;
    private final DeviceInfo deviceInfo = DeviceInfo.builder()
            .ip("192.168.1.110")
            .port(DeviceInfo.getDeviceDefaultPort(providerType))
            .timeOut(10000)
            .retryTime(0)
            .build();
    private final PosLinkManageData posLinkManageData = new PosLinkManageData("TA5714099666","Hotsauce9!","887000001519","88700000151901");

    //region "unit Test"
    @Test
    public void testAuth() {
        CreditCard<PosLinkManageData> creditCard = new POSLink(deviceInfo);
        creditCard.setManagementData(posLinkManageData);
        Request request = new Request("123", BigDecimal.ONE);
        creditCard.auth(request, new IDataReadCallBack<com.hotsauce.creditcard.io.base.Response<Response>>() {
            @Override
            public void onDataRead(com.hotsauce.creditcard.io.base.Response<Response> response) {
                System.out.println(response.getResultMessage());
            }
        });
    }
    @Test
    public void testVoidAuth() {

    }
    @Test
    public void testCapture() {

    }
    @Test
    public void testSale() {

    }
    @Test
    public void testVoid() {

    }
    @Test
    public void testEnterTips() {

    }
    @Test
    public void testAdjustTips() {

    }
    @Test
    public void testBatch() {

    }
    //

    @Test
    public void testAuthAndVoidAuth() {

    }
    @Test
    public void testAuthAndCapture() {

    }


}
