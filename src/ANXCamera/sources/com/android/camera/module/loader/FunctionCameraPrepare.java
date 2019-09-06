package com.android.camera.module.loader;

import android.text.TextUtils;
import com.android.camera.Camera;
import com.android.camera.CameraAppImpl;
import com.android.camera.CameraSettings;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.constant.BeautyConstant;
import com.android.camera.constant.GlobalConstant;
import com.android.camera.data.DataRepository;
import com.android.camera.data.backup.DataBackUp;
import com.android.camera.data.data.config.ComponentConfigBeauty;
import com.android.camera.data.data.config.ComponentConfigBokeh;
import com.android.camera.data.data.config.ComponentConfigFlash;
import com.android.camera.data.data.config.ComponentConfigHdr;
import com.android.camera.data.data.config.ComponentConfigRatio;
import com.android.camera.data.data.config.ComponentConfigSlowMotion;
import com.android.camera.data.data.config.ComponentConfigUltraWide;
import com.android.camera.data.data.config.ComponentManuallyDualLens;
import com.android.camera.data.data.config.ComponentManuallyET;
import com.android.camera.data.data.config.ComponentRunningMacroMode;
import com.android.camera.data.data.config.DataItemConfig;
import com.android.camera.data.data.global.DataItemGlobal;
import com.android.camera.data.data.runing.ComponentRunningAutoZoom;
import com.android.camera.data.data.runing.ComponentRunningSuperEIS;
import com.android.camera.data.data.runing.DataItemRunning;
import com.android.camera.data.provider.DataProvider.ProviderEditor;
import com.android.camera.log.Log;
import com.android.camera.module.BaseModule;
import com.android.camera.permission.PermissionManager;
import com.mi.config.b;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;

public class FunctionCameraPrepare extends Func1Base<Camera, BaseModule> {
    private static final String TAG = "FunctionCameraPrepare";
    private BaseModule baseModule;
    private boolean mNeedReConfigureData;
    private int mResetType;

    public FunctionCameraPrepare(int i, int i2, boolean z, BaseModule baseModule2) {
        super(i);
        this.mResetType = i2;
        this.mNeedReConfigureData = z;
        this.baseModule = baseModule2;
    }

