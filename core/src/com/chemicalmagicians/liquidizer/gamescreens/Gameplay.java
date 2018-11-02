package com.chemicalmagicians.liquidizer.gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.interfaces.IGameplay;

public class Gameplay extends GameScreen implements IGameplay {

    private SpriteBatch batch;

    private CatmullRomSpline<Vector2> path;
    private ShapeRenderer sr;
    private int steps = 300;
    private Vector2[] controlPoints = new Vector2[4];
    private Vector2[] curvePoints = new Vector2[steps];

    private Image elixir;
    private Image flask1;

    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
    }

    @Override
    public void configureForData (LevelData data) {

    }

    @Override
    public void start () {
        batch = new SpriteBatch();

        elixir = new Image(new Texture("elixir-particle.png") );
        flask1 = new Image(new Texture("flask1.png") );

        controlPoints[0] = new Vector2(300, 300);
        controlPoints[1] = new Vector2(300, 500);
        controlPoints[2] = new Vector2(700, 300);
        controlPoints[3] = new Vector2(700, 500);

        path = new CatmullRomSpline<Vector2>(controlPoints, true);

        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);

        elixir.setPosition(100, 100);
        this.addActor(elixir);
        sr.setColor(Color.RED);
        sr.begin();
        for (int i = 0; i < steps; ++i) {
            float t = i / (float) steps;
            Vector2 st = new Vector2();
            Vector2 end = new Vector2();
            path.valueAt(st, t);
            path.valueAt(end, t - 0.01f);
            sr.line(st.x, st.y, end.x, end.y);
            curvePoints[i] = new Vector2(st.x, st.y);
        }
        sr.end();

        this.addActor(flask1);
    }

    public class SubstanceActor extends Actor {
        public SubstanceActor() {

        }
    }

    private int elixirCurrentPos = 0;
    public void render() {
        //  debugging curve


        elixir.setPosition(curvePoints[elixirCurrentPos].x, Gdx.graphics.getHeight()-curvePoints[elixirCurrentPos].y);
        if (elixirCurrentPos < curvePoints.length-1) {
            elixirCurrentPos++;
        } else {
            elixirCurrentPos = 0;

        }
    }
}
