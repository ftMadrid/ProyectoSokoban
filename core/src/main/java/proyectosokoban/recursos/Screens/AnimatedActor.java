/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;

    public AnimatedActor() {}

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        this.stateTime = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (animation != null) {
            stateTime += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }
}
