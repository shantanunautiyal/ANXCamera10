package com.android.camera.effect;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import com.android.camera.CameraAppImpl;
import com.android.camera.log.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;

public class ShaderUtil {
    public static final String DIR = "shading_script";
    public static final int NO_TEXTURE = -1;
    private static final String TAG = "Camera_ShaderUtil";

    public static void checkGlError(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ES20_ERROR: op ");
            sb.append(str);
            String str2 = ": glError ";
            sb.append(str2);
            sb.append(glGetError);
            Log.e(TAG, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(str2);
            sb2.append(glGetError);
            throw new RuntimeException(sb2.toString());
        }
    }

    public static int createProgram(String str, String str2) {
        int loadShader = loadShader(35633, str);
        String str3 = TAG;
        if (loadShader == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fail to init vertex shader ");
            sb.append(str);
            Log.e(str3, sb.toString());
            return 0;
        }
        int loadShader2 = loadShader(35632, str2);
        if (loadShader2 == 0) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Fail to init fragment shader ");
            sb2.append(str2);
            Log.e(str3, sb2.toString());
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram != 0) {
            GLES20.glGetError();
            GLES20.glAttachShader(glCreateProgram, loadShader);
            String str4 = "glAttachShader";
            checkGlError(str4);
            GLES20.glAttachShader(glCreateProgram, loadShader2);
            checkGlError(str4);
            GLES20.glLinkProgram(glCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
            if (iArr[0] != 1) {
                Log.e(str3, "Could not link program: ");
                Log.e(str3, GLES20.glGetProgramInfoLog(glCreateProgram));
                GLES20.glDeleteProgram(glCreateProgram);
                glCreateProgram = 0;
            }
        }
        GLES20.glDeleteShader(loadShader);
        GLES20.glDeleteShader(loadShader2);
        return glCreateProgram;
    }

    public static String loadFromAssetsFile(String str) {
        String str2 = null;
        try {
            AssetManager assets = CameraAppImpl.getAndroidContext().getAssets();
            StringBuilder sb = new StringBuilder();
            sb.append("shading_script/");
            sb.append(str);
            InputStream open = assets.open(sb.toString());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int read = open.read();
                if (read != -1) {
                    byteArrayOutputStream.write(read);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    open.close();
                    String str3 = new String(byteArray, "UTF-8");
                    try {
                        return str3.replaceAll("\\r\\n", "\n");
                    } catch (Exception e2) {
                        Exception exc = e2;
                        str2 = str3;
                        e = exc;
                        e.printStackTrace();
                        return str2;
                    }
                }
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return str2;
        }
    }

    public static int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader == 0) {
            return glCreateShader;
        }
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not compile shader ");
        sb.append(i);
        sb.append(":");
        sb.append(str);
        String sb2 = sb.toString();
        String str2 = TAG;
        Log.e(str2, sb2);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Info: ");
        sb3.append(GLES20.glGetShaderInfoLog(glCreateShader));
        Log.e(str2, sb3.toString());
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    public static void loadYuvImageTextures(int i, int i2, int i3, int i4, int[] iArr) {
        int i5 = 0;
        if (iArr == null || iArr.length < 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid output texture size ");
            if (iArr != null) {
                i5 = iArr.length;
            }
            sb.append(i5);
            Log.e(TAG, sb.toString());
            return;
        }
        if (iArr[0] == -1) {
            GLES20.glGenTextures(1, iArr, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, iArr[0]);
        } else {
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, iArr[0]);
        }
        ShaderNativeUtil.texChannelY(i, i2, i3);
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, 10242, 33071.0f);
        GLES20.glTexParameterf(3553, 10243, 33071.0f);
        if (iArr[1] == -1) {
            GLES20.glGenTextures(1, iArr, 1);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, iArr[1]);
        } else {
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, iArr[1]);
        }
        ShaderNativeUtil.texChannelUV(i >> 1, i2 >> 1, i4);
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, 10242, 33071.0f);
        GLES20.glTexParameterf(3553, 10243, 33071.0f);
    }

    public static void loadYuvToTextures(Buffer buffer, Buffer buffer2, int i, int i2, int[] iArr) {
        int i3;
        float f2;
        int i4;
        int[] iArr2 = iArr;
        String str = TAG;
        if (buffer == null || buffer2 == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid channel ");
            sb.append(buffer == null ? "Y" : "UV");
            Log.e(str, sb.toString());
            return;
        }
        int i5 = 0;
        if (iArr2 == null || iArr2.length < 2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("invalid output texture size ");
            if (iArr2 != null) {
                i5 = iArr2.length;
            }
            sb2.append(i5);
            Log.e(str, sb2.toString());
            return;
        }
        if (iArr2[0] == -1) {
            GLES20.glGenTextures(1, iArr2, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, iArr2[0]);
            GLES20.glTexImage2D(3553, 0, 6409, i, i2, 0, 6409, 5121, buffer);
        } else {
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, iArr2[0]);
            GLES20.glTexImage2D(3553, 0, 6409, i, i2, 0, 6409, 5121, buffer);
        }
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, 10242, 33071.0f);
        GLES20.glTexParameterf(3553, 10243, 33071.0f);
        if (iArr2[1] == -1) {
            GLES20.glGenTextures(1, iArr2, 1);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, iArr2[1]);
            f2 = 33071.0f;
            GLES20.glTexImage2D(3553, 0, 6410, i / 2, i2 / 2, 0, 6410, 5121, buffer2);
            i3 = 10242;
            i4 = 10243;
        } else {
            f2 = 33071.0f;
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, iArr2[1]);
            i3 = 10242;
            i4 = 10243;
            GLES20.glTexImage2D(3553, 0, 6410, i / 2, i2 / 2, 0, 6410, 5121, buffer2);
        }
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, i3, f2);
        GLES20.glTexParameterf(3553, i4, f2);
    }
}
