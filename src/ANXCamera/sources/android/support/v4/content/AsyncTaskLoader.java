package android.support.v4.content;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.os.OperationCanceledException;
import android.support.v4.util.TimeUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public abstract class AsyncTaskLoader<D> extends Loader<D> {
    static final boolean DEBUG = false;
    static final String TAG = "AsyncTaskLoader";
    volatile LoadTask mCancellingTask;
    private final Executor mExecutor;
    Handler mHandler;
    long mLastLoadCompleteTime;
    volatile LoadTask mTask;
    long mUpdateThrottle;

    final class LoadTask extends ModernAsyncTask<Void, Void, D> implements Runnable {
        private final CountDownLatch mDone = new CountDownLatch(1);
        boolean waiting;

        LoadTask() {
        }

        /* access modifiers changed from: protected */
        public D doInBackground(Void... voidArr) {
            try {
                return AsyncTaskLoader.this.onLoadInBackground();
            } catch (OperationCanceledException e2) {
                if (isCancelled()) {
                    return null;
                }
                throw e2;
            }
        }

        /* access modifiers changed from: protected */
        public void onCancelled(D d2) {
            try {
                AsyncTaskLoader.this.dispatchOnCancelled(this, d2);
            } finally {
                this.mDone.countDown();
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(D d2) {
            try {
                AsyncTaskLoader.this.dispatchOnLoadComplete(this, d2);
            } finally {
                this.mDone.countDown();
            }
        }

        public void run() {
            this.waiting = false;
            AsyncTaskLoader.this.executePendingTask();
        }

        public void waitForLoader() {
            try {
                this.mDone.await();
            } catch (InterruptedException unused) {
            }
        }
    }

    public AsyncTaskLoader(@NonNull Context context) {
        this(context, ModernAsyncTask.THREAD_POOL_EXECUTOR);
    }

    private AsyncTaskLoader(@NonNull Context context, @NonNull Executor executor) {
        super(context);
        this.mLastLoadCompleteTime = -10000;
        this.mExecutor = executor;
    }

    public void cancelLoadInBackground() {
    }

    /* access modifiers changed from: 0000 */
    public void dispatchOnCancelled(LoadTask loadTask, D d2) {
        onCanceled(d2);
        if (this.mCancellingTask == loadTask) {
            rollbackContentChanged();
            this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
            this.mCancellingTask = null;
            deliverCancellation();
            executePendingTask();
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchOnLoadComplete(LoadTask loadTask, D d2) {
        if (this.mTask != loadTask) {
            dispatchOnCancelled(loadTask, d2);
        } else if (isAbandoned()) {
            onCanceled(d2);
        } else {
            commitContentChanged();
            this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
            this.mTask = null;
            deliverResult(d2);
        }
    }

    @Deprecated
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        String str2 = " waiting=";
        if (this.mTask != null) {
            printWriter.print(str);
            printWriter.print("mTask=");
            printWriter.print(this.mTask);
            printWriter.print(str2);
            printWriter.println(this.mTask.waiting);
        }
        if (this.mCancellingTask != null) {
            printWriter.print(str);
            printWriter.print("mCancellingTask=");
            printWriter.print(this.mCancellingTask);
            printWriter.print(str2);
            printWriter.println(this.mCancellingTask.waiting);
        }
        if (this.mUpdateThrottle != 0) {
            printWriter.print(str);
            printWriter.print("mUpdateThrottle=");
            TimeUtils.formatDuration(this.mUpdateThrottle, printWriter);
            printWriter.print(" mLastLoadCompleteTime=");
            TimeUtils.formatDuration(this.mLastLoadCompleteTime, SystemClock.uptimeMillis(), printWriter);
            printWriter.println();
        }
    }

    /* access modifiers changed from: 0000 */
    public void executePendingTask() {
        if (this.mCancellingTask == null && this.mTask != null) {
            if (this.mTask.waiting) {
                this.mTask.waiting = false;
                this.mHandler.removeCallbacks(this.mTask);
            }
            if (this.mUpdateThrottle <= 0 || SystemClock.uptimeMillis() >= this.mLastLoadCompleteTime + this.mUpdateThrottle) {
                this.mTask.executeOnExecutor(this.mExecutor, null);
            } else {
                this.mTask.waiting = true;
                this.mHandler.postAtTime(this.mTask, this.mLastLoadCompleteTime + this.mUpdateThrottle);
            }
        }
    }

    public boolean isLoadInBackgroundCanceled() {
        return this.mCancellingTask != null;
    }

    @Nullable
    public abstract D loadInBackground();

    /* access modifiers changed from: protected */
    public boolean onCancelLoad() {
        if (this.mTask == null) {
            return false;
        }
        if (!this.mStarted) {
            this.mContentChanged = true;
        }
        if (this.mCancellingTask != null) {
            if (this.mTask.waiting) {
                this.mTask.waiting = false;
                this.mHandler.removeCallbacks(this.mTask);
            }
            this.mTask = null;
            return false;
        } else if (this.mTask.waiting) {
            this.mTask.waiting = false;
            this.mHandler.removeCallbacks(this.mTask);
            this.mTask = null;
            return false;
        } else {
            boolean cancel = this.mTask.cancel(false);
            if (cancel) {
                this.mCancellingTask = this.mTask;
                cancelLoadInBackground();
            }
            this.mTask = null;
            return cancel;
        }
    }

    public void onCanceled(@Nullable D d2) {
    }

    /* access modifiers changed from: protected */
    public void onForceLoad() {
        super.onForceLoad();
        cancelLoad();
        this.mTask = new LoadTask<>();
        executePendingTask();
    }

    /* access modifiers changed from: protected */
    @Nullable
    public D onLoadInBackground() {
        return loadInBackground();
    }

    public void setUpdateThrottle(long j) {
        this.mUpdateThrottle = j;
        if (j != 0) {
            this.mHandler = new Handler();
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void waitForLoader() {
        LoadTask loadTask = this.mTask;
        if (loadTask != null) {
            loadTask.waitForLoader();
        }
    }
}
