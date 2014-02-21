
package com.holidaystudios.kngt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.holidaystudios.kngt.screens.GameScreen;


public class KngtzMain extends Game {

    public static final String LOG = KngtzMain.class.getSimpleName();

	public SpriteBatch batch;

	@Override
	public void create () {
        Gdx.app.log( KngtzMain.LOG, "Creating game" );

        batch = new SpriteBatch();

        this.setScreen(new GameScreen(this));
	}

	@Override
	public void render ()
    {
        super.render();
	}

    @Override
    public void dispose() {
        batch.dispose();
    }
}
