package com.chemicalmagicians.liquidizer.gamescreens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
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

    private ShaderProgram metaBallShader;
    private FrameBuffer buffer;

    Array<Elixir> elixirs = new Array<Elixir>();
    private boolean isPressed = false;
    private boolean isElixirFlowing = false;

    // todo: fill intersectPointIndexes array dynamically from data source
    Vector2 intersectPointIndexes[] = new Vector2[] {new Vector2(112, 262)};



    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
        gameScreenUI = new GameScreenUI();
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }


    @Override
    public void configureForData (LevelData data) { }

    @Override
    public void start () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
        elixirTexture = new Sprite(atlas.findRegion("elixir-particle"));

        metaBallShader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));
        if (!metaBallShader.isCompiled()) {
            System.out.println(metaBallShader.getLog());
        }

        createCurve();
    }

    public void render() {

        if(Gdx.input.isKeyPressed(Input.Keys.A) && !isPressed) {
            fillWithElixir(20, 0, Color.RED);
            isPressed = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S) && !isPressed) {
            fillWithElixir(20, 0, Color.GREEN);
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

        checkIntersections();


        //converting from touch to stage coordinates    -- todo: don't delete the comments below
//        Batch batch = liquidizer.stage.getBatch();
//
//        batch.begin();
//        Vector3 temp = new Vector3();
//        liquidizer.stage.getCamera().unproject(temp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
//        batch.draw(elixirTexture, temp.x, temp.y, 64, 64);
//        batch.end();

    }

    private void checkIntersections() {
        for(int i=0; i<elixirs.size; i++) {
            if ((elixirs.get(i).elixirParticles.first().currentIndex < intersectPointIndexes[0].x &&
                 elixirs.get(i).elixirParticles.first().currentIndex+elixirs.get(i).length > intersectPointIndexes[0].x) ||
                (elixirs.get(i).elixirParticles.first().currentIndex < intersectPointIndexes[0].y &&
                 elixirs.get(i).elixirParticles.first().currentIndex+elixirs.get(i).length > intersectPointIndexes[0].y) ) {
//                System.out.println(i);

                for(int j=0; j<elixirs.size; j++) {
                    if(i!=j) {
                        if ((elixirs.get(j).elixirParticles.first().currentIndex < intersectPointIndexes[0].x &&
                             elixirs.get(j).elixirParticles.first().currentIndex + elixirs.get(i).length > intersectPointIndexes[0].x) ||
                            (elixirs.get(j).elixirParticles.first().currentIndex < intersectPointIndexes[0].y &&
                             elixirs.get(j).elixirParticles.first().currentIndex + elixirs.get(i).length > intersectPointIndexes[0].y)) {
                            System.out.println("66666666");
                        }
                    }
                }

            }
        }
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
        public int length = 0;
        Array<ElixirParticle> elixirParticles = new Array<ElixirParticle>();
        public Elixir(int length, int startIndex, Color color) {
            this.length = length;
            Group group = new Group() {
                @Override
                public void act(float delta) {
                    super.act(delta);
//                    metaBallShader.dispose();
//                    metaBallShader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));

                }

                @Override
                public void draw(Batch batch, float parentAlpha) {

                    batch.flush();
                    buffer.begin();
                    Gdx.gl.glClearColor(0, 0, 0, 0);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    super.draw(batch, parentAlpha);
                    batch.flush();
                    buffer.end();

                    batch.setShader(metaBallShader);
                    batch.draw(buffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    batch.setShader(null);

                }
            };
            for (int i=0; i<length; i++) {
                elixirParticles.add(new ElixirParticle(startIndex+i, elixirTexture, color));
                elixirParticles.get(i).image.setPosition(curvePoints[startIndex+i].x, curvePoints[startIndex+i].y);
                group.addActor(elixirParticles.get(i).image);
            }
            addActor(group);
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
