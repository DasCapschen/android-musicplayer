package de.dascapschen.android.jeanne.adapters;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.fragments.AlbumsFragment;
import de.dascapschen.android.jeanne.fragments.ArtistsFragment;
import de.dascapschen.android.jeanne.fragments.FoldersFragment;
import de.dascapschen.android.jeanne.fragments.PlaylistsFragment;
import de.dascapschen.android.jeanne.fragments.TitlesFragment;

public class TabAdapter extends FragmentPagerAdapter
{

    Context mContext;

    private final ArrayList<Fragment> fragments;
    private final ArrayList<String> titles;

    public TabAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;

        fragments = new ArrayList<>();
        titles = new ArrayList<>();

        fragments.add( new TitlesFragment() );
        titles.add( mContext.getString(R.string.tab_titles) );

        fragments.add( new AlbumsFragment() );
        titles.add( mContext.getString(R.string.tab_albums) );

        fragments.add( new ArtistsFragment() );
        titles.add( mContext.getString(R.string.tab_artists) );

        fragments.add( new PlaylistsFragment() );
        titles.add( mContext.getString(R.string.tab_playlists) );

        fragments.add( new FoldersFragment() );
        titles.add( mContext.getString(R.string.tab_folders) );
    }

    @Override
    public Fragment getItem(int i)
    {
        return fragments.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i)
    {
        return titles.get(i);
    }

    @Override
    public int getCount()
    {
        return titles.size();
    }
}
