package miui.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import miui.util.concurrent.ConcurrentRingQueue;

public final class Pools {
    /* access modifiers changed from: private */
    public static final HashMap<Class<?>, InstanceHolder<?>> Tk = new HashMap<>();
    /* access modifiers changed from: private */
    public static final HashMap<Class<?>, SoftReferenceInstanceHolder<?>> Tl = new HashMap<>();
    private static final Pool<StringBuilder> Tm = createSoftReferencePool(new Manager<StringBuilder>() {
        /* renamed from: dF */
        public StringBuilder createInstance() {
            return new StringBuilder();
        }

        /* renamed from: a */
        public void onRelease(StringBuilder sb) {
            sb.setLength(0);
        }
    }, 4);

    static abstract class BasePool<T> implements Pool<T> {
        private final Object Jz = new Object() {
            /* access modifiers changed from: protected */
            public void finalize() throws Throwable {
                try {
                    BasePool.this.close();
                } finally {
                    super.finalize();
                }
            }
        };
        private final int QN;
        private final Manager<T> Tn;
        private IInstanceHolder<T> To;

        /* access modifiers changed from: package-private */
        public abstract void a(IInstanceHolder<T> iInstanceHolder, int i);

        /* access modifiers changed from: package-private */
        public abstract IInstanceHolder<T> d(Class<T> cls, int i);

        public BasePool(Manager<T> manager, int i) {
            if (manager == null || i < 1) {
                this.QN = this.Jz.hashCode();
                throw new IllegalArgumentException("manager cannot be null and size cannot less then 1");
            }
            this.Tn = manager;
            this.QN = i;
            T createInstance = this.Tn.createInstance();
            if (createInstance != null) {
                this.To = d(createInstance.getClass(), i);
                doRelease(createInstance);
                return;
            }
            throw new IllegalStateException("manager create instance cannot return null");
        }

        /* access modifiers changed from: protected */
        public final T doAcquire() {
            if (this.To != null) {
                T t = this.To.get();
                if (t == null) {
                    t = this.Tn.createInstance();
                    if (t == null) {
                        throw new IllegalStateException("manager create instance cannot return null");
                    }
                }
                this.Tn.onAcquire(t);
                return t;
            }
            throw new IllegalStateException("Cannot acquire object after close()");
        }

        /* access modifiers changed from: protected */
        public final void doRelease(T t) {
            if (this.To == null) {
                throw new IllegalStateException("Cannot release object after close()");
            } else if (t != null) {
                this.Tn.onRelease(t);
                if (!this.To.put(t)) {
                    this.Tn.onDestroy(t);
                }
            }
        }

        public T acquire() {
            return doAcquire();
        }

        public void release(T t) {
            doRelease(t);
        }

        public void close() {
            if (this.To != null) {
                a(this.To, this.QN);
                this.To = null;
            }
        }

        public int getSize() {
            if (this.To == null) {
                return 0;
            }
            return this.QN;
        }
    }

    private interface IInstanceHolder<T> {
        Class<T> eh();

        T get();

        int getSize();

        boolean put(T t);

        void resize(int i);
    }

    private static class InstanceHolder<T> implements IInstanceHolder<T> {
        private final Class<T> Tq;
        private final ConcurrentRingQueue<T> Tr;

        InstanceHolder(Class<T> cls, int i) {
            this.Tq = cls;
            this.Tr = new ConcurrentRingQueue<>(i, false, true);
        }

        public Class<T> eh() {
            return this.Tq;
        }

        public int getSize() {
            return this.Tr.getCapacity();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x002f, code lost:
            return;
         */
        public synchronized void resize(int i) {
            int capacity = i + this.Tr.getCapacity();
            if (capacity <= 0) {
                synchronized (Pools.Tk) {
                    Pools.Tk.remove(eh());
                }
            } else if (capacity > 0) {
                this.Tr.increaseCapacity(capacity);
            } else {
                this.Tr.decreaseCapacity(-capacity);
            }
        }

        public T get() {
            return this.Tr.get();
        }

        public boolean put(T t) {
            return this.Tr.put(t);
        }
    }

    public static abstract class Manager<T> {
        public abstract T createInstance();

        public void onAcquire(T t) {
        }

        public void onRelease(T t) {
        }

        public void onDestroy(T t) {
        }
    }

    public interface Pool<T> {
        T acquire();

        void close();

        int getSize();

        void release(T t);
    }

    public static class SimplePool<T> extends BasePool<T> {
        public /* bridge */ /* synthetic */ Object acquire() {
            return super.acquire();
        }

        public /* bridge */ /* synthetic */ void close() {
            super.close();
        }

        public /* bridge */ /* synthetic */ int getSize() {
            return super.getSize();
        }

        public /* bridge */ /* synthetic */ void release(Object obj) {
            super.release(obj);
        }

        SimplePool(Manager<T> manager, int i) {
            super(manager, i);
        }

        /* access modifiers changed from: package-private */
        public final IInstanceHolder<T> d(Class<T> cls, int i) {
            return Pools.b(cls, i);
        }

        /* access modifiers changed from: package-private */
        public final void a(IInstanceHolder<T> iInstanceHolder, int i) {
            Pools.a((InstanceHolder) iInstanceHolder, i);
        }
    }

    private static class SoftReferenceInstanceHolder<T> implements IInstanceHolder<T> {
        private volatile int QN;
        private final Class<T> Tq;
        private volatile SoftReference<T>[] Ts;
        private volatile int mIndex = 0;

        SoftReferenceInstanceHolder(Class<T> cls, int i) {
            this.Tq = cls;
            this.QN = i;
            this.Ts = new SoftReference[i];
        }

        public Class<T> eh() {
            return this.Tq;
        }

        public int getSize() {
            return this.QN;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
            return;
         */
        public synchronized void resize(int i) {
            int i2 = i + this.QN;
            if (i2 <= 0) {
                synchronized (Pools.Tl) {
                    Pools.Tl.remove(eh());
                }
                return;
            }
            this.QN = i2;
            SoftReference<T>[] softReferenceArr = this.Ts;
            int i3 = this.mIndex;
            if (i2 > softReferenceArr.length) {
                SoftReference<T>[] softReferenceArr2 = new SoftReference[i2];
                System.arraycopy(softReferenceArr, 0, softReferenceArr2, 0, i3);
                this.Ts = softReferenceArr2;
            }
        }

        public synchronized T get() {
            int i = this.mIndex;
            SoftReference<T>[] softReferenceArr = this.Ts;
            while (i != 0) {
                i--;
                if (softReferenceArr[i] != null) {
                    T t = softReferenceArr[i].get();
                    softReferenceArr[i] = null;
                    if (t != null) {
                        this.mIndex = i;
                        return t;
                    }
                }
            }
            return null;
        }

        public synchronized boolean put(T t) {
            int i = this.mIndex;
            SoftReference<T>[] softReferenceArr = this.Ts;
            if (i >= this.QN) {
                int i2 = 0;
                while (i2 < i) {
                    if (softReferenceArr[i2] != null) {
                        if (softReferenceArr[i2].get() != null) {
                            i2++;
                        }
                    }
                    softReferenceArr[i2] = new SoftReference<>(t);
                    return true;
                }
                return false;
            }
            softReferenceArr[i] = new SoftReference<>(t);
            this.mIndex = i + 1;
            return true;
        }
    }

    public static class SoftReferencePool<T> extends BasePool<T> {
        public /* bridge */ /* synthetic */ Object acquire() {
            return super.acquire();
        }

        public /* bridge */ /* synthetic */ void close() {
            super.close();
        }

        public /* bridge */ /* synthetic */ int getSize() {
            return super.getSize();
        }

        public /* bridge */ /* synthetic */ void release(Object obj) {
            super.release(obj);
        }

        SoftReferencePool(Manager<T> manager, int i) {
            super(manager, i);
        }

        /* access modifiers changed from: package-private */
        public final IInstanceHolder<T> d(Class<T> cls, int i) {
            return Pools.c(cls, i);
        }

        /* access modifiers changed from: package-private */
        public final void a(IInstanceHolder<T> iInstanceHolder, int i) {
            Pools.a((SoftReferenceInstanceHolder) iInstanceHolder, i);
        }
    }

    public static Pool<StringBuilder> getStringBuilderPool() {
        return Tm;
    }

    static <T> InstanceHolder<T> b(Class<T> cls, int i) {
        InstanceHolder<T> instanceHolder;
        synchronized (Tk) {
            instanceHolder = Tk.get(cls);
            if (instanceHolder == null) {
                instanceHolder = new InstanceHolder<>(cls, i);
                Tk.put(cls, instanceHolder);
            } else {
                instanceHolder.resize(i);
            }
        }
        return instanceHolder;
    }

    static <T> void a(InstanceHolder<T> instanceHolder, int i) {
        synchronized (Tk) {
            instanceHolder.resize(-i);
        }
    }

    static <T> SoftReferenceInstanceHolder<T> c(Class<T> cls, int i) {
        SoftReferenceInstanceHolder<T> softReferenceInstanceHolder;
        synchronized (Tl) {
            softReferenceInstanceHolder = Tl.get(cls);
            if (softReferenceInstanceHolder == null) {
                softReferenceInstanceHolder = new SoftReferenceInstanceHolder<>(cls, i);
                Tl.put(cls, softReferenceInstanceHolder);
            } else {
                softReferenceInstanceHolder.resize(i);
            }
        }
        return softReferenceInstanceHolder;
    }

    static <T> void a(SoftReferenceInstanceHolder<T> softReferenceInstanceHolder, int i) {
        synchronized (Tl) {
            softReferenceInstanceHolder.resize(-i);
        }
    }

    public static <T> SimplePool<T> createSimplePool(Manager<T> manager, int i) {
        return new SimplePool<>(manager, i);
    }

    public static <T> SoftReferencePool<T> createSoftReferencePool(Manager<T> manager, int i) {
        return new SoftReferencePool<>(manager, i);
    }
}
