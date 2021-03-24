package com.cywl.launcher.model;

public class RecycleViewItemData<T> {
    //用来装载不同类型的item数据bean
    T t;
    //item数据bean的类型
    MusicData testData;
//    Map<Integer, MusicData> map = new HashMap<>();


    public RecycleViewItemData(MusicData testData) {
        this.testData = testData;
//        this.map.put(testData.getItemViewTag(), testData);
    }


    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public MusicData getTestData() {
        return testData;
    }

    public void setTestData(MusicData testData) {
        this.testData = testData;
    }
}
