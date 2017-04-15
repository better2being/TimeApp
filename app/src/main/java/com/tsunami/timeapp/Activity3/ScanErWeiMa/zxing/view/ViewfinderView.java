package com.tsunami.timeapp.Activity3.ScanErWeiMa.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


import com.tsunami.timeapp.Activity3.ScanErWeiMa.zxing.camera.CameraManager;
import com.google.zxing.ResultPoint;
import com.tsunami.timeapp.R;

import java.util.ArrayList;
import java.util.List;

//找到view的view
public final class ViewfinderView extends View {
	//额外添加的代码
	private int mMoveStepDistance;
	private int mAnimDelayTime;

	private Rect mFramingRect;
	private float mScanLineTop;
	private float mScanLineLeft;
	private Paint mPaint;
	private TextPaint mTipPaint;

	private int mMaskColor;
	private int mCornerColor;
	private int mCornerLength;
	private int mCornerSize;
	private int mRectWidth;
	private int mRectHeight;
	private int mBarcodeRectHeight;
	private int mTopOffset;
	private int mScanLineSize;
	private int mScanLineColor;
	private int mScanLineMargin;
	private boolean mIsShowDefaultScanLineDrawable;
	private Drawable mCustomScanLineDrawable;
	private Bitmap mScanLineBitmap;
	private int mBorderSize;
	private int mBorderColor;
	private int mAnimTime;
	private boolean mIsCenterVertical;
	private int mToolbarHeight;
	private boolean mIsBarcode;
	private String mQRCodeTipText;
	private String mBarCodeTipText;
	private String mTipText;
	private int mTipTextSize;
	private int mTipTextColor;
	private boolean mIsTipTextBelowRect;
	private int mTipTextMargin;
	private boolean mIsShowTipTextAsSingleLine;
	private int mTipBackgroundColor;
	private boolean mIsShowTipBackground;
	private boolean mIsScanLineReverse;
	private boolean mIsShowDefaultGridScanLineDrawable;
	private Drawable mCustomGridScanLineDrawable;
	private Bitmap mGridScanLineBitmap;
	private float mGridScanLineBottom;
	private float mGridScanLineRight;

	private Bitmap mOriginQRCodeScanLineBitmap;
	private Bitmap mOriginBarCodeScanLineBitmap;
	private Bitmap mOriginQRCodeGridScanLineBitmap;
	private Bitmap mOriginBarCodeGridScanLineBitmap;


	private float mHalfCornerSize;
	private StaticLayout mTipTextSl;
	private int mTipBackgroundRadius;

	private boolean mIsOnlyDecodeScanBoxArea;
	// 扫描页面的透明度
	private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
	// 动画延迟时间
	private static final long ANIMATION_DELAY = 40L;//修改刷新延时从80L降低到40L
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 20;
	private static final int POINT_SIZE = 6;
	// 相机管理者
	private CameraManager cameraManager;
	// 画笔
	private final Paint paint;
	// 返回的照片
	private Bitmap resultBitmap;
	//
	private final int frameColor;
	// 面具颜色
	private final int maskColor;
	// 结果颜色
	private final int resultColor;
	// 扫描线的颜色
	private final int laserColor;
	// 结果点的颜色
	private final int resultPointColor;
	// 扫描页的透明度
	private int scannerAlpha;
	// 可能的结果点数
	private List<ResultPoint> possibleResultPoints;
	// 最后的结果点数
	private List<ResultPoint> lastPossibleResultPoints;

	//middle 闪烁线的位置提为全局
	private int middle = 0;
	private int middleType = '+';

