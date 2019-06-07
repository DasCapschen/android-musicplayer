package de.dascapschen.android.jeanne.singletons;

import android.content.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SingletonBase<T>
{
    protected static SingletonBase mInstance = null;
    protected Map<Integer, T> mData = null;

    protected SingletonBase(){}

    public static SingletonBase instance(Context context)
    {
        if( mInstance.mData == null )
        {
            mInstance.mData = new HashMap<>();
            mInstance.queryAll(context);
        }
        return mInstance;
    }

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
