.class public Lcom/ss/android/ttve/common/TETrackIndexManager;
.super Ljava/lang/Object;
.source "TETrackIndexManager.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/ss/android/ttve/common/TETrackIndexManager$TETrackType;
    }
.end annotation


# static fields
.field public static final TRACK_TYPE_AUDIO:I = 0x1

.field public static final TRACK_TYPE_VIDEO:I = 0x2


# instance fields
.field private mAudioTrackIndexList:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field private mFirstAudioIndex:I

.field private mFirstVideoIndex:I

.field private mVideoTrackIndexList:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .locals 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, -0x1

    iput v0, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    iput v0, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    return-void
.end method


# virtual methods
.method public addTrack(II)I
    .locals 1

    const/4 v0, -0x1

    packed-switch p1, :pswitch_data_0

    return p2

    :pswitch_0
    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    if-ne p1, v0, :cond_0

    iput p2, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    :cond_0
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    if-lez p1, :cond_1

    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    iget-object p2, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-interface {p2}, Ljava/util/List;->size()I

    move-result p2

    add-int/lit8 p2, p2, -0x1

    invoke-interface {p1, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/lang/Integer;

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    add-int/lit8 p2, p1, 0x1

    :cond_1
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    invoke-interface {p1, v0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return p2

    :pswitch_1
    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    if-ne p1, v0, :cond_2

    iput p2, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    :cond_2
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    if-lez p1, :cond_3

    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    iget-object p2, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-interface {p2}, Ljava/util/List;->size()I

    move-result p2

    add-int/lit8 p2, p2, -0x1

    invoke-interface {p1, p2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/lang/Integer;

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    add-int/lit8 p2, p1, 0x1

    :cond_3
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    invoke-interface {p1, v0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return p2

    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getNativeTrackIndex(II)I
    .locals 2

    const/4 v0, 0x0

    const/4 v1, -0x1

    packed-switch p1, :pswitch_data_0

    goto :goto_4

    :pswitch_0
    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    if-lt p2, p1, :cond_2

    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    if-ne p1, v1, :cond_0

    goto :goto_1

    :cond_0
    :goto_0
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    if-ge v0, p1, :cond_7

    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-interface {p1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/lang/Integer;

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    if-ne p2, p1, :cond_1

    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstVideoIndex:I

    add-int/2addr v0, p1

    return v0

    :cond_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_2
    :goto_1
    return p2

    :pswitch_1
    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    if-lt p2, p1, :cond_6

    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    if-ne p1, v1, :cond_3

    goto :goto_3

    :cond_3
    :goto_2
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    if-ge v0, p1, :cond_5

    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-interface {p1, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object p1

    check-cast p1, Ljava/lang/Integer;

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result p1

    if-ne p2, p1, :cond_4

    iget p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mFirstAudioIndex:I

    add-int/2addr v0, p1

    return v0

    :cond_4
    add-int/lit8 v0, v0, 0x1

    goto :goto_2

    :cond_5
    goto :goto_4

    :cond_6
    :goto_3
    return p2

    :cond_7
    :goto_4
    return p2

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public removeTrack(II)V
    .locals 0

    packed-switch p1, :pswitch_data_0

    return-void

    :pswitch_0
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mVideoTrackIndexList:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    invoke-interface {p1, p2}, Ljava/util/List;->remove(Ljava/lang/Object;)Z

    return-void

    :pswitch_1
    iget-object p1, p0, Lcom/ss/android/ttve/common/TETrackIndexManager;->mAudioTrackIndexList:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object p2

    invoke-interface {p1, p2}, Ljava/util/List;->remove(Ljava/lang/Object;)Z

    return-void

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method