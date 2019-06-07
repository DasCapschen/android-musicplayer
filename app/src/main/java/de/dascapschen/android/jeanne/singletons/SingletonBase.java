package de.dascapschen.android.jeanne.singletons;

import android.content.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SingletonBase<T>
{
    protected Map<Integer, T> mData = null;

    public Collection<T> data()
    {
        return mData.values();
    }

    public T get(int key)
    {
        return mData.get(key);
    }

    protected abstract void queryAll(Context context);
}
