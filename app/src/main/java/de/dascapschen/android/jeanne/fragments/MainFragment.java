package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.TabAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment
{


    public MainFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //setup tabs
        ViewPager viewPager = view.findViewById(R.id.frag_main_viewPager);
        TabLayout tabs = view.findViewById(R.id.frag_main_tabs);

        TabAdapter adapter = new TabAdapter(getContext(), getChildFragmentManager());

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }
}
