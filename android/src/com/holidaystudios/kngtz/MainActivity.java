
package com.holidaystudios.kngtz;

import android.os.Bundle;

import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.holidaystudios.kngt.KngtzMain;
import com.holidaystudios.kngt.view.UIAssets;


public class MainActivity extends AndroidApplication {
    /** Defines callbacks for binding to the GameServerService, passed to bindService() */
	GameServerService gameServerService;
	boolean mBound = false;
	private ServiceConnection gameServerConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className,
						       IBinder service) {
				// We've bound to LocalService, cast the IBinder and get LocalService instance
				GameServerService.LocalBinder binder = (GameServerService.LocalBinder) service;
				gameServerService = binder.getService();
				mBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				mBound = false;
			}
		};

    @Override
	protected void onStart() {
		super.onStart();

		// bind to GameServerService
        Intent intent = new Intent(this, GameServerService.class);
        bindService(intent, gameServerConnection, Context.BIND_AUTO_CREATE);
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the GameServerService
		if (mBound) {
			unbindService(gameServerConnection);
			mBound = false;
		}
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // create game components
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new KngtzMain(), config);
	}
}