    private void reconfigureData() {
        boolean z;
        DataItemConfig dataItemConfig;
        int i;
        boolean z2;
        boolean z3;
        boolean z4 = this.mNeedReConfigureData;
        String str = CameraSettings.KEY_ZOOM;
        if (!z4) {
            DataRepository.dataItemConfig().editor().remove(str).apply();
            return;
        }
        CameraSettings.upgradeGlobalPreferences();
        DataItemGlobal dataItemGlobal = DataRepository.dataItemGlobal();
        DataItemRunning dataItemRunning = DataRepository.dataItemRunning();
        DataItemConfig dataItemConfig2 = DataRepository.dataItemConfig();
        int lastCameraId = dataItemGlobal.getLastCameraId();
        ComponentConfigFlash componentFlash = dataItemConfig2.getComponentFlash();
        ProviderEditor editor = dataItemConfig2.editor();
        ProviderEditor editor2 = dataItemGlobal.editor();
        ProviderEditor editor3 = DataRepository.dataItemLive().editor();
        DataBackUp backUp = DataRepository.getInstance().backUp();
        editor.remove(str).remove(CameraSettings.KEY_EXPOSURE);
        ComponentRunningAutoZoom componentRunningAutoZoom = dataItemRunning.getComponentRunningAutoZoom();
        if (componentRunningAutoZoom != null) {
            componentRunningAutoZoom.reInitIntentType(dataItemGlobal.getIntentType() == 0);
        }
        ComponentRunningSuperEIS componentRunningSuperEIS = dataItemRunning.getComponentRunningSuperEIS();
        if (componentRunningSuperEIS != null) {
            componentRunningSuperEIS.reInitIntentType(dataItemGlobal.getIntentType() == 0);
        }
        String persistValue = componentFlash.getPersistValue(this.mTargetMode);
        String str2 = "2";
        if (!componentFlash.isValidFlashValue(persistValue)) {
            editor.remove(componentFlash.getKey(this.mTargetMode));
        } else if (persistValue.equals(str2) || componentFlash.getPersistValue(this.mTargetMode).equals("5")) {
            editor.putString(componentFlash.getKey(this.mTargetMode), componentFlash.getDefaultValue(this.mTargetMode));
        }
        ComponentConfigRatio componentConfigRatio = dataItemConfig2.getComponentConfigRatio();
        int i2 = this.mTargetMode;
        String str3 = TAG;
        int i3 = lastCameraId;
        if (i2 == 163 || i2 == 165 || i2 == 167 || i2 == 173 || i2 == 175 || i2 == 171) {
            String[] fullSupportRatioValues = componentConfigRatio.getFullSupportRatioValues();
            String persistValue2 = componentConfigRatio.getPersistValue(this.mTargetMode);
            int length = fullSupportRatioValues.length;
            int i4 = 0;
            while (true) {
                if (i4 >= length) {
                    z3 = false;
                    break;
                } else if (TextUtils.equals(fullSupportRatioValues[i4], persistValue2)) {
                    z3 = true;
                    break;
                } else {
                    i4++;
                }
            }
            if (!z3 && (this.mTargetMode == 165 || !persistValue2.equals(ComponentConfigRatio.RATIO_1X1))) {
                Log.d(str3, "reconfigureData: clear DATA_CONFIG_RATIO");
                editor.remove("pref_camera_picturesize_key");
            }
        }
        if (this.mTargetMode == 172) {
            ComponentConfigSlowMotion componentConfigSlowMotion = dataItemConfig2.getComponentConfigSlowMotion();
            String componentValue = componentConfigSlowMotion.getComponentValue(172);
            String[] supportAllFPS = componentConfigSlowMotion.getSupportAllFPS();
            int length2 = supportAllFPS.length;
            int i5 = 0;
            while (true) {
                if (i5 >= length2) {
                    z2 = false;
                    break;
                } else if (TextUtils.equals(supportAllFPS[i5], componentValue)) {
                    z2 = true;
                    break;
                } else {
                    i5++;
                }
            }
            if (!z2) {
                Log.d(str3, "reconfigureData: clear DATA_CONFIG_NEW_SLOW_MOTION_KEY");
                editor.remove(DataItemConfig.DATA_CONFIG_NEW_SLOW_MOTION_KEY);
            }
        }
        if (this.mTargetMode == 167) {
            String string = CameraAppImpl.getAndroidContext().getString(R.string.pref_camera_iso_default);
            String str4 = CameraSettings.KEY_QC_ISO;
            if (!Util.isStringValueContained((Object) dataItemConfig2.getString(str4, string), (int) R.array.pref_camera_iso_entryvalues)) {
                editor.remove(str4);
            }
        }
        if (!b.lj()) {
            editor.remove(CameraSettings.KEY_QC_FOCUS_POSITION);
            editor.remove(CameraSettings.KEY_QC_EXPOSURETIME);
        } else if (this.mTargetMode == 167) {
            ComponentManuallyET componentManuallyET = new ComponentManuallyET(dataItemConfig2);
            if (!componentManuallyET.checkValueValid(componentManuallyET.getComponentValue(this.mTargetMode))) {
                componentManuallyET.resetComponentValue(this.mTargetMode);
            }
        }
        if (!Util.isLabOptionsVisible()) {
            editor2.remove(CameraSettings.KEY_FACE_DETECTION).remove(CameraSettings.KEY_CAMERA_PORTRAIT_WITH_FACEBEAUTY).remove(CameraSettings.KEY_CAMERA_FACE_DETECTION_AUTO_HIDDEN).remove(CameraSettings.KEY_CAMERA_DUAL_ENABLE).remove(CameraSettings.KEY_CAMERA_DUAL_SAT_ENABLE).remove(CameraSettings.KEY_CAMERA_MFNR_SAT_ENABLE).remove(CameraSettings.KEY_CAMERA_SR_ENABLE);
        }
        String str5 = CameraSettings.KEY_ANTIBANDING;
        if (!Util.isValidValue(dataItemGlobal.getString(str5, "1"))) {
            editor2.remove(str5);
        }
        if (!b.Wh()) {
            editor2.remove(CameraSettings.KEY_FINGERPRINT_CAPTURE);
        }
        switch (this.mResetType) {
            case 2:
            case 7:
                z = false;
                backUp.revertRunning(dataItemRunning, dataItemGlobal.getDataBackUpKey(this.mTargetMode), dataItemGlobal.getCurrentCameraId());
                if (componentFlash.getPersistValue(this.mTargetMode).equals(str2)) {
                    editor.putString(componentFlash.getKey(this.mTargetMode), componentFlash.getDefaultValue(this.mTargetMode));
                    break;
                }
                break;
            case 3:
            case 6:
                resetFlash(componentFlash, editor);
                resetHdr(dataItemConfig2.getComponentHdr(), editor);
                resetVideoBeauty(dataItemConfig2.getComponentConfigBeauty(), editor);
                resetUltraWide(dataItemConfig2.getComponentConfigUltraWide(), editor);
                resetLensType(dataItemConfig2.getComponentConfigUltraWide(), dataItemConfig2.getManuallyDualLens(), editor);
                editor.remove(dataItemConfig2.getComponentConfigSlowMotion().getKey(this.mTargetMode));
                if (dataItemGlobal.getCurrentCameraId() == 0) {
                    resetBeautyBackLevel(editor);
                    resetBeautyCaptureFigure(editor);
                    dataItemConfig = (DataItemConfig) DataRepository.provider().dataConfig(1);
                    z = false;
                } else {
                    z = false;
                    dataItemConfig = (DataItemConfig) DataRepository.provider().dataConfig(0);
                }
                ProviderEditor editor4 = dataItemConfig.editor();
                resetFlash(dataItemConfig.getComponentFlash(), editor4);
                resetHdr(dataItemConfig.getComponentHdr(), editor4);
                resetFrontBokenh(dataItemConfig.getComponentBokeh(), editor4);
                resetVideoBeauty(dataItemConfig.getComponentConfigBeauty(), editor4);
                resetBeautyVideoFront(dataItemConfig);
                editor4.apply();
                dataItemRunning.clearArrayMap();
                backUp.clearBackUp();
                DataRepository.dataItemLive().clearAll();
                resetMacroMode(dataItemRunning.getComponentRunningMacroMode());
                resetAutoZoom(dataItemRunning.getComponentRunningAutoZoom());
                resetSuperEIS(dataItemRunning.getComponentRunningSuperEIS());
                if (DataRepository.dataItemFeature().Rb()) {
                    editor3.remove(CameraSettings.KEY_LIVE_MUSIC_PATH).remove(CameraSettings.KEY_LIVE_MUSIC_HINT).remove(CameraSettings.KEY_LIVE_STICKER).remove(CameraSettings.KEY_LIVE_STICKER_NAME).remove(CameraSettings.KEY_LIVE_STICKER_HINT).remove(CameraSettings.KEY_LIVE_SPEED).remove(CameraSettings.KEY_LIVE_FILTER).remove("key_live_shrink_face_ratio").remove("key_live_enlarge_eye_ratio").remove("key_live_smooth_strength").remove(CameraSettings.KEY_LIVE_BEAUTY_STATUS);
                }
                if (DataRepository.dataItemFeature().Nb()) {
                    editor3.remove(CameraSettings.KEY_MIMOJI_INDEX).remove(CameraSettings.KEY_MIMOJI_PANNEL_STATE);
                    break;
                }
                break;
            case 4:
            case 5:
                int i6 = this.mTargetMode;
                if (i6 != 161) {
                    if (i6 != 162) {
                        if (!(i6 == 166 || i6 == 167)) {
                            if (i6 == 169) {
                                i = dataItemGlobal.getCurrentCameraId();
                            } else if (i6 != 171) {
                                if (i6 != 174) {
                                    i = dataItemGlobal.getCurrentCameraId();
                                }
                            } else if (DataRepository.dataItemFeature().rc()) {
                                i = dataItemGlobal.getCurrentCameraId();
                            }
                        }
                        i = 0;
                    } else {
                        i = dataItemGlobal.getCurrentCameraId();
                        if (i == 0) {
                            backUp.removeOtherVideoMode();
                        }
                    }
                    dataItemGlobal.setCameraIdTransient(i);
                    backUp.revertRunning(dataItemRunning, dataItemGlobal.getDataBackUpKey(this.mTargetMode), i);
                    break;
                }
                i = dataItemGlobal.getCurrentCameraId();
                dataItemGlobal.setCameraIdTransient(i);
                backUp.revertRunning(dataItemRunning, dataItemGlobal.getDataBackUpKey(this.mTargetMode), i);
        }
        z = false;
        boolean Pb = DataRepository.dataItemFeature().Pb();
        if (!(this.mResetType == 4 && i3 == dataItemGlobal.getCurrentCameraId())) {
            z = Pb;
        }
        if (z) {
            editor.putBoolean(CameraSettings.KEY_LENS_DIRTY_DETECT_ENABLED, true);
        }
        editor.apply();
        editor2.apply();
        editor3.apply();
    }

