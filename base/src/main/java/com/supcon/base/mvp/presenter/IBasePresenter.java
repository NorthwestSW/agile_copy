package com.supcon.base.mvp.presenter;

public interface IBasePresenter<V> {

    void attachView(V v);
    V getView();
    void detachView();
}
