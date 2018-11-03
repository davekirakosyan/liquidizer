package com.chemicalmagicians.liquidizer.gamescreens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.interfaces.IGameplay;
import com.chemicalmagicians.liquidizer.ui.*;

public class Gameplay extends GameScreen implements IGameplay {

    private CatmullRomSpline<Vector2> path;
    private ShapeRenderer sr;
    private int steps = 300;
    private Vector2[] controlPoints = new Vector2[4];
    private Vector2[] curvePoints = new Vector2[steps];
    private GameScreenUI gameScreenUI;

    private Sprite elixirTexture;

    Array<Elixir> elixirs = new Array<Elixir>();
    private boolean isPressed = false;
    private boolean isElixirFlowing = false;

    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
        gameScreenUI = new GameScreenUI();
    }


    @Override
    public void configureForData (LevelData data) { }

    @Override
    public void start () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
        elixirTexture = new Sprite(atlas.findRegion("elixir-particle"));

        createCurve();
    }

    public void render() {

        if(Gdx.input.isKeyPressed(Input.Keys.A) && !isPressed) {
            fillWithElixir(5, 0, Color.RED);
            isPressed = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S) && !isPressed) {
            fillWithElixir(5, 0, Color.GREEN);
            isPressed = true;
        } else if(!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
            isPressed = false;
        }


        if(isElixirFlowing) {
            for (int i = 0; i<elixirs.size; i++) {
                for (int j = 0; j<elixirs.get(0).elixirParticles.size; j++) {
                    elixirs.get(i).elixirParticles.get(j).draw();
                }
            }
        }

        //converting from touch to stage coordinates    -- todo: don't delete the comments below
//        Batch batch = liquidizer.stage.getBatch();
//
//        batch.begin();
//        Vector3 temp = new Vector3();
//        liquidizer.stage.getCamera().unproject(temp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
//        batch.draw(elixirTexture, temp.x, temp.y, 64, 64);
//        batch.end();

    }

    private void createCurve() {
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(liquidizer.stage.getViewport().getCamera().combined);
        sr.setAutoShapeType(true);

        controlPoints[0] = new Vector2(300, 300);
        controlPoints[1] = new Vector2(300, 500);
        controlPoints[2] = new Vector2(700, 300);
        controlPoints[3] = new Vector2(700, 500);

        path = new CatmullRomSpline<Vector2>(controlPoints, true);

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
        setFillParent(true);
        add(gameScreenUI).grow();

    }

    private void fillWithElixir(int length, int startIndex, Color color) {
        elixirs.add(new Elixir(length, startIndex, color));
        isElixirFlowing = true;
    }

    public class Elixir {
        Array<ElixirParticle> elixirParticles = new Array<ElixirParticle>();
        public Elixir(int length, int startIndex, Color color) {
            for (int i=0; i<length; i++) {
                elixirParticles.add(new ElixirParticle(startIndex+i*3, elixirTexture, color));
                elixirParticles.get(i).image.setPosition(curvePoints[startIndex+i].x, curvePoints[startIndex+i].y);
                addActor(elixirParticles.get(i).image);
            }
        }
    }

    public class ElixirParticle {
        public int currentIndex;
        public Image image;
        public ElixirParticle(int currentIndex, Sprite image, Color color) {
            this.image = new Image(image);
            this.image.setColor(color);
//            this.image.scaleBy((float)Math.random()*0.5f);
            this.currentIndex = currentIndex;
        }

        public void draw() {
            if(currentIndex < curvePoints.length-1) {
                image.setPosition(curvePoints[currentIndex+1].x, curvePoints[currentIndex+1].y);
                currentIndex++;
            } else {
                currentIndex = 0;
                image.setPosition(curvePoints[currentIndex].x, curvePoints[currentIndex].y);
                currentIndex++;
            }
        }
    }
}
