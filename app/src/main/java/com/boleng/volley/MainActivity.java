package com.boleng.volley;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private WebView webContainer;
    private MyHandler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        webContainer = (WebView) findViewById(R.id.webContainer);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024 * 10);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();


        String url = "http://blog.csdn.net/haozidao/article/details/50946715";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", response);
                message.setData(bundle);
                handler.sendMessage(message);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        });
        requestQueue.add(stringRequest);

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {

                e.onNext("send data");
            }
        });
        observable = Observable.just("who care");
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            arrayList.add(i + " ");
        }
        observable = Observable.fromIterable(arrayList);

        observable = Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just("lengbo");
            }
        });

//         Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter e) throws Exception {
//                Log.i(TAG,Thread.currentThread().getName());
//                int count=0;
//                while (true){
//                    e.onNext("other thread"+count);
//                    count++;
//                }
//
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
//            @Override
//            public void accept(Object o) throws Exception {
//                Thread.sleep(2000);
//                Log.i(TAG,Thread.currentThread().getName());
//                Log.i(TAG,o.toString());
//            }
//        });
//        Observer observer=new Observer() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Object o) {
//                Log.i(TAG,o.toString());
//                Log.i(TAG,Thread.currentThread().getName());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//        observable.subscribe(observer);
//        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
//                int count = 0;
//                while (count < 130) {
//                    emitter.onNext( count);
//                    count++;
//                }
//            }
//        }, BackpressureStrategy.BUFFER); //增加了一个参数 Subscriber<Integer> subscriber = new Subscriber<Integer>() { @Override public void onSubscribe(Subscription s) { Log.d(TAG, "onSubscribe"); s.request(Long.MAX_VALUE); } @Override public void onNext(Integer integer) { Log.d(TAG, "onNext: " + integer); } @Override public void onError(Throwable t) { Log.w(TAG, "onError: ", t); } @Override public void onComplete() { Log.d(TAG, "onComplete"); } }; flowable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
//
//
//        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                Log.d(TAG, "onSubscribe");
//                s.request(Long.MAX_VALUE);
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//                Log.d(TAG, "onNext: " + integer);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Log.w(TAG, "onError: ", t);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete");
//            }
//        };
//        flowable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);


//        Flowable.create(new FlowableOnSubscribe<String>() {
//            @Override
//            public void subscribe(FlowableEmitter<String> e) throws Exception {
//                int count = 0;
//                while (count < 129) {
//                    e.onNext("" + count);
//                    count++;
//                }
//            }
//        }, BackpressureStrategy.ERROR)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onSubscribe(Subscription s) {
//                        s.request(Long.MAX_VALUE);
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.i(TAG, s);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                for (int i = 0; i < 199; i++)
                    e.onNext("fafs" + i);
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, s);
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG,t.toString());
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }


    class MyHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            if (activity != null) {
                Bundle bundle = msg.getData();
                String result = (String) bundle.get("result");
//                Log.i(TAG, result);
                webContainer.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
            }
        }
    }
}
