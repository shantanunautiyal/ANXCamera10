package com.arcsoft.camera.utils;

import android.os.Parcel;
import android.os.Parcelable;

/* compiled from: MPoint */
public class b implements Parcelable {

    /* renamed from: c  reason: collision with root package name */
    public static final Parcelable.Creator<b> f158c = new f();

    /* renamed from: a  reason: collision with root package name */
    public int f159a;

    /* renamed from: b  reason: collision with root package name */
    public int f160b;

    public b() {
    }

    public b(int i, int i2) {
        this.f159a = i;
        this.f160b = i2;
    }

    public b(b bVar) {
        this.f159a = bVar.f159a;
        this.f160b = bVar.f160b;
    }

    public int a() {
        return 0;
    }

    public void a(Parcel parcel) {
        this.f159a = parcel.readInt();
        this.f160b = parcel.readInt();
    }

    public void a(Parcel parcel, int i) {
        parcel.writeInt(this.f159a);
        parcel.writeInt(this.f160b);
    }

    public final boolean a(int i, int i2) {
        return this.f159a == i && this.f160b == i2;
    }

    public int b() {
        return (this.f159a * 31) + this.f160b;
    }

    public final void c() {
        this.f159a = -this.f159a;
        this.f160b = -this.f160b;
    }

    public String d() {
        return "Point(" + this.f159a + ", " + this.f160b + ")";
    }

    public final void d(int i, int i2) {
        this.f159a += i;
        this.f160b += i2;
    }

    public void e(int i, int i2) {
        this.f159a = i;
        this.f160b = i2;
    }

    public boolean h(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && b.class == obj.getClass()) {
            b bVar = (b) obj;
            if (this.f159a == bVar.f159a && this.f160b == bVar.f160b) {
                return true;
            }
        }
        return false;
    }
}
