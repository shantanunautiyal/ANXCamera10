package com.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import com.android.camera.Util;
import com.android.camera.log.Log;
import com.android.camera.protocol.ModeCoordinatorImpl;
import com.android.camera.protocol.ModeProtocol;
import com.android.camera.ui.BaseHorizontalZoomView;

public class HorizontalSlideView extends BaseHorizontalZoomView {
    private static final String TAG = "HSlideView";
    private BaseHorizontalZoomView.HorizontalDrawAdapter mDrawAdapter;
    private volatile boolean mEnableCallBack = true;
    private GestureDetector mGestureDetector;
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onDown(MotionEvent motionEvent) {
            HorizontalSlideView.this.mScroller.forceFinished(true);
            boolean unused = HorizontalSlideView.this.mNeedJustify = false;
            return true;
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
            HorizontalSlideView.this.flingX(-((int) f2));
            return true;
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
            boolean unused = HorizontalSlideView.this.mIsScrollingPerformed = true;
            HorizontalSlideView horizontalSlideView = HorizontalSlideView.this;
            if (horizontalSlideView.mPositionX == horizontalSlideView.mMinX && f2 < 0.0f) {
                return false;
            }
            HorizontalSlideView horizontalSlideView2 = HorizontalSlideView.this;
            if (horizontalSlideView2.mPositionX == horizontalSlideView2.mMaxX && f2 > 0.0f) {
                return false;
            }
            HorizontalSlideView horizontalSlideView3 = HorizontalSlideView.this;
            horizontalSlideView3.setPositionX((int) (((float) horizontalSlideView3.mPositionX) + f2));
            return true;
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            HorizontalSlideView.this.scroll((int) (motionEvent.getX() - HorizontalSlideView.this.mOriginX));
            if (HorizontalSlideView.this.mOnTabListener == null) {
                return true;
            }
            HorizontalSlideView.this.mOnTabListener.onTab(HorizontalSlideView.this);
            return true;
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsScrollingPerformed;
    private boolean mJustifyEnabled = true;
    /* access modifiers changed from: private */
    public int mMaxX = 0;
    /* access modifiers changed from: private */
    public int mMinX = 0;
    /* access modifiers changed from: private */
    public boolean mNeedJustify;
    private OnItemSelectListener mOnItemSelectListener;
    private BaseHorizontalZoomView.OnPositionSelectListener mOnPositionSelectListener;
    /* access modifiers changed from: private */
    public OnTabListener mOnTabListener;
    protected float mOriginX;
    protected int mPositionX = 0;
    protected Scroller mScroller;
    protected int mSelectedItemIndex;
    private boolean mTouchDown;

    public interface OnItemSelectListener {
        void onItemSelect(HorizontalSlideView horizontalSlideView, int i);
    }

    public interface OnTabListener {
        void onTab(View view);
    }

    public HorizontalSlideView(Context context) {
        super(context);
        init(context);
    }

    public HorizontalSlideView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public HorizontalSlideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private float calculateLength(int i, int i2) {
        float f2;
        int i3 = i;
        int i4 = i2;
        float f3 = this.mOriginX;
        float f4 = 0.0f;
        if (i3 == i4) {
            return 0.0f;
        }
        if (this.mDrawAdapter != null) {
            boolean isLayoutRTL = Util.isLayoutRTL(getContext());
            int count = isLayoutRTL ? this.mDrawAdapter.getCount() - 1 : 0;
            int count2 = isLayoutRTL ? 0 : this.mDrawAdapter.getCount() - 1;
            int i5 = isLayoutRTL ? -1 : 1;
            f2 = 0.0f;
            float f5 = 0.0f;
            float f6 = 0.0f;
            for (int i6 = 0; i6 < this.mDrawAdapter.getCount(); i6++) {
                int i7 = (i6 * i5) + count;
                boolean z = i7 == count;
                boolean z2 = i7 == count2;
                float itemWidth = getItemWidth(i7);
                float f7 = itemWidth / 2.0f;
                if (z) {
                    f6 = this.mOriginX - f7;
                }
                float f8 = z ? f3 : f6 + f7;
                f6 += z2 ? 0.0f : itemWidth + getItemGap(i6);
                if (i7 == i3) {
                    f2 = f8;
                } else if (i7 == i4) {
                    f5 = f8;
                }
            }
            f4 = f5;
        } else {
            f2 = 0.0f;
        }
        return Math.abs(f4 - f2);
    }

    /* access modifiers changed from: private */
    public void flingX(int i) {
        this.mIsScrollingPerformed = true;
        this.mScroller.fling(this.mPositionX, 0, i, 0, this.mMinX, this.mMaxX, 0, 0);
        invalidate();
    }

    private float getItemGap(int i) {
        return this.mDrawAdapter.measureGap(i);
    }

    private float getItemWidth(int i) {
        return this.mDrawAdapter.measureWidth(i);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0011, code lost:
        if (r1 > r2) goto L_0x000a;
     */
    public void scroll(int i) {
        if (i != 0) {
            int i2 = this.mPositionX;
            int i3 = i2 + i;
            int i4 = this.mMinX;
            if (i3 >= i4) {
                int i5 = i2 + i;
                i4 = this.mMaxX;
            }
            i = i4 - i2;
            this.mIsScrollingPerformed = true;
            this.mScroller.startScroll(this.mPositionX, 0, i, 0);
            invalidate();
        }
    }

    private void select(int i) {
        ModeProtocol.CameraAction cameraAction = (ModeProtocol.CameraAction) ModeCoordinatorImpl.getInstance().getAttachProtocol(161);
        if (cameraAction == null || !cameraAction.isDoingAction()) {
            if (this.mSelectedItemIndex != i || !isScrolling()) {
                this.mSelectedItemIndex = i;
                OnItemSelectListener onItemSelectListener = this.mOnItemSelectListener;
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(this, this.mSelectedItemIndex);
                }
            }
            if (this.mOnPositionSelectListener != null && this.mEnableCallBack) {
                float f2 = ((float) this.mPositionX) / ((float) (this.mMaxX - this.mMinX));
                BaseHorizontalZoomView.OnPositionSelectListener onPositionSelectListener = this.mOnPositionSelectListener;
                int i2 = this.mSelectedItemIndex;
                if (Util.isLayoutRTL(getContext())) {
                    f2 = 1.0f - f2;
                }
                onPositionSelectListener.onPositionSelect(this, i2, f2);
            }
            this.mEnableCallBack = true;
            return;
        }
        Log.d(TAG, "is doing action, ignore select.");
    }

    /* access modifiers changed from: private */
    public void setPositionX(int i) {
        this.mPositionX = i;
        int i2 = this.mPositionX;
        int i3 = this.mMinX;
        if (i2 < i3) {
            this.mPositionX = i3;
        } else {
            int i4 = this.mMaxX;
            if (i2 > i4) {
                this.mPositionX = i4;
            }
        }
        invalidate();
    }

    public boolean canPositionScroll() {
        return true;
    }

    public int getSelectedItemIndex() {
        return this.mSelectedItemIndex;
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        this.mGestureDetector = new GestureDetector(context, this.mGestureListener);
        this.mGestureDetector.setIsLongpressEnabled(false);
        this.mScroller = new Scroller(context);
    }

    public boolean isScrolling() {
        return this.mIsScrollingPerformed;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f2;
        Canvas canvas2 = canvas;
        if (this.mScroller.computeScrollOffset()) {
            this.mPositionX = this.mScroller.getCurrX();
            invalidate();
        }
        boolean z = false;
        if (this.mScroller.isFinished() && !this.mTouchDown) {
            this.mIsScrollingPerformed = false;
        }
        float f3 = this.mOriginX - ((float) this.mPositionX);
        float f4 = 2.0f;
        float height = ((float) getHeight()) / 2.0f;
        float f5 = 0.0f;
        if (this.mDrawAdapter != null) {
            boolean isLayoutRTL = Util.isLayoutRTL(getContext());
            int count = isLayoutRTL ? this.mDrawAdapter.getCount() - 1 : 0;
            int count2 = isLayoutRTL ? 0 : this.mDrawAdapter.getCount() - 1;
            int i = isLayoutRTL ? -1 : 1;
            float f6 = f3;
            int i2 = 0;
            float f7 = 0.0f;
            float f8 = 0.0f;
            float f9 = 0.0f;
            boolean z2 = true;
            while (i2 < this.mDrawAdapter.getCount()) {
                int i3 = (i2 * i) + count;
                boolean z3 = i3 == count ? true : z;
                boolean z4 = i3 == count2 ? true : z;
                float itemWidth = getItemWidth(i3);
                float f10 = itemWidth / f4;
                if (z3) {
                    f7 = 0.0f;
                }
                float itemGap = z4 ? 0.0f : getItemGap(i3) / f4;
                if (z3) {
                    f8 = f3 - f10;
                }
                f6 = z3 ? f3 : f8 + f10;
                if (z2) {
                    float f11 = f6 - this.mOriginX;
                    if ((f11 <= 0.0f && (-f11) <= f10 + itemGap) || (f11 > 0.0f && f11 <= f10 + f7)) {
                        select(i3);
                        f9 = f11;
                        z2 = false;
                    }
                }
                f8 += z4 ? 0.0f : itemWidth + getItemGap(i3);
                i2++;
                f7 = itemGap;
                z = false;
                f4 = 2.0f;
            }
            this.mMaxX = (int) (f6 - f3);
            int i4 = 0;
            while (i4 < this.mDrawAdapter.getCount()) {
                int i5 = (i4 * i) + count;
                boolean z5 = i5 == count;
                boolean z6 = i5 == count2;
                float itemWidth2 = getItemWidth(i5);
                float f12 = itemWidth2 / 2.0f;
                if (z5) {
                    f8 = f3 - f12;
                }
                float f13 = z5 ? f3 : f8 + f12;
                float f14 = f8 + itemWidth2;
                if (f14 >= f5 && f8 <= ((float) getWidth())) {
                    canvas.save();
                    if (this.mDrawAdapter.getAlign(i5) == Paint.Align.LEFT) {
                        canvas2.translate(f8, height);
                    } else if (this.mDrawAdapter.getAlign(i5) == Paint.Align.CENTER) {
                        canvas2.translate(f13, height);
                    } else {
                        canvas2.translate(f14, height);
                    }
                    this.mDrawAdapter.draw(i5, canvas2, this.mSelectedItemIndex == i5);
                    canvas.restore();
                }
                f8 += z6 ? 0.0f : itemWidth2 + getItemGap(i5);
                i4++;
                f5 = 0.0f;
            }
            f2 = f9;
        } else {
            f2 = 0.0f;
        }
        if (this.mJustifyEnabled && this.mNeedJustify && this.mScroller.isFinished()) {
            this.mNeedJustify = false;
            scroll((int) f2);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mOriginX = ((float) i) / 2.0f;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = this.mGestureDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mTouchDown = true;
            if (!canPositionScroll()) {
                Log.d(TAG, "cannot scroll, do not process the down event.");
                return false;
            }
        } else if (action == 1 || action == 3) {
            this.mTouchDown = false;
            this.mNeedJustify = true;
            invalidate();
        }
        return onTouchEvent;
    }

    public void setDrawAdapter(BaseHorizontalZoomView.HorizontalDrawAdapter horizontalDrawAdapter) {
        this.mDrawAdapter = horizontalDrawAdapter;
        this.mNeedJustify = false;
        this.mSelectedItemIndex = 0;
        this.mScroller.forceFinished(true);
        BaseHorizontalZoomView.HorizontalDrawAdapter horizontalDrawAdapter2 = this.mDrawAdapter;
        if (horizontalDrawAdapter2 != null) {
            this.mMaxX = this.mMinX + ((int) calculateLength(0, horizontalDrawAdapter2.getCount() - 1));
        }
        if (Util.isLayoutRTL(getContext())) {
            this.mPositionX = this.mMaxX;
        } else {
            this.mPositionX = this.mMinX;
        }
        invalidate();
    }

    public void setJustifyEnabled(boolean z) {
        this.mJustifyEnabled = z;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.mOnItemSelectListener = onItemSelectListener;
    }

    public void setOnPositionSelectListener(BaseHorizontalZoomView.OnPositionSelectListener onPositionSelectListener) {
        this.mOnPositionSelectListener = onPositionSelectListener;
    }

    public void setOnTabListener(OnTabListener onTabListener) {
        this.mOnTabListener = onTabListener;
    }

    public void setSelection(float f2, boolean z) {
        if (Util.isLayoutRTL(getContext()) && this.mDrawAdapter != null) {
            f2 = 1.0f - f2;
        }
        this.mNeedJustify = false;
        this.mScroller.forceFinished(true);
        this.mPositionX = (int) (f2 * ((float) (this.mMaxX - this.mMinX)));
        this.mEnableCallBack = !z;
        invalidate();
    }

    public void setSelection(int i) {
        if (this.mSelectedItemIndex != i) {
            this.mNeedJustify = false;
            this.mScroller.forceFinished(true);
            BaseHorizontalZoomView.HorizontalDrawAdapter horizontalDrawAdapter = this.mDrawAdapter;
            if (horizontalDrawAdapter != null) {
                if (i >= horizontalDrawAdapter.getCount()) {
                    i = this.mDrawAdapter.getCount() - 1;
                }
                if (Util.isLayoutRTL(getContext())) {
                    this.mPositionX = this.mMaxX - ((int) calculateLength(0, i));
                } else {
                    this.mPositionX = this.mMinX + ((int) calculateLength(0, i));
                }
            }
            select(i);
            invalidate();
        }
    }

    public void setSelectionUpdateUI(int i) {
        if (this.mSelectedItemIndex != i) {
            this.mNeedJustify = false;
            this.mScroller.forceFinished(true);
            this.mEnableCallBack = false;
            BaseHorizontalZoomView.HorizontalDrawAdapter horizontalDrawAdapter = this.mDrawAdapter;
            if (horizontalDrawAdapter != null) {
                if (i >= horizontalDrawAdapter.getCount()) {
                    i = this.mDrawAdapter.getCount() - 1;
                }
                if (Util.isLayoutRTL(getContext())) {
                    this.mPositionX = this.mMaxX - ((int) calculateLength(0, i));
                } else {
                    this.mPositionX = this.mMinX + ((int) calculateLength(0, i));
                }
            }
            invalidate();
        }
    }

    public void stopScroll() {
        this.mNeedJustify = true;
        this.mScroller.forceFinished(true);
        invalidate();
    }
}
