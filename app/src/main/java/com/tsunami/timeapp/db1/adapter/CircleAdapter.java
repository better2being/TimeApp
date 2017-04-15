package com.tsunami.timeapp.db1.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.circle.adapter.viewholder.CircleViewHolder;
import com.tsunami.timeapp.circle.adapter.viewholder.ImageViewHolder;
import com.tsunami.timeapp.circle.adapter.viewholder.URLViewHolder;
import com.tsunami.timeapp.circle.adapter.viewholder.VideoViewHolder;
import com.tsunami.timeapp.circle.mvp.presenter.CirclePresenter;
import com.tsunami.timeapp.circle.utils.GlideCircleTransform;
import com.tsunami.timeapp.circle.utils.UrlUtils;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Circle;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.model1.User;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author shenxiaoming
 */
public class CircleAdapter extends BaseRecycleViewAdapter {

    public final static int TYPE_HEAD = 0;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    public static final int HEADVIEW_SIZE = 1;

    private CirclePresenter presenter;
    private Context context;
    public void setCirclePresenter(CirclePresenter presenter){
        this.presenter = presenter;
    }

    public CircleAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEAD;
        }
        int itemType = CircleViewHolder.TYPE_URL;

        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == TYPE_HEAD){
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_circle, parent, false);
            ImageView userPhoto = (ImageView) headView.findViewById(R.id.circle_userhead);
            // 朋友圈設置用戶頭像
            if (!TextUtils.isEmpty(User.getInstance().getHeadUrl())) {
                File outputImage = new File(Environment.getExternalStorageDirectory(), "username.jpg");

                try {
                    Uri imageUri = Uri.fromFile(outputImage);
                    Bitmap bitmap1 = BitmapFactory.decodeStream(parent.getContext().getContentResolver().openInputStream(imageUri));
                    if (bitmap1 != null) {
                        //picture.setImageBitmap(bitmap);
                        userPhoto.setImageBitmap(bitmap1);
                    } else {

                        //Bitmap bitmap = getLoacalBitmap("/sdcard/tempImage.jpg");
                        // iv_avatar.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            viewHolder = new HeaderViewHolder(headView);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter2_circle_item, parent, false);

            if(viewType == CircleViewHolder.TYPE_URL){
                viewHolder = new URLViewHolder(view);
            }else if(viewType == CircleViewHolder.TYPE_IMAGE){
                viewHolder = new ImageViewHolder(view);
            }else if(viewType == CircleViewHolder.TYPE_VIDEO){
                viewHolder = new VideoViewHolder(view);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if(getItemViewType(position)==TYPE_HEAD){
            //HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        }else{
            final int circlePosition = position - HEADVIEW_SIZE;
            final CircleViewHolder holder = (CircleViewHolder) viewHolder;
            Circle circle = (Circle) datas.get(circlePosition);
            String name;
            if (User.getInstance().getUsername().equals(circle.getAuthor())) {
                name = "自己";
            } else {
                name = circle.getAuthor();
            }
            String headImg = circle.getHeadUrl();
            String content = circle.getContent();
            // 界面更新动态添加到adapter   9月06 14:22 形式
            String createTime = circle.getCreateTime();
            String showTime;
            if (createTime.charAt(5) == '0') {
                showTime = createTime.substring(6, 16);
            } else{
                showTime = createTime.substring(5, 16);
            }

            showTime = showTime.replace("-", "月");

            Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.bg_no_photo).transform(new GlideCircleTransform(context)).into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(showTime);

            if(!TextUtils.isEmpty(content)){
                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            if("自己".equals(name)){
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }else{
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Circle> circles = getDatas();
                    for(int i=0; i<circles.size(); i++){
                        if(createTime.equals(circles.get(i).getCreateTime())){
                            ProgressDialog dialog = new ProgressDialog(v.getContext());
                            dialog.setMessage("正在删除");
                            dialog.show();
                            // 创建网络访问的url地址栏
                            String url = "http://192.168.1.119:8080/ServeNew/friendCheck";
                            RequestParams rp = new RequestParams(url);
                            // 封装该rp对象的请求参数
                            rp.addBodyParameter("op", "deletedynamic");
                            rp.addBodyParameter("client", User.getInstance().getUsername());
                            rp.addBodyParameter("datetime", createTime);
                            // xutils post提交
                            x.http().post(rp, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    // dialog清除
                                    dialog.dismiss();
                                    if ("ok".equals(result)) {
                                        // 同步数据库
                                        UserDB.getInstance(v.getContext()).deleteCircle(createTime);
                                        circles.remove(circle);
                                        notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    Toast.makeText(v.getContext(), "删除失败", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onCancelled(CancelledException e) {

                                }
                                @Override
                                public void onFinished() {
                                    dialog.dismiss();
                                }
                            });
                            return;
                        }
                    }
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if(holder instanceof URLViewHolder){
                        ((URLViewHolder)holder).urlContentTv.setText(circle.getContent());
                        ((URLViewHolder)holder).urlBody.setVisibility(View.VISIBLE);
                        ((URLViewHolder)holder).urlTipTv.setVisibility(View.VISIBLE);
                    }
                    break;
                case CircleViewHolder.TYPE_IMAGE:// 处理图片
                    if(holder instanceof ImageViewHolder){
//                        final List<String> photos = circleItem.getPhotos();
//                        if (photos != null && photos.size() > 0) {
//                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.VISIBLE);
//                            ((ImageViewHolder)holder).multiImageView.setList(photos);
//                            ((ImageViewHolder)holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(View view, int position) {
//                                    //imagesize是作为loading时的图片size
//                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
//                                    ImagePagerActivity.startImagePagerActivity(context, photos, position, imageSize);
//                                }
//                            });
//                        } else {
//                            ((ImageViewHolder)holder).multiImageView.setVisibility(View.GONE);
//                        }
                    }
                    break;
                case CircleViewHolder.TYPE_VIDEO:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size()+1;  //有head需要加1
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
