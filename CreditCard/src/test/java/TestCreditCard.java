import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.IReferNumber;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.auth.Request;
import com.hotsauce.creditcard.io.base.Response;
import com.hotsauce.creditcard.io.manage.DejavooManageData;
import com.hotsauce.creditcard.io.manage.PosLinkManageData;
import com.hotsauce.creditcard.providers.CreditCardFactory;
import com.hotsauce.creditcard.providers.ProviderType;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;
import com.hotsauce.promise.Promise;
import com.hotsauce.promise.PromiseReceived;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TestCreditCard {
    @Test
    public void testAuthAndVoidAuth() {
        createPromise()
                .promiseThen(createAuthPromiseThen())
                .promiseThen(createVoidAuthPromiseThen())
                .promiseThen(createFinalPromiseThen("Void Auth"));
    }
    @Test
    public void testAuthAndCaptureAndVoid() {
        createPromise()
                .promiseThen(createAuthPromiseThen())
                .promiseThen(createCapturePromiseThen())
                .promiseThen(createVoidSalePromiseThen("Capture",false))
                .promiseThen(createFinalPromiseThen("Void Sale"));
    }
    @Test
    public void testSaleAndVoidSale() {
        createPromise()
                .promiseThen(createSalePromiseThen())
                .promiseThen(createVoidSalePromiseThen("Sale",false))
                .promiseThen(createFinalPromiseThen("Void Sale"));
    }

    @Test
    public void testSaleAndEnterTipAndAdjustTipAndVoidSale() {
        createPromise()
                .promiseThen(createSalePromiseThen())
                .promiseThen(createEnterTipsPromiseThen("Sale"))
                .promiseThen(createAdjustTipsPromiseThen("Enter Tips"))
                .promiseThen(createVoidSalePromiseThen("Adjust Tips",false))
                .promiseThen(createFinalPromiseThen("Void Sale"));
    }

    @Test
    public void testSaleAndBatchAndRefund() {
        createPromise()
                .promiseThen(createSalePromiseThen())
                .promiseThen(createBatchPromiseThen("Sale"))
                .promiseThen(createVoidSalePromiseThen("Batch",true))
                .promiseThen(createFinalPromiseThen("Refund"));
    }

    @Test
    public void testBatchAll() {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.batchsettlement.Request request = new com.hotsauce.creditcard.io.batchsettlement.Request(getTransactionID(),"");
        creditCard.batch(request);
    }

    @Test
    public void testTokenizeAndSale() {
        createPromise()
                .promiseThen(createTokenizePromiseThen())
                .promiseThen(createSaleTokenPromiseThen())
                .promiseThen(createFinalPromiseThen("Sale With Token"));
    }

    private final boolean showEnterTips = false;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    private final ProviderType providerType = ProviderType.POSLink;
    private final DeviceInfo deviceInfo = DeviceInfo.builder()
            .ip("192.168.1.110")
            .port(DeviceInfo.getDeviceDefaultPort(providerType))
            .timeOut(60000)
            .retryTime(0)
            .build();
    private final PosLinkManageData posLinkManageData = new PosLinkManageData("TA5714099","Hotsauce987!","887000001519","88700000151901");
    private final DejavooManageData dejavooManageData = new DejavooManageData("X9rH3Z9WKk","103315001");

    private CreditCard<?> generateCreditCard() {
        CreditCard<?> creditCard = CreditCardFactory.createInstance(providerType,deviceInfo);
        switch (providerType) {
            case POSLink:
                ((CreditCard<PosLinkManageData>) creditCard).setManagementData(posLinkManageData);
                break;
            case Dejavoo:
                ((CreditCard<DejavooManageData>) creditCard).setManagementData(dejavooManageData);
                break;
        }
        return creditCard;
    }

    public String getTransactionID() {
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    //region "unit Test"
    public Response<com.hotsauce.creditcard.io.auth.Response> testAuth() {
        CreditCard<?> creditCard = generateCreditCard();
        Request request = new Request(getTransactionID(), BigDecimal.ONE);
        return creditCard.auth(request);
    }
    public Response<com.hotsauce.creditcard.io.voidauth.Response> testVoidAuth(String refNumber) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.voidauth.Request request = new com.hotsauce.creditcard.io.voidauth.Request(getTransactionID(),refNumber);
        return creditCard.voidAuth(request);
    }
    public Response<com.hotsauce.creditcard.io.capture.Response> testCapture(String refNumber) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.capture.Request request = new com.hotsauce.creditcard.io.capture.Request(getTransactionID(),BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ONE,BigDecimal.ZERO,refNumber);
        return creditCard.capture(request);
    }
    public Response<com.hotsauce.creditcard.io.sale.Response> testSale() {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.sale.Request request = new com.hotsauce.creditcard.io.sale.Request(getTransactionID(),BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ONE,BigDecimal.ZERO,showEnterTips);
        return creditCard.sale(request);
    }
    public Response<com.hotsauce.creditcard.io.sale.Response> testSaleWithToken(String refNumber, String token, String expDate, CreditCardUtil.CardIssuers cardIssuers) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.sale.RequestToken request = new com.hotsauce.creditcard.io.sale.RequestToken(getTransactionID(),BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ONE,BigDecimal.ZERO,showEnterTips,refNumber,token,expDate,cardIssuers);
        return creditCard.sale(request);
    }
    public Response<com.hotsauce.creditcard.io.voidsale.Response> testVoid(String refNumber,boolean isSettle) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.voidsale.Request request = new com.hotsauce.creditcard.io.voidsale.Request(getTransactionID(),refNumber,isSettle);
        return creditCard.voidSale(request);
    }
    public Response<com.hotsauce.creditcard.io.entertips.Response> testEnterTips(String refNumber) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.entertips.Request request = new com.hotsauce.creditcard.io.entertips.Request(getTransactionID(),BigDecimal.ZERO,refNumber);
        return creditCard.enterTips(request);
    }
    public Response<com.hotsauce.creditcard.io.adjusttips.Response> testAdjustTips(String refNumber) {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.adjusttips.Request request = new com.hotsauce.creditcard.io.adjusttips.Request(getTransactionID(),BigDecimal.ONE,refNumber);
        return creditCard.adjustTips(request);
    }
    public Response<com.hotsauce.creditcard.io.tokenize.Response> testTokenize() {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.tokenize.Request request = new com.hotsauce.creditcard.io.tokenize.Request(getTransactionID());
        return creditCard.tokenize(request);
    }
    public Response<com.hotsauce.creditcard.io.batchsettlement.Response> testBatch() {
        CreditCard<?> creditCard = generateCreditCard();
        com.hotsauce.creditcard.io.batchsettlement.Request request = new com.hotsauce.creditcard.io.batchsettlement.Request(getTransactionID(),"");
        return creditCard.batch(request);
    }
    //endregion

    //region "Promise"
    private Promise<Void> createPromise() {
        return new Promise<>() {
            @Override
            protected void onPromise() {
                resolve(null);
            }
        };
    }
    private <T> PromiseReceived<T,Response<com.hotsauce.creditcard.io.auth.Response>> createAuthPromiseThen(){
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(T t) {
                System.out.println("Start Auth");
                Response<com.hotsauce.creditcard.io.auth.Response> response = testAuth();
                if (Objects.equals(response.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response);
                } else {
                    reject(response);
                }
            }

            @Override
            protected void onPromiseRejected(T t) {

            }
        };
    }
    private PromiseReceived<Response<com.hotsauce.creditcard.io.auth.Response>,Response<com.hotsauce.creditcard.io.voidauth.Response>> createVoidAuthPromiseThen() {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<com.hotsauce.creditcard.io.auth.Response> response) {
                System.out.println("Auth Success");
                System.out.println("Start Void Auth");
                Response<com.hotsauce.creditcard.io.voidauth.Response> response1 = testVoidAuth(response.getData().getRefNumber());
                if(Objects.equals(response.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(Response<com.hotsauce.creditcard.io.auth.Response> response) {
                System.out.println("Auth" + " Fail : " + response.getResultMessage());
            }
        };
    }
    private PromiseReceived<Response<com.hotsauce.creditcard.io.auth.Response>,Response<com.hotsauce.creditcard.io.capture.Response>> createCapturePromiseThen() {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<com.hotsauce.creditcard.io.auth.Response> response) {
                System.out.println("Auth Success");
                System.out.println("Start Capture");
                Response<com.hotsauce.creditcard.io.capture.Response> response1 = testCapture(response.getData().getRefNumber());
                if(Objects.equals(response.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(Response<com.hotsauce.creditcard.io.auth.Response> response) {
                System.out.println("Auth" + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T> PromiseReceived<T,Response<com.hotsauce.creditcard.io.sale.Response>> createSalePromiseThen() {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(T response) {
                System.out.println("Start Sale");
                Response<com.hotsauce.creditcard.io.sale.Response> response1 = testSale();
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(T response) {
            }
        };
    }
    private <T extends IReferNumber> PromiseReceived<Response<T>,Response<com.hotsauce.creditcard.io.voidsale.Response>> createVoidSalePromiseThen(String lastAction,boolean isSettle) {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<T> response) {
                System.out.println(lastAction + " Success");
                System.out.println("Start Void Sale");
                Response<com.hotsauce.creditcard.io.voidsale.Response> response1 = testVoid(response.getData().getRefNumber(),isSettle);
                if(Objects.equals(response.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(Response<T> response) {
                System.out.println(lastAction + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T extends IReferNumber> PromiseReceived<Response<T>,Response<com.hotsauce.creditcard.io.entertips.Response>> createEnterTipsPromiseThen(String lastAction) {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<T> response) {
                System.out.println(lastAction + " Success");
                System.out.println("Start Enter Tips");
                Response<com.hotsauce.creditcard.io.entertips.Response> response1 = testEnterTips(response.getData().getRefNumber());
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(Response<T> response) {
                System.out.println(lastAction + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T extends IReferNumber> PromiseReceived<Response<T>,Response<com.hotsauce.creditcard.io.adjusttips.Response>> createAdjustTipsPromiseThen(String lastAction) {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<T> response) {
                System.out.println(lastAction + " Success");
                System.out.println("Start Adjust Tips");
                Response<com.hotsauce.creditcard.io.adjusttips.Response> response1 = testAdjustTips(response.getData().getRefNumber());
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(Response<T> response) {
                System.out.println(lastAction + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T> PromiseReceived<T,Response<com.hotsauce.creditcard.io.tokenize.Response>> createTokenizePromiseThen() {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(T t) {
                System.out.println("Start Tokenize");
                Response<com.hotsauce.creditcard.io.tokenize.Response> response1 = testTokenize();
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }
            @Override
            protected void onPromiseRejected(T t) {}
        };
    }
    private PromiseReceived<Response<com.hotsauce.creditcard.io.tokenize.Response>,Response<com.hotsauce.creditcard.io.sale.Response>> createSaleTokenPromiseThen() {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<com.hotsauce.creditcard.io.tokenize.Response> response) {
                System.out.println("Start Sale");
                Response<com.hotsauce.creditcard.io.sale.Response> response1 = testSaleWithToken(response.getData().getRefNumber(),response.getData().getToken(),response.getData().getExpDate(),response.getData().getCardIssuer());
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    resolve(response1);
                }else {
                    reject(response1);
                }
            }

            @Override
            protected void onPromiseRejected(Response<com.hotsauce.creditcard.io.tokenize.Response> response) {
                System.out.println("Get Token" + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T extends IReferNumber> PromiseReceived<Response<T>,Response<T>> createBatchPromiseThen(String lastAction) {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<T> response) {
                System.out.println(lastAction + " Success");
                System.out.println("Start Batch");
                Response<com.hotsauce.creditcard.io.batchsettlement.Response> response1 = testBatch();
                if(Objects.equals(response1.getResultCode(), ResultCode.SUCCESS.getCode())) {
                    response.setResultCode(response1.getResultCode());
                    response.setResultMessage(response1.getResultMessage());
                    resolve(response);
                }else {
                    reject(response);
                }
            }

            @Override
            protected void onPromiseRejected(Response<T> response) {
                System.out.println("Batch" + " Fail : " + response.getResultMessage());
            }
        };
    }
    private <T> PromiseReceived<Response<T>,Void> createFinalPromiseThen(String lastAction) {
        return new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Response<T> response) {
                System.out.println(lastAction + " Success");
                System.out.println("All Done");
            }
            @Override
            protected void onPromiseRejected(Response<T> response) {
                System.out.println(lastAction + " Fail : " + response.getResultMessage());
            }
        };
    }
    //endregion
}
