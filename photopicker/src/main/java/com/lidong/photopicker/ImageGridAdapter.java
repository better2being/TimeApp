package com.lidong.photopicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ͼƬAdapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageGridAdapter(Context context, boolean showCamera, int itemSize){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.mItemSize = itemSize;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
    }


    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }


    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }


    public void setDefaultSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        notifyDataSetChanged();
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size() > 0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }


    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size() > 0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    public void setItemSize(int columnWidth) {

        if(mItemSize == columnWidth){
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if(showCamera){
            if(i == 0){
                return null;
            }
            return mImages.get(i - 1);
        }else{
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if(type == TYPE_CAMERA){
            view = mInflater.inflate(R.layout.item_camera, viewGroup, false);
            view.setTag(null);
        }else if(type == TYPE_NORMAL){
            ViewHolde holde;
            if(view == null){
                view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
                holde = new ViewHolde(view);
            }else{
                holde = (ViewHolde) view.getTag();
                if(holde == null){
                    view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
                    holde = new ViewHolde(view);
                }
            }
            if(holde != null) {
                holde.bindData(getItem(i));
            }
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if(lp.height != mItemSize){
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    class ViewHolde {
        ImageView image;
        ImageView indicator;
        View mask;

        ViewHolde(View view){
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(final Image data){
            if(data == null) return;
            //
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedImages.contains(data)){
                    //
                    indicator.setImageResource(R.mipmap.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    //
                    indicator.setImageResource(R.mipmap.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);

            if(mItemSize > 0) {
                // 显示图片
                Glide.with(mContext)
                        .load(imageFile)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .override(mItemSize, mItemSize)
                        .centerCrop()
                        .into(image);
            }
        }
    }

}
