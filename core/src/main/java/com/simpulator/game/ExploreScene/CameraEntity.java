package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.CollidableEntity;
import com.simpulator.engine.Cuboid;
import com.simpulator.engine.GJKShape;
import com.simpulator.game.EmptyRenderer;
import java.util.Arrays;

public class CameraEntity extends CollidableEntity {

    private Camera camera;

    public CameraEntity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        Camera camera
    ) {
        super(position, size, rotation, new EmptyRenderer());
        camera.position.set(position);
        camera.lookAt(rotation.transform(new Vector3(0, 0, -1)).add(position));
        camera.update();
        this.camera = camera;
    }

    public Camera getCamera() {
        camera.update();
        return camera;
    }

    @Override
    public void translate(Vector3 delta) {
        super.translate(delta);
        camera.position.add(delta);
    }

    @Override
    public void rotate(Quaternion delta) {
        camera.rotate(delta);
    }

    @Override
    public void rotate(Vector3 axis, float radians) {
        camera.rotate(axis, (float) Math.toDegrees(radians));
    }

    @Override
    public Iterable<GJKShape> getShapes() {
        Cuboid cuboid = new Cuboid(new OrientedBoundingBox(getBounds()));
        return Arrays.asList(new GJKShape[] { cuboid });
    }

    @Override
    public BoundingBox getBounds() {
        // TODO: make this actually collide
        Vector3 minCorner = size.cpy().scl(-0.5f).add(position);
        Vector3 maxCorner = size.cpy().scl(0.5f).add(position);
        return new BoundingBox(minCorner, maxCorner);
    }

    @Override
    public void onCollision(CollidableEntity other) {
        Vector3 mtv = new Vector3();
        if (intersects(other, mtv)) {
            // Move ourselves outside of the colliding mesh
            translate(mtv);
        }
    }
}
