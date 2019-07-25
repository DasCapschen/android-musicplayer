package de.dascapschen.android.jeanne;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import de.dascapschen.android.jeanne.services.PlayerService;
import de.dascapschen.android.jeanne.singletons.AllAlbums;
import de.dascapschen.android.jeanne.singletons.AllArtists;
import de.dascapschen.android.jeanne.singletons.AllPlaylists;
import de.dascapschen.android.jeanne.singletons.AllSongs;

public class MainActivity extends AppCompatActivity implements NavigationRequest
{
    MediaBrowserCompat mediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected()
        {
            super.onConnected();

            try
            {
                MediaControllerCompat mediaController
                        = new MediaControllerCompat(MainActivity.this, mediaBrowser.getSessionToken());

                mediaController.registerCallback(controllerCallback);

                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback()
    {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata)
        {
            super.onMetadataChanged(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state)
        {
            super.onPlaybackStateChanged(state);
        }
    };

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
            queryAllData();
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
            case R.id.app_bar_search:
                //navigate(R.id.action_to_search);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void navigate(int actionID, Bundle arguments)
    {
        navController.navigate(actionID, arguments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void back()
    {
        navController.popBackStack(R.id.destination_main, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle( R.string.app_name );
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

        mediaBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, PlayerService.class),
                connectionCallbacks,
                null);

        //ask for permission first
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            queryAllData(); //FIXME: this takes half a billion years on EVERY start!!
            setupView();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if(MediaControllerCompat.getMediaController(this) != null)
        {
            MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback);
        }

        mediaBrowser.disconnect();
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
    }

    public void onBottomSheetPressed(View v)
    {
        /* animate bottom sheet up to the full layout */
    }

    public void onBottomSheetHidePressed(View v)
    {
        /* animate bottom sheet down to the closed layout */
    }

    public void onBtnPlayPressed(View v)
    {
        ImageButton playBtn = (ImageButton)v;
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);

        if( controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
        {
            controller.getTransportControls().pause();
            playBtn.setImageResource(R.drawable.ic_play);
        }
        else
        {
            controller.getTransportControls().play();
            playBtn.setImageResource(R.drawable.ic_pause);
        }
    }

    public void onBtnNextPressed(View v)
    {
        MediaControllerCompat.getMediaController(this).getTransportControls().skipToNext();
    }

    public void onBtnPrevPressed(View v)
    {
        MediaControllerCompat.getMediaController(this).getTransportControls().skipToPrevious();
    }
}
