package ru.a5x5retail.db;

import java.util.ArrayList;
import java.util.List;


public class DataBaseQuery {

    /**********************************************************************************************/


    public interface IAsyncQueryEventListener {
        void onAsyncQueryEvent(CallableQAsync query, int returnCode, String returnMessage);

    }

    protected List<IAsyncQueryEventListener> asyncQueryEventListenerList;

    public void addAsyncQueryEventListener(IAsyncQueryEventListener listener) {
        if (asyncQueryEventListenerList == null) {
            asyncQueryEventListenerList = new ArrayList<>();
        }
        if (!asyncQueryEventListenerList.contains(listener)) {
            asyncQueryEventListenerList.add(listener);
        }
    }

    public void removeAsyncQueryEventListener(IAsyncQueryEventListener listener) {
        if (asyncQueryEventListenerList == null) {
            return;
        }
        if (asyncQueryEventListenerList.contains(listener)) {
            asyncQueryEventListenerList.remove(listener);
        }
    }

    protected void sendAsyncQueryEvent(CallableQAsync query, int returnCode, String returnMessage) {
        if (asyncQueryEventListenerList != null) {
            for (IAsyncQueryEventListener listener : asyncQueryEventListenerList) {
                listener.onAsyncQueryEvent(query, returnCode, returnMessage);
            }
        }
    }

    /**********************************************************************************************/


    public interface IAsyncQueryExceptionListener {
        void onAsyncQueryException(CallableQAsync query, Exception e);
    }

    protected List<IAsyncQueryExceptionListener> asyncQueryExceptionListenerList;

    public void addAsyncExceptionEventListener(IAsyncQueryExceptionListener listener) {
        if (asyncQueryExceptionListenerList == null) {
            asyncQueryExceptionListenerList = new ArrayList<>();
        }
        if (!asyncQueryExceptionListenerList.contains(listener)) {
            asyncQueryExceptionListenerList.add(listener);
        }
    }

    public void removeAsyncvEventListener(IAsyncQueryExceptionListener listener) {
        if (asyncQueryExceptionListenerList == null) {
            return;
        }
        if (asyncQueryExceptionListenerList.contains(listener)) {
            asyncQueryExceptionListenerList.remove(listener);
        }
    }

    protected void sendAsyncQueryException(CallableQAsync query, Exception e) {
        if (asyncQueryExceptionListenerList != null) {
            for (IAsyncQueryExceptionListener listener : asyncQueryExceptionListenerList) {
                listener.onAsyncQueryException(query, e);
            }
        }
    }


    /**********************************************************************************************/


    public interface OnSuccessfulListener {
        void onSuccessful();
    }

    protected List<OnSuccessfulListener> onSuccessfulListenerList;

    public void addOnSuccessfulListener(OnSuccessfulListener canceled) {
        if (onSuccessfulListenerList == null) {
            onSuccessfulListenerList = new ArrayList<>();
        }
        if (!onSuccessfulListenerList.contains(canceled)) {
            onSuccessfulListenerList.add(canceled);
        }
    }

    public void removeOnSuccessfulListener(OnSuccessfulListener canceled) {
        if (onSuccessfulListenerList == null) {
            return;
        }
        if (onSuccessfulListenerList.contains(canceled)) {
            onSuccessfulListenerList.remove(canceled);
        }
    }

    protected void sendOnSuccessfulListener() {
        if (onSuccessfulListenerList != null) {
            for (OnSuccessfulListener listener : onSuccessfulListenerList) {
                listener.onSuccessful();
            }
        }
    }

    /**********************************************************************************************/
}
