package com.xiaomi.stat;

import com.xiaomi.stat.a.c;

/* renamed from: com.xiaomi.stat.i  reason: case insensitive filesystem */
class C0166i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f559a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0162e f560b;

    C0166i(C0162e eVar, boolean z) {
        this.f560b = eVar;
        this.f559a = z;
    }

    public void run() {
        if (C0159b.d(this.f560b.f549c)) {
            int i = 2;
            if (!this.f559a && C0159b.e(this.f560b.f549c) != 2) {
                c.a().a(this.f560b.f549c);
            }
            String b2 = this.f560b.f549c;
            if (this.f559a) {
                i = 1;
            }
            C0159b.a(b2, i);
        }
    }
}
