package com.android.camera;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.SerializeOptions;
import com.android.camera.log.Log;
import com.android.camera.storage.Storage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class XmpHelper {
    public static final String GOOGLE_MICROVIDEO_NAMESPACE = "http://ns.google.com/photos/1.0/camera/";
    private static final int MAX_XMP_BUFFER_SIZE = 65502;
    public static final String MICROVIDEO_OFFSET = "MicroVideoOffset";
    public static final String MICROVIDEO_PREFIX = "GCamera";
    public static final String MICROVIDEO_PRESENTATION_TIMESTAMP = "MicroVideoPresentationTimestampUs";
    public static final String MICROVIDEO_TYPE = "MicroVideo";
    public static final String MICROVIDEO_VERSION = "MicroVideoVersion";
    private static final int M_APP1 = 225;
    private static final int M_SOI = 216;
    private static final int M_SOS = 218;
    private static final String TAG = "XmpHelper";
    public static final String XIAOMI_XMP_METADATA_NAMESPACE = "http://ns.xiaomi.com/photos/1.0/camera/";
    public static final String XIAOMI_XMP_METADATA_PREFIX = "MiCamera";
    public static final String XIAOMI_XMP_METADATA_PROPERTY_NAME = "XMPMeta";
    private static final String XMP_HEADER = "http://ns.adobe.com/xap/1.0/\u0000";
    private static final int XMP_HEADER_SIZE = 29;

    private static class Section {
        public byte[] data;
        public int length;
        public int marker;

        private Section() {
        }
    }

    static {
        try {
            XMPMetaFactory.getSchemaRegistry().registerNamespace(GOOGLE_MICROVIDEO_NAMESPACE, MICROVIDEO_PREFIX);
            XMPMetaFactory.getSchemaRegistry().registerNamespace(XIAOMI_XMP_METADATA_NAMESPACE, XIAOMI_XMP_METADATA_PREFIX);
        } catch (XMPException e2) {
            String str = TAG;
            Log.d(str, "Failed to register namespaces: " + e2);
        }
    }

    private XmpHelper() {
    }

    public static XMPMeta createXMPMeta() {
        return XMPMetaFactory.create();
    }

    public static XMPMeta extractOrCreateXMPMeta(String str) {
        XMPMeta extractXMPMeta = extractXMPMeta(str);
        return extractXMPMeta == null ? createXMPMeta() : extractXMPMeta;
    }

    public static XMPMeta extractXMPMeta(InputStream inputStream) {
        List<Section> parse = parse(inputStream, true);
        if (parse == null) {
            return null;
        }
        for (Section next : parse) {
            if (hasXMPHeader(next.data)) {
                byte[] bArr = new byte[(getXMPContentEnd(next.data) - 29)];
                System.arraycopy(next.data, 29, bArr, 0, bArr.length);
                try {
                    return XMPMetaFactory.parseFromBuffer(bArr);
                } catch (XMPException e2) {
                    String str = TAG;
                    Log.d(str, "XMP parse error: " + e2);
                }
            }
        }
        return null;
    }

    public static XMPMeta extractXMPMeta(String str) {
        if (str.toLowerCase().endsWith(Storage.JPEG_SUFFIX) || str.toLowerCase().endsWith(".jpeg")) {
            try {
                return extractXMPMeta((InputStream) new FileInputStream(str));
            } catch (FileNotFoundException e2) {
                String str2 = TAG;
                Log.e(str2, "Could not read from " + str + ": " + e2);
                return null;
            }
        } else {
            Log.d(TAG, "XMP parse: only jpeg file is supported");
            return null;
        }
    }

    private static int getXMPContentEnd(byte[] bArr) {
        for (int length = bArr.length - 1; length >= 1; length--) {
            if (bArr[length] == 62 && bArr[length - 1] != 63) {
                return length + 1;
            }
        }
        return bArr.length;
    }

    private static boolean hasXMPHeader(byte[] bArr) {
        if (bArr.length < 29) {
            return false;
        }
        try {
            byte[] bArr2 = new byte[29];
            System.arraycopy(bArr, 0, bArr2, 0, 29);
            return new String(bArr2, "UTF-8").equals(XMP_HEADER);
        } catch (UnsupportedEncodingException unused) {
        }
    }

    private static List<Section> insertXMPSection(List<Section> list, XMPMeta xMPMeta) {
        if (list != null) {
            int i = 1;
            if (list.size() > 1) {
                try {
                    SerializeOptions serializeOptions = new SerializeOptions();
                    serializeOptions.setUseCompactFormat(true);
                    serializeOptions.setOmitPacketWrapper(true);
                    byte[] serializeToBuffer = XMPMetaFactory.serializeToBuffer(xMPMeta, serializeOptions);
                    if (serializeToBuffer.length > MAX_XMP_BUFFER_SIZE) {
                        return null;
                    }
                    byte[] bArr = new byte[(serializeToBuffer.length + 29)];
                    System.arraycopy(XMP_HEADER.getBytes(), 0, bArr, 0, 29);
                    System.arraycopy(serializeToBuffer, 0, bArr, 29, serializeToBuffer.length);
                    Section section = new Section();
                    section.marker = 225;
                    section.length = bArr.length + 2;
                    section.data = bArr;
                    int i2 = 0;
                    while (i2 < list.size()) {
                        if (list.get(i2).marker != 225 || !hasXMPHeader(list.get(i2).data)) {
                            i2++;
                        } else {
                            list.set(i2, section);
                            return list;
                        }
                    }
                    ArrayList arrayList = new ArrayList();
                    if (list.get(0).marker != 225) {
                        i = 0;
                    }
                    arrayList.addAll(list.subList(0, i));
                    arrayList.add(section);
                    arrayList.addAll(list.subList(i, list.size()));
                    return arrayList;
                } catch (XMPException e2) {
                    Log.d(TAG, "Serialize xmp failed: " + e2);
                }
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003b, code lost:
        if (r9 != false) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r9 = new com.android.camera.XmpHelper.Section((com.android.camera.XmpHelper.AnonymousClass1) null);
        r9.marker = r3;
        r9.length = -1;
        r9.data = new byte[r8.available()];
        r8.read(r9.data, 0, r9.data.length);
        r1.add(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0059, code lost:
        if (r8 == null) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r8.close();
     */
    private static List<Section> parse(InputStream inputStream, boolean z) {
        ArrayList arrayList;
        int read;
        try {
            if (inputStream.read() == 255) {
                if (inputStream.read() == 216) {
                    arrayList = new ArrayList();
                    while (true) {
                        int read2 = inputStream.read();
                        if (read2 == -1) {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException unused) {
                                }
                            }
                            return arrayList;
                        } else if (read2 != 255) {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException unused2) {
                                }
                            }
                            return null;
                        } else {
                            do {
                                read = inputStream.read();
                            } while (read == 255);
                            if (read == -1) {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException unused3) {
                                    }
                                }
                                return null;
                            } else if (read == 218) {
                                break;
                            } else {
                                int read3 = inputStream.read();
                                int read4 = inputStream.read();
                                if (read3 == -1) {
                                    break;
                                } else if (read4 == -1) {
                                    break;
                                } else {
                                    int i = (read3 << 8) | read4;
                                    if (z) {
                                        if (read != 225) {
                                            inputStream.skip((long) (i - 2));
                                        }
                                    }
                                    Section section = new Section();
                                    section.marker = read;
                                    section.length = i;
                                    int i2 = i - 2;
                                    section.data = new byte[i2];
                                    inputStream.read(section.data, 0, i2);
                                    arrayList.add(section);
                                }
                            }
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException unused4) {
                        }
                    }
                    return null;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused5) {
                }
            }
            return null;
            return arrayList;
        } catch (IOException e2) {
            Log.d(TAG, "Could not parse file: " + e2);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused6) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused7) {
                }
            }
            throw th;
        }
    }

    private static void writeJpegFile(OutputStream outputStream, List<Section> list) throws IOException {
        outputStream.write(255);
        outputStream.write(216);
        for (Section next : list) {
            outputStream.write(255);
            outputStream.write(next.marker);
            int i = next.length;
            if (i > 0) {
                outputStream.write(i >> 8);
                outputStream.write(i & 255);
            }
            outputStream.write(next.data);
        }
    }

    public static boolean writeXMPMeta(InputStream inputStream, OutputStream outputStream, XMPMeta xMPMeta) {
        List<Section> insertXMPSection = insertXMPSection(parse(inputStream, false), xMPMeta);
        if (insertXMPSection == null) {
            return false;
        }
        try {
            writeJpegFile(outputStream, insertXMPSection);
            if (outputStream == null) {
                return true;
            }
            try {
                outputStream.close();
                return true;
            } catch (IOException unused) {
                return true;
            }
        } catch (IOException e2) {
            String str = TAG;
            Log.d(str, "Write to stream failed: " + e2);
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException unused2) {
                }
            }
            return false;
        } catch (Throwable th) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException unused3) {
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0042, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004b, code lost:
        throw r3;
     */
    public static boolean writeXMPMeta(String str, XMPMeta xMPMeta) {
        if (str.toLowerCase().endsWith(Storage.JPEG_SUFFIX) || str.toLowerCase().endsWith(".jpeg")) {
            try {
                List<Section> insertXMPSection = insertXMPSection(parse(new FileInputStream(str), false), xMPMeta);
                if (insertXMPSection == null) {
                    return false;
                }
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(str);
                    writeJpegFile(fileOutputStream, insertXMPSection);
                    fileOutputStream.close();
                    return true;
                } catch (IOException e2) {
                    String str2 = TAG;
                    Log.d(str2, "Failed to write to " + str + ": " + e2);
                    return false;
                } catch (Throwable th) {
                    r6.addSuppressed(th);
                }
            } catch (FileNotFoundException e3) {
                String str3 = TAG;
                Log.e(str3, "Could not read from " + str + ": " + e3);
                return false;
            }
        } else {
            Log.d(TAG, "XMP parse: only jpeg file is supported");
            return false;
        }
    }
}
