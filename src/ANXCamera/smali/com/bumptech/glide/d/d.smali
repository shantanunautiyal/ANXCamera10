.class public Lcom/bumptech/glide/d/d;
.super Ljava/lang/Object;
.source "ModelToResourceClassCache.java"


# instance fields
.field private final nC:Ljava/util/concurrent/atomic/AtomicReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/atomic/AtomicReference<",
            "Lcom/bumptech/glide/util/h;",
            ">;"
        }
    .end annotation
.end field

.field private final nD:Landroid/support/v4/util/ArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/ArrayMap<",
            "Lcom/bumptech/glide/util/h;",
            "Ljava/util/List<",
            "Ljava/lang/Class<",
            "*>;>;>;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .locals 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    new-instance v0, Ljava/util/concurrent/atomic/AtomicReference;

    invoke-direct {v0}, Ljava/util/concurrent/atomic/AtomicReference;-><init>()V

    iput-object v0, p0, Lcom/bumptech/glide/d/d;->nC:Ljava/util/concurrent/atomic/AtomicReference;

    new-instance v0, Landroid/support/v4/util/ArrayMap;

    invoke-direct {v0}, Landroid/support/v4/util/ArrayMap;-><init>()V

    iput-object v0, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    return-void
.end method


# virtual methods
.method public a(Ljava/lang/Class;Ljava/lang/Class;Ljava/util/List;)V
    .locals 3
    .param p1    # Ljava/lang/Class;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p2    # Ljava/lang/Class;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p3    # Ljava/util/List;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/util/List<",
            "Ljava/lang/Class<",
            "*>;>;)V"
        }
    .end annotation

    iget-object v0, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    monitor-enter v0

    :try_start_0
    iget-object v1, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    new-instance v2, Lcom/bumptech/glide/util/h;

    invoke-direct {v2, p1, p2}, Lcom/bumptech/glide/util/h;-><init>(Ljava/lang/Class;Ljava/lang/Class;)V

    invoke-virtual {v1, v2, p3}, Landroid/support/v4/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    monitor-exit v0

    return-void

    :catchall_0
    move-exception p1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw p1
.end method

.method public clear()V
    .locals 2

    iget-object v0, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    monitor-enter v0

    :try_start_0
    iget-object v1, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    invoke-virtual {v1}, Landroid/support/v4/util/ArrayMap;->clear()V

    monitor-exit v0

    return-void

    :catchall_0
    move-exception v1

    monitor-exit v0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    throw v1
.end method

.method public g(Ljava/lang/Class;Ljava/lang/Class;)Ljava/util/List;
    .locals 2
    .param p1    # Ljava/lang/Class;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p2    # Ljava/lang/Class;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class<",
            "*>;",
            "Ljava/lang/Class<",
            "*>;)",
            "Ljava/util/List<",
            "Ljava/lang/Class<",
            "*>;>;"
        }
    .end annotation

    iget-object v0, p0, Lcom/bumptech/glide/d/d;->nC:Ljava/util/concurrent/atomic/AtomicReference;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->getAndSet(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/bumptech/glide/util/h;

    if-nez v0, :cond_0

    new-instance v0, Lcom/bumptech/glide/util/h;

    invoke-direct {v0, p1, p2}, Lcom/bumptech/glide/util/h;-><init>(Ljava/lang/Class;Ljava/lang/Class;)V

    goto :goto_0

    :cond_0
    invoke-virtual {v0, p1, p2}, Lcom/bumptech/glide/util/h;->j(Ljava/lang/Class;Ljava/lang/Class;)V

    :goto_0
    iget-object p1, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    monitor-enter p1

    :try_start_0
    iget-object p2, p0, Lcom/bumptech/glide/d/d;->nD:Landroid/support/v4/util/ArrayMap;

    invoke-virtual {p2, v0}, Landroid/support/v4/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Ljava/util/List;

    monitor-exit p1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    iget-object p1, p0, Lcom/bumptech/glide/d/d;->nC:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {p1, v0}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    return-object p2

    :catchall_0
    move-exception p2

    :try_start_1
    monitor-exit p1
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    throw p2
.end method