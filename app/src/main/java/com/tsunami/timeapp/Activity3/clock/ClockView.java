package com.tsunami.timeapp.Activity3.clock;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Calendar;

/**
 * @author zhenglifeng
 */
public class ClockView extends View {

	//ZLF 倒计时部分代码
	private MyCountDownTimer mc;
	private String timer = "00:00:00:0";
	private long totalTime = 0;//总时间
	private long countDownInterval = 100; //刷新时间
	private long millisUntilFinished = 0;//剩余时间


	private static final String TAG = "abc";//ClockView.class.getSimpleName();
	private static boolean DEBUG = true;

	private int colorBg = 0xff237ead;
	private int colorBgRing = 0x80ffffff;
	private int colorMPOuterRing = Color.WHITE;
	private int colorHPOuterRing = 0xccdddddd;
	private int colorMinutePointer = Color.WHITE;
	private int colorHourPointer = 0xccdddddd;
	private int colorTriangle = 0xffdddddd;
	private int colorScaleRing = 0xccdddddd;

	private Paint paintBgRing;
	private Paint paintProgressRing;
	private Paint paintTriangle;
	private Paint paintMinutePointer;
	private Paint paintHourPointer;
	private Paint paintMPOuterRing;
	private Paint paintMPInnerCircle;
	private Paint paintHPOuterRing;
	private Paint paintHPInnerCircle;
	private Paint paintNumber;
	private Paint paintScaleRing;

	private RectF boundScaleRing;
	private RectF boundTimeRing;

	private int centerX;
	private int centerY;

	private float radius;
	private float radiusScaleRing;
	private float radiusMPOuterRing = 20;
	private float radiusMPInnerRing = 10;
	private float radiusHPOuterRing = 18;
	private float radiusHPInnerRing = 8;

	private float strokeWidthRing = 30;
	private float strokeWidthScaleRing = 2;

	private float zDepthScaleRing = 180;
	private float zDepthDashRing = 100;
	private float zDepthHourPointer = 50;
	private float zDepthMinutePointer = 0;

	private float hpTopEdgeLength = 6;
	private float hpBottomEdgeLength = 10;
	private float hpPointerLength = 10;
	private float hpTopCYOffset = 6;

	private float mpTopEdgeLength = 5;
	private float mpBottomEdgeLength = 8;
	private float mpPointerLength = 20;
	private float mpTopCYOffset = 6;

	private float trianglePointerOffset = 6;
	private float triangleSideLength = 40;

	private float canvasRotateX = 0;
	private float canvasRotateY = 0;

	private float rotateHourPointer = 0;
	private float rotateMinutePointer = 0;
	private float rotateSecondPointer = 0;

	private float scaleTextSize = 13;
	private float scaleTextAngle = 5;
	private float[][] scaleTextDrawCoordinates;

	private float progressRingInitRotateDegree = 270;

	private float canvasMaxRotateDegree = 20;

	private float[] ringDashIntervals = new float[]{3f, 6f};

	private float[] sweepGradientColorPos = new float[]{0f, 300f / 360f, 330f / 360f, 1f};
	private int[] sweepGradientColors = new int[]{Color.TRANSPARENT, 0x80ffffff, 0xddffffff, Color.WHITE};

	private ValueAnimator steadyAnim;
	private ValueAnimator secondAnim;

	private Path pathTriangle;
	private Path pathMinutePointer;
	private Path pathHourPointer;

	private Matrix matrixCanvas = new Matrix();
	private Matrix matrixSweepGradient = new Matrix();

	private Shader sweepGradient;

	private Camera camera = new Camera();

	public ClockView(Context context,long totalTime) {
		super(context);
		this.totalTime = totalTime;
		mc = new MyCountDownTimer(this.totalTime, countDownInterval);
		mc.start();
		init();
	}
	public ClockView(Context context) {
		super(context);
		init();
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		Log.d(TAG, "2");
	}

