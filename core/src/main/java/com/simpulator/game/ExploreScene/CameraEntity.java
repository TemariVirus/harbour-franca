package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.CollidableEntity;
import com.simpulator.engine.Cuboid;
import com.simpulator.engine.GJKShape;
import com.simpulator.game.EmptyRenerer;
import java.util.Arrays;

public class CameraEntity extends CollidableEntity {

    private Camera camera;

    public CameraEntity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        Camera camera
    ) {
        super(position, size, rotation, new EmptyRenerer());
        camera.position.set(position);
        camera.lookAt(
            position.cpy().add(rotation.transform(new Vector3(0, 0, -1)))
        );
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
        Vector3 size = transform.getScale(new Vector3());
        Vector3 minCorner = size.cpy().scl(-0.5f).add(getPosition());
        Vector3 maxCorner = size.scl(0.5f).add(getPosition());
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
