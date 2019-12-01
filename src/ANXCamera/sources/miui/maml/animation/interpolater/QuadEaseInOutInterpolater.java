package miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class QuadEaseInOutInterpolater implements Interpolator {
    public float getInterpolation(float t) {
        float f = t * 2.0f;
        float t2 = f;
        if (f < 1.0f) {
            return 0.5f * t2 * t2;
        }
        float t3 = t2 - 1.0f;
        return -0.5f * ((t3 * (t3 - 2.0f)) - 1.0f);
    }
}
