package de.dascapschen.android.jeanne.singletons;

import android.content.Context;

import de.dascapschen.android.jeanne.data.Artist;

public class AllArtists extends SingletonBase<Artist>
{
    private AllArtists(){}

    @Override
    protected void queryAll(Context context)
    {
        super.queryAll(context);
    }
}
