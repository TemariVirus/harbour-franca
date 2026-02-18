package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Action;
import com.simpulator.engine.MouseManager;

public class FirstPersonCameraAction
    implements Action<MouseManager.MouseMoveEvent>
{

    private Camera camera;
    private float sensitivity;

    public FirstPersonCameraAction(Camera camera, float sensitivity) {
        this.camera = camera;
        this.sensitivity = sensitivity;
    }

    @Override
    public void act(MouseManager.MouseMoveEvent event) {
        // TODO: verify
        // https://stackoverflow.com/questions/21825959/libgdx-first-person-camera-controll
        boolean cameraMoved = false;
        int magX = Math.abs(event.deltaX);
        int magY = Math.abs(event.deltaY);

        // TODO: allow mouse to move indefinitely
        if (event.deltaX != 0) {
            camera.rotate(Vector3.Y, -event.deltaX * sensitivity);
            cameraMoved = true;
        }
        // if (mouseX > screenX) {
        //     camera.rotate(Vector3.Y, 1 * magX * sensitivity);
        //     cameraMoved = true;
        // }

        // if (mouseX < screenX) {
        //     camera.rotate(Vector3.Y, -1 * magX * sensitivity);
        //     cameraMoved = true;
        // }

        if (event.deltaY > 0 && camera.direction.y > -0.965) {
            camera.rotate(
                camera.direction.cpy().crs(Vector3.Y),
                -magY * sensitivity
            );
            cameraMoved = true;
        }

        if (event.deltaY < 0 && camera.direction.y < 0.965) {
            camera.rotate(
                camera.direction.cpy().crs(Vector3.Y),
                magY * sensitivity
            );
            cameraMoved = true;
        }

        if (cameraMoved) {
            camera.update();
        }
    }
}
