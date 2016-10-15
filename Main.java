package com.mynameislaurence.fluid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {

    ShapeRenderer shapes;
    SpriteBatch batch;
    public Simulation sim;
    private BitmapFont font;
    public Sprite circle;


    @Override
	public void create () {
        AssetManager manager = new AssetManager();
        manager.load("metacircle.png", Texture.class);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
         sim = new Simulation();
        sim.init();
        font = new BitmapFont();
        manager.finishLoading();
        circle = new Sprite(manager.get("metacircle.png", Texture.class));
      circle.setCenter(20, 20);

        Gdx.input.setInputProcessor(new MyInputProcessor(this));

    }

    @Override
    public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.BLACK);
        shapes.rect(0, 0, 1000, 900);
        sim.render(shapes, circle);
        shapes.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();

        sim.overlay(batch,font);
        batch.end();
        sim.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glDisable(GL20.GL_BLEND);

	}
}
