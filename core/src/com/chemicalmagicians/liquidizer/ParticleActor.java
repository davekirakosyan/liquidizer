package com.chemicalmagicians.liquidizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleActor extends Actor {

    private ParticleEffect particleEffect;

    public ParticleActor (ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        PolygonBatch polygonBatch = (PolygonBatch) batch;
        particleEffect.setPosition(getX(), getY());
        particleEffect.update(Gdx.graphics.getDeltaTime());
        particleEffect.draw(polygonBatch);
        batch.setColor(1, 1, 1, 1);
    }

    public void startEffect () {
        particleEffect.start();
    }

    public ParticleEffect getParticleEffect () {
        return particleEffect;
    }
}
