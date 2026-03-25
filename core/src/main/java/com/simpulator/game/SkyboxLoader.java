package com.simpulator.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.simpulator.engine.graphics.Skybox;
import com.simpulator.engine.scene.TextureCache;

public final class SkyboxLoader {

    public static Skybox load(
        TextureCache textures,
        String path,
        float cameraFarDistance
    ) {
        TextureRegion[] faces = new TextureRegion[6];
        faces[0] = new TextureRegion(textures.get(path + "/ft.png"));
        faces[1] = new TextureRegion(textures.get(path + "/bk.png"));
        faces[2] = new TextureRegion(textures.get(path + "/lf.png"));
        faces[3] = new TextureRegion(textures.get(path + "/rt.png"));
        faces[4] = new TextureRegion(textures.get(path + "/up.png"));
        faces[5] = new TextureRegion(textures.get(path + "/dn.png"));
        return new Skybox(faces, cameraFarDistance);
    }
}
