.class public Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;
.super Ljava/lang/Object;
.source "MiGLSurfaceViewRender.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "MiGLSurfaceViewRender"

.field private static final cameraFragmentShaderString:Ljava/lang/String; = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;uniform samplerExternalOES tex_rgb;varying vec2 textureOut;void main() {gl_FragColor = texture2D(tex_rgb, textureOut);}"

.field private static final dispalyFragmentShaderString:Ljava/lang/String; = "precision mediump float;uniform sampler2D tex_rgb;varying vec2 textureOut;void main() {vec4 res = texture2D(tex_rgb, textureOut);float r = clamp(1.1643 * (res.r - 0.0625) + 1.5958  * (res.b - 0.5), 0.0, 1.0);float g = clamp(1.1643 * (res.r - 0.0625) - 0.39173 * (res.g - 0.5) - 0.81290 * (res.b - 0.5), 0.0, 1.0);float b = clamp(1.1643 * (res.r - 0.0625) + 2.017   * (res.g - 0.5), 0.0, 1.0);gl_FragColor = vec4(r, g, b, 1.0);}"

.field private static final previewShaderString:Ljava/lang/String; = "precision mediump float;\nuniform sampler2D tex_rgb, filter_rgb;\nuniform bool extraVideoFilter;\nvarying vec2 textureOut;\nvoid main() {\n    vec2 uv = vec2(textureOut.x, 1.0 - textureOut.y);\n    vec4 res = texture2D(tex_rgb, uv);\n    if (extraVideoFilter) {\n        float quadx, quady, x, y;\n        float bi = floor(res.b * 63.0);\n        float mixratio = res.b * 63.0 - floor(res.b * 63.0);\n\n        quady = floor(bi / 8.0);\n        quadx = bi - quady * 8.0;\n        x = quadx * 64.0 + clamp(res.r * 63.0, 1.0, 63.0);\n        y = quady * 64.0 + clamp(res.g * 63.0, 1.0, 63.0);\n        vec2 poss1 = vec2(x / 512.0, y / 512.0);\n\n        bi = bi + 1.0;\n        quady = floor(bi / 8.0);\n        quadx = bi - quady * 8.0;\n        x = quadx * 64.0 + clamp(res.r * 63.0, 1.0, 63.0);\n        y = quady * 64.0 + clamp(res.g * 63.0, 1.0, 63.0);\n        vec2 poss2 = vec2(x / 512.0, y / 512.0);\n\n        vec4 color1 = texture2D(filter_rgb, poss1);\n        vec4 color2 = texture2D(filter_rgb, poss2);\n        res = mix(color1, color2, mixratio);\n\n}\n    gl_FragColor = res;\n}"

.field private static textureVertices:[F = null

.field private static final vertexShaderString:Ljava/lang/String; = "attribute vec4 vertexIn;attribute vec2 textureIn;varying vec2 textureOut;uniform mat4 modelViewProjectionMatrix;void main() {gl_Position = modelViewProjectionMatrix*vertexIn ;textureOut = (vec4(textureIn, 0.0, 1.0)).xy;}"

.field private static vertexVertices:[F


# instance fields
.field public ATTRIB_TEXTURE:I

.field public ATTRIB_TEXTURE2:I

.field public ATTRIB_VERTEX:I

.field public ATTRIB_VERTEX2:I

.field RGBColor:Ljava/nio/ByteBuffer;

.field private final TABLESIZE:I

.field camera_texture_id:[I

.field private extraVideoFilter:I

.field private filter_rgb:I

.field private mFbo:I

.field private mFragshaderRgb:I

.field private mFramebufferTexture:I

.field private mInputSurfaceTexture:Landroid/graphics/SurfaceTexture;

.field private mOpenGlRender:Lcom/xiaomi/mediaprocess/OpenGlRender;

.field private mProgramID:I

.field private mProgramID2:I

.field private mRgbTexture:[I

.field private mTargetSurface:Landroid/opengl/GLSurfaceView;

.field private final mUpdateListener:Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;

.field private mcamera_fragshader_texture:I

.field private mcamera_texture:I

.field private mmodelMatrix:I

.field private mpreviewFilterProgramID:I

.field private mtransformMatrix:[F

.field textureVertices_buffer:Ljava/nio/ByteBuffer;

.field vertexVertices_buffer:Ljava/nio/ByteBuffer;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    const/16 v0, 0x8

    new-array v1, v0, [F

    fill-array-data v1, :array_0

    sput-object v1, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices:[F

    new-array v0, v0, [F

    fill-array-data v0, :array_1

    sput-object v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices:[F

    return-void

    nop

    :array_0
    .array-data 4
        -0x40800000    # -1.0f
        -0x40800000    # -1.0f
        0x3f800000    # 1.0f
        -0x40800000    # -1.0f
        -0x40800000    # -1.0f
        0x3f800000    # 1.0f
        0x3f800000    # 1.0f
        0x3f800000    # 1.0f
    .end array-data

    :array_1
    .array-data 4
        0x0
        0x3f800000    # 1.0f
        0x3f800000    # 1.0f
        0x3f800000    # 1.0f
        0x0
        0x0
        0x3f800000    # 1.0f
        0x0
    .end array-data
.end method

.method public constructor <init>(Lcom/xiaomi/mediaprocess/OpenGlRender;)V
    .locals 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    iput v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    iput v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    iput v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX2:I

    iput v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE2:I

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    const/16 v0, 0x10

    new-array v0, v0, [F

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    const/16 v0, 0x200

    iput v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->TABLESIZE:I

    const/4 v0, 0x1

    new-array v1, v0, [I

    iput-object v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mRgbTexture:[I

    new-array v0, v0, [I

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->camera_texture_id:[I

    new-instance v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender$1;

    invoke-direct {v0, p0}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender$1;-><init>(Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;)V

    iput-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mUpdateListener:Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;

    iput-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mOpenGlRender:Lcom/xiaomi/mediaprocess/OpenGlRender;

    return-void
.end method

.method private InitShaders()V
    .locals 21

    move-object/from16 v0, p0

    sget-object v1, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices:[F

    array-length v1, v1

    mul-int/lit8 v1, v1, 0x4

    invoke-static {v1}, Ljava/nio/ByteBuffer;->allocateDirect(I)Ljava/nio/ByteBuffer;

    move-result-object v1

    iput-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-static {}, Ljava/nio/ByteOrder;->nativeOrder()Ljava/nio/ByteOrder;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/nio/ByteBuffer;->order(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v1}, Ljava/nio/ByteBuffer;->asFloatBuffer()Ljava/nio/FloatBuffer;

    move-result-object v1

    sget-object v2, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices:[F

    invoke-virtual {v1, v2}, Ljava/nio/FloatBuffer;->put([F)Ljava/nio/FloatBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    const/4 v2, 0x0

    invoke-virtual {v1, v2}, Ljava/nio/ByteBuffer;->position(I)Ljava/nio/Buffer;

    sget-object v1, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices:[F

    array-length v1, v1

    mul-int/lit8 v1, v1, 0x4

    invoke-static {v1}, Ljava/nio/ByteBuffer;->allocateDirect(I)Ljava/nio/ByteBuffer;

    move-result-object v1

    iput-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-static {}, Ljava/nio/ByteOrder;->nativeOrder()Ljava/nio/ByteOrder;

    move-result-object v3

    invoke-virtual {v1, v3}, Ljava/nio/ByteBuffer;->order(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v1}, Ljava/nio/ByteBuffer;->asFloatBuffer()Ljava/nio/FloatBuffer;

    move-result-object v1

    sget-object v3, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices:[F

    invoke-virtual {v1, v3}, Ljava/nio/FloatBuffer;->put([F)Ljava/nio/FloatBuffer;

    iget-object v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v1, v2}, Ljava/nio/ByteBuffer;->position(I)Ljava/nio/Buffer;

    const-string v1, "attribute vec4 vertexIn;attribute vec2 textureIn;varying vec2 textureOut;uniform mat4 modelViewProjectionMatrix;void main() {gl_Position = modelViewProjectionMatrix*vertexIn ;textureOut = (vec4(textureIn, 0.0, 1.0)).xy;}"

    const-string v3, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;uniform samplerExternalOES tex_rgb;varying vec2 textureOut;void main() {gl_FragColor = texture2D(tex_rgb, textureOut);}"

    invoke-direct {v0, v1, v3}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->createProgram(Ljava/lang/String;Ljava/lang/String;)I

    move-result v1

    iput v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    const-string/jumbo v3, "vertexIn"

    invoke-static {v1, v3}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result v1

    iput v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    const/4 v3, -0x1

    if-ne v1, v3, :cond_0

    const-string v1, "MiGLSurfaceViewRender"

    const-string v4, "glGetAttribLocation error "

    invoke-static {v1, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    const-string/jumbo v4, "textureIn"

    invoke-static {v1, v4}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result v1

    iput v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    if-ne v1, v3, :cond_1

    const-string v1, "MiGLSurfaceViewRender"

    const-string v3, "glGetAttribLocation error "

    invoke-static {v1, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_1
    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glUseProgram(I)V

    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    const-string/jumbo v3, "tex_rgb"

    invoke-static {v1, v3}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result v1

    iput v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_fragshader_texture:I

    const-string v1, "MiGLSurfaceViewRender"

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "glGetAttribLocation mcamera_fragshader_texture: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_fragshader_texture:I

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v1, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    iget v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    const-string v3, "modelViewProjectionMatrix"

    invoke-static {v1, v3}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result v1

    iput v1, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mmodelMatrix:I

    const/4 v1, 0x1

    new-array v3, v1, [I

    invoke-static {v1, v3, v2}, Landroid/opengl/GLES20;->glGenTextures(I[II)V

    aget v3, v3, v2

    iput v3, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_texture:I

    iget v3, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_texture:I

    const v4, 0x8d65

    invoke-static {v4, v3}, Landroid/opengl/GLES20;->glBindTexture(II)V

    const/16 v3, 0x2800

    const/16 v5, 0x2601

    invoke-static {v4, v3, v5}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    const/16 v6, 0x2801

    invoke-static {v4, v6, v5}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    const/16 v7, 0x2802

    const v8, 0x812f

    invoke-static {v4, v7, v8}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    const/16 v9, 0x2803

    invoke-static {v4, v9, v8}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const-string v4, "MiGLSurfaceViewRender"

    new-instance v10, Ljava/lang/StringBuilder;

    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "glGetAttribLocation mcamera_texture: "

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v11, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_texture:I

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v10

    invoke-static {v4, v10}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v4, "attribute vec4 vertexIn;attribute vec2 textureIn;varying vec2 textureOut;uniform mat4 modelViewProjectionMatrix;void main() {gl_Position = modelViewProjectionMatrix*vertexIn ;textureOut = (vec4(textureIn, 0.0, 1.0)).xy;}"

    const-string v10, "precision mediump float;uniform sampler2D tex_rgb;varying vec2 textureOut;void main() {vec4 res = texture2D(tex_rgb, textureOut);float r = clamp(1.1643 * (res.r - 0.0625) + 1.5958  * (res.b - 0.5), 0.0, 1.0);float g = clamp(1.1643 * (res.r - 0.0625) - 0.39173 * (res.g - 0.5) - 0.81290 * (res.b - 0.5), 0.0, 1.0);float b = clamp(1.1643 * (res.r - 0.0625) + 2.017   * (res.g - 0.5), 0.0, 1.0);gl_FragColor = vec4(r, g, b, 1.0);}"

    invoke-direct {v0, v4, v10}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->createProgram(Ljava/lang/String;Ljava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    const-string/jumbo v10, "vertexIn"

    invoke-static {v4, v10}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX2:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX2:I

    if-gez v4, :cond_2

    const-string v4, "MiGLSurfaceViewRender"

    const-string v10, "programID_2 glGet vertex Location error "

    invoke-static {v4, v10}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_2
    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    const-string/jumbo v10, "textureIn"

    invoke-static {v4, v10}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE2:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE2:I

    if-gez v4, :cond_3

    const-string v4, "MiGLSurfaceViewRender"

    const-string v10, "programID_2 glGet texture bLocation error "

    invoke-static {v4, v10}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    :cond_3
    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    invoke-static {v4}, Landroid/opengl/GLES20;->glUseProgram(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    const-string/jumbo v10, "tex_rgb"

    invoke-static {v4, v10}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFragshaderRgb:I

    const-string v4, "MiGLSurfaceViewRender"

    new-instance v10, Ljava/lang/StringBuilder;

    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "programID_2 param ATTRIB_VERTEX2: "

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v11, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX2:I

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v11, " ATTRIB_TEXTURE2:"

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v11, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE2:I

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v11, " textuer2d samp:"

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v11, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFragshaderRgb:I

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v10

    invoke-static {v4, v10}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    new-array v4, v1, [I

    invoke-static {v1, v4, v2}, Landroid/opengl/GLES20;->glGenFramebuffers(I[II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    aget v10, v4, v2

    iput v10, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFbo:I

    iget v10, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFbo:I

    const v11, 0x8d40

    invoke-static {v11, v10}, Landroid/opengl/GLES20;->glBindFramebuffer(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v1, v4, v2}, Landroid/opengl/GLES20;->glGenTextures(I[II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    aget v4, v4, v2

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    const/16 v10, 0xde1

    invoke-static {v10, v4}, Landroid/opengl/GLES20;->glBindTexture(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const/16 v12, 0xde1

    const/4 v13, 0x0

    const/16 v14, 0x1908

    const/16 v15, 0xf00

    const/16 v16, 0x870

    const/16 v17, 0x0

    const/16 v18, 0x1908

    const/16 v19, 0x1401

    const/16 v20, 0x0

    invoke-static/range {v12 .. v20}, Landroid/opengl/GLES20;->glTexImage2D(IIIIIIIILjava/nio/Buffer;)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const v4, 0x46180400    # 9729.0f

    invoke-static {v10, v3, v4}, Landroid/opengl/GLES20;->glTexParameterf(IIF)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v10, v6, v4}, Landroid/opengl/GLES20;->glTexParameterf(IIF)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const v4, 0x47012f00    # 33071.0f

    invoke-static {v10, v7, v4}, Landroid/opengl/GLES20;->glTexParameterf(IIF)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v10, v9, v4}, Landroid/opengl/GLES20;->glTexParameterf(IIF)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const v4, 0x8ce0

    iget v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    invoke-static {v11, v4, v10, v12, v2}, Landroid/opengl/GLES20;->glFramebufferTexture2D(IIIII)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const/4 v4, 0x0

    const/high16 v12, 0x3f000000    # 0.5f

    const/high16 v13, 0x3f000000    # 0.5f

    const/high16 v14, 0x3f800000    # 1.0f

    invoke-static {v4, v12, v13, v14}, Landroid/opengl/GLES20;->glClearColor(FFFF)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v10, v2}, Landroid/opengl/GLES20;->glBindTexture(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v11, v2}, Landroid/opengl/GLES20;->glBindFramebuffer(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const-string v4, "MiGLSurfaceViewRender"

    new-instance v11, Ljava/lang/StringBuilder;

    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    const-string v12, "fbo id:"

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFbo:I

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v12, " mFramebufferTexture:"

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v11

    invoke-static {v4, v11}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string v4, "attribute vec4 vertexIn;attribute vec2 textureIn;varying vec2 textureOut;uniform mat4 modelViewProjectionMatrix;void main() {gl_Position = modelViewProjectionMatrix*vertexIn ;textureOut = (vec4(textureIn, 0.0, 1.0)).xy;}"

    const-string v11, "precision mediump float;\nuniform sampler2D tex_rgb, filter_rgb;\nuniform bool extraVideoFilter;\nvarying vec2 textureOut;\nvoid main() {\n    vec2 uv = vec2(textureOut.x, 1.0 - textureOut.y);\n    vec4 res = texture2D(tex_rgb, uv);\n    if (extraVideoFilter) {\n        float quadx, quady, x, y;\n        float bi = floor(res.b * 63.0);\n        float mixratio = res.b * 63.0 - floor(res.b * 63.0);\n\n        quady = floor(bi / 8.0);\n        quadx = bi - quady * 8.0;\n        x = quadx * 64.0 + clamp(res.r * 63.0, 1.0, 63.0);\n        y = quady * 64.0 + clamp(res.g * 63.0, 1.0, 63.0);\n        vec2 poss1 = vec2(x / 512.0, y / 512.0);\n\n        bi = bi + 1.0;\n        quady = floor(bi / 8.0);\n        quadx = bi - quady * 8.0;\n        x = quadx * 64.0 + clamp(res.r * 63.0, 1.0, 63.0);\n        y = quady * 64.0 + clamp(res.g * 63.0, 1.0, 63.0);\n        vec2 poss2 = vec2(x / 512.0, y / 512.0);\n\n        vec4 color1 = texture2D(filter_rgb, poss1);\n        vec4 color2 = texture2D(filter_rgb, poss2);\n        res = mix(color1, color2, mixratio);\n\n}\n    gl_FragColor = res;\n}"

    invoke-direct {v0, v4, v11}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->createProgram(Ljava/lang/String;Ljava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    invoke-static {v4}, Landroid/opengl/GLES20;->glUseProgram(I)V

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    const-string v11, "filter_rgb"

    invoke-static {v4, v11}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->filter_rgb:I

    iget v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    const-string v11, "extraVideoFilter"

    invoke-static {v4, v11}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result v4

    iput v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->extraVideoFilter:I

    const-string v4, "MiGLSurfaceViewRender"

    new-instance v11, Ljava/lang/StringBuilder;

    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    const-string v12, "glGetAttribLocation filter rgb id: "

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->filter_rgb:I

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v12, " extraVideoFilter id:"

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->extraVideoFilter:I

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v11

    invoke-static {v4, v11}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    iget-object v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mRgbTexture:[I

    invoke-static {v1, v4, v2}, Landroid/opengl/GLES20;->glGenTextures(I[II)V

    const-string v4, "MiGLSurfaceViewRender"

    new-instance v11, Ljava/lang/StringBuilder;

    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    const-string v12, "generate texture rgb id: "

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mRgbTexture:[I

    aget v12, v12, v2

    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v11

    invoke-static {v4, v11}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const v4, 0x84c1

    invoke-static {v4}, Landroid/opengl/GLES20;->glActiveTexture(I)V

    iget-object v4, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mRgbTexture:[I

    aget v4, v4, v2

    invoke-static {v10, v4}, Landroid/opengl/GLES20;->glBindTexture(II)V

    invoke-static {v10, v3, v5}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    invoke-static {v10, v6, v5}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    invoke-static {v10, v7, v8}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    invoke-static {v10, v9, v8}, Landroid/opengl/GLES20;->glTexParameteri(III)V

    iget v3, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->filter_rgb:I

    invoke-static {v3, v1}, Landroid/opengl/GLES20;->glUniform1i(II)V

    const/16 v4, 0xde1

    const/4 v5, 0x0

    const/16 v6, 0x1908

    const/16 v7, 0x200

    const/16 v8, 0x200

    const/4 v9, 0x0

    const/16 v10, 0x1908

    const/16 v11, 0x1401

    iget-object v12, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    invoke-static/range {v4 .. v12}, Landroid/opengl/GLES20;->glTexImage2D(IIIIIIIILjava/nio/Buffer;)V

    iget v3, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->extraVideoFilter:I

    iget-object v0, v0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    if-eqz v0, :cond_4

    goto :goto_0

    :cond_4
    move v1, v2

    :goto_0
    invoke-static {v3, v1}, Landroid/opengl/GLES20;->glUniform1i(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    return-void
.end method

.method private TransferExternalImagetoFbo()V
    .locals 9

    const/4 v0, 0x0

    const/16 v1, 0xf00

    const/16 v2, 0x870

    invoke-static {v0, v0, v1, v2}, Landroid/opengl/GLES20;->glViewport(IIII)V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFbo:I

    const v2, 0x8d40

    invoke-static {v2, v1}, Landroid/opengl/GLES20;->glBindFramebuffer(II)V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glUseProgram(I)V

    const v1, 0x84c0

    invoke-static {v1}, Landroid/opengl/GLES20;->glActiveTexture(I)V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_texture:I

    const v3, 0x8d65

    invoke-static {v3, v1}, Landroid/opengl/GLES20;->glBindTexture(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_fragshader_texture:I

    invoke-static {v1, v0}, Landroid/opengl/GLES20;->glUniform1i(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mmodelMatrix:I

    iget-object v3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    const/4 v4, 0x1

    invoke-static {v1, v4, v0, v3, v0}, Landroid/opengl/GLES20;->glUniformMatrix4fv(IIZ[FI)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glEnableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    iget-object v8, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    const/4 v4, 0x2

    const/16 v5, 0x1406

    const/4 v6, 0x0

    const/4 v7, 0x0

    invoke-static/range {v3 .. v8}, Landroid/opengl/GLES20;->glVertexAttribPointer(IIIZILjava/nio/Buffer;)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glEnableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    iget-object v8, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-static/range {v3 .. v8}, Landroid/opengl/GLES20;->glVertexAttribPointer(IIIZILjava/nio/Buffer;)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const/4 v1, 0x5

    const/4 v3, 0x4

    invoke-static {v1, v0, v3}, Landroid/opengl/GLES20;->glDrawArrays(III)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {}, Landroid/opengl/GLES20;->glFlush()V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glDisableVertexAttribArray(I)V

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE:I

    invoke-static {v1}, Landroid/opengl/GLES20;->glDisableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {v2, v0}, Landroid/opengl/GLES20;->glBindFramebuffer(II)V

    invoke-static {}, Landroid/opengl/GLES20;->glFlush()V

    return-void
.end method

.method private static abortUnless(ZLjava/lang/String;)V
    .locals 0

    if-eqz p0, :cond_0

    return-void

    :cond_0
    new-instance p0, Ljava/lang/RuntimeException;

    invoke-direct {p0, p1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    throw p0
.end method

.method static synthetic access$000(Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;)Landroid/opengl/GLSurfaceView;
    .locals 0

    iget-object p0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mTargetSurface:Landroid/opengl/GLSurfaceView;

    return-object p0
.end method

.method private static checkNoGLES2Error()V
    .locals 4

    invoke-static {}, Landroid/opengl/GLES20;->glGetError()I

    move-result v0

    if-eqz v0, :cond_0

    const-string v1, "MiGLSurfaceViewRender"

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "GLES20 error:"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :cond_0
    if-nez v0, :cond_1

    const/4 v1, 0x1

    goto :goto_0

    :cond_1
    const/4 v1, 0x0

    :goto_0
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "GLES20 error: "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v1, v0}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->abortUnless(ZLjava/lang/String;)V

    return-void
.end method

.method private createProgram(Ljava/lang/String;Ljava/lang/String;)I
    .locals 6

    const v0, 0x8b31

    invoke-direct {p0, v0, p1}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->loadShader(ILjava/lang/String;)I

    move-result v0

    const v1, 0x8b30

    invoke-direct {p0, v1, p2}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->loadShader(ILjava/lang/String;)I

    move-result v1

    const-string v2, "MiGLSurfaceViewRender"

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string/jumbo v4, "vertex shader: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, " -- "

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-static {v2, p1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const-string p1, "MiGLSurfaceViewRender"

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "fragment shader: "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p2, " -- "

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p2

    invoke-static {p1, p2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-static {}, Landroid/opengl/GLES20;->glCreateProgram()I

    move-result p1

    const/4 p2, 0x1

    const/4 v2, 0x0

    if-lez p1, :cond_0

    move v3, p2

    goto :goto_0

    :cond_0
    move v3, v2

    :goto_0
    const-string v4, "Create OpenGL program failed."

    invoke-static {v3, v4}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->abortUnless(ZLjava/lang/String;)V

    const-string v3, "MiGLSurfaceViewRender"

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "program: "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz p1, :cond_1

    invoke-static {p1, v0}, Landroid/opengl/GLES20;->glAttachShader(II)V

    invoke-static {p1, v1}, Landroid/opengl/GLES20;->glAttachShader(II)V

    invoke-static {p1}, Landroid/opengl/GLES20;->glLinkProgram(I)V

    new-array v0, p2, [I

    const v1, 0x8b82

    invoke-static {p1, v1, v0, v2}, Landroid/opengl/GLES20;->glGetProgramiv(II[II)V

    aget v0, v0, v2

    if-eq v0, p2, :cond_1

    invoke-static {p1}, Landroid/opengl/GLES20;->glDeleteProgram(I)V

    nop

    move p1, v2

    :cond_1
    const-string p2, "MiGLSurfaceViewRender"

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, " end if program: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {p2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return p1
.end method

.method private loadShader(ILjava/lang/String;)I
    .locals 3

    invoke-static {p1}, Landroid/opengl/GLES20;->glCreateShader(I)I

    move-result p1

    const-string v0, "MiGLSurfaceViewRender"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string/jumbo v2, "shader: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const/4 v0, 0x0

    if-eqz p1, :cond_0

    invoke-static {p1, p2}, Landroid/opengl/GLES20;->glShaderSource(ILjava/lang/String;)V

    invoke-static {p1}, Landroid/opengl/GLES20;->glCompileShader(I)V

    const/4 p2, 0x1

    new-array p2, p2, [I

    const v1, 0x8b81

    invoke-static {p1, v1, p2, v0}, Landroid/opengl/GLES20;->glGetShaderiv(II[II)V

    aget p2, p2, v0

    if-nez p2, :cond_0

    invoke-static {p1}, Landroid/opengl/GLES20;->glDeleteShader(I)V

    nop

    move p1, v0

    :cond_0
    const-string p2, "MiGLSurfaceViewRender"

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "end shader: "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {p2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    return p1
.end method


# virtual methods
.method public DrawCameraPreview(IIII)V
    .locals 6

    invoke-direct {p0}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->TransferExternalImagetoFbo()V

    invoke-static {p1, p2, p3, p4}, Landroid/opengl/GLES20;->glViewport(IIII)V

    iget p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    invoke-static {p1}, Landroid/opengl/GLES20;->glUseProgram(I)V

    const p1, 0x84c0

    invoke-static {p1}, Landroid/opengl/GLES20;->glActiveTexture(I)V

    iget p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    const/16 p2, 0xde1

    invoke-static {p2, p1}, Landroid/opengl/GLES20;->glBindTexture(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    const-string/jumbo p2, "tex_rgb"

    invoke-static {p1, p2}, Landroid/opengl/GLES20;->glGetUniformLocation(ILjava/lang/String;)I

    move-result p1

    const/4 p2, 0x0

    invoke-static {p1, p2}, Landroid/opengl/GLES20;->glUniform1i(II)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    invoke-static {p1, p2}, Landroid/opengl/Matrix;->setIdentityM([FI)V

    iget p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mmodelMatrix:I

    iget-object p3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    const/4 p4, 0x1

    invoke-static {p1, p4, p2, p3, p2}, Landroid/opengl/GLES20;->glUniformMatrix4fv(IIZ[FI)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    const-string/jumbo p3, "vertexIn"

    invoke-static {p1, p3}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result p1

    invoke-static {p1}, Landroid/opengl/GLES20;->glEnableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget-object v5, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    const/4 v1, 0x2

    const/16 v2, 0x1406

    const/4 v3, 0x0

    const/4 v4, 0x0

    move v0, p1

    invoke-static/range {v0 .. v5}, Landroid/opengl/GLES20;->glVertexAttribPointer(IIIZILjava/nio/Buffer;)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget p3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mpreviewFilterProgramID:I

    const-string/jumbo p4, "textureIn"

    invoke-static {p3, p4}, Landroid/opengl/GLES20;->glGetAttribLocation(ILjava/lang/String;)I

    move-result p3

    invoke-static {p3}, Landroid/opengl/GLES20;->glEnableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    iget-object v5, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    move v0, p3

    invoke-static/range {v0 .. v5}, Landroid/opengl/GLES20;->glVertexAttribPointer(IIIZILjava/nio/Buffer;)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    const/4 p4, 0x5

    const/4 v0, 0x4

    invoke-static {p4, p2, v0}, Landroid/opengl/GLES20;->glDrawArrays(III)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {}, Landroid/opengl/GLES20;->glFlush()V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    invoke-static {p1}, Landroid/opengl/GLES20;->glDisableVertexAttribArray(I)V

    invoke-static {p3}, Landroid/opengl/GLES20;->glDisableVertexAttribArray(I)V

    invoke-static {}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->checkNoGLES2Error()V

    return-void
.end method

.method public bind(Landroid/graphics/Rect;II)V
    .locals 0

    invoke-direct {p0}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->TransferExternalImagetoFbo()V

    return-void
.end method

.method public init(Landroid/graphics/SurfaceTexture;)V
    .locals 9

    const-string v0, "MiGLSurfaceViewRender"

    const-string v1, "init :"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-direct {p0}, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->InitShaders()V

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v0}, Ljava/nio/ByteBuffer;->remaining()I

    move-result v0

    new-array v7, v0, [B

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v0, v7}, Ljava/nio/ByteBuffer;->get([B)Ljava/nio/ByteBuffer;

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->vertexVertices_buffer:Ljava/nio/ByteBuffer;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/nio/ByteBuffer;->position(I)Ljava/nio/Buffer;

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v0}, Ljava/nio/ByteBuffer;->remaining()I

    move-result v0

    new-array v8, v0, [B

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v0, v8}, Ljava/nio/ByteBuffer;->get([B)Ljava/nio/ByteBuffer;

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->textureVertices_buffer:Ljava/nio/ByteBuffer;

    invoke-virtual {v0, v1}, Ljava/nio/ByteBuffer;->position(I)Ljava/nio/Buffer;

    iget-object v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mOpenGlRender:Lcom/xiaomi/mediaprocess/OpenGlRender;

    iget v2, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mProgramID2:I

    iget v3, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    iget v4, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFragshaderRgb:I

    iget v5, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_VERTEX2:I

    iget v6, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->ATTRIB_TEXTURE2:I

    invoke-virtual/range {v1 .. v8}, Lcom/xiaomi/mediaprocess/OpenGlRender;->SetOpengGlRenderParams(IIIII[B[B)V

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mOpenGlRender:Lcom/xiaomi/mediaprocess/OpenGlRender;

    iget v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mFramebufferTexture:I

    invoke-virtual {v0, v1}, Lcom/xiaomi/mediaprocess/OpenGlRender;->SetCurrentGLContext(I)V

    iput-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mInputSurfaceTexture:Landroid/graphics/SurfaceTexture;

    iget-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mInputSurfaceTexture:Landroid/graphics/SurfaceTexture;

    iget v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mcamera_texture:I

    invoke-virtual {p1, v0}, Landroid/graphics/SurfaceTexture;->attachToGLContext(I)V

    return-void
.end method

.method public initColorFilter(Landroid/graphics/Bitmap;)V
    .locals 8

    if-nez p1, :cond_0

    return-void

    :cond_0
    const/high16 v0, 0x40000

    new-array v0, v0, [I

    const/4 v1, 0x0

    move v2, v1

    :goto_0
    const/16 v3, 0x200

    if-ge v2, v3, :cond_3

    move v4, v1

    :goto_1
    if-ge v4, v3, :cond_2

    invoke-virtual {p1, v4, v2}, Landroid/graphics/Bitmap;->getPixel(II)I

    move-result v5

    invoke-virtual {p1}, Landroid/graphics/Bitmap;->hasAlpha()Z

    move-result v6

    if-eqz v6, :cond_1

    invoke-static {v5}, Landroid/graphics/Color;->alpha(I)I

    move-result v6

    goto :goto_2

    :cond_1
    const/16 v6, 0xff

    :goto_2
    mul-int/lit16 v6, v6, 0x100

    invoke-static {v5}, Landroid/graphics/Color;->blue(I)I

    move-result v7

    add-int/2addr v6, v7

    mul-int/lit16 v6, v6, 0x100

    invoke-static {v5}, Landroid/graphics/Color;->green(I)I

    move-result v7

    add-int/2addr v6, v7

    mul-int/lit16 v6, v6, 0x100

    invoke-static {v5}, Landroid/graphics/Color;->red(I)I

    move-result v5

    add-int/2addr v6, v5

    mul-int/lit16 v5, v2, 0x200

    add-int/2addr v5, v4

    aput v6, v0, v5

    add-int/lit8 v4, v4, 0x1

    goto :goto_1

    :cond_2
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    :cond_3
    array-length p1, v0

    mul-int/lit8 p1, p1, 0x20

    invoke-static {p1}, Ljava/nio/ByteBuffer;->allocateDirect(I)Ljava/nio/ByteBuffer;

    move-result-object p1

    iput-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    iget-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    invoke-static {}, Ljava/nio/ByteOrder;->nativeOrder()Ljava/nio/ByteOrder;

    move-result-object v2

    invoke-virtual {p1, v2}, Ljava/nio/ByteBuffer;->order(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;

    iget-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->asIntBuffer()Ljava/nio/IntBuffer;

    move-result-object p1

    invoke-virtual {p1, v0}, Ljava/nio/IntBuffer;->put([I)Ljava/nio/IntBuffer;

    iget-object p1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->RGBColor:Ljava/nio/ByteBuffer;

    invoke-virtual {p1, v1}, Ljava/nio/ByteBuffer;->position(I)Ljava/nio/Buffer;

    return-void
.end method

.method public updateTexImage()V
    .locals 8

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mInputSurfaceTexture:Landroid/graphics/SurfaceTexture;

    invoke-virtual {v0}, Landroid/graphics/SurfaceTexture;->updateTexImage()V

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mInputSurfaceTexture:Landroid/graphics/SurfaceTexture;

    iget-object v1, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    invoke-virtual {v0, v1}, Landroid/graphics/SurfaceTexture;->getTransformMatrix([F)V

    iget-object v0, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    const/4 v1, 0x0

    invoke-static {v0, v1}, Landroid/opengl/Matrix;->setIdentityM([FI)V

    iget-object v2, p0, Lcom/android/camera/module/impl/component/MiGLSurfaceViewRender;->mtransformMatrix:[F

    const/4 v3, 0x0

    const/high16 v4, -0x3d4c0000    # -90.0f

    const/4 v5, 0x0

    const/4 v6, 0x0

    const/high16 v7, 0x3f800000    # 1.0f

    invoke-static/range {v2 .. v7}, Landroid/opengl/Matrix;->rotateM([FIFFFF)V

    return-void
.end method