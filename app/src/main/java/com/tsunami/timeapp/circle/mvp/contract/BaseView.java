package com.tsunami.timeapp.circle.mvp.contract;

/**
 * @author jilihao
 */
public interface BaseView {
    void showLoading(String msg);
    void hideLoading();
    void showError(String errorMsg);


}
