package com.android.camera.module.impl.component;

import com.android.camera.ActivityBase;
import com.android.camera.CameraSettings;
import com.android.camera.HybridZoomingSystem;
import com.android.camera.R;
import com.android.camera.data.DataRepository;
import com.android.camera.log.Log;
import com.android.camera.protocol.ModeCoordinatorImpl;
import com.android.camera.protocol.ModeProtocol.ActionProcessing;
import com.android.camera.protocol.ModeProtocol.BackStack;
import com.android.camera.protocol.ModeProtocol.BottomPopupTips;
import com.android.camera.protocol.ModeProtocol.DualController;
import com.android.camera.protocol.ModeProtocol.LiveVVProcess;
import com.android.camera.protocol.ModeProtocol.MainContentProtocol;
import com.android.camera.protocol.ModeProtocol.MimojiAvatarEngine;
import com.android.camera.protocol.ModeProtocol.PanoramaProtocol;
import com.android.camera.protocol.ModeProtocol.RecordState;
import com.android.camera.protocol.ModeProtocol.TopAlert;
import com.android.camera.protocol.ModeProtocol.WideSelfieProtocol;
import com.android.camera.protocol.ModeProtocol.ZoomProtocol;

public class RecordingStateChangeImpl implements RecordState {
    private static final String TAG = "RecordingState";
    private ActivityBase mActivity;

    public RecordingStateChangeImpl(ActivityBase activityBase) {
        this.mActivity = activityBase;
    }

    public static RecordingStateChangeImpl create(ActivityBase activityBase) {
        return new RecordingStateChangeImpl(activityBase);
    }

    private int getCurrentModuleIndex() {
        ActivityBase activityBase = this.mActivity;
        if (activityBase == null) {
            return 160;
        }
        return activityBase.getCurrentModuleIndex();
    }

    private boolean isFPS960() {
        if (getCurrentModuleIndex() != 172 || !DataRepository.dataItemFeature().pc()) {
            return false;
        }
        return DataRepository.dataItemConfig().getComponentConfigSlowMotion().isSlowMotionFps960();
    }

    private void setZoomRatio(float f2, float f3) {
        ZoomProtocol zoomProtocol = (ZoomProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(170);
        if (zoomProtocol != null) {
            zoomProtocol.setMaxZoomRatio(f2);
            zoomProtocol.setMinZoomRatio(f3);
        }
    }

    public void onFailed() {
        Log.d(TAG, "onFailed");
        onFinish();
        ((ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162)).processingFailed();
    }

    public void onFinish() {
        Log.d(TAG, "onFinish");
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 174) {
            topAlert.showConfigMenu();
            bottomPopupTips.reInitTipImage();
            if (!HybridZoomingSystem.IS_3_OR_MORE_SAT) {
                bottomPopupTips.reConfigBottomTipOfUltraWide();
            }
            actionProcessing.processingFinish();
            topAlert.setRecordingTimeState(2);
            topAlert.enableMenuItem(true, 225, 245);
            topAlert.alertMusicClose(true);
        } else if (currentModuleIndex != 179) {
            topAlert.showConfigMenu();
            bottomPopupTips.reInitTipImage();
            if (!HybridZoomingSystem.IS_3_OR_MORE_SAT) {
                bottomPopupTips.reConfigBottomTipOfUltraWide();
            }
            actionProcessing.processingFinish();
            topAlert.setRecordingTimeState(2);
        } else {
            topAlert.showConfigMenu();
            ((LiveVVProcess) ModeCoordinatorImpl.getInstance().getAttachProtocol(230)).processingFinish();
        }
    }

    public void onMimojiCreateBack() {
        ((ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162)).processingMimojiBack();
        MimojiAvatarEngine mimojiAvatarEngine = (MimojiAvatarEngine) ModeCoordinatorImpl.getInstance().getAttachProtocol(217);
        if (mimojiAvatarEngine != null) {
            mimojiAvatarEngine.backToPreview(false, true);
        }
        ((MainContentProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(166)).mimojiEnd();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 174) {
            actionProcessing.processingPause();
            BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
            bottomPopupTips.reInitTipImage();
            if (!HybridZoomingSystem.IS_3_OR_MORE_SAT) {
                bottomPopupTips.reConfigBottomTipOfUltraWide();
            }
            topAlert.disableMenuItem(true, 225, 245);
            topAlert.showConfigMenu();
        } else if (currentModuleIndex != 179) {
            actionProcessing.processingPause();
            topAlert.setRecordingTimeState(3);
        } else {
            topAlert.showConfigMenu();
            ((LiveVVProcess) ModeCoordinatorImpl.getInstance().getAttachProtocol(230)).processingPause();
        }
    }

