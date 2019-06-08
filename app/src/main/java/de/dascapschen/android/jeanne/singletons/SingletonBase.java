package de.dascapschen.android.jeanne.singletons;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import de.dascapschen.android.jeanne.data.MusicalData;

public abstract class SingletonBase<T extends MusicalData>
{
    private ArrayList<T> mData = new ArrayList<>();
    private HashMap<Integer, Integer> mKeys = new HashMap<>();

    protected void put(int key, T datum)
    {
        mData.add(datum);
        mKeys.put(datum.getId(), mData.indexOf(datum));
    }

    public ArrayList<T> data()
    {
        return mData;
    }

    public T getByIndex(int index)
    {
        return mData.get(index);
    }
    public T getByKey(int key) { return mData.get( mKeys.get(key) ); }

    protected abstract void queryAll(Context context);
}
