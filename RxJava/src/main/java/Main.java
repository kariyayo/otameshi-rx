import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws Exception {
        http();
    }

    private static void http() throws IOException {
        start(new StreamHttpClient());
    }

    private static void hoge() {
        rangeObservable();
        System.out.println("\n############");
        myObservable();
        System.out.println("\n############");
        myObserver();
        System.out.println("\n############");
        threadCheck();
    }

    private static void rangeObservable() {
        Observable.range(1, 20)
                .filter(n -> n % 3 == 0)
                .map(n -> String.format("[%02d] ", n))
                .subscribe(System.out::print);
    }

    private static void myObservable() {
        Observable<Integer> observable = Observable.create(subscriber -> {
            IntStream.range(1, 20).forEach(x -> subscriber.onNext(x));
            subscriber.onCompleted();
        });
        observable.filter(n -> n % 3 == 0)
                .map(n -> String.format("[%02d] ", n))
                .subscribe(System.out::print);
    }

    private static void myObserver() {
        Observable<Integer> observable = Observable.create(subscriber -> {
            IntStream.range(1, 20).forEach(x -> subscriber.onNext(x));
            subscriber.onCompleted();
        });

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.print("COMPLETE!");
            }

            @Override
            public void onError(Throwable e) {
                System.out.print("ERROR!");
            }

            @Override
            public void onNext(Integer integer) {
                if (integer % 3 == 0) System.out.print(String.format("[%02d] ", integer));
            }
        };

        observable.subscribe(observer);
    }

    private static void threadCheck() {
        Observable<Integer> observable = Observable.create(subscriber -> {
            System.out.println(Thread.currentThread().getName() + ": observable.call");
            IntStream.range(1, 20).forEach(x -> subscriber.onNext(x));
            subscriber.onCompleted();
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.print("[" + Thread.currentThread().getName() + ": COMPLETE!]");
            }

            @Override
            public void onError(Throwable e) {
                System.out.print("ERROR!");
            }

            @Override
            public void onNext(String s) {
                System.out.print("[" + Thread.currentThread().getName() + ": " + s + "] ");
            }
        };

        observable
                .subscribeOn(Schedulers.newThread())
                .map(x -> Thread.currentThread().getName() + ": " + x.toString())
                .observeOn(Schedulers.computation())
                .subscribe(observer);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void start(HttpClient httpClient) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> urls = new ArrayList<>();
        HttpClient client;
        while (true) {
            System.out.print("> ");
            String s = new String(in.readLine());
            if (s.equalsIgnoreCase("singlesync")) {
                client = new SingleSyncHttpClient();
                break;
            } else if (s.equalsIgnoreCase("singleasync")) {
                client = new SingleAsyncHttpClient();
                break;
            } else if (s.equalsIgnoreCase("multisync")) {
                client = new MultiSyncHttpClient();
                break;
            } else if (s.equalsIgnoreCase("multiasync")) {
                client = new MultiAsyncHttpClient();
                break;
            } else {
                urls.add(s);
            }
        }
        client.exec(urls);
        while (true) {
            String s = new String(in.readLine());
            if (s.equals("end")) {
                break;
            }
        }
        System.out.println("\nBye!");
    }
}
