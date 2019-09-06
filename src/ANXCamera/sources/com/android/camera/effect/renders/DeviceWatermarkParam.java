package com.android.camera.effect.renders;

public class DeviceWatermarkParam {
    private boolean mIsDualWatermarkEnable;
    private boolean mIsFrontWatermarkEnable;
    private boolean mIsUltraMPWatermarkEnable;
    private float mPaddingX;
    private float mPaddingY;
    private String mPath;
    private float mSize;

    public DeviceWatermarkParam(boolean z, boolean z2, boolean z3, String str, float f2, float f3, float f4) {
        this.mIsDualWatermarkEnable = z;
        this.mIsFrontWatermarkEnable = z2;
        this.mIsUltraMPWatermarkEnable = z3;
        this.mPath = str;
        this.mSize = f2;
        this.mPaddingX = f3;
        this.mPaddingY = f4;
    }

    public float getPaddingX() {
        return this.mPaddingX;
    }

    public float getPaddingY() {
        return this.mPaddingY;
    }

    public String getPath() {
        return this.mPath;
    }

    public float getSize() {
        return this.mSize;
    }

    public boolean isDualWatermarkEnable() {
        return this.mIsDualWatermarkEnable;
    }

    public boolean isFrontWatermarkEnable() {
        return this.mIsFrontWatermarkEnable;
    }

    public boolean isUltraWatermarkEnable() {
        return this.mIsUltraMPWatermarkEnable;
    }
}
