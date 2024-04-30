import com.hotsauce.promise.Promise;
import com.hotsauce.promise.PromiseWithReceived;
import org.junit.Test;

public class TestPromise {
    final boolean[] allDone = {false};
    @Test
    public void test() {
        System.out.println("Start");

        var a = createGetDriverPromise();
        var b = createGetGuestNumberPromise();
        var c = createGetPricePromise();
        a.then(b).execute();
        //To record Log
        while (true) {
            if(allDone[0]) {
                break;
            }
            try {
                Thread.sleep(500);
            }catch (Exception e) {}
        }
    }

    private Promise<Boolean> createGetDriverPromise() {
        Promise<Boolean> promise = new Promise<>() {
            @Override
            protected void onPromise() {
                System.out.println("Finding Driver please wait");
                BaseApiResponse<Boolean> response = callGetDriverApi();
                if (response.getStatus() != TestPromise.Status.OK || !response.getData()) {
                    System.out.println("Finding Driver Fail");
                    reject();
                } else {
                    System.out.println("Fond Driver");
                    resolve(response.getData());
                }
            }
            @Override
            protected void onFinally() {
                System.out.println("Find Driver Done");
                allDone[0] = true;
            }
        };
        return promise;
    }
    private PromiseWithReceived<Boolean, Integer> createGetGuestNumberPromise() {
        PromiseWithReceived<Boolean,Integer> received = new PromiseWithReceived<>() {
            @Override
            protected void onPromise(Boolean result) {
                BaseApiResponse<Integer> response = callGetGuestNumberApi();
                if (response.getStatus() != TestPromise.Status.OK) {
                    System.out.println("Get Guest Number Fail");
                    reject();
                } else {
                    if (response.getData() > 4) {
                        System.out.println("Too much people");
                        reject();
                    } else {
                        System.out.println("Let GO");
                        resolve(response.getData());
                    }
                }
            }

            @Override
            protected void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            protected void onFinally() {
                System.out.println("Get Guest Number Done");
            }
        };
        return received;
    }
    private PromiseWithReceived<Integer,Void> createGetPricePromise() {
        PromiseWithReceived<Integer,Void> received = new PromiseWithReceived<>() {
            @Override
            protected void onPromise(Integer result) {
                BaseApiResponse<Integer> response = callGetPriceApi(result);
                if(response.getStatus() != TestPromise.Status.OK) {
                    System.out.println("Get Price Fail");
                    reject();
                }else {
                    System.out.println(response.getData() + " per person");
                    resolve(null);
                }
            }
            @Override
            protected void onFinally() {
                System.out.println("Get Price Done");
            }
        };
        return received;
    }

    private BaseApiResponse<Boolean> callGetDriverApi() {
        try {
            Thread.sleep(0);
        }catch (Exception exception){}
        return new BaseApiResponse<>(Status.OK,"",true);
    }
    private BaseApiResponse<Integer> callGetGuestNumberApi() {
        try {
            Thread.sleep(0);
        }catch (Exception exception){}
        return new BaseApiResponse<>(Status.OK,"",3);
    }
    private BaseApiResponse<Integer> callGetPriceApi(int person) {
        try {
            Thread.sleep(0);
        }catch (Exception exception){}
        return new BaseApiResponse<>(Status.OK,"",120 / person);
    }


    public class BaseApiResponse<T> {
        private final Status status;
        private final String message;
        private final T data;
        BaseApiResponse(Status status, String message, T data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
        public Status getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public T getData() {
            return data;
        }
    }
    public enum Status {
        OK,
        FAILED
    }
}
