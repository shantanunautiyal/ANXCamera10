package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.util.Log;

/* compiled from: com.google.android.gms:play-services-measurement-impl@@17.3.0 */
public final class zzcj {
    private static volatile zzcy<Boolean> zza = zzcy.zzc();
    private static final Object zzb = new Object();

    private static boolean zza(Context context) {
        try {
            return (context.getPackageManager().getApplicationInfo("com.google.android.gms", 0).flags & 129) != 0;
        } catch (PackageManager.NameNotFoundException e2) {
            return false;
        }
    }

    public static boolean zza(Context context, Uri uri) {
        boolean z;
        String authority = uri.getAuthority();
        boolean z2 = false;
        if (!"com.google.android.gms.phenotype".equals(authority)) {
            StringBuilder sb = new StringBuilder(String.valueOf(authority).length() + 91);
            sb.append(authority);
            sb.append(" is an unsupported authority. Only com.google.android.gms.phenotype authority is supported.");
            Log.e("PhenotypeClientHelper", sb.toString());
            return false;
        } else if (zza.zza()) {
            return zza.zzb().booleanValue();
        } else {
            synchronized (zzb) {
                if (zza.zza()) {
                    boolean booleanValue = zza.zzb().booleanValue();
                    return booleanValue;
                }
                if ("com.google.android.gms".equals(context.getPackageName())) {
                    z = true;
                } else {
                    ProviderInfo resolveContentProvider = context.getPackageManager().resolveContentProvider("com.google.android.gms.phenotype", 0);
                    z = resolveContentProvider != null && "com.google.android.gms".equals(resolveContentProvider.packageName);
                }
                if (z && zza(context)) {
                    z2 = true;
                }
                zza = zzcy.zza(Boolean.valueOf(z2));
                return zza.zzb().booleanValue();
            }
        }
    }
}