package io.reactivex.internal.disposables;

import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ArrayCompositeDisposable extends AtomicReferenceArray<Disposable> implements Disposable {
    private static final long serialVersionUID = 2746389416410565408L;

    public ArrayCompositeDisposable(int i) {
        super(i);
    }

    public void dispose() {
        if (get(0) != DisposableHelper.DISPOSED) {
            int length = length();
            for (int i = 0; i < length; i++) {
                Disposable disposable = (Disposable) get(i);
                DisposableHelper disposableHelper = DisposableHelper.DISPOSED;
                if (disposable != disposableHelper) {
                    Disposable disposable2 = (Disposable) getAndSet(i, disposableHelper);
                    if (!(disposable2 == DisposableHelper.DISPOSED || disposable2 == null)) {
                        disposable2.dispose();
                    }
                }
            }
        }
    }

    public boolean isDisposed() {
        return get(0) == DisposableHelper.DISPOSED;
    }

    public Disposable replaceResource(int i, Disposable disposable) {
        Disposable disposable2;
        do {
            disposable2 = (Disposable) get(i);
            if (disposable2 == DisposableHelper.DISPOSED) {
                disposable.dispose();
                return null;
            }
        } while (!compareAndSet(i, disposable2, disposable));
        return disposable2;
    }

    public boolean setResource(int i, Disposable disposable) {
        Disposable disposable2;
        do {
            disposable2 = (Disposable) get(i);
            if (disposable2 == DisposableHelper.DISPOSED) {
                disposable.dispose();
                return false;
            }
        } while (!compareAndSet(i, disposable2, disposable));
        if (disposable2 != null) {
            disposable2.dispose();
        }
        return true;
    }
}
