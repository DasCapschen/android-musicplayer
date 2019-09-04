package de.dascapschen.android.jeanne;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
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

import java.util.List;

import de.dascapschen.android.jeanne.service.MusicService;

public class MainActivity extends AppCompatActivity implements NavigationRequest
{
    MediaBrowserCompat mediaBrowser;
    MediaBrowserConnectionCallbacks connectionCallbacks = new MediaBrowserConnectionCallbacks();
    MediaBrowserSubscriptionCallbacks subscriptionCallbacks = new MediaBrowserSubscriptionCallbacks();
    MediaControllerCallbacks controllerCallbacks = new MediaControllerCallbacks();

    NavController navController;

    boolean isPlaying = false;

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

        //ask for permission first
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            setupView();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Log.i("ACTIVITY", "On Start Called!");

        mediaBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                connectionCallbacks,
                null);
        mediaBrowser.connect();
    }
/*
    @Override
    protected void onResume()
    {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
 */

    @Override
    protected void onStop()
    {
        super.onStop();

        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if( controller != null )
        {
            controller.unregisterCallback(controllerCallbacks);
            MediaControllerCompat.setMediaController(this, null);
        }

        if( mediaBrowser != null )
        {
            mediaBrowser.disconnect();
            mediaBrowser = null;
        }
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
        if(controller == null)
        {
            return;
        }

        if( isPlaying )
        {
            controller.getTransportControls().pause();
        }
        else
        {
            controller.getTransportControls().play();
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


    class MediaBrowserConnectionCallbacks extends MediaBrowserCompat.ConnectionCallback
    {

        @Override
        public void onConnectionFailed()
        {
            super.onConnectionFailed();
            Log.e("CONNECTION", "Failed");
        }

        @Override
        public void onConnected()
        {
            super.onConnected();

            Log.i("CONNECTION", "Called");

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

            try
            {
                // Create a MediaControllerCompat
                MediaControllerCompat mediaController =
                        new MediaControllerCompat(MainActivity.this, // Context
                                token);

                // Save the controller
                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

                mediaController.registerCallback(controllerCallbacks);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            // Finish building the UI
            //updateState();

            mediaBrowser.subscribe(mediaBrowser.getRoot(), subscriptionCallbacks);
        }
    }

    class MediaBrowserSubscriptionCallbacks extends MediaBrowserCompat.SubscriptionCallback
    {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children)
        {
            super.onChildrenLoaded(parentId, children);

            MediaControllerCompat controller = MediaControllerCompat.getMediaController(MainActivity.this);

            //test: add all mediaItems into our playlist
            for(MediaBrowserCompat.MediaItem item : children)
            {
                controller.addQueueItem(item.getDescription());
            }

            //prepare the player (now pressing play will start it)
            controller.getTransportControls().prepare();
        }
    }

    //callbacks for when we play a new song, or press pause etc
    class MediaControllerCallbacks extends MediaControllerCompat.Callback
    {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state)
        {
            super.onPlaybackStateChanged(state);

            ImageButton playBtn = findViewById(R.id.btnPlay);

            if(state != null)
            {
                isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
            }
            else isPlaying = false;

            if(isPlaying)
            {
                playBtn.setImageResource(R.drawable.ic_pause);
            }
            else
            {
                playBtn.setImageResource(R.drawable.ic_play);
            }


        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata)
        {
            super.onMetadataChanged(metadata);

            TextView text = findViewById(R.id.bottom_song_title);

            String s = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            s = s.concat(" - ");
            s = s.concat(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

            text.setText(s);

            ImageView art = findViewById(R.id.bottom_album_image);
            art.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
        }
    }

}
