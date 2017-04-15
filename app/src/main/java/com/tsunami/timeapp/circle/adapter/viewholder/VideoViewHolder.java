package com.tsunami.timeapp.circle.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.tsunami.timeapp.R;
import com.tsunami.timeapp.circle.widgets.CircleVideoView;


/**
 * @author jilihao
 */
public class VideoViewHolder extends CircleViewHolder {

    public CircleVideoView videoView;

    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.viewstub_videobody);
        View subView = viewStub.inflate();

        CircleVideoView videoBody = (CircleVideoView) subView.findViewById(R.id.videoView);
        if(videoBody!=null){
            this.videoView = videoBody;
        }
    }
}