	@Override
	protected void onAttachedToWindow() {
		Log.d(TAG, "1");
		super.onAttachedToWindow();
		if (DEBUG) {
			Log.d(TAG, "onAttachedToWindow");
		}
//		startNewSecondAnim();

		// register broadcast receiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		getContext().registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (DEBUG) {
			Log.d(TAG, "onDetachedFromWindow");
		}
		getContext().unregisterReceiver(receiver);
		cancelSecondAnimIfNeed();
		Log.d(TAG, "4");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		reset();
		Log.d(TAG, "6");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(colorBg);
		rotateCanvas(canvas);
		drawContent(canvas);
	}

	private void rotateCanvas(Canvas canvas) {
		matrixCanvas.reset();

		camera.save();
		camera.rotateX(canvasRotateX);
		camera.rotateY(canvasRotateY);
		camera.getMatrix(matrixCanvas);
		camera.restore();

		int matrixCenterX = centerX;
		int matrixCenterY = centerY;
		// This moves the center of the view into the upper left corner (0,0)
		// which is necessary because Matrix always uses 0,0, as it's transform point
		matrixCanvas.preTranslate(-matrixCenterX, -matrixCenterY);
		// This happens after the camera rotations are applied, moving the view
		// back to where it belongs, allowing us to rotate around the center or
		// any point we choose
		matrixCanvas.postTranslate(matrixCenterX, matrixCenterY);

		canvas.concat(matrixCanvas);
	}

	private void translateCanvas(Canvas canvas, float x, float y, float z) {
		matrixCanvas.reset();
		camera.save();
		camera.translate(x, y, z);
		camera.getMatrix(matrixCanvas);
		camera.restore();

		int matrixCenterX = centerX;
		int matrixCenterY = centerY;
		matrixCanvas.preTranslate(-matrixCenterX, -matrixCenterY);
		matrixCanvas.postTranslate(matrixCenterX, matrixCenterY);

		canvas.concat(matrixCanvas);
	}

	private void drawContent(Canvas canvas) {
		// Check rotate bound
		if (rotateSecondPointer >= 360f) {
			rotateSecondPointer %= 360f;
		}
		if (rotateMinutePointer >= 360f) {
			rotateMinutePointer %= 360f;
		}
		if (rotateHourPointer >= 360f) {
			rotateHourPointer %= 360f;
		}

		// Rotate ring sweep gradient
		matrixSweepGradient.setRotate(getProgressRingRotateDegree(), centerX, centerY);
		sweepGradient.setLocalMatrix(matrixSweepGradient);
		paintProgressRing.setShader(sweepGradient);

		// Draw scale ring
		canvas.save();
		translateCanvas(canvas, 0f, 0f, zDepthScaleRing);
		canvas.drawArc(boundScaleRing, scaleTextAngle, 90 - 2 * scaleTextAngle, false, paintScaleRing);
		canvas.drawArc(boundScaleRing, 90 + scaleTextAngle, 90 - 2 * scaleTextAngle, false, paintScaleRing);
		canvas.drawArc(boundScaleRing, 180 + scaleTextAngle, 90 - 2 * scaleTextAngle, false, paintScaleRing);
		canvas.drawArc(boundScaleRing, 270 + scaleTextAngle, 90 - 2 * scaleTextAngle, false, paintScaleRing);
		//ZLF 稍稍修改几个数字的位置
		canvas.drawText("0", scaleTextDrawCoordinates[0][0]+9, scaleTextDrawCoordinates[0][1], paintNumber);
		canvas.drawText("30", scaleTextDrawCoordinates[1][0]-6, scaleTextDrawCoordinates[1][1], paintNumber);
		canvas.drawText("45", scaleTextDrawCoordinates[2][0]-6, scaleTextDrawCoordinates[2][1], paintNumber);
		canvas.drawText("15", scaleTextDrawCoordinates[3][0]-6, scaleTextDrawCoordinates[3][1], paintNumber);
		canvas.restore();

		// Draw dash ring and second pointer
		canvas.save();
		translateCanvas(canvas, 0f, 0f, zDepthDashRing);
		canvas.drawArc(boundTimeRing, 0, 359.5f, false, paintBgRing);
		canvas.drawArc(boundTimeRing, 0, 359.5f, false, paintProgressRing);
		canvas.rotate(rotateSecondPointer, centerX, centerY);
		canvas.drawPath(pathTriangle, paintTriangle);
		canvas.restore();

		// Draw hour pointer
		canvas.save();
		translateCanvas(canvas, 0f, 0f, zDepthHourPointer);
		canvas.rotate(rotateHourPointer, centerX, centerY);
		canvas.drawPath(pathHourPointer, paintHourPointer);
		/*canvas.drawCircle(centerX, centerY, radiusHPOuterRing, paintHPOuterRing);
		canvas.drawCircle(centerX, centerY, radiusHPInnerRing, paintHPInnerCircle);*/
		Rect bounds = new Rect();
		//mPaint.getTextBounds(testString, 0, testString.length(), bounds);
		//****************************************************************************************************************
		//这里在中心输入文本

		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(dp2px(3));
		mPaint.setTextSize(dp2px(30));
		mPaint.setColor(Color.WHITE);
		mPaint.setTextAlign(Paint.Align.LEFT);
		Rect bound = new Rect();
		mPaint.getTextBounds(timer, 0, timer.length(), bound);
		Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
		int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		canvas.drawText(timer,(getMeasuredWidth() / 2 - dp2px(70))/* /  - bound.width()  / 2 */, baseline, mPaint);
		//****************************************************************************************************************
		canvas.restore();
		//ZLF 这里即下面的drawCircle代码主要设置了
		//消除中间的圆环的效果

		// Draw minute pointer
		canvas.save();
		translateCanvas(canvas, 0f, 0f, zDepthMinutePointer);
		canvas.rotate(rotateMinutePointer, centerX, centerY);
		canvas.drawPath(pathMinutePointer, paintMinutePointer);
	/*	canvas.drawCircle(centerX, centerY, radiusMPOuterRing, paintMPOuterRing);
		canvas.drawCircle(centerX, centerY, radiusMPInnerRing, paintMPInnerCircle);*/
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		int action = event.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				cancelSteadyAnimIfNeed();
				rotateCanvasWhenMove(x, y);
				return true;
			}
			case MotionEvent.ACTION_MOVE: {
				rotateCanvasWhenMove(x, y);
				return true;
			}
			case MotionEvent.ACTION_UP: {
				cancelSteadyAnimIfNeed();
				startNewSteadyAnim();
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	private void rotateCanvasWhenMove(float x, float y) {
		float dx = x - centerX;
		float dy = y - centerY;

		float percentX = dx / (getWidth() / 2);
		float percentY = dy / (getHeight() / 2);

		if (percentX > 1f) {
			percentX = 1f;
		} else if (percentX < -1f) {
			percentX = -1f;
		}
		if (percentY > 1f) {
			percentY = 1f;
		} else if (percentY < -1f) {
			percentY = -1f;
		}

		canvasRotateY = canvasMaxRotateDegree * percentX;
		canvasRotateX = -(canvasMaxRotateDegree * percentY);
	}

	private void init() {
		initValues();
		startNewSecondAnim();
	}

	private void initValues() {
		// Colors
		//colorBg = 0xff237ead;
		colorBg = 0xFF876BB9;
		colorBgRing = 0x80ffffff;
		colorMPOuterRing = Color.WHITE;
		colorHPOuterRing = 0xccdddddd;
		colorMinutePointer = Color.WHITE;
		colorHourPointer = 0xccdddddd;
		colorTriangle = 0xffdddddd;
		colorScaleRing = 0xccdddddd;

		// Angle
		progressRingInitRotateDegree = 270;
		canvasMaxRotateDegree = 20;
		scaleTextAngle = 5;

		// Radius
		radiusMPOuterRing = dp2px(8);
		radiusMPInnerRing = dp2px(4);
		radiusHPOuterRing = dp2px(8);
		radiusHPInnerRing = dp2px(4);

		// Stroke width
		strokeWidthRing = dp2px(25);
		strokeWidthScaleRing = dp2px(2);

		// zDepth
		zDepthScaleRing = dp2px(130);
		zDepthDashRing = dp2px(100);
		zDepthHourPointer = dp2px(50);
		zDepthMinutePointer = 0;

		// Text properties on scale ring
		scaleTextSize = sp2px(20);

		//ZLF
		int width=0;
		// Hour pointer
		hpTopEdgeLength = width;//dp2px(3);
		hpBottomEdgeLength =  width;//dp2px(5);
		hpPointerLength =  width;//radius * 3 / 5;
		hpTopCYOffset =  width;//dp2px(3);

		// Minute pointer
		mpTopEdgeLength =  width;//dp2px(2);
		mpBottomEdgeLength =  width;//dp2px(3);
		mpPointerLength = width;// radius * 4 / 5;
		mpTopCYOffset =  width;//dp2px(3);

		// Second pointer
		trianglePointerOffset = dp2px(6);
		triangleSideLength = dp2px(20);

		// Ring dash intervals
		ringDashIntervals = new float[]{dp2px(1), dp2px(3)};

		// Sweep gradient
		sweepGradientColorPos = new float[]{0f, 300f / 360f, 330f / 360f, 1f};
		sweepGradientColors = new int[]{Color.TRANSPARENT, 0x80ffffff, 0xddffffff, Color.WHITE};
	}

	private void reset() {
		initBound();
		initBgRing();
		initProgressRing();
		initScaleRing();
		initTriangleSecondPointer();//ZLF
		initMinutePointer();
		initHourPointer();
		updateTimePointer();
	}

	private void initBound() {
		radius = getWidth() * 3 / 7;
		radiusScaleRing = radius * 4 / 3;

		centerX = getWidth() / 2;
		centerY = getHeight() / 2;

		boundTimeRing = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		boundScaleRing = new RectF(centerX - radiusScaleRing, centerY - radiusScaleRing, centerX + radiusScaleRing, centerY + radiusScaleRing);

		hpPointerLength = radius * 3 / 5;
		mpPointerLength = radius * 3 / 5;
	}

	private void initScaleRing() {
		// Scale ring paint
		paintScaleRing = new Paint();
		paintScaleRing.setAntiAlias(true);
		paintScaleRing.setStyle(Paint.Style.STROKE);
		paintScaleRing.setStrokeWidth(strokeWidthScaleRing);
		paintScaleRing.setColor(colorScaleRing);

		// Number text paint
		paintNumber = new Paint();
		paintNumber.setAntiAlias(true);
		paintNumber.setStyle(Paint.Style.FILL);
		paintNumber.setColor(colorScaleRing);
		paintNumber.setTextSize(scaleTextSize);

		// Parse text baseline
		float scaleTextWidthTwo = paintNumber.measureText("12");
		float scaleTextWidthOne = paintNumber.measureText("6");
		float scaleTextHeight = paintNumber.measureText("12");

		RectF topTextBound = new RectF();
		topTextBound.left = centerX - scaleTextWidthTwo / 2;
		topTextBound.top = centerY - radiusScaleRing - scaleTextHeight / 2;
		topTextBound.right = centerX + scaleTextWidthTwo / 2;
		topTextBound.bottom = centerY - radiusScaleRing + scaleTextHeight / 2;

		RectF bottomTextBound = new RectF();
		bottomTextBound.left = centerX - scaleTextWidthOne / 2;
		bottomTextBound.top = centerY + radiusScaleRing - scaleTextHeight / 2;
		bottomTextBound.right = centerX + scaleTextWidthOne / 2;
		bottomTextBound.bottom = centerY + radiusScaleRing + scaleTextHeight / 2;

		RectF leftTextBound = new RectF();
		leftTextBound.left = centerX - radiusScaleRing - scaleTextWidthOne / 2;
		leftTextBound.top = centerY - scaleTextHeight / 2;
		leftTextBound.right = centerX - radiusScaleRing + scaleTextWidthOne / 2;
		leftTextBound.bottom = centerY + scaleTextHeight / 2;

		RectF rightTextBound = new RectF();
		rightTextBound.left = centerX + radiusScaleRing - scaleTextWidthOne / 2;
		rightTextBound.top = leftTextBound.top;
		rightTextBound.right = centerX + radiusScaleRing + scaleTextWidthOne / 2;
		rightTextBound.bottom = leftTextBound.bottom;

		Paint.FontMetrics fm = paintNumber.getFontMetrics();

		scaleTextDrawCoordinates = new float[4][2];
		scaleTextDrawCoordinates[0][0] = topTextBound.left;
		scaleTextDrawCoordinates[0][1] = topTextBound.top + (topTextBound.bottom - topTextBound.top) / 2 - (fm.bottom - fm.top) / 2 - fm.top;
		scaleTextDrawCoordinates[1][0] = bottomTextBound.left;
		scaleTextDrawCoordinates[1][1] = bottomTextBound.top + (bottomTextBound.bottom - bottomTextBound.top) / 2 - (fm.bottom - fm.top) / 2 - fm
				.top;
		scaleTextDrawCoordinates[2][0] = leftTextBound.left;
		scaleTextDrawCoordinates[2][1] = leftTextBound.top + (leftTextBound.bottom - leftTextBound.top) / 2 - (fm.bottom - fm.top) / 2 - fm.top;
		scaleTextDrawCoordinates[3][0] = rightTextBound.left;
		scaleTextDrawCoordinates[3][1] = rightTextBound.top + (rightTextBound.bottom - rightTextBound.top) / 2 - (fm.bottom - fm.top) / 2 - fm.top;
	}

	private void initHourPointer() {
		// Center Ring
		paintHPOuterRing = new Paint();
		paintHPOuterRing.setStyle(Paint.Style.FILL);
		paintHPOuterRing.setAntiAlias(true);
		paintHPOuterRing.setColor(colorHPOuterRing);

		paintHPInnerCircle = new Paint(paintHPOuterRing);
		paintHPInnerCircle.setColor(colorBg);

		// Minute Pointer
		paintHourPointer = new Paint();
		paintHourPointer.setAntiAlias(true);
		paintHourPointer.setStyle(Paint.Style.FILL);
		paintHourPointer.setColor(colorHourPointer);

		float topX1 = centerX - hpTopEdgeLength / 2;
		float topY1 = centerY - hpPointerLength + strokeWidthRing / 2;
		float topX2 = centerX + hpTopEdgeLength / 2;
		float topY2 = topY1;
		float topCX1 = centerX;
		float topCY1 = topY1 - hpTopCYOffset;

		float bottomX1 = centerX - hpBottomEdgeLength / 2;
		float bottomY1 = centerY;
		float bottomX2 = centerX + hpBottomEdgeLength / 2;
		float bottomY2 = bottomY1;

		pathHourPointer = new Path();
		pathHourPointer.moveTo(bottomX1, bottomY1);
		pathHourPointer.lineTo(bottomX2, bottomY2);
		pathHourPointer.lineTo(topX2, topY2);
		pathHourPointer.quadTo(topCX1, topCY1, topX1, topY1);
		pathHourPointer.close();
	}

	private void initMinutePointer() {
		// Center Ring
		paintMPOuterRing = new Paint();
		paintMPOuterRing.setStyle(Paint.Style.FILL);
		paintMPOuterRing.setAntiAlias(true);
		paintMPOuterRing.setColor(colorMPOuterRing);

		paintMPInnerCircle = new Paint(paintMPOuterRing);
		paintMPInnerCircle.setColor(colorBg);

		// Minute Pointer
		paintMinutePointer = new Paint();
		paintMinutePointer.setStyle(Paint.Style.FILL);
		paintMinutePointer.setAntiAlias(true);
		paintMinutePointer.setColor(colorMinutePointer);

		float topX1 = centerX - mpTopEdgeLength / 2;
		float topY1 = centerY - mpPointerLength + strokeWidthRing / 2;
		float topX2 = centerX + mpTopEdgeLength / 2;
		float topY2 = topY1;
		float topCX1 = centerX;
		float topCY1 = topY1 - mpTopCYOffset;

		float bottomX1 = centerX - mpBottomEdgeLength / 2;
		float bottomY1 = centerY;
		float bottomX2 = centerX + mpBottomEdgeLength / 2;
		float bottomY2 = bottomY1;

		pathMinutePointer = new Path();
		pathMinutePointer.moveTo(bottomX1, bottomY1);
		pathMinutePointer.lineTo(bottomX2, bottomY2);
		pathMinutePointer.lineTo(topX2, topY2);
		pathMinutePointer.quadTo(topCX1, topCY1, topX1, topY1);
		pathMinutePointer.close();
	}

	private void initTriangleSecondPointer() {
		paintTriangle = new Paint();
		paintTriangle.setColor(colorTriangle);
		paintTriangle.setStyle(Paint.Style.FILL);
		paintTriangle.setAntiAlias(true);

		float height = (float) (1.0 * Math.sqrt(3f) / 2 * triangleSideLength);

		float point1x = centerX;
		float point1y = centerY - radius + strokeWidthRing / 2 + trianglePointerOffset;
		float point2x = point1x - triangleSideLength / 2;
		float point2y = point1y + height;
		float point3x = point1x + triangleSideLength / 2;
		float point3y = point1y + height;

		pathTriangle = new Path();
		pathTriangle.moveTo(point1x, point1y);
		pathTriangle.lineTo(point2x, point2y);
		pathTriangle.lineTo(point3x, point3y);
		pathTriangle.close();
	}

	private void initBgRing() {
		paintBgRing = new Paint();
		paintBgRing.setStyle(Paint.Style.STROKE);
		paintBgRing.setStrokeWidth(strokeWidthRing);
		paintBgRing.setAntiAlias(true);
		paintBgRing.setPathEffect(new DashPathEffect(ringDashIntervals, 0));
		paintBgRing.setColor(colorBgRing);
	}

	private void initProgressRing() {
		paintProgressRing = new Paint(paintBgRing);
		paintProgressRing.setColor(Color.WHITE);
		sweepGradient = new SweepGradient(centerX, centerY, sweepGradientColors, sweepGradientColorPos);
		paintProgressRing.setShader(sweepGradient);
	}

	private void startNewSecondAnim() {
		if (DEBUG) {
			Log.d(TAG, "startNewSecondAnim");
		}
		cancelSecondAnimIfNeed();
		updateTimePointer();

		final float startDegree = 0f;
		final float endDegree = 360f;
		secondAnim = ValueAnimator.ofFloat(startDegree, endDegree);
		// FIXME 不知为何某些机型动画实际执行时间是duration的一半
		// 在构造函数里启动动画就正常，在其他方法（如onAttachedToWindow或onSizeChanged）里调用本方法就不正常
		// 出问题的机型：小米4、小米1S
		secondAnim.setDuration(60 * 1000);
		secondAnim.setInterpolator(new LinearInterpolator());
		secondAnim.setRepeatMode(ValueAnimator.RESTART);
		secondAnim.setRepeatCount(ValueAnimator.INFINITE);
		secondAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			private float lastDrawValue = 0;
			private float drawInterval = 0.1f;

			private float lastUpdatePointerValue = 0;
			private float updatePointerInterval = 360 / 60 * 5;

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float newValue = (float) animation.getAnimatedValue();

				// Check if it is the time to update pointer position
				float increasedPointerValue = newValue - lastUpdatePointerValue;
				if (increasedPointerValue < 0) {
					increasedPointerValue = endDegree + increasedPointerValue;
				}
				if (increasedPointerValue >= updatePointerInterval) {
					lastUpdatePointerValue = newValue;
					updateTimePointer();
				}

				// Check if it is the time to invalidate
				float increasedDrawValue = newValue - lastDrawValue;
				if (increasedDrawValue < 0) {
					increasedDrawValue = endDegree + increasedDrawValue;
				}
				if (increasedDrawValue >= drawInterval) {
					lastDrawValue = newValue;
					rotateSecondPointer += increasedDrawValue;
					invalidate();
//					if (DEBUG) {
//						Log.d(TAG, String.format("newValue:%s , currentPlayTime:%s", newValue, animation.getCurrentPlayTime()));
//					}
				}
			}
		});
		secondAnim.start();
	}

	private void cancelSecondAnimIfNeed() {
		if (secondAnim != null && (secondAnim.isStarted() || secondAnim.isRunning())) {
			secondAnim.cancel();
			if (DEBUG) {
				Log.d(TAG, "cancelSecondAnimIfNeed");
			}
		}
	}

	private void startNewSteadyAnim() {
		final String propertyNameRotateX = "canvasRotateX";
		final String propertyNameRotateY = "canvasRotateY";

		PropertyValuesHolder holderRotateX = PropertyValuesHolder.ofFloat(propertyNameRotateX, canvasRotateX, 0);
		PropertyValuesHolder holderRotateY = PropertyValuesHolder.ofFloat(propertyNameRotateY, canvasRotateY, 0);
		steadyAnim = ValueAnimator.ofPropertyValuesHolder(holderRotateX, holderRotateY);
		steadyAnim.setDuration(1000);
		steadyAnim.setInterpolator(new BounceInterpolator());
		steadyAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				canvasRotateX = (float) animation.getAnimatedValue(propertyNameRotateX);
				canvasRotateY = (float) animation.getAnimatedValue(propertyNameRotateY);
			}
		});
		steadyAnim.start();
	}

	private void cancelSteadyAnimIfNeed() {
		if (steadyAnim != null && (steadyAnim.isStarted() || steadyAnim.isRunning())) {
			steadyAnim.cancel();
		}
	}

	private void updateTimePointer() {
		//ZLF
		/*long hour =  millisUntilFinished / 1000 / 60 / 60;
		long minute = (millisUntilFinished - hour * 1000 * 60 * 60) / 1000 / 60;
		long second = (millisUntilFinished - hour * 1000 * 60 * 60 - minute * 60 * 1000) / 1000;*/
		int second = Calendar.getInstance().get(Calendar.SECOND);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		int hour = Calendar.getInstance().get(Calendar.HOUR);
		float percentSecond = (float) (1.0 * second / 60);
		float percentMinute = (float) (1.0 * (minute * 60 + second) / (60 * 60));
		float percentHour = (float) (1.0 * (hour * 60 * 60 + minute * 60 + second) / (60 * 60 * 12));
		rotateSecondPointer = 360 * percentSecond;
		//rotateMinutePointer = 360 * percentMinute;
		//rotateHourPointer = 360 * percentHour;
	}

	private float getProgressRingRotateDegree() {
		float degree = (rotateSecondPointer + progressRingInitRotateDegree) % 360;
		return degree;
	}

	public float dp2px(float dpValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
	}

	public float sp2px(float spValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			if (TextUtils.isEmpty(action)) {
				return;
			}
			if (DEBUG) {
				Log.d(TAG, String.format("action -> %s", action));
			}
			if (action.equals(Intent.ACTION_TIME_TICK)) {
				updateTimePointer();
			} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				startNewSecondAnim();
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				cancelSecondAnimIfNeed();
			}
		}
	};
	//ZLF 新增的倒计时的类
	class MyCountDownTimer extends CountDownTimer {
		/**
		 *
		 * @param millisInFuture
		 *      表示以毫秒为单位 倒计时的总数
		 *
		 *      例如 millisInFuture=1000 表示1秒
		 *
		 * @param countDownInterval
		 *      表示 间隔 多少微秒 调用一次 onTick 方法
		 *
		 *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
		 *
		 */
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			//这里实际上是个后台任务 所以即使结束时活动不在前台 也会发生响应
			timer = "00:00:00:0";
			Intent intent = new Intent(getContext(),XiuYiXiuActivity.class);
			intent.putExtra("stopCountDownTimer",true);
			getContext().startActivity(intent);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long hour =  millisUntilFinished / 1000 / 60 / 60;
			long minute = (millisUntilFinished - hour * 1000 * 60 * 60) / 1000 / 60;
			long second = (millisUntilFinished - hour * 1000 * 60 * 60 - minute * 60 * 1000) / 1000;
			long tenOfASencond = (millisUntilFinished - hour * 1000 * 60 * 60 - minute * 60 * 1000 - second * 1000) / 100;
			timer = (hour>9 ? hour : "0"+hour) + ":" +
					(minute>9? minute : "0" + minute) + ":" +
					(second>9? second : "0" + second )+ ":" +
					tenOfASencond;

			ClockView.this.millisUntilFinished = millisUntilFinished;
			//tv.setText("倒计时(" + millisUntilFinished / 1000 + ")...");
		}
	}

	class MyTherad implements Runnable{
		@Override
		public void run(){
			//调用开始倒计时
			/*mc = new MyCountDownTimer(totalTime, countDownInterval);
			mc.start();*/
		}
	}

}