.class Lcom/ss/android/vesdk/runtime/VERuntime$3;
.super Ljava/lang/Thread;
.source "VERuntime.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/ss/android/vesdk/runtime/VERuntime;->initConfig()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/ss/android/vesdk/runtime/VERuntime;


# direct methods
.method constructor <init>(Lcom/ss/android/vesdk/runtime/VERuntime;)V
    .locals 0

    iput-object p1, p0, Lcom/ss/android/vesdk/runtime/VERuntime$3;->this$0:Lcom/ss/android/vesdk/runtime/VERuntime;

    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    :try_start_0
    iget-object v0, p0, Lcom/ss/android/vesdk/runtime/VERuntime$3;->this$0:Lcom/ss/android/vesdk/runtime/VERuntime;

    iget-object v0, v0, Lcom/ss/android/vesdk/runtime/VERuntime;->mContext:Landroid/content/Context;

    invoke-static {v0}, Lcom/ss/android/ttve/monitor/DeviceInfoDetector;->init(Landroid/content/Context;)V

    invoke-static {}, Lcom/ss/android/vesdk/runtime/persistence/VESP;->getInstance()Lcom/ss/android/vesdk/runtime/persistence/VESP;

    move-result-object v0

    const-string v1, "sensor_reported"

    const/4 v2, 0x0

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Lcom/ss/android/vesdk/runtime/persistence/VESP;->get(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Boolean;

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/ss/android/vesdk/runtime/VERuntime$3;->this$0:Lcom/ss/android/vesdk/runtime/VERuntime;

    iget-object v0, v0, Lcom/ss/android/vesdk/runtime/VERuntime;->mContext:Landroid/content/Context;

    invoke-static {v0}, Lcom/ss/android/ttve/monitor/MonitorUtils;->sensorReport(Landroid/content/Context;)V

    invoke-static {}, Lcom/ss/android/vesdk/runtime/persistence/VESP;->getInstance()Lcom/ss/android/vesdk/runtime/persistence/VESP;

    move-result-object v0

    const-string v1, "sensor_reported"

    const/4 v2, 0x1

    invoke-static {v2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    invoke-virtual {v0, v1, v2}, Lcom/ss/android/vesdk/runtime/persistence/VESP;->put(Ljava/lang/String;Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :cond_0
    goto :goto_0

    :catch_0
    move-exception v0

    const-string v1, "VERuntime"

    const-string v2, "DeviceInfoDetector init failed"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :goto_0
    iget-object v0, p0, Lcom/ss/android/vesdk/runtime/VERuntime$3;->this$0:Lcom/ss/android/vesdk/runtime/VERuntime;

    invoke-static {v0}, Lcom/ss/android/vesdk/runtime/VERuntime;->access$200(Lcom/ss/android/vesdk/runtime/VERuntime;)Z

    move-result v0

    if-eqz v0, :cond_1

    const-string v0, "on"

    const-string v1, "on"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    :try_start_1
    invoke-static {}, Lcom/ss/android/vesdk/runtime/cloudconfig/PerformanceConfig;->restoreFromCache()V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_1

    :catch_1
    move-exception v0

    const-string v1, "VERuntime"

    const-string v2, "PerformanceConfig restoreFromCache failed"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :goto_1
    invoke-static {}, Lcom/ss/android/vesdk/runtime/cloudconfig/PerformanceConfig;->fetch()V

    :cond_1
    return-void
.end method