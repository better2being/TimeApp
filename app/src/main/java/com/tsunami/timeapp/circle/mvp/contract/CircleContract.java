package com.tsunami.timeapp.circle.mvp.contract;


import com.tsunami.timeapp.circle.bean.CircleItem;
import com.tsunami.timeapp.circle.bean.CommentConfig;
import com.tsunami.timeapp.circle.bean.CommentItem;
import com.tsunami.timeapp.circle.bean.FavortItem;

import java.util.List;

/**
 * @author jilihao
 */
public interface CircleContract {

    interface View extends BaseView{
        void update2DeleteCircle(String circleId);
        void update2AddFavorite(int circlePosition, FavortItem addItem);
        void update2DeleteFavort(int circlePosition, String favortId);
        void update2AddComment(int circlePosition, CommentItem addItem);
        void update2DeleteComment(int circlePosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<CircleItem> datas);
    }

    interface Presenter extends BasePresenter{
        void loadData(int loadType);
        void deleteCircle(final String circleId);
        void addFavort(final int circlePosition);
        void deleteFavort(final int circlePosition, final String favortId);
        void deleteComment(final int circlePosition, final String commentId);

    }
}
