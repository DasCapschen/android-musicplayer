package de.dascapschen.android.jeanne.singletons;

import android.content.Context;

import de.dascapschen.android.jeanne.data.Album;

public class AllAlbums extends SingletonBase<Album>
{
    private AllAlbums() {}

    @Override
    protected void queryAll(Context context)
    {
        super.queryAll(context);
    }
}
