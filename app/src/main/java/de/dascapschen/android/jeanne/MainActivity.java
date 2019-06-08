package de.dascapschen.android.jeanne;

import android.Manifest;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.transition.AutoTransition;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllAlbums;
import de.dascapschen.android.jeanne.singletons.AllArtists;
import de.dascapschen.android.jeanne.singletons.AllPlaylists;
import de.dascapschen.android.jeanne.singletons.AllSongs;

public class MainActivity extends AppCompatActivity
        implements SongController, NavigationRequest, MediaPlayer.OnCompletionListener
{
    MediaPlayer mediaPlayer;

    //we use a list so we can go back
    ArrayList<Song> playlist;
    int index = 0;
    boolean repeat = false;
    boolean shuffle = false;

    ViewGroup sceneRoot;
    float bottomSheetPosition = 0;
    boolean bottomSheetOpen = false;
    boolean animationRunning = false;

    NavController navController;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(this)
                    .setIcon( android.R.drawable.ic_dialog_alert )
                    .setTitle(R.string.alert_need_permission)
                    .setMessage(R.string.alert_need_permission_text)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    })
                    .show();
        }
        else
        {
            setupView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate( R.menu.popup_menu, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home: //back button
                back();
                break;
            case R.id.menu_item_settings:
                break;
            case R.id.menu_item_about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void navigate(int actionID)
    {
        navController.navigate(actionID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void back()
    {
        navController.popBackStack(R.id.destination_main, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onBackPressed()
    {
        if(navController.getCurrentDestination().getId() != R.id.destination_main)
        {
            back();
        }
        else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //ask for permission first
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            queryAllData();
            setupView();
        }
    }

    private void queryAllData()
    {
        AllArtists.initialize(this);
        AllAlbums.initialize(this);
        AllPlaylists.initialize(this);
        AllSongs.initialize(this);
    }

    private void setupView()
    {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController( findViewById(R.id.main_navHost) );

        setupBottomSheet();
    }

    private void setupBottomSheet()
    {
        TextView songText = findViewById(R.id.bottom_song_title);
        songText.setSelected(true); // to make marquee scrolling work

        //setup bottom drawer
        BottomSheetBehavior bottomSheet =
                BottomSheetBehavior.from( findViewById(R.id.main_bottomSheetFrame) );

        final GradientDrawable bottomBg = (GradientDrawable)getResources()
                .getDrawable(R.drawable.bottomsheet_bg, getTheme());

        final ValueAnimator bgAnim = ValueAnimator.ofFloat(100,0);
        bgAnim.setDuration(100);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                bottomBg.setCornerRadius( (float)animation.getAnimatedValue() );
            }
        });

        sceneRoot = (ViewGroup) findViewById(R.id.main_bottomSheetFrame);
        final Scene openScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_open, this);
        final Scene collapsedScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_closed, this);

        final AutoTransition transition = new AutoTransition();
        TimeInterpolator interpolator = new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {return bottomSheetPosition;}
        };

        //FIXME: hacky af
        transition.setInterpolator(interpolator);
        transition.setDuration(999999999);
        transition.setOrdering( TransitionSet.ORDERING_TOGETHER );

        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i)
            {
                switch( i )
                {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetOpen = false;
                        animationRunning = false;
                        TransitionManager.endTransitions(sceneRoot);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetOpen = true;
                        animationRunning = false;
                        TransitionManager.endTransitions(sceneRoot);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v)
            {
                bgAnim.setCurrentPlayTime( (long)(v * 100) );
                if(bottomSheetOpen)
                {
                    if(!animationRunning)
                    {
                        TransitionManager.go(collapsedScene, transition);
                        animationRunning = true;
                    }
                    bottomSheetPosition = 1-v;
                }
                else
                {
                    if(!animationRunning)
                    {
                        TransitionManager.go(openScene, transition);
                        animationRunning = true;
                    }
                    bottomSheetPosition = v;
                }
            }
        });
    }

    @Override
    public void startNewSong(Song song)   //when user clicks on a song.
    {
        if( mediaPlayer != null )
        {
            mediaPlayer.release();
            mediaPlayer = null;     //kein Plan ob release den auf null setzt.
        }

        mediaPlayer = MediaPlayer.create(this, song.getUri());
        mediaPlayer.setOnCompletionListener(this);

        TextView text = findViewById(R.id.bottom_song_title);
        ImageView image = findViewById(R.id.bottom_album_image);

        text.setText( song.getName() );
        //TODO: image.setImageDrawable( song.getImage() );

        playSong();
    }

    @Override
    public void setPlaylist(ArrayList<Song> songs)
    {
        playlist = songs;
    }

    @Override
    public void setPlaylist(Artist artist)
    {
        List<Integer> albumIds = artist.getAlbumIDs();
        List<Integer> songIds = new ArrayList<>();

        AllAlbums allAlbums = AllAlbums.instance();

        for( int album : albumIds ) {
            songIds.addAll( allAlbums.getByKey(album).getSongIds() );
        }

        AllSongs allSongs = AllSongs.instance();

        playlist.clear();
        for( int songID : songIds ){
            playlist.add( allSongs.getByKey(songID) );
        }

    }

    @Override
    public void setPlaylist(Album album)
    {
        AllSongs allSongs = AllSongs.instance();

        playlist.clear();
        for( int songid : album.getSongIds() ){
            playlist.add( allSongs.getByKey(songid) );
        }
    }

    @Override
    public void playSong()
    {
        mediaPlayer.start();

        ImageButton btn = findViewById(R.id.btnPlay);
        btn.setImageResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void pauseSong()
    {
        mediaPlayer.pause();

        ImageButton btn = findViewById(R.id.btnPlay);
        btn.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void nextSong()
    {
        if( playlist != null && !playlist.isEmpty() )
        {
            index = (index+1) % playlist.size();
            startNewSong( playlist.get(index) );
        }
    }

    @Override
    public void prevSong()
    {
        if( playlist != null && !playlist.isEmpty() )
        {
            if( index-1 < 0 )
                index = playlist.size()-1;  //for some reason (-1) % x does not become positive!
            else
                index--;
            startNewSong( playlist.get(index) );
        }
    }

    public void onBtnPlayPressed(View v)
    {
        if( mediaPlayer.isPlaying() )
        {
            pauseSong();
        }
        else
        {
            playSong();
        }
    }

    public void onBtnNextPressed(View v)
    {
        nextSong();
    }

    public void onBtnPrevPressed(View v)
    {
        prevSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if ( playlist != null && !playlist.isEmpty()
            && (index+1 < playlist.size() || repeat) )
        {
            nextSong();
        }
        else
        {
            ImageButton btn = findViewById(R.id.btnPlay);
            btn.setImageResource(android.R.drawable.ic_media_play);
        }
    }
}
