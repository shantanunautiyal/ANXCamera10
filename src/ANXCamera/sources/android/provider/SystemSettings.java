package android.provider;

import aeonax.Build;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;
import com.mi.config.d;
import com.miui.system.internal.R;
import java.util.Iterator;
import miui.os.SystemProperties;
import miui.text.ChinesePinyinConverter;
import miui.util.FeatureParser;
import miui.util.Utf8TextUtils;

public class SystemSettings {
    private static final String TAG = "SystemSettings";
    private static final String UTF8 = "UTF-8";

    public static class Secure {
        public static final String PRIVACY_MODE_ENABLED = "privacy_mode_enabled";
        public static final String SCREEN_BUTTONS_STATE = "screen_buttons_state";

        public static Cursor checkPrivacyAndReturnCursor(Context context) {
            boolean z = false;
            if (1 == Settings.Secure.getInt(context.getContentResolver(), PRIVACY_MODE_ENABLED, 0)) {
                z = true;
            }
            if (z) {
                return new MatrixCursor(new String[]{"_id"});
            }
            return null;
        }

        public static boolean isCtaSupported(ContentResolver contentResolver) {
            return false;
        }
    }

    public static class System {
        private static final String E10_DEVICE = "beryllium";
        private static final String INDIA = "INDIA";
        public static final String LOCK_WALLPAPER_PROVIDER_AUTHORITY = "lock_wallpaper_provider_authority";
        public static final String PERSIST_SYS_DEVICE_NAME = "persist.sys.device_name";
        public static final String STATUS_BAR_WINDOW_LOADED = "status_bar_window_loaded";

        public static boolean getBoolean(ContentResolver contentResolver, String str, boolean z) {
            return Settings.System.getInt(contentResolver, str, z ? 1 : 0) != 0;
        }

        public static String getDeviceName(Context context) {
            return SystemProperties.get(PERSIST_SYS_DEVICE_NAME, context.getString(FeatureParser.getBoolean("is_redmi", false) ? R.string.device_redmi : FeatureParser.getBoolean("is_poco", false) ? R.string.device_poco : FeatureParser.getBoolean(d.IS_HONGMI, false) ? R.string.device_hongmi : FeatureParser.getBoolean(d.IS_XIAOMI, false) ? E10_DEVICE.equals(SystemProperties.get("ro.product.vendor.device")) ? SystemProperties.get("ro.boot.hwc", "").contains(INDIA) ? R.string.device_poco_india : R.string.device_poco_global : R.string.device_xiaomi : FeatureParser.getBoolean(d.Cn, false) ? R.string.device_pad : R.string.miui_device_name));
        }

        public static void setDeviceName(Context context, String str) {
            SystemProperties.set(PERSIST_SYS_DEVICE_NAME, str);
            setNetHostName(context);
        }

        public static void setNetHostName(Context context) {
            String str = SystemProperties.get("net.hostname");
            StringBuilder sb = new StringBuilder();
            sb.append(Build.ANXMODEL);
            sb.append("-");
            Iterator it = ChinesePinyinConverter.getInstance().get(getDeviceName(context)).iterator();
            while (it.hasNext()) {
                sb.append(((ChinesePinyinConverter.Token) it.next()).target);
            }
            String replace = sb.toString().replace(" ", "");
            if (!replace.equals(str)) {
                String truncateByte = Utf8TextUtils.truncateByte(replace, 20);
                if (truncateByte != null) {
                    SystemProperties.set("net.hostname", truncateByte);
                }
            }
        }
    }
}
