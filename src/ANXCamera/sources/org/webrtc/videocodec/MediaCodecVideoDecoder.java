package org.webrtc.videocodec;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CryptoException;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class MediaCodecVideoDecoder {
    public static final Boolean DEBUG = Boolean.valueOf(true);
    public static final int DECODE = 0;
    public static final int EXIT = 2;
    public static final int PUSHBUFFER = 1;
    /* access modifiers changed from: private */
    public static String TAG;
    private static boolean decodeStarted = false;
    private LinkedList<Integer> availableInputBufferIndices;
    private LinkedList<Integer> availableOutputBufferIndices;
    private LinkedList<BufferInfo> availableOutputBufferInfos;
    /* access modifiers changed from: private */
    public MediaCodec codec = null;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;
    private int codecheight;
    private int codecwidth;
    private Context context;
    /* access modifiers changed from: private */
    public DecodeHandler decodehandler;
    private Thread decodelooperThread;
    private long deltaTimeUs;
    private MediaFormat format;
    int frameCount = 0;
    long frameNum = 0;
    private LinkedList<Frame> frameQueue;
    private long native_context;
    /* access modifiers changed from: private */
    public DecodeHandler pushhandler;
    private Thread pushlooperThread;
    private SurfaceView surfaceView;
    /* access modifiers changed from: private */
    public Map<String, String> timeMap;

    /* renamed from: org.webrtc.videocodec.MediaCodecVideoDecoder$4 reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName = new int[CodecName.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            $SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName[CodecName.HWH264.ordinal()] = 1;
            $SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName[CodecName.ON2_VP8.ordinal()] = 2;
            $SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName[CodecName.GOOGLE_VPX.ordinal()] = 3;
            try {
                $SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName[CodecName.EXYNOX_VP8.ordinal()] = 4;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    private enum CodecName {
        HWH264,
        ON2_VP8,
        GOOGLE_VPX,
        EXYNOX_VP8
    }

    class DecodeHandler extends Handler {
        DecodeHandler() {
        }

        public void handleMessage(Message message) {
            try {
                int i = message.what;
                if (i != 0) {
                    if (i != 1) {
                        if (i == 2) {
                            if (MediaCodecVideoDecoder.DEBUG.booleanValue()) {
                                Log.d(MediaCodecVideoDecoder.TAG, "handleMessage EXIT.");
                            }
                        }
                    } else if (MediaCodecVideoDecoder.this.validCodec(MediaCodecVideoDecoder.this.codec)) {
                        MediaCodecVideoDecoder.this.PushPendingBuffers();
                        MediaCodecVideoDecoder.this.pushhandler.sendMessageDelayed(MediaCodecVideoDecoder.this.pushhandler.obtainMessage(1), 5);
                    }
                } else if (MediaCodecVideoDecoder.this.validCodec(MediaCodecVideoDecoder.this.codec)) {
                    MediaCodecVideoDecoder.this.decodePendingBuffers();
                    MediaCodecVideoDecoder.this.decodehandler.sendMessageDelayed(MediaCodecVideoDecoder.this.decodehandler.obtainMessage(0), 5);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    class Frame {
        public ByteBuffer buffer;
        public int buflen;
        public int frameType;
        public int height;
        public long ntp_timestapUs;
        public long timestampUs;
        public int width;

        Frame(ByteBuffer byteBuffer, int i, long j, long j2, int i2, int i3, int i4) {
            this.buffer = byteBuffer;
            this.buflen = i;
            this.timestampUs = j;
            this.ntp_timestapUs = j2;
            this.width = i2;
            this.height = i3;
            this.frameType = i4;
            MediaCodecVideoDecoder.this.timeMap.put(String.valueOf(j), String.valueOf(j2));
        }
    }

    public MediaCodecVideoDecoder(int i, long j) {
        TAG = "HW-Decoder";
        this.native_context = j;
        this.frameQueue = new LinkedList<>();
        this.availableInputBufferIndices = new LinkedList<>();
        this.availableOutputBufferIndices = new LinkedList<>();
        this.availableOutputBufferInfos = new LinkedList<>();
        this.timeMap = new HashMap();
        decodeStarted = false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0027, code lost:
        if (feedInputBuffer() == false) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002a, code lost:
        return;
     */
    public void PushPendingBuffers() {
        synchronized (this.codec) {
            if (validCodec(this.codec)) {
                int dequeueInputBuffer = this.codec.dequeueInputBuffer((long) 10);
                if (dequeueInputBuffer != -1) {
                    this.availableInputBufferIndices.add(Integer.valueOf(dequeueInputBuffer));
                }
            }
        }
    }

    private void check(boolean z, String str) {
        if (!z) {
            Log.e("WEBRTC-CHECK", str);
            AlertDialog create = new Builder(this.context).create();
            create.setTitle("WebRTC Error");
            create.setMessage(str);
            create.setButton(-1, "OK", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            create.show();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0081, code lost:
        if (drainOutputBuffer() == false) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0084, code lost:
        return;
     */
    public void decodePendingBuffers() {
        synchronized (this.codec) {
            if (validCodec(this.codec)) {
                BufferInfo bufferInfo = new BufferInfo();
                int dequeueOutputBuffer = this.codec.dequeueOutputBuffer(bufferInfo, (long) 0);
                if (logInfoEnable()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("decodePendingBuffers DECODE. index:");
                    sb.append(dequeueOutputBuffer);
                    Log.d(str, sb.toString());
                }
                if (dequeueOutputBuffer >= 0) {
                    this.availableOutputBufferIndices.add(Integer.valueOf(dequeueOutputBuffer));
                    this.availableOutputBufferInfos.add(bufferInfo);
                }
                if (dequeueOutputBuffer == -3) {
                    this.codecOutputBuffers = this.codec.getOutputBuffers();
                } else if (dequeueOutputBuffer == -2) {
                    MediaFormat outputFormat = this.codec.getOutputFormat();
                    this.codecwidth = outputFormat.getInteger("width");
                    this.codecheight = outputFormat.getInteger("height");
                    if (this.native_context != 0) {
                        ChangeResolution(this.codecwidth, this.codecheight, this.native_context);
                    }
                }
            }
        }
    }

    private Frame dequeueFrame() {
        Frame frame;
        synchronized (this.frameQueue) {
            this.frameCount--;
            frame = (Frame) this.frameQueue.removeFirst();
        }
        return frame;
    }

    private boolean drainOutputBuffer() {
        if (this.availableOutputBufferIndices.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return false;
        }
        int intValue = ((Integer) this.availableOutputBufferIndices.peekFirst()).intValue();
        BufferInfo bufferInfo = (BufferInfo) this.availableOutputBufferInfos.peekFirst();
        if ((bufferInfo.flags & 4) != 0) {
            check(false, "Saw output end of stream.");
            return false;
        }
        if (logInfoEnable()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("drainOutputBuffer got one frame:");
            sb.append(intValue);
            sb.append("info.presentationTimeUs:");
            sb.append(bufferInfo.presentationTimeUs);
            Log.d(str, sb.toString());
        }
        String str2 = (String) this.timeMap.get(String.valueOf(bufferInfo.presentationTimeUs));
        if (str2 == null) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("drainOutputBuffer timestap error from decoder -index:");
            sb2.append(intValue);
            sb2.append("info.presentationTimeUs:");
            sb2.append(bufferInfo.presentationTimeUs);
            Log.e(str3, sb2.toString());
            if (this.native_context != 0) {
                Frame frame = new Frame(this.codecOutputBuffers[intValue], bufferInfo.size, bufferInfo.presentationTimeUs, -1, this.codecwidth, this.codecheight, -1);
                DeliverFrame(frame, this.native_context);
            }
            synchronized (this.codec) {
                if (!validCodec(this.codec)) {
                    return false;
                }
                this.codec.releaseOutputBuffer(intValue, false);
                this.availableOutputBufferIndices.removeFirst();
                this.availableOutputBufferInfos.removeFirst();
                return false;
            }
        }
        long parseLong = Long.parseLong(str2);
        if (logInfoEnable()) {
            String str4 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("drainOutputBuffer before DeliverFrame:");
            sb3.append(intValue);
            sb3.append("info.presentationTimeUs:");
            sb3.append(bufferInfo.presentationTimeUs);
            Log.d(str4, sb3.toString());
        }
        if (this.native_context != 0) {
            Frame frame2 = r1;
            Frame frame3 = new Frame(this.codecOutputBuffers[intValue], bufferInfo.size, bufferInfo.presentationTimeUs, parseLong, this.codecwidth, this.codecheight, -1);
            DeliverFrame(frame2, this.native_context);
        }
        if (logInfoEnable()) {
            String str5 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("drainOutputBuffer after DeliverFrame:");
            sb4.append(intValue);
            sb4.append("info.presentationTimeUs:");
            sb4.append(bufferInfo.presentationTimeUs);
            Log.d(str5, sb4.toString());
        }
        this.timeMap.remove(str2);
        synchronized (this.codec) {
            if (!validCodec(this.codec)) {
                return false;
            }
            this.codec.releaseOutputBuffer(intValue, false);
            this.availableOutputBufferIndices.removeFirst();
            this.availableOutputBufferInfos.removeFirst();
            return true;
        }
    }

    private boolean feedInputBuffer() {
        if (this.availableInputBufferIndices.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return false;
        } else if (!hasFrame()) {
            try {
                Thread.sleep(10);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            return false;
        } else {
            Frame dequeueFrame = dequeueFrame();
            ByteBuffer byteBuffer = dequeueFrame.buffer;
            int intValue = ((Integer) this.availableInputBufferIndices.pollFirst()).intValue();
            ByteBuffer byteBuffer2 = this.codecInputBuffers[intValue];
            check(byteBuffer2.capacity() >= byteBuffer.capacity(), "Buffer is too small to copy a frame.");
            byteBuffer.rewind();
            byteBuffer2.rewind();
            byteBuffer2.put(byteBuffer);
            try {
                if (logInfoEnable()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("feedInputBuffer -index:");
                    sb.append(intValue);
                    sb.append("frame.timestampUs:");
                    sb.append(dequeueFrame.timestampUs);
                    Log.d(str, sb.toString());
                }
                synchronized (this.codec) {
                    if (!validCodec(this.codec)) {
                        return false;
                    }
                    this.codec.queueInputBuffer(intValue, 0, byteBuffer.capacity(), dequeueFrame.timestampUs, 0);
                }
            } catch (CryptoException e4) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("CryptoException w/ errorCode ");
                sb2.append(e4.getErrorCode());
                sb2.append(", '");
                sb2.append(e4.getMessage());
                sb2.append("'");
                check(false, sb2.toString());
            }
        }
        return true;
    }

    private void flush() {
        this.availableInputBufferIndices.clear();
        this.availableOutputBufferIndices.clear();
        this.availableOutputBufferInfos.clear();
        this.timeMap.clear();
        this.codec.flush();
    }

    private boolean hasFrame() {
        synchronized (this.frameQueue) {
            if (!this.frameQueue.isEmpty()) {
                return true;
            }
            if (logInfoEnable()) {
                Log.d(TAG, "hasFrame frameQueue is empty");
            }
            try {
                this.frameQueue.wait();
                return false;
            } catch (InterruptedException unused) {
                Log.w(TAG, "frameQueue.wait exception");
                return false;
            }
        }
    }

    private boolean logInfoEnable() {
        if (!DEBUG.booleanValue()) {
            return false;
        }
        long j = this.frameNum;
        return j < 60 || 0 == j % 50;
    }

    private long mediaTimeToSystemTime(long j) {
        if (this.deltaTimeUs == -1) {
            this.deltaTimeUs = (System.currentTimeMillis() * 1000) - j;
        }
        return this.deltaTimeUs + j;
    }

    private void pushBuffer(ByteBuffer byteBuffer, int i, long j, long j2, int i2, int i3, int i4) {
        try {
            synchronized (this.frameQueue) {
                LinkedList<Frame> linkedList = this.frameQueue;
                Frame frame = new Frame(byteBuffer, i, j, j2, i2, i3, i4);
                linkedList.add(frame);
                if (logInfoEnable()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("pushBuffer timestampUs:");
                    sb.append(j);
                    sb.append("ntp_timestapUs:");
                    sb.append(j2);
                    sb.append("frameCount:");
                    sb.append(this.frameCount);
                    Log.d(str, sb.toString());
                }
                this.frameNum++;
                if (this.frameNum > 1000) {
                    this.frameNum = 0;
                }
                this.frameCount++;
                this.frameQueue.notifyAll();
                if (!decodeStarted) {
                    decodeStarted = true;
                    this.decodehandler.sendMessage(this.decodehandler.obtainMessage(0));
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private boolean setCodecState(int i, int i2, CodecName codecName) {
        this.format = new MediaFormat();
        this.codecwidth = i;
        this.codecheight = i2;
        this.format.setInteger("width", i);
        this.format.setInteger("height", i2);
        try {
            int i3 = AnonymousClass4.$SwitchMap$org$webrtc$videocodec$MediaCodecVideoDecoder$CodecName[codecName.ordinal()];
            if (i3 == 1) {
                this.format.setString("mime", "video/avc");
                this.codec = MediaCodec.createDecoderByType("video/avc");
            } else if (i3 == 2) {
                this.format.setString("mime", "video/x-vnd.on2.vp8");
                this.codec = MediaCodec.createDecoderByType("video/x-vnd.on2.vp8");
            } else if (i3 == 3) {
                this.codec = MediaCodec.createByCodecName("OMX.google.vpx.decoder");
            } else if (i3 != 4) {
                return false;
            } else {
                this.codec = MediaCodec.createByCodecName("OMX.Exynos.VP8.Decoder");
            }
            if (Build.MODEL.equalsIgnoreCase("MiTV2")) {
                this.format.setInteger("forceDecode", 1);
            }
            this.format.setInteger("color-format", 19);
            synchronized (this.codec) {
                this.codec.configure(this.format, null, null, 0);
                this.codec.start();
                if (DEBUG.booleanValue()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("setCodecState after codec start -width:");
                    sb.append(i);
                    sb.append(" height:");
                    sb.append(i2);
                    Log.d(str, sb.toString());
                }
                this.codecInputBuffers = this.codec.getInputBuffers();
                this.codecOutputBuffers = this.codec.getOutputBuffers();
            }
            return true;
        } catch (Exception e2) {
            if (e2 instanceof IOException) {
                Log.e(TAG, "Failed to create MediaCodec for VP8.", e2);
                return false;
            }
            throw new RuntimeException(e2);
        }
    }

    private boolean start(int i, int i2) {
        this.deltaTimeUs = -1;
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec != null) {
            synchronized (mediaCodec) {
                if (this.codec != null) {
                    this.codec.stop();
                    this.codec.release();
                    this.codec = null;
                }
            }
        }
        if (!setCodecState(i, i2, CodecName.HWH264)) {
            return false;
        }
        startPushLooperThread();
        startDecodeLooperThread();
        DecodeHandler decodeHandler = this.pushhandler;
        decodeHandler.sendMessage(decodeHandler.obtainMessage(1));
        return true;
    }

    private void startDecodeLooperThread() {
        this.decodelooperThread = new Thread() {
            public void run() {
                Looper.prepare();
                MediaCodecVideoDecoder mediaCodecVideoDecoder = MediaCodecVideoDecoder.this;
                mediaCodecVideoDecoder.decodehandler = new DecodeHandler();
                if (MediaCodecVideoDecoder.DEBUG.booleanValue()) {
                    Log.d(MediaCodecVideoDecoder.TAG, "startDecodeLooperThread Decoder-HW");
                }
                synchronized (MediaCodecVideoDecoder.this) {
                    MediaCodecVideoDecoder.this.notify();
                }
                Looper.loop();
            }
        };
        this.decodelooperThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void startPushLooperThread() {
        this.pushlooperThread = new Thread() {
            public void run() {
                Looper.prepare();
                MediaCodecVideoDecoder mediaCodecVideoDecoder = MediaCodecVideoDecoder.this;
                mediaCodecVideoDecoder.pushhandler = new DecodeHandler();
                if (MediaCodecVideoDecoder.DEBUG.booleanValue()) {
                    Log.d(MediaCodecVideoDecoder.TAG, "startPushLooperThread HW-Decoder");
                }
                synchronized (MediaCodecVideoDecoder.this) {
                    MediaCodecVideoDecoder.this.notify();
                }
                Looper.loop();
            }
        };
        this.pushlooperThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean validCodec(MediaCodec mediaCodec) {
        if (mediaCodec != null) {
            return true;
        }
        if (DEBUG.booleanValue()) {
            Log.d(TAG, "validCodec codec is null.");
        }
        return false;
    }

    public native void ChangeResolution(int i, int i2, long j);

    public native void DeliverFrame(Frame frame, long j);

    public void dispose() {
        if (DEBUG.booleanValue()) {
            Log.d(TAG, "dispose");
        }
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec != null) {
            synchronized (mediaCodec) {
                if (this.codec != null) {
                    this.codec.stop();
                    this.codec.release();
                    this.codec = null;
                }
                decodeStarted = false;
                this.native_context = 0;
                this.timeMap.clear();
            }
        }
    }

    public SurfaceView getView() {
        return this.surfaceView;
    }
}
