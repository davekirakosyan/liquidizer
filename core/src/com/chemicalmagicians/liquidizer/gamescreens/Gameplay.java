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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private MainMenuUI mainMenuUI;

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

    public static Color currentUsingColor = new Color();

    Level currentLevel;
    Level level1;
    Level level2;

    // todo: fill intersectPointIndexes array dynamically from data source
    Vector2 intersectPointIndexes[] = new Vector2[] {new Vector2(112, 262)};

    public static Color[] currentLvlElixirColors;


    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
        atlas=new TextureAtlas(Gdx.files.internal("atlas.pack"));
        Image glassPath = new Image(atlas.findRegion("glass-path"));
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        level1 = new Level(1, "No Mixing", new Color[] {Color.RED, Color.BLUE}, Color.PURPLE, false, 30);
//        level2 = new Level(2, "Mix red, yellow. No green", new Color[] {Color.RED, Color.YELLOW, Color.GREEN}, Color.ORANGE, true, 20);
        currentLevel = level1;

        colb=new Group();
        glassPath.setOrigin(Align.center);
        glassPath.setPosition(200,120);
        colb.addActor(glassPath);
        gameScreenUI = new GameScreenUI();
//        mainMenuUI =new MainMenuUI();
        addActor(elixirGroup);
        addActor(colb);

        setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
    }

    @Override
    public void configureForData (LevelData data) { }

    @Override
    public void start () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
        elixirTexture = new Sprite(atlas.findRegion("elixir-particle"));

        metaBallShader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));

        currentUsingColor = Color.GOLD;

        createCurve();
    }

    private boolean isJustClicked = false;
    public void render() {
        Vector3 temp = new Vector3();
        liquidizer.stage.getCamera().unproject(temp.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        currentLevel.update();

        Batch batch = liquidizer.stage.getBatch();
        batch.begin();

        for(int i=0; i<curvePoints.length; i++) {
            if (temp.x < curvePoints[i].x+30 && temp.x > curvePoints[i].x-30  &&
                temp.y < curvePoints[i].y+100 && temp.y > curvePoints[i].y+70 ) {
                if(Gdx.input.isTouched() && !isJustClicked) {
                        fillWithElixir(currentLevel.amountOfElixirs, i, currentUsingColor);
                    isJustClicked = true;
                } else if(!Gdx.input.isTouched()) {
                    isJustClicked = false;
                }
                batch.draw(elixirTexture, curvePoints[i].x-20, curvePoints[i].y+20, 60, 60);
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

	private void mixElixirs (Elixir elixirA, Elixir elixirB, int startIndex) {

        Color outcomeColor = new Color();

		if (!isMixing) {
			if ((elixirA.color == Color.RED && elixirB.color == Color.YELLOW) || (elixirB.color == Color.RED
				&& elixirA.color == Color.YELLOW)) {
			    outcomeColor = Color.ORANGE;
				fillWithElixir(elixirA.length, startIndex, outcomeColor);
				elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
				isMixing = true;
			} else if ((elixirA.color == Color.GREEN && elixirB.color == Color.YELLOW) || (elixirB.color == Color.GREEN
				&& elixirA.color == Color.YELLOW)) {
                outcomeColor = Color.CYAN;
				fillWithElixir(elixirA.length, startIndex, outcomeColor);
				elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
				isMixing = true;
			}else if ((elixirA.color == Color.RED && elixirB.color == Color.BLUE) || (elixirB.color == Color.RED
                && elixirA.color == Color.BLUE )) {
                outcomeColor = Color.PURPLE;
                fillWithElixir(elixirA.length, startIndex, outcomeColor);
                elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
                isMixing = true;
            }

            currentLevel.onMixing(outcomeColor);

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
//        add(mainMenuUI).grow();
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
            currentLevel.usedElixirs.add(color);
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


                    batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                    batch.setShader(metaBallShader);
                    batch.draw(buffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1f, 1);
                    batch.setShader(null);

                    batch.setProjectionMatrix(Gameplay.this.getStage().getViewport().getCamera().combined);

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
        public int lvl;
        public String goal;
        public boolean areMixing = false;
        public int amountOfElixirs = 0;
        Color idealOutcome;

        public Color[] elixirColors;
        public Array<Color> usedElixirs = new Array<Color>();

        private float deltaTime = 0;

        public Level(int lvl, String goal, Color[] elixirColors, Color idealOutcome, boolean areMixing, int amountOfElixirs) {
            this.amountOfElixirs = amountOfElixirs;
            this.elixirColors = elixirColors;
            this.idealOutcome = idealOutcome;
            this.lvl = lvl;
            this.areMixing = areMixing;
            currentLvlElixirColors = elixirColors;
        }
        public void onMixing(Color outcomeColor) {
            if(!areMixing) {
                System.out.println("go fuck yourself");
            } else {
                if(outcomeColor == idealOutcome) {
                    System.out.println("don't fuck yourself");
                }
            }
        }
        private boolean finishCheck = false;
        public void update() {
            if(!areMixing) {
                finishCheck = true;
                for (int i=0; i<elixirColors.length; i++) {
                    if (!usedElixirs.contains(elixirColors[i], true)) {
                        finishCheck = false;
                    }
                }
                if(finishCheck)
                    deltaTime+=Gdx.graphics.getDeltaTime();
                if(deltaTime>=6)
                    System.out.println("Level "+lvl+" passed");
            }
        }
    }
}
