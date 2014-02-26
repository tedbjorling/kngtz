
package com.holidaystudios.kngt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.holidaystudios.kngt.controller.Game;
import com.holidaystudios.kngt.view.UIAssets;

public class KngtzMain extends com.badlogic.gdx.Game {

    public static final String LOG = KngtzMain.class.getSimpleName();
    public SpriteBatch batch;

	@Override
	public void create () {
        Gdx.app.log( KngtzMain.LOG, "Creating game" );

        batch = new SpriteBatch();
        UIAssets.load();

        final Game game = new Game();
        this.setScreen(game.getView());
	}

	@Override
	public void render ()
    {
        super.render();
	}

    @Override
    public void dispose() {
    }
}
