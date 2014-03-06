
package com.holidaystudios.kngt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.holidaystudios.kngt.controller.Human;
import com.holidaystudios.kngt.networking.GameClient;
import com.holidaystudios.kngt.view.UIAssets;

import java.net.SocketException;

public class KngtzMain extends com.badlogic.gdx.Game {

    public static final String LOG = KngtzMain.class.getSimpleName();
    public SpriteBatch batch;

    GameClient gameClient;

	public void create () {
        Gdx.app.log( KngtzMain.LOG, "Creating game" );

        batch = new SpriteBatch();
        UIAssets.load();

        try {
            gameClient = new GameClient();
        } catch(SocketException e) {
            System.exit(-1);
        }
        gameClient.start();

        this.setScreen(gameClient.getView());
	}

    @Override
    public void resume() {
        UIAssets.refresh();
    }

	@Override
	public void render () {
        gameClient.processEvents();
        super.render();
	}

    @Override
    public void dispose() {
    }
}
