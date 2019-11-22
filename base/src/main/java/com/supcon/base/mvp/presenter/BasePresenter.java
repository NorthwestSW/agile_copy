package com.supcon.base.mvp.presenter;

import com.supcon.base.mvp.view.IBaseView;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {

    private WeakReference<V> mViewRef;
    protected CompositeDisposable mCompositeSubscription = new CompositeDisposable();
    private V mProxyView;

    protected void onAttached(){

    }

    protected void onDetached() {

    }

    /**
     * 判断界面是否销毁
     */
    protected boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }
    @Override
    public void attachView(V v) {
        this.mViewRef = new WeakReference<>(v);
        this.onAttached();
        MvpViewHandler viewHandler = new MvpViewHandler(v);
        mProxyView = (V) Proxy.newProxyInstance(v.getClass().getClassLoader(), v.getClass().getInterfaces(), viewHandler);


    }

    @Override
    public V getView() {
        return null;
    }

    @Override
    public void detachView() {
        mCompositeSubscription.dispose();
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        this.onDetached();
    }

    private  class MvpViewHandler implements InvocationHandler{
        private IBaseView mvpView;

        MvpViewHandler(IBaseView mvpView) {
            this.mvpView = mvpView;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //如果V层没被销毁, 执行V层的方法.
            if (isViewAttached()) {
                return method.invoke(mvpView, args);
            }
            //P层不需要关注V层的返回值
            return null;
        }
    }
}
