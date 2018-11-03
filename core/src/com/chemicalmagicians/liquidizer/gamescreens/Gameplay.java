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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
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

    private ShaderProgram metaBallShader;
    private FrameBuffer buffer;
    private TextureAtlas atlas;
    private Sprite elixirTexture;
    public Array<Elixir> elixirs = new Array<Elixir>();
    private boolean isPressed = false;
    private boolean isElixirFlowing = false;
    private boolean isMixing = false;

    private Group colb;
    public Group singleElixir;
    private Group elixirGroup=new Group();

    Level level1;

    // todo: fill intersectPointIndexes array dynamically from data source
    Vector2 intersectPointIndexes[] = new Vector2[] {new Vector2(112, 262)};

    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
        atlas=new TextureAtlas(Gdx.files.internal("atlas.pack"));
        Image glassPath = new Image(atlas.findRegion("glass-path"));
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        colb=new Group();
        glassPath.setOrigin(Align.center);
        glassPath.setPosition(200,150);
        colb.addActor(glassPath);
        gameScreenUI = new GameScreenUI();
        addActor(elixirGroup);
        addActor(colb);
    }

    @Override
    public void configureForData (LevelData data) { }

    @Override
    public void start () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
        elixirTexture = new Sprite(atlas.findRegion("elixir-particle"));

        metaBallShader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));
        if (!metaBallShader.isCompiled()) {
            //System.out.println(metaBallShader.getLog());
        }

        level1 = new Level("No Overlapping", new Color[] {Color.RED, Color.YELLOW}, false, 20);


        createCurve();
    }

    boolean isJustClicked = false;
    public void render() {
        getStage().getRoot().setTouchable(Touchable.enabled);
        getStage().getRoot().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //System.out.println("I got clicked!");
            }
        });

        Vector3 temp = new Vector3();
        liquidizer.stage.getCamera().unproject(temp.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        Batch batch = liquidizer.stage.getBatch();
        batch.begin();

        for(int i=0; i<curvePoints.length; i++) {
            if (temp.x < curvePoints[i].x+30 && temp.x > curvePoints[i].x-30  &&
                temp.y < curvePoints[i].y+50 && temp.y > curvePoints[i].y+40 ) {
                if(Gdx.input.isTouched() && !isJustClicked) {
                    if(i>=0 && i<=75) {
                        fillWithElixir(20, 75 - i, Color.RED);
                    } else if(i>75 && i<=150) {
                        fillWithElixir(20, 300-(i-75), Color.RED);
                    } else if(i>150 && i<=225) {
                        fillWithElixir(20, 375-i, Color.RED);
                    } else {
                        fillWithElixir(20, 150-(i-225), Color.RED);
                    }
                    isJustClicked = true;
                } else if(!Gdx.input.isTouched()) {
                    isJustClicked = false;
                }
                batch.draw(elixirTexture, curvePoints[i].x, curvePoints[i].y, 64, 64);
            }
        }
        batch.end();


        if(Gdx.input.isKeyPressed(Input.Keys.R) && !isPressed) {
            fillWithElixir(20, 0,Color.RED);
            isPressed = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.Y) && !isPressed) {
            fillWithElixir(20, 0, Color.YELLOW);
            isPressed = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.G) && !isPressed) {
            fillWithElixir(20, 0, Color.GREEN);
            isPressed = true;
        } else if(!Gdx.input.isKeyPressed(Input.Keys.R) && !Gdx.input.isKeyPressed(Input.Keys.Y) && !Gdx.input.isKeyPressed(Input.Keys.G)) {
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
                for(int j=0; j<elixirs.size; j++) {
                    if(i!=j) {
                        if ((elixirs.get(j).elixirParticles.first().currentIndex < intersectPointIndexes[0].x &&
                             elixirs.get(j).elixirParticles.first().currentIndex + elixirs.get(i).length > intersectPointIndexes[0].x) ||
                            (elixirs.get(j).elixirParticles.first().currentIndex < intersectPointIndexes[0].y &&
                             elixirs.get(j).elixirParticles.first().currentIndex + elixirs.get(i).length > intersectPointIndexes[0].y)) {
                            mixElixirs(elixirs.get(i), elixirs.get(j), elixirs.get(i).elixirParticles.first().currentIndex);
                        }
                    }
                }

            }
        }
    }

    private void mixElixirs(Elixir elixirA, Elixir elixirB, int startIndex) {

        if (!isMixing) {
            if( (elixirA.color == Color.RED && elixirB.color == Color.YELLOW) ||
                (elixirB.color == Color.RED && elixirA.color == Color.YELLOW) ) {
                fillWithElixir(elixirA.length, startIndex, Color.ORANGE);
                elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
                isMixing = true;
            } else if( (elixirA.color == Color.GREEN && elixirB.color == Color.YELLOW) ||
                (elixirB.color == Color.GREEN && elixirA.color == Color.YELLOW) ) {
                fillWithElixir(elixirA.length, startIndex, Color.CYAN);
                elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
                isMixing = true;
            }

        }
    }

    private void createCurve() {
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(liquidizer.stage.getViewport().getCamera().combined);
        sr.setAutoShapeType(true);

        controlPoints[0] = new Vector2(300, 160);
        controlPoints[1] = new Vector2(300, 465);
        controlPoints[2] = new Vector2(900, 160);
        controlPoints[3] = new Vector2(900, 465);

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

    private int p = 0;

    public class Elixir {
        public int length = 0;
        public int elixirPersonalIndex = 0;
        public Color color;
        Array<ElixirParticle> elixirParticles = new Array<ElixirParticle>();
        public Elixir(int length, int startIndex, Color color) {
            this.color = color;
            this.length = length;
            singleElixir = new Group() {
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
                if(startIndex+i>=300) {
                    startIndex -= 300-i;
                }
//                else if()
                elixirParticles.get(i).image.setPosition(curvePoints[startIndex+i].x, curvePoints[startIndex+i].y);
                singleElixir.addActor(elixirParticles.get(i).image);
            }

            elixirGroup.addActor(singleElixir);

            this.elixirPersonalIndex = p;
            p++;
        }

        public void removeElixir(int index1, int index2) {
            elixirGroup.removeActor(elixirGroup.getChildren().items[index1]);

            if(index1<index2) {
                 elixirGroup.removeActor(elixirGroup.getChildren().items[index2-1]);
            } else {
                elixirGroup.removeActor(elixirGroup.getChildren().items[index2]);
            }
            elixirGroup.removeActor(elixirGroup.getChildren().items[index2]);
//            isMixing = false;
        }

    }

    public class ElixirParticle {
        public int currentIndex;
        public Image image;
        public ElixirParticle(int currentIndex, Sprite image, Color color) {
            this.image = new Image(image);
            this.image.setColor(color);
            this.image.scaleBy((float)Math.random()*0.35f+0.15f);
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

    private class Level {
        public String goal;
        public Color[] elixirColors;
        public boolean areMixing = false;
        public int amountOfElixirs = 0;

        public Level(String goal, Color[] elixirColors, boolean areMixing, int amountOfElixirs) {

        }
    }
}
