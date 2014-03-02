package com.holidaystudios.kngtz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.holidaystudios.kngt.networking.ServerFinder;

public class StartupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.startup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void launchServer(View view) {
        Intent login_intent = new Intent(GameServerService.ACTION_CREATE_SERVER);
		startService(login_intent);
        finish();
    }

    public void loginToServer(int index) {
        Intent login_intent = new Intent(GameServerService.ACTION_LOGIN_TO_SERVER);
        login_intent.putExtra("serverAddress", servers[index].address);
        startService(login_intent);
        finish();
    }

    ServerFinder.Server[] servers;

    private CharSequence[] getServerNames() {
        servers = ServerFinder.getKngtzServerArray();
        CharSequence[] serverNames = new CharSequence[servers.length];
        int k;
        for(k = 0; k < servers.length; k++) {
            serverNames[k] = servers[k].name;
        }

        return serverNames;
    }

    public void connectToServer(View view) {
        AlertDialog diag = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(getString(R.string.select_server))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .setItems(getServerNames(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginToServer(i);
                    }
                })
                .create();
        diag.show();
    }

}