	// This constructor is used when the class is built from an XML resource.
	// 当调用的地方findViewById时调用
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//ZLF
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mMaskColor = Color.parseColor("#33FFFFFF");
		mCornerColor = Color.WHITE;
		mCornerLength =20;/* BGAQRCodeUtil.dp2px(context, 20);*/
		mCornerSize = 3;/*BGAQRCodeUtil.dp2px(context, 3);*/
		mScanLineSize = 1;/*BGAQRCodeUtil.dp2px(context, 1);*/
		mScanLineColor = Color.WHITE;
		mTopOffset = 90;//BGAQRCodeUtil.dp2px(context, 90);
		mRectWidth = 200;//BGAQRCodeUtil.dp2px(context, 200);
		mBarcodeRectHeight = 140;//BGAQRCodeUtil.dp2px(context, 140);
		mScanLineMargin = 0;
		mIsShowDefaultScanLineDrawable = false;
		mCustomScanLineDrawable = null;
		mScanLineBitmap = null;
		mBorderSize = 1;//BGAQRCodeUtil.dp2px(context, 1);
		mBorderColor = Color.WHITE;
		mAnimTime = 1000;
		mIsCenterVertical = false;
		mToolbarHeight = 0;
		mIsBarcode = true;
		mMoveStepDistance = 2;//BGAQRCodeUtil.dp2px(context, 2);
		mTipText = null;
		mTipTextSize =14;// BGAQRCodeUtil.sp2px(context, 14);
		mTipTextColor = Color.WHITE;
		mIsTipTextBelowRect = false;
		mTipTextMargin = 20;//BGAQRCodeUtil.dp2px(context, 20);
		mIsShowTipTextAsSingleLine = false;
		mTipBackgroundColor = Color.parseColor("#22000000");
		mIsShowTipBackground = false;
		mIsScanLineReverse = false;
		mIsShowDefaultGridScanLineDrawable = false;

		mTipPaint = new TextPaint();
		mTipPaint.setAntiAlias(true);

		mTipBackgroundRadius = 4;//BGAQRCodeUtil.dp2px(context, 4);

