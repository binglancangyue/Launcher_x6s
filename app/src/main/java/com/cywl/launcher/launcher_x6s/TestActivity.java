package com.cywl.launcher.launcher_x6s;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class TestActivity extends Activity {
    private final static String TAG = "TestActivity";
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;
    private Button textView;
    private PublishSubject<String> subject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContext = this;
        sharedPreferences = mContext.getSharedPreferences("date",
                Context.MODE_PRIVATE);
        initView();
        initRXJava();
    }

    private void initView() {
        textView = findViewById(R.id.tv_aaa);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initRXJava();
                subject.onNext("点击了");
                subject.onComplete();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rcv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    }

    @SuppressLint("CheckResult")
    private void initRXJava() {
        compositeDisposable = new CompositeDisposable();
        subject = PublishSubject.create();
        subject.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                textView.setText(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
