
package com.holidaystudios.kngt;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.holidaystudios.kngt.entities.Cave;


public class KngtzMain extends ApplicationAdapter {

    public static final String LOG = KngtzMain.class.getSimpleName();

    // a libgdx helper class that logs the current FPS each second
    private FPSLogger fpsLogger;

	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
        Gdx.app.log( KngtzMain.LOG, "Creating game" );
        fpsLogger = new FPSLogger();

        //Create cave
        final Cave cave = new Cave("Foo", 100, 100);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
//		try {
//			new FreeTypeFontGenerator(Gdx.files.internal("test.fnt"));
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		Bullet.init();
	}

	@Override
	public void render ()
    {
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();

		// output the current FPS
        fpsLogger.log();
	}
    @Override
    public void resize(int width, int height)
    {
        Gdx.app.log( KngtzMain.LOG, "Resizing game to: " + width + " x " + height );
    }

    @Override
    public void pause()
    {
        Gdx.app.log( KngtzMain.LOG, "Pausing game" );
    }

    @Override
    public void resume()
    {
        Gdx.app.log( KngtzMain.LOG, "Resuming game" );
    }

    @Override
    public void dispose()
    {
        Gdx.app.log( KngtzMain.LOG, "Disposing game" );
    }
}
