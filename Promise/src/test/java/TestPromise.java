import com.hotsauce.promise.Promise;
import com.hotsauce.promise.PromiseReceived;
import org.junit.Test;

public class TestPromise {
    @Test
    public void test() {
        boolean haveDriver = true;
        boolean isDriverHappy = true;
        int passenger = 4;
        var a = new Promise<Void>() {
            @Override
            protected void onPromise() {
                if(haveDriver) {
                    resolve(null);
                }else {
                    reject(null);
                }
            }
        };
        a.<Integer>promiseThen(new PromiseReceived<>() {
            @Override
            public void onPromiseResolved(Void result) {
                System.out.println("Found Driver");
                if(isDriverHappy) {
                    resolve(passenger);
                }else {
                    reject(passenger);
                }
            }
            @Override
            public void onPromiseRejected(Void result) {
                System.out.println("There is no Driver");
            }
        }).<Void>promiseThen(new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Integer result) {
                System.out.println("There are " + result + " passenger");
                if(result > 4) {
                    reject(null);
                }else {
                    resolve(null);
                }
            }

            @Override
            protected void onPromiseRejected(Integer result) {
                System.out.println("Driver un Happy and leave");
            }
        }).promiseThen(new PromiseReceived<>() {
            @Override
            protected void onPromiseResolved(Void result) {
                System.out.println("GO");
            }

            @Override
            protected void onPromiseRejected(Void result) {
                System.out.println("TO Much People EXIT");
            }
        });
    }
}
