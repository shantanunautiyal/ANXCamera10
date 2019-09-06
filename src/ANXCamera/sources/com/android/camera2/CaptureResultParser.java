package com.android.camera2;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureResult;
import com.android.camera.data.DataRepository;
import com.android.camera.log.Log;
import com.android.camera2.vendortag.CaptureResultVendorTags;
import com.android.camera2.vendortag.VendorTagHelper;
import com.android.camera2.vendortag.struct.AECFrameControl;
import com.android.camera2.vendortag.struct.AFFrameControl;
import com.android.camera2.vendortag.struct.AWBFrameControl;

@TargetApi(21)
public class CaptureResultParser {
    private static final float AECGAIN_THRESHOLD = 2.0f;
    private static final String TAG = "CaptureResultParser";

    public static AECFrameControl getAECFrameControl(CaptureResult captureResult) {
        return (AECFrameControl) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AEC_FRAME_CONTROL);
    }

    public static AFFrameControl getAFFrameControl(CaptureResult captureResult) {
        return (AFFrameControl) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AF_FRAME_CONTROL);
    }

    public static AWBFrameControl getAWBFrameControl(CaptureResult captureResult) {
        return (AWBFrameControl) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AWB_FRAME_CONTROL);
    }

    public static float getAecLux(CaptureResult captureResult) {
        Float f2 = (Float) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AEC_LUX);
        if (f2 == null) {
            return 0.0f;
        }
        return f2.floatValue();
    }

    public static int getAsdDetectedModes(CaptureResult captureResult) {
        Integer num = (Integer) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AI_SCENE_DETECTED);
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public static int getBeautyBodySlimCountResult(CaptureResult captureResult) {
        Integer num = (Integer) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.BEAUTY_BODY_SLIM_COUNT);
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public static boolean getFastZoomResult(CaptureResult captureResult) {
        Byte b2 = (Byte) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.FAST_ZOOM_RESULT);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("FAST_ZOOM_RESULT = ");
        sb.append(b2);
        Log.d(str, sb.toString());
        return b2 != null && b2.byteValue() == 1;
    }

    public static byte[] getHdrCheckerValues(CaptureResult captureResult) {
        return (byte[]) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.HDR_CHECKTER_EV_VALUES);
    }

    public static int getHdrDetectedScene(CaptureResult captureResult) {
        Byte b2 = (Byte) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AI_HDR_DETECTED);
        if (b2 != null) {
            return b2.byteValue();
        }
        return 0;
    }

    public static byte[] getSatDbgInfo(CaptureResult captureResult) {
        return (byte[]) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.SAT_DBG_INFO);
    }

    public static int getSatMasterCameraId(CaptureResult captureResult) {
        Integer num;
        if (captureResult != null) {
            num = (Integer) VendorTagHelper.getValue(captureResult, CaptureResultVendorTags.SAT_MATER_CAMERA_ID);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getSatMasterCameraId: ");
            sb.append(num);
            Log.d(str, sb.toString());
        } else {
            num = null;
        }
        if (num == null) {
            Log.w(TAG, "getSatMasterCameraId: not found");
            num = Integer.valueOf(2);
        }
        return num.intValue();
    }

    public static int getUltraWideDetectedResult(CaptureResult captureResult) {
        Integer num = (Integer) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.ULTRA_WIDE_RECOMMENDED_RESULT);
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public static boolean isASDEnable(CaptureResult captureResult) {
        Byte b2 = (Byte) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AI_SCENE_ENABLE);
        if (b2 == null) {
            return false;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("isASDEnable: ");
        sb.append(b2);
        Log.d(str, sb.toString());
        return b2.byteValue() == 1;
    }

    public static boolean isHdrMotionDetected(CaptureResult captureResult) {
        Byte b2 = (Byte) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.HDR_MOTION_DETECTED);
        return (b2 == null || b2.byteValue() == 0) ? false : true;
    }

    public static boolean isLensDirtyDetected(CaptureResult captureResult) {
        Integer num = (Integer) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.LENS_DIRTY_DETECTED);
        return num != null && num.intValue() == 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:14:? A[RETURN, SYNTHETIC] */
    public static boolean isQuadCfaRunning(CaptureResult captureResult) {
        float f2;
        boolean _b = DataRepository.dataItemFeature()._b();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("isQuadCfaRunning: support=");
        sb.append(_b);
        Log.d(str, sb.toString());
        if (_b) {
            AECFrameControl aECFrameControl = (AECFrameControl) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.AEC_FRAME_CONTROL);
            if (!(aECFrameControl == null || aECFrameControl.getAecExposureDatas() == null || aECFrameControl.getAecExposureDatas().length <= 0)) {
                f2 = aECFrameControl.getAecExposureDatas()[0].getLinearGain();
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("isQuadCfaRunning: gain=");
                sb2.append(f2);
                Log.d(str2, sb2.toString());
                return f2 >= 2.0f;
            }
        }
        f2 = 3.0f;
        String str22 = TAG;
        StringBuilder sb22 = new StringBuilder();
        sb22.append("isQuadCfaRunning: gain=");
        sb22.append(f2);
        Log.d(str22, sb22.toString());
        if (f2 >= 2.0f) {
        }
    }

    public static boolean isRemosaicDetected(CaptureResult captureResult) {
        Boolean bool = (Boolean) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.REMOSAIC_DETECTED);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("isRemosaicDetected: ");
        sb.append(bool);
        Log.d(str, sb.toString());
        return bool != null && bool.booleanValue();
    }

    public static boolean isSREnable(CaptureResult captureResult) {
        Boolean bool = (Boolean) VendorTagHelper.getValueSafely(captureResult, CaptureResultVendorTags.IS_SR_ENABLE);
        return bool != null && bool.booleanValue();
    }
}