		mIsOnlyDecodeScanBoxArea = false;

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		// 初始化相关数据(初始化一次，总比每次onDraw要好)
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		frameColor = resources.getColor(R.color.viewfinder_frame);
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;
	}

	// 设置相机管理者
	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	// 画图
	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		// 获取相机的frame
		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}
		if(middle == 0){	//修改扫描矩形横线代码
			//初始化middle的位置为矩形上边缘
			middle = frame.top;
		}
		// 获取宽高
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		// Draw the exterior (i.e. outside the framing rect) darkened
		// 画区域，其余地方变黑
		// 判断结果图片是否为空，返回对应的颜色
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);// 画：上部的颜色
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);// 画：左边中间的颜色
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);// 画：右边中间的颜色
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);// 画：下面的颜色
		// 判断bitmap是否为空
		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			// 设置有结果时画布的透明度
			paint.setAlpha(CURRENT_POINT_OPACITY);
			// 在画布上绘制图像
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		} else {


			//白色边框
			Resources resources = getResources();
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(resources.getColor(R.color.encode_view));
			paint.setStrokeWidth(1);
			canvas.drawRect(frame, paint);

			//扫描线



			//修改扫描矩形四角代码
			int linewidth = 5;
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(resources.getColor(R.color.colorPrimary));

			//四角靠边
			canvas.drawRect(frame.left, frame.top, (linewidth + frame.left), (50 + frame.top), paint);
			canvas.drawRect(frame.left, frame.top, (50 + frame.left), (linewidth + frame.top), paint);
			canvas.drawRect(((0 - linewidth) + frame.right), frame.top, (1 + frame.right), (50 + frame.top), paint);
			canvas.drawRect((-50 + frame.right), frame.top, frame.right, (linewidth + frame.top), paint);
			canvas.drawRect(frame.left, (-49 + frame.bottom), (linewidth + frame.left), (1 + frame.bottom), paint);
			canvas.drawRect(frame.left, ((0 - linewidth)+frame.bottom), (50 + frame.left), (1 + frame.bottom), paint);
			canvas.drawRect(((0 - linewidth) + frame.right), (-49 + frame.bottom), (1 + frame.right), (1 + frame.bottom), paint);
			canvas.drawRect((-50 + frame.right), ((0 - linewidth) + frame.bottom), frame.right, (linewidth - (linewidth - 1) + frame.bottom), paint);
			//四角靠边 内聚15dp
//			canvas.drawRect(15 + frame.left, 15 + frame.top, 15 + (linewidth + frame.left), 15 + (50 + frame.top), paint);
//			canvas.drawRect(15 + frame.left, 15 + frame.top, 15 + (50 + frame.left), 15 + (linewidth + frame.top), paint);
//			canvas.drawRect(-15 + ((0 - linewidth) + frame.right), 15 + frame.top, -15 + (1 + frame.right), 15 + (50 + frame.top), paint);
//			canvas.drawRect(-15 + (-50 + frame.right), 15 + frame.top, -15 + frame.right, 15 + (linewidth + frame.top), paint);
//			canvas.drawRect(15 + frame.left, -15 + (-49 + frame.bottom), 15 + (linewidth + frame.left), -15 + (1 + frame.bottom), paint);
//			canvas.drawRect(15 + frame.left, -15 + ((0 - linewidth) + frame.bottom), 15 + (50 + frame.left), -15 + (1 + frame.bottom), paint);
//			canvas.drawRect(-15 + ((0 - linewidth) + frame.right), -15 + (-49 + frame.bottom), -15 + (1 + frame.right), -15 + (1 + frame.bottom), paint);
//			canvas.drawRect(-15 + (-50 + frame.right), -15 + ((0 - linewidth) + frame.bottom), -15 + frame.right, -15 + (linewidth - (linewidth - 1) + frame.bottom), paint);

             // 画扫描线 ZLF
			//drawScanLine(canvas);
			// 移动扫描线的位置
			//moveScanLine();
			// 没有结果时
			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			// 画中间的红线
			paint.setColor(laserColor);
			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			// 设置高度
//			int middle = frame.height() / 2 + frame.top;
			//修改扫描矩形横线 为上下移动 代码
			if(middleType == '+'){
				middle += 2;
				if(middle >= frame.bottom-linewidth)
					middleType = '-';
			}else if(middleType == '-'){
				middle -= 2;
				if(middle <= frame.top+linewidth)
					middleType = '+';
			}

//			canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
			//修改横线 不靠近四角
			canvas.drawRect(frame.left + linewidth, middle - 1, frame.right - linewidth, middle + 3, paint);

			// 准备的frame
			Rect previewFrame = cameraManager.getFramingRectInPreview();
			float scaleX = frame.width() / (float) previewFrame.width();
			float scaleY = frame.height() / (float) previewFrame.height();
			List<ResultPoint> currentPossible = possibleResultPoints;
			List<ResultPoint> currentLast = lastPossibleResultPoints;
			int frameLeft = frame.left;
			int frameTop = frame.top;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				// 获取坐标点，画点
				possibleResultPoints = new ArrayList<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(CURRENT_POINT_OPACITY);
				paint.setColor(resultPointColor);
				synchronized (currentPossible) {
					for (ResultPoint point : currentPossible) {
						canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX), frameTop + (int) (point.getY() * scaleY), POINT_SIZE, paint);
					}
				}
			}
			if (currentLast != null) {
				paint.setAlpha(CURRENT_POINT_OPACITY / 2);
				paint.setColor(resultPointColor);
				synchronized (currentLast) {
					float radius = POINT_SIZE / 2.0f;
					for (ResultPoint point : currentLast) {
						canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX), frameTop + (int) (point.getY() * scaleY), radius, paint);
					}
				}
			}
			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			// 刷新，但是只刷新区域内的界面，也就是，只刷新点和红线
			postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE, frame.top - POINT_SIZE, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
		}
	}

	// 画结果
	public void drawViewfinder() {
		// 回收
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 *
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	// 画最后的结果图
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	// 添加结果点
	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				// 清除多余的点，保持最新的10个点
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}
	//**************** ZLF
	/*private void drawScanLine(Canvas canvas) {
		if (mIsBarcode) {
			if (mGridScanLineBitmap != null) {
				RectF dstGridRectF = new RectF(mFramingRect.left + mHalfCornerSize + 0.5f, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mGridScanLineRight, mFramingRect.bottom - mHalfCornerSize - mScanLineMargin);

				Rect srcGridRect = new Rect((int) (mGridScanLineBitmap.getWidth() - dstGridRectF.width()), 0, mGridScanLineBitmap.getWidth(), mGridScanLineBitmap.getHeight());

				if (srcGridRect.left < 0) {
					srcGridRect.left = 0;
					dstGridRectF.left = dstGridRectF.right - srcGridRect.width();
				}

				canvas.drawBitmap(mGridScanLineBitmap, srcGridRect, dstGridRectF, mPaint);
			} else if (mScanLineBitmap != null) {
				RectF lineRect = new RectF(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineBitmap.getWidth(), mFramingRect.bottom - mHalfCornerSize - mScanLineMargin);
				canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
			} else {
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setColor(mScanLineColor);
				canvas.drawRect(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineSize, mFramingRect.bottom - mHalfCornerSize - mScanLineMargin, mPaint);
			}
		} else {
			if (mGridScanLineBitmap != null) {
				RectF dstGridRectF = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mFramingRect.top + mHalfCornerSize + 0.5f, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mGridScanLineBottom);

				Rect srcRect = new Rect(0, (int) (mGridScanLineBitmap.getHeight() - dstGridRectF.height()), mGridScanLineBitmap.getWidth(), mGridScanLineBitmap.getHeight());

				if (srcRect.top < 0) {
					srcRect.top = 0;
					dstGridRectF.top = dstGridRectF.bottom - srcRect.height();
				}

				canvas.drawBitmap(mGridScanLineBitmap, srcRect, dstGridRectF, mPaint);
			} else if (mScanLineBitmap != null) {
				RectF lineRect = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineBitmap.getHeight());
				canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
			} else {
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setColor(mScanLineColor);
				canvas.drawRect(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineSize, mPaint);
			}
		}
	}
	*//**
	 * 移动扫描线的位置
	 *//*
	private void moveScanLine() {
		if (mIsBarcode) {
			if (mGridScanLineBitmap == null) {
				// 处理非网格扫描图片的情况
				mScanLineLeft += mMoveStepDistance;
				int scanLineSize = mScanLineSize;
				if (mScanLineBitmap != null) {
					scanLineSize = mScanLineBitmap.getWidth();
				}

				if (mIsScanLineReverse) {
					if (mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize || mScanLineLeft < mFramingRect.left + mHalfCornerSize) {
						mMoveStepDistance = -mMoveStepDistance;
					}
				} else {
					if (mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize) {
						mScanLineLeft = mFramingRect.left + mHalfCornerSize + 0.5f;
					}
				}
			} else {
				// 处理网格扫描图片的情况
				mGridScanLineRight += mMoveStepDistance;
				if (mGridScanLineRight > mFramingRect.right - mHalfCornerSize) {
					mGridScanLineRight = mFramingRect.left + mHalfCornerSize + 0.5f;
				}
			}
		} else {
			if (mGridScanLineBitmap == null) {
				// 处理非网格扫描图片的情况
				mScanLineTop += mMoveStepDistance;
				int scanLineSize = mScanLineSize;
				if (mScanLineBitmap != null) {
					scanLineSize = mScanLineBitmap.getHeight();
				}

				if (mIsScanLineReverse) {
					if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize || mScanLineTop < mFramingRect.top + mHalfCornerSize) {
						mMoveStepDistance = -mMoveStepDistance;
					}
				} else {
					if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize) {
						mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
					}
				}
			} else {
				// 处理网格扫描图片的情况
				mGridScanLineBottom += mMoveStepDistance;
				if (mGridScanLineBottom > mFramingRect.bottom - mHalfCornerSize) {
					mGridScanLineBottom = mFramingRect.top + mHalfCornerSize + 0.5f;
				}
			}

		}
		postInvalidateDelayed(mAnimDelayTime, mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
	}*/
}