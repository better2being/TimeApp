package com.lidong.photopicker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * ：http://www.cnblogs.com/over140/archive/2012/08/29/2661752.html
 * Created by foamtrace on 2015/8/26.
 */
public class ImageConfig implements Parcelable {

    //
    public int minWidth;
    //
    public int minHeight;
    //
    public long minSize;
    //  { image/jpeg, image/png, ... }
    public String[] mimeType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.minWidth);
        dest.writeInt(this.minHeight);
        dest.writeLong(this.minSize);
        dest.writeStringArray(this.mimeType);
    }

    public ImageConfig() {
    }

    protected ImageConfig(Parcel in) {
        this.minWidth = in.readInt();
        this.minHeight = in.readInt();
        this.minSize = in.readLong();
        this.mimeType = in.createStringArray();
    }

    public static final Creator<ImageConfig> CREATOR = new Creator<ImageConfig>() {
        public ImageConfig createFromParcel(Parcel source) {
            return new ImageConfig(source);
        }

        public ImageConfig[] newArray(int size) {
            return new ImageConfig[size];
        }
    };
}
