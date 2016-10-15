package com.mynameislaurence.fluid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {

    ShapeRenderer shapes;
    public Simulation sim;


    @Override
	public void create () {
        shapes = new ShapeRenderer();
         sim = new Simulation();
        sim.init();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.BLACK);
        shapes.rect(0,0,700,700);

        sim.render(shapes);

        shapes.end();
        sim.update(Gdx.graphics.getDeltaTime()*0.05f);


	}
}
