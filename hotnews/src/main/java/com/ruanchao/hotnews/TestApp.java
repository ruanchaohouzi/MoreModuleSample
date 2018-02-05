package com.ruanchao.hotnews;

/**
 * Created by ruanchao on 2018/2/5.
 * 主要用于测试加载Application 是否成功
 */

public class TestApp {

    private static TestApp mTestApp = null;
    static Object o = new Object();
    String text = "我是初始化状态";

    private TestApp(){}

    public static TestApp getInstance() {
        if (mTestApp == null) {
            synchronized (o) {
                if (mTestApp == null) {
                    mTestApp = new TestApp();
                }
            }
        }
        return mTestApp;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
