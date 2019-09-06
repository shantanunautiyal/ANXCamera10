package com.android.camera.lib.compatibility.related.v23;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.telecom.TelecomManager;
import android.util.Log;
import java.io.File;
import java.util.Iterator;
import java.util.List;

@TargetApi(23)
public class V23Utils {
    private static final String TAG = "TelecomManagerProxy-v23";

    public static String getSdcardPath(Context context) {
        VolumeInfo volumeInfo;
        int i = VERSION.SDK_INT;
        String str = TAG;
        String str2 = null;
        if (i >= 23) {
            List volumes = ((StorageManager) context.getSystemService("storage")).getVolumes();
            if (volumes != null) {
                Iterator it = volumes.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    volumeInfo = (VolumeInfo) it.next();
                    if (volumeInfo.getType() == 0 && volumeInfo.isMountedWritable()) {
                        DiskInfo disk = volumeInfo.getDisk();
                        StringBuilder sb = new StringBuilder();
                        sb.append("getSdcardPath: diskInfo = ");
                        sb.append(disk);
                        Log.d(str, sb.toString());
                        if (disk != null && disk.isSd()) {
                            break;
                        }
                    }
                }
            }
        }
        volumeInfo = null;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("getSdcardPath: sdcardVolume = ");
        sb2.append(volumeInfo);
        Log.d(str, sb2.toString());
        if (volumeInfo != null) {
            File path = volumeInfo.getPath();
            if (path != null) {
                str2 = path.getPath();
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("getSdcardPath sd=");
            sb3.append(str2);
            Log.v(str, sb3.toString());
        }
        return str2;
    }

    public static final boolean isInVideoCall(Context context) {
        Log.d(TAG, "isInVideoCall: start");
        if (VERSION.SDK_INT >= 21) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService("telecom");
            if (telecomManager != null) {
                return telecomManager.isInCall();
            }
        }
        return false;
    }
}
