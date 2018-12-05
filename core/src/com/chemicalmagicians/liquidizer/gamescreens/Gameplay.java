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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.interfaces.IGameplay;
import com.chemicalmagicians.liquidizer.ui.*;

public class Gameplay extends GameScreen implements IGameplay {

    private int steps = 300;
    private Vector2[] controlPoints = new Vector2[4];
    private Vector2[] curvePoints = new Vector2[steps];
    private GameScreenUI gameScreenUI;
    private Level currentLevel;

    private ShaderProgram metaBallShader;
    private FrameBuffer buffer;
    private TextureAtlas atlas;
    private Sprite elixirTexture;
    private Array<Elixir> elixirs = new Array<Elixir>();
    private Vector2 intersectPointIndexes[] = new Vector2[] {new Vector2(112, 262)};
    private int lastElixirPersonalIndex = 0;

    private boolean isPressed = false;  // todo: this is only for testing, later should be deleted
    private boolean isPathJustClicked = false;
    public static boolean isElixirFlowing = false;
    private boolean isMixing = false;
    private boolean isGameOver = false;
    private boolean hasTheRightMix = false;

    public Group singleElixir;
    private Group elixirGroup=new Group();

    public static Color currentUsingColor = new Color();
    public static Color[] currentLvlElixirColors;


    public Gameplay (Liquidizer liquidizer, int lvl, String lvlGoal, Color[] colors, Color idealColor, boolean areMixing, int amountOfElixirs) {
        super(liquidizer);
        atlas=new TextureAtlas(Gdx.files.internal("atlas.pack"));
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        currentLevel = new Level(lvl, lvlGoal, colors, idealColor, areMixing, amountOfElixirs);
        currentUsingColor = colors[0];

        Image glassPath = new Image(atlas.findRegion("glass-path"));
        glassPath.setOrigin(Align.center);
        glassPath.setPosition(200,120);

        gameScreenUI = new GameScreenUI();

        Group colb = new Group();
        colb.addActor(glassPath);
        addActor(elixirGroup);
        addActor(colb);

        setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
    }

    @Override
    public void start () {
        Liquidizer.currLvl = currentLevel.lvl;
        elixirTexture = new Sprite(atlas.findRegion("elixir-particle"));
        metaBallShader = new ShaderProgram(Gdx.files.internal("shader.vert"), Gdx.files.internal("shader.frag"));
        createCurve();

        if(currentLevel.lvl == 1)
            setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
        else if(currentLevel.lvl == 2)
            setBackground(new TextureRegionDrawable(new Texture("ui/lvl2-back.png")));

    }