    private void resetAutoZoom(ComponentRunningAutoZoom componentRunningAutoZoom) {
        componentRunningAutoZoom.clearArrayMap();
    }

    private void resetBeautyBackLevel(ProviderEditor providerEditor) {
        String[] strArr;
        for (String str : BeautyConstant.BEAUTY_CATEGORY_LEVEL) {
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForCapture(str));
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForVideo(str));
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForPortrait(str));
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForFun(str));
        }
    }

    private void resetBeautyCaptureFigure(ProviderEditor providerEditor) {
        for (String wrappedSettingKeyForCapture : BeautyConstant.BEAUTY_CATEGORY_BACK_FIGURE) {
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForCapture(wrappedSettingKeyForCapture));
        }
    }

    private void resetBeautyVideoFront(ProviderEditor providerEditor) {
        for (String wrappedSettingKeyForVideo : BeautyConstant.BEAUTY_CATEGORY_LEVEL) {
            providerEditor.remove(BeautyConstant.wrappedSettingKeyForVideo(wrappedSettingKeyForVideo));
        }
    }

    private void resetFlash(ComponentConfigFlash componentConfigFlash, ProviderEditor providerEditor) {
        if (!componentConfigFlash.getPersistValue(this.mTargetMode).equals("3")) {
            providerEditor.putString(componentConfigFlash.getKey(this.mTargetMode), componentConfigFlash.getDefaultValue(this.mTargetMode));
        }
    }

    private void resetFrontBokenh(ComponentConfigBokeh componentConfigBokeh, ProviderEditor providerEditor) {
        if ("on".equals(componentConfigBokeh.getPersistValue(this.mTargetMode))) {
            componentConfigBokeh.setComponentValue(this.mTargetMode, "off");
        }
    }

    private void resetHdr(ComponentConfigHdr componentConfigHdr, ProviderEditor providerEditor) {
        String persistValue = componentConfigHdr.getPersistValue(this.mTargetMode);
        if (!persistValue.equals("auto") && !persistValue.equals("off")) {
            providerEditor.putString(componentConfigHdr.getKey(this.mTargetMode), componentConfigHdr.getDefaultValue(this.mTargetMode));
        }
    }

    private void resetLensType(ComponentConfigUltraWide componentConfigUltraWide, ComponentManuallyDualLens componentManuallyDualLens, ProviderEditor providerEditor) {
        if (componentConfigUltraWide != null && componentManuallyDualLens != null) {
            componentManuallyDualLens.resetLensType(componentConfigUltraWide, providerEditor);
        }
    }

    private void resetMacroMode(ComponentRunningMacroMode componentRunningMacroMode) {
        componentRunningMacroMode.clearArrayMap();
    }

    private void resetSuperEIS(ComponentRunningSuperEIS componentRunningSuperEIS) {
        componentRunningSuperEIS.clearArrayMap();
    }

    private void resetUltraWide(ComponentConfigUltraWide componentConfigUltraWide, ProviderEditor providerEditor) {
        if (componentConfigUltraWide != null) {
            componentConfigUltraWide.resetUltraWide(providerEditor);
        }
    }

    private void resetVideoBeauty(ComponentConfigBeauty componentConfigBeauty, ProviderEditor providerEditor) {
        String persistValue = componentConfigBeauty.getPersistValue(162);
        String defaultValue = componentConfigBeauty.getDefaultValue(162);
        if (!TextUtils.equals(persistValue, defaultValue)) {
            providerEditor.putString(componentConfigBeauty.getKey(162), defaultValue);
        }
    }

    public NullHolder<BaseModule> apply(@NonNull NullHolder<Camera> nullHolder) throws Exception {
        if (!nullHolder.isPresent()) {
            return NullHolder.ofNullable(null, 234);
        }
        if (!PermissionManager.checkCameraLaunchPermissions()) {
            return NullHolder.ofNullable(null, 229);
        }
        Camera camera = (Camera) nullHolder.get();
        if (camera.isFinishing()) {
            Log.d(TAG, "activity is finishing, the content of BaseModuleHolder is set to null");
            return NullHolder.ofNullable(null, 235);
        }
        camera.changeRequestOrientation();
        if (this.baseModule.isDeparted()) {
            return NullHolder.ofNullable(this.baseModule, 225);
        }
        reconfigureData();
        return NullHolder.ofNullable(this.baseModule);
    }

    public Scheduler getWorkThread() {
        return GlobalConstant.sCameraSetupScheduler;
    }
}