    public void onPostPreview() {
        Log.d(TAG, "onPostPreview");
        ((BackStack) ModeCoordinatorImpl.getInstance().getAttachProtocol(171)).handleBackStackFromShutter();
        ((TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172)).hideConfigMenu();
        ((ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162)).processingWorkspace(false);
    }

    public void onPostPreviewBack() {
        Log.d(TAG, "onPostPreviewBack");
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        if (getCurrentModuleIndex() != 177) {
            actionProcessing.processingWorkspace(true);
        } else {
            actionProcessing.processingFinish();
        }
        ((BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175)).reInitTipImage();
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        int[] iArr = new int[2];
        iArr[0] = getCurrentModuleIndex() == 177 ? 197 : 225;
        iArr[1] = 245;
        topAlert.enableMenuItem(true, iArr);
        topAlert.showConfigMenu();
    }

    public void onPostSavingFinish() {
        String str = TAG;
        Log.d(str, "onPostSavingFinish");
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 166) {
            PanoramaProtocol panoramaProtocol = (PanoramaProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(176);
            if (panoramaProtocol != null) {
                Log.d(str, "onPostExecute setDisplayPreviewBitmap null");
                panoramaProtocol.setDisplayPreviewBitmap(null);
                panoramaProtocol.showSmallPreview(false);
            }
        } else if (currentModuleIndex != 176) {
            ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
            if (actionProcessing != null) {
                actionProcessing.processingFinish();
            }
        } else {
            WideSelfieProtocol wideSelfieProtocol = (WideSelfieProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(216);
            if (wideSelfieProtocol != null) {
                wideSelfieProtocol.updatePreviewBitmap(null, null, null);
            }
        }
    }

    public void onPostSavingStart() {
        Log.d(TAG, "onPostSaving");
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        topAlert.showConfigMenu();
        ((BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175)).reInitTipImage();
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 166) {
            actionProcessing.processingFinish();
            actionProcessing.updateLoading(false);
            if (DataRepository.dataItemFeature().Yb()) {
                DualController dualController = (DualController) ModeCoordinatorImpl.getInstance().getAttachProtocol(182);
                if (dualController != null) {
                    dualController.showZoomButton();
                    if (topAlert != null) {
                        topAlert.clearAlertStatus();
                    }
                }
            }
            ((PanoramaProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(176)).resetShootUI();
        } else if (currentModuleIndex == 173) {
            actionProcessing.processingPostAction();
            DualController dualController2 = (DualController) ModeCoordinatorImpl.getInstance().getAttachProtocol(182);
            if (dualController2 != null) {
                dualController2.showZoomButton();
                if (topAlert != null) {
                    topAlert.clearAlertStatus();
                }
            }
            BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
            if (bottomPopupTips != null) {
                bottomPopupTips.hideTipImage();
            }
        } else if (currentModuleIndex == 176) {
            actionProcessing.processingFinish();
            actionProcessing.updateLoading(false);
            ((WideSelfieProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(216)).resetShootUI();
        } else if (currentModuleIndex != 177) {
            topAlert.setRecordingTimeState(2);
            actionProcessing.processingPostAction();
        }
    }

    public void onPrepare() {
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        topAlert.hideConfigMenu();
        if (getCurrentModuleIndex() != 179) {
            ((BackStack) ModeCoordinatorImpl.getInstance().getAttachProtocol(171)).handleBackStackFromShutter();
            ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
            actionProcessing.processingPrepare();
            if (actionProcessing.isShowFilterView()) {
                actionProcessing.showOrHideFilterView();
            }
            BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
            bottomPopupTips.hideTipImage();
            bottomPopupTips.hideLeftTipImage();
            bottomPopupTips.hideSpeedTipImage();
            bottomPopupTips.hideCenterTipImage();
            if (HybridZoomingSystem.IS_3_OR_MORE_SAT) {
                if (HybridZoomingSystem.toFloat(HybridZoomingSystem.getZoomRatioHistory(getCurrentModuleIndex(), "1.0"), 1.0f) < 1.0f) {
                    bottomPopupTips.directlyHideTips();
                }
            } else if (CameraSettings.isUltraWideConfigOpen(getCurrentModuleIndex())) {
                bottomPopupTips.directlyHideTips();
            }
            if (CameraSettings.isFPS960(getCurrentModuleIndex())) {
                bottomPopupTips.directlyHideTips();
            }
        } else {
            ((LiveVVProcess) ModeCoordinatorImpl.getInstance().getAttachProtocol(230)).processingPrepare();
        }
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 166) {
            ((PanoramaProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(176)).setShootUI();
            if (DataRepository.dataItemFeature().Yb()) {
                DualController dualController = (DualController) ModeCoordinatorImpl.getInstance().getAttachProtocol(182);
                if (dualController != null) {
                    dualController.hideZoomButton();
                    if (topAlert != null) {
                        topAlert.alertUpdateValue(2);
                    }
                }
            }
        } else if (currentModuleIndex == 176) {
            MainContentProtocol mainContentProtocol = (MainContentProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(166);
            if (mainContentProtocol != null) {
                mainContentProtocol.clearIndicator(1);
            }
            WideSelfieProtocol wideSelfieProtocol = (WideSelfieProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(216);
            if (wideSelfieProtocol != null) {
                wideSelfieProtocol.setShootingUI();
            }
        } else if (currentModuleIndex != 179 && currentModuleIndex != 173 && currentModuleIndex != 174 && !isFPS960()) {
            topAlert.setRecordingTimeState(1);
        }
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 174) {
            actionProcessing.processingResume();
            topAlert.hideConfigMenu();
            BackStack backStack = (BackStack) ModeCoordinatorImpl.getInstance().getAttachProtocol(171);
            if (backStack != null) {
                backStack.handleBackStackFromShutter();
            }
            BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
            bottomPopupTips.hideTipImage();
            bottomPopupTips.hideLeftTipImage();
            bottomPopupTips.hideSpeedTipImage();
            bottomPopupTips.hideCenterTipImage();
        } else if (currentModuleIndex != 179) {
            actionProcessing.processingResume();
            topAlert.setRecordingTimeState(4);
        } else {
            topAlert.hideConfigMenu();
            ((LiveVVProcess) ModeCoordinatorImpl.getInstance().getAttachProtocol(230)).processingResume();
        }
    }

    public void onStart() {
        Log.d(TAG, "onStart");
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        int currentModuleIndex = getCurrentModuleIndex();
        if (currentModuleIndex == 174) {
            actionProcessing.processingStart();
            topAlert.alertMusicClose(false);
        } else if (currentModuleIndex == 176) {
            actionProcessing.processingStart();
            WideSelfieProtocol wideSelfieProtocol = (WideSelfieProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(216);
            if (wideSelfieProtocol != null) {
                wideSelfieProtocol.updateHintText(R.string.wideselfie_rotate_slowly);
            }
        } else if (currentModuleIndex != 179) {
            actionProcessing.processingStart();
        } else {
            ((LiveVVProcess) ModeCoordinatorImpl.getInstance().getAttachProtocol(230)).processingStart();
        }
    }

    public void prepareCreateMimoji() {
        ((BackStack) ModeCoordinatorImpl.getInstance().getAttachProtocol(171)).handleBackStackFromShutter();
        ActionProcessing actionProcessing = (ActionProcessing) ModeCoordinatorImpl.getInstance().getAttachProtocol(162);
        actionProcessing.processingMimojiPrepare();
        if (actionProcessing.isShowFilterView()) {
            actionProcessing.showOrHideFilterView();
        }
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        topAlert.disableMenuItem(true, 197, 193);
        topAlert.alertFlash(8, "1", false);
        BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
        bottomPopupTips.hideTipImage();
        bottomPopupTips.hideLeftTipImage();
        bottomPopupTips.hideSpeedTipImage();
        bottomPopupTips.directHideCenterTipImage();
        bottomPopupTips.directlyHideTips();
    }

    public void registerProtocol() {
        ModeCoordinatorImpl.getInstance().attachProtocol(212, this);
    }

    public void unRegisterProtocol() {
        this.mActivity = null;
        ModeCoordinatorImpl.getInstance().detachProtocol(212, this);
    }

    /* access modifiers changed from: protected */
    public void updateZoomRatioToggleButtonState(boolean z) {
        DualController dualController = (DualController) ModeCoordinatorImpl.getInstance().getAttachProtocol(182);
        if (dualController != null) {
            dualController.setRecordingOrPausing(z);
            if (z) {
                dualController.hideZoomButton();
            } else {
                dualController.showZoomButton();
                dualController.setImmersiveModeEnabled(false);
            }
        }
        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
        if (topAlert == null) {
            return;
        }
        if (z) {
            topAlert.alertUpdateValue(2);
        } else {
            topAlert.clearAlertStatus();
        }
    }
}