    private void createCurve() {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(liquidizer.stage.getViewport().getCamera().combined);
        sr.setAutoShapeType(true);

        controlPoints[0] = new Vector2(300, 160);
        controlPoints[1] = new Vector2(300, 465);
        controlPoints[2] = new Vector2(900, 160);
        controlPoints[3] = new Vector2(900, 465);

        CatmullRomSpline<Vector2> path = new CatmullRomSpline<Vector2>(controlPoints, true);

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


    public void render() {
        Vector3 temp = new Vector3();
        liquidizer.stage.getCamera().unproject(temp.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        currentLevel.update();

        Batch batch = liquidizer.stage.getBatch();
        batch.begin();

        for(int i=0; i<curvePoints.length; i++) {

            if(Gdx.input.isTouched() && !isPathJustClicked) {
                if (temp.x < curvePoints[i].x+60 && temp.x > curvePoints[i].x && temp.y < curvePoints[i].y+60 && temp.y > curvePoints[i].y-10 ) {
                    fillWithElixir(currentLevel.amountOfElixirs, i, currentUsingColor);
                    isPathJustClicked = true;
                }
            } else if(!Gdx.input.isTouched()) {
                isPathJustClicked = false;
            }
            batch.draw(elixirTexture, curvePoints[i].x-20, curvePoints[i].y+20, 60, 60);

        }
        batch.end();


        // todo: this is only for testing, later should be deleted
        if(Gdx.input.isKeyPressed(Input.Keys.R) && !isPressed) {
            fillWithElixir(20, 0,Color.RED);
            isPressed = true;
        } else if(!Gdx.input.isKeyPressed(Input.Keys.R)) {
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
        checkCollision();

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

    private void checkCollision() {
        for (int i=0; i<elixirs.size; i++) {
            for (int j=0; j<elixirs.size; j++) {
                if(i!=j) {
                    Elixir elixirA = elixirs.get(i);
                    Elixir elixirB = elixirs.get(j);
                    if ((elixirA.elixirParticles.first().currentIndex <= elixirB.elixirParticles.first().currentIndex + elixirB.length &&
                            elixirA.elixirParticles.first().currentIndex >= elixirB.elixirParticles.first().currentIndex ) ||
                            (elixirA.elixirParticles.first().currentIndex+elixirA.length <= elixirB.elixirParticles.first().currentIndex + elixirB.length &&
                                    elixirA.elixirParticles.first().currentIndex+elixirA.length >= elixirB.elixirParticles.first().currentIndex ) ) {
                        mixElixirs(elixirA, elixirB, elixirA.elixirParticles.first().currentIndex);
//                        System.out.println("wooooorrrrrrkiiiiiing");
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
            } else if ((elixirA.color == Color.GREEN && elixirB.color == Color.YELLOW) || (elixirB.color == Color.GREEN
                    && elixirA.color == Color.YELLOW)) {
                outcomeColor = Color.CYAN;
            } else if ((elixirA.color == Color.RED && elixirB.color == Color.BLUE) || (elixirB.color == Color.RED
                    && elixirA.color == Color.BLUE )) {
                outcomeColor = Color.PURPLE;
            } else if(elixirA.color == elixirB.color) {
                outcomeColor = Color.PURPLE;
            } else {
                return;
            }

            fillWithElixir(elixirA.length, startIndex, outcomeColor);
            elixirA.removeElixir(elixirA.elixirPersonalIndex, elixirB.elixirPersonalIndex);
            currentLevel.onMixing(outcomeColor);
            isMixing = true;
        }
    }


    private void fillWithElixir(int length, int startIndex, Color color) {
        elixirs.add(new Elixir(length, startIndex, color));
        isElixirFlowing = true;
    }

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
                elixirParticles.get(i).image.setPosition(curvePoints[startIndex+i].x, curvePoints[startIndex+i].y);
                singleElixir.addActor(elixirParticles.get(i).image);
            }

            elixirGroup.addActor(singleElixir);

            this.elixirPersonalIndex = lastElixirPersonalIndex;
            lastElixirPersonalIndex++;
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

    private float deltaTime = 0;
    private boolean finishCheck = false;

    private class Level {
        public int lvl;
        public String goal;
        public boolean shouldMix;
        public int amountOfElixirs = 0;
        Color idealOutcome;

        public Color[] elixirColors;
        public Array<Color> usedElixirs = new Array<Color>();


        public Level(int lvl, String goal, Color[] elixirColors, Color idealOutcome, boolean areMixing, int amountOfElixirs) {
            this.amountOfElixirs = amountOfElixirs;
            this.elixirColors = elixirColors;
            this.idealOutcome = idealOutcome;
            this.lvl = lvl;
            this.shouldMix = areMixing;
            currentLvlElixirColors = elixirColors;
        }


        public void onMixing(Color outcomeColor) {
            if(!shouldMix) {
                if(!isGameOver) {
                    gameOver();
                    isGameOver = true;
                }
            } else {
                if(lvl==2) {
                    if (outcomeColor == idealOutcome) {
                        hasTheRightMix = true;
                    } else {
                        if(!isGameOver) {
                            gameOver();
                            isGameOver = true;
                        }
                    }
                }
            }
        }

        public void gameOver() {
            isElixirFlowing = false;
            System.out.println("loser");
            WinLoseUI winLoseUI = new WinLoseUI(liquidizer);
            winLoseUI.failTable().setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
            winLoseUI.setPosition(500,500);
            addActor(winLoseUI.failTable());
        }

        private boolean lvl2Check = false;
        private boolean checkForWinL2 = false;
        public void update() {
            if(!shouldMix) {
                finishCheck = true;
                for (int i=0; i<elixirColors.length; i++) {
                    if (!usedElixirs.contains(elixirColors[i], true)) {
                        finishCheck = false;
                    }
                }
                if(finishCheck)
                    deltaTime+=Gdx.graphics.getDeltaTime();
                if(deltaTime>=4 && !lvl2Check ) {
                    lvl2Check = true;
                    WinLoseUI winLoseUI = new WinLoseUI(liquidizer);
                    winLoseUI.winTable().setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
                    winLoseUI.setPosition(500,500);
                    addActor(winLoseUI.winTable());
                    System.out.println("kakaaaa");
                }
            }

            if(lvl==2 && hasTheRightMix && usedElixirs.contains(Color.GREEN, true) && !checkForWinL2) {
                WinLoseUI winLoseUI = new WinLoseUI(liquidizer);
                winLoseUI.winTable().setBackground(new TextureRegionDrawable(atlas.findRegion("background")));
                winLoseUI.setPosition(500,500);
                addActor(winLoseUI.winTable());
                checkForWinL2 = true;
            }
        }
        public void levelUp2() {
            System.out.println("lvl2");
            resetElixirs();
        }

    }

    public void resetElixirs() {
//        for (int i=0; i<elixirs.size; i++) {
//            elixirs.get(0).remove(0);
//        }
//        deltaTime = 0;
//         isElixirFlowing = false;
//        Liquidizer.lvl2();
//        this.remove();
//        level2 = new Level(2, "Mix red, yellow. No green", new Color[] {Color.RED, Color.YELLOW, Color.GREEN}, Color.ORANGE, true, 20);
//        currentLevel = level2;
    }

    @Override
    public void configureForData (LevelData data) {}

}
