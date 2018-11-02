package com.chemicalmagicians.liquidizer.gamescreens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

    private Texture elixirTexture;

    private boolean isElixirFlowing = false;

    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
        gameScreenUI = new GameScreenUI();
    }
    Array<Elixir> blueElixir = new Array<Elixir>();

    @Override
    public void configureForData (LevelData data) { }

    @Override
    public void start () {
        elixirTexture = new Texture("elixir-particle.png");

        createCurve();
    }

    public void render() {

        fillWithElixir(10, 0);

        if(isElixirFlowing) {
            for (int i=0; i<blueElixir.size; i++) {
                blueElixir.get(i).draw();
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
        add(gameScreenUI).expand().right().top();

    }

    private void fillWithElixir(int length, int startIndex) {
        for (int i=0; i<length; i++) {
            blueElixir.add(new Elixir(startIndex+i*3, elixirTexture));
            blueElixir.get(i).image.setPosition(curvePoints[startIndex+i].x, curvePoints[startIndex+i].y);
            this.addActor(blueElixir.get(i).image);
        }
        isElixirFlowing = true;
    }

    public class Elixir {
        public int currentIndex;
        public Image image;
        public Elixir(int currentIndex, Texture image) {
            this.image = new Image(image);
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
