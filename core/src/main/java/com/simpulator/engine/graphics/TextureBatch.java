/*******************************************************************************
 * Based on https://github.com/libgdx/libgdx/blob/91caf85c5701edb297495e38c356bc7ab9db1131/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java
 *
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.simpulator.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.nio.Buffer;

/** Draws batched quads using indices. */
public class TextureBatch implements Batch {

    private static final int SPRITE_SIZE = 24;
    /** Z value of 2D sprites. Set to 0 to put them above everything else. */
    private static final float Z_2D = 0;

    private VertexDataType currentDataType;

    private Mesh mesh;

    final float[] vertices;
    int idx = 0;
    Texture lastTexture = null;
    float invTexWidth = 0,
        invTexHeight = 0;

    boolean drawing = false;

    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
    private int blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private final ShaderProgram shader;
    private ShaderProgram customShader = null;
    private boolean ownsShader;

    private final Color color = new Color(1, 1, 1, 1);
    float colorPacked = Color.WHITE_FLOAT_BITS;

    public TextureBatch() {
        this(1024, null);
    }

    public TextureBatch(int size) {
        this(size, null);
    }

    /** Constructs a new TextureBatch. Sets the projection matrix to an orthographic projection with y-axis point upwards, x-axis
     * point to the right and the origin being in the bottom left corner of the screen. The projection will be pixel perfect with
     * respect to the current screen resolution.
     * <p>
     * The defaultShader specifies the shader to use. Note that the names for uniforms for this default shader are different than
     * the ones expect for shaders set with {@link #setShader(ShaderProgram)}.
     * @param size The max number of sprites in a single batch. Max of 8191.
     * @param defaultShader The default shader to use. This is not owned by the TextureBatch and must be disposed separately. */
    public TextureBatch(int size, ShaderProgram defaultShader) {
        // 32767 is max vertex index, so 32767 / 4 vertices per sprite = 8191 sprites max.
        if (size > 8191) throw new IllegalArgumentException(
            "Can't have more than 8191 sprites per batch: " + size
        );

        VertexDataType vertexDataType = (Gdx.gl30 != null)
            ? VertexDataType.VertexBufferObjectWithVAO
            : VertexDataType.VertexBufferObject;

        currentDataType = vertexDataType;

        mesh = new Mesh(
            currentDataType,
            false,
            size * 4,
            size * 6,
            new VertexAttribute(
                Usage.Position,
                3,
                ShaderProgram.POSITION_ATTRIBUTE
            ),
            new VertexAttribute(
                Usage.ColorPacked,
                4,
                ShaderProgram.COLOR_ATTRIBUTE
            ),
            new VertexAttribute(
                Usage.TextureCoordinates,
                2,
                ShaderProgram.TEXCOORD_ATTRIBUTE + "0"
            )
        );

        projectionMatrix.setToOrtho2D(
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        vertices = new float[size * SPRITE_SIZE];

        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);

        if (defaultShader == null) {
            shader = createDefaultShader();
            ownsShader = true;
        } else shader = defaultShader;

        // Pre bind the mesh to force the upload of indices data.
        if (vertexDataType != VertexDataType.VertexArray) {
            mesh.getIndexData().bind();
            mesh.getIndexData().unbind();
        }
    }

    public static ShaderProgram createDefaultShader() {
        // @formatter:off
        String vertexShader =
              "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
			+ "uniform mat4 u_projTrans;\n"
			+ "varying vec4 v_color;\n"
			+ "varying vec2 v_texCoords;\n"
			+ "\n"
			+ "void main()\n"
			+ "{\n"
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
			+ "   v_color.a = v_color.a * (255.0/254.0);\n"
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
			+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
			+ "}\n";
        String fragmentShader =
              "#ifdef GL_ES\n"
			+ "#define LOWP lowp\n"
			+ "precision mediump float;\n"
			+ "#else\n"
			+ "#define LOWP \n"
			+ "#endif\n"
			+ "varying LOWP vec4 v_color;\n"
			+ "varying vec2 v_texCoords;\n"
			+ "uniform sampler2D u_texture;\n"
			+ "void main()\n"
			+ "{\n"
			+ "  vec4 texColor = v_color * texture2D(u_texture, v_texCoords);\n"
			+ "  if(texColor.a < 0.1)\n" // Discard low alpha pixels to make them transparent
			+ "      discard;\n"
			+ "  gl_FragColor = texColor;\n"
			+ "}";
        // @formatter:on

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException(
            "Error compiling shader: " + shader.getLog()
        );
        return shader;
    }

    @Override
    public void begin() {
        if (drawing) throw new IllegalStateException(
            "end must be called before begin."
        );

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDepthRangef(0f, 1f);

        if (customShader != null) customShader.bind();
        else shader.bind();
        setupMatrices();

        drawing = true;
    }

    @Override
    public void end() {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before end."
        );
        if (idx > 0) flush();
        lastTexture = null;
        drawing = false;

        if (isBlendingEnabled()) Gdx.gl.glDisable(GL20.GL_BLEND);

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public void setColor(Color tint) {
        color.set(tint);
        colorPacked = tint.toFloatBits();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        colorPacked = color.toFloatBits();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(color, packedColor);
        this.colorPacked = packedColor;
    }

    @Override
    public float getPackedColor() {
        return colorPacked;
    }

    /**
     * Draw a TextureRegion as a perspective-correct 3D quad using the given
     * 4x4 transform matrix (model matrix). The camera's combined matrix is
     * applied by the shader, so pass world-space vertices here.
     *
     * The quad is a unit square [-0.5, 0.5] in local X/Y, Z=0.
     */
    public void draw3D(TextureRegion region, Matrix4 transform) {
        draw3D(region, transform, -0.5f, -0.5f, 0.5f, 0.5f);
    }

    /**
     * Draw a TextureRegion as a perspective-correct 3D quad using the given
     * 4x4 transform matrix (model matrix). The camera's combined matrix is
     * applied by the shader, so pass world-space vertices here.
     *
     * The quad is defined by the given left, bottom, right, and top coordinates in local space, with Z=0.
     */
    public void draw3D(
        TextureRegion region,
        Matrix4 transform,
        float left,
        float bottom,
        float right,
        float top
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        float[] vertices = this.vertices;

        Texture texture = region.getTexture();
        if (texture != lastTexture) switchTexture(texture);
        else if (idx == vertices.length) flush();

        float u = region.getU();
        float v = region.getV2();
        float u2 = region.getU2();
        float v2 = region.getV();

        float color = this.colorPacked;
        int idx = this.idx;
        writeLocalVertex(transform, left, bottom, 0, u, v, color, idx);
        writeLocalVertex(transform, left, top, 0, u, v2, color, idx + 6);
        writeLocalVertex(transform, right, top, 0, u2, v2, color, idx + 12);
        writeLocalVertex(transform, right, bottom, 0, u2, v, color, idx + 18);
        this.idx = idx + SPRITE_SIZE;
    }

    private final Vector3 scratchVertex = new Vector3();

    private void writeLocalVertex(
        Matrix4 transform,
        float x,
        float y,
        float z,
        float u,
        float v,
        float color,
        int idx
    ) {
        scratchVertex.set(x, y, z).mul(transform);
        vertices[idx] = scratchVertex.x;
        vertices[idx + 1] = scratchVertex.y;
        vertices[idx + 2] = scratchVertex.z;
        vertices[idx + 3] = color;
        vertices[idx + 4] = u;
        vertices[idx + 5] = v;
    }

    @Override
    public void draw(
        Texture texture,
        float x,
        float y,
        float originX,
        float originY,
        float width,
        float height,
        float scaleX,
        float scaleY,
        float rotation,
        int srcX,
        int srcY,
        int srcWidth,
        int srcHeight,
        boolean flipX,
        boolean flipY
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        float[] vertices = this.vertices;

        if (texture != lastTexture) switchTexture(texture);
        else if (idx == vertices.length) flush();

        // bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        // scale
        if (scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        // construct corner points, start from top left and go counter clockwise
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        if (rotation != 0) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);

            x1 = cos * p1x - sin * p1y;
            y1 = sin * p1x + cos * p1y;

            x2 = cos * p2x - sin * p2y;
            y2 = sin * p2x + cos * p2y;

            x3 = cos * p3x - sin * p3y;
            y3 = sin * p3x + cos * p3y;

            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;

            x2 = p2x;
            y2 = p2y;

            x3 = p3x;
            y3 = p3y;

            x4 = p4x;
            y4 = p4y;
        }

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;

        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }

        if (flipY) {
            float tmp = v;
            v = v2;
            v2 = tmp;
        }

        float color = this.colorPacked;
        int idx = this.idx;
        vertices[idx] = x1;
        vertices[idx + 1] = y1;
        vertices[idx + 2] = Z_2D;
        vertices[idx + 3] = color;
        vertices[idx + 4] = u;
        vertices[idx + 5] = v;

        vertices[idx + 6] = x2;
        vertices[idx + 7] = y2;
        vertices[idx + 8] = Z_2D;
        vertices[idx + 9] = color;
        vertices[idx + 10] = u;
        vertices[idx + 11] = v2;

        vertices[idx + 12] = x3;
        vertices[idx + 13] = y3;
        vertices[idx + 14] = Z_2D;
        vertices[idx + 15] = color;
        vertices[idx + 16] = u2;
        vertices[idx + 17] = v2;

        vertices[idx + 18] = x4;
        vertices[idx + 19] = y4;
        vertices[idx + 20] = Z_2D;
        vertices[idx + 21] = color;
        vertices[idx + 22] = u2;
        vertices[idx + 23] = v;
        this.idx = idx + SPRITE_SIZE;
    }

    @Override
    public void draw(
        Texture texture,
        float x,
        float y,
        float width,
        float height,
        int srcX,
        int srcY,
        int srcWidth,
        int srcHeight,
        boolean flipX,
        boolean flipY
    ) {
        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;
        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }
        if (flipY) {
            float tmp = v;
            v = v2;
            v2 = tmp;
        }

        draw(texture, x, y, width, height, u, v, u2, v2);
    }

    @Override
    public void draw(
        Texture texture,
        float x,
        float y,
        int srcX,
        int srcY,
        int srcWidth,
        int srcHeight
    ) {
        final float u = srcX * invTexWidth;
        final float v = (srcY + srcHeight) * invTexHeight;
        final float u2 = (srcX + srcWidth) * invTexWidth;
        final float v2 = srcY * invTexHeight;
        draw(texture, x, y, srcWidth, srcHeight, u, v, u2, v2);
    }

    @Override
    public void draw(
        Texture texture,
        float x,
        float y,
        float width,
        float height,
        float u,
        float v,
        float u2,
        float v2
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        float[] vertices = this.vertices;

        if (texture != lastTexture) switchTexture(texture);
        else if (idx == vertices.length) flush();

        final float fx2 = x + width;
        final float fy2 = y + height;

        float color = this.colorPacked;
        int idx = this.idx;
        vertices[idx] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = Z_2D;
        vertices[idx + 3] = color;
        vertices[idx + 4] = u;
        vertices[idx + 5] = v;

        vertices[idx + 6] = x;
        vertices[idx + 7] = fy2;
        vertices[idx + 8] = Z_2D;
        vertices[idx + 9] = color;
        vertices[idx + 10] = u;
        vertices[idx + 11] = v2;

        vertices[idx + 12] = fx2;
        vertices[idx + 13] = fy2;
        vertices[idx + 14] = Z_2D;
        vertices[idx + 15] = color;
        vertices[idx + 16] = u2;
        vertices[idx + 17] = v2;

        vertices[idx + 18] = fx2;
        vertices[idx + 19] = y;
        vertices[idx + 20] = Z_2D;
        vertices[idx + 21] = color;
        vertices[idx + 22] = u2;
        vertices[idx + 23] = v;
        this.idx = idx + SPRITE_SIZE;
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        draw(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void draw(
        Texture texture,
        float x,
        float y,
        float width,
        float height
    ) {
        draw(texture, x, y, width, height, 0, 1, 1, 0);
    }

    @Override
    public void draw(
        Texture texture,
        float[] spriteVertices,
        int offset,
        int count
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        int verticesLength = vertices.length;
        int remainingVertices = verticesLength;
        if (texture != lastTexture) switchTexture(texture);
        else {
            remainingVertices -= idx;
            if (remainingVertices == 0) {
                flush();
                remainingVertices = verticesLength;
            }
        }
        count = (count / 5) * 6;
        int copyCount = Math.min(remainingVertices, count);

        copyVertices(spriteVertices, offset, count);
        count -= copyCount;
        while (count > 0) {
            offset += copyCount;
            flush();
            copyCount = Math.min(verticesLength, count);
            copyVertices(spriteVertices, offset, count);
            count -= copyCount;
        }
    }

    private void copyVertices(float[] spriteVertices, int offset, int count) {
        for (int i = 0, j = offset; i < count; i += 6, j += 5) {
            vertices[idx] = spriteVertices[j];
            vertices[idx + 1] = spriteVertices[j + 1];
            vertices[idx + 2] = Z_2D;
            vertices[idx + 3] = spriteVertices[j + 2];
            vertices[idx + 4] = spriteVertices[j + 3];
            vertices[idx + 5] = spriteVertices[j + 4];
            idx += 6;
        }
    }

    @Override
    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    @Override
    public void draw(
        TextureRegion region,
        float x,
        float y,
        float width,
        float height
    ) {
        draw(
            region.getTexture(),
            x,
            y,
            width,
            height,
            region.getU(),
            region.getV2(),
            region.getU2(),
            region.getV()
        );
    }

    @Override
    public void draw(
        TextureRegion region,
        float x,
        float y,
        float originX,
        float originY,
        float width,
        float height,
        float scaleX,
        float scaleY,
        float rotation
    ) {
        draw(
            region.getTexture(),
            x,
            y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation,
            region.getRegionX(),
            region.getRegionY(),
            region.getRegionWidth(),
            region.getRegionHeight(),
            region.isFlipX(),
            region.isFlipY()
        );
    }

    @Override
    public void draw(
        TextureRegion region,
        float x,
        float y,
        float originX,
        float originY,
        float width,
        float height,
        float scaleX,
        float scaleY,
        float rotation,
        boolean clockwise
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        float[] vertices = this.vertices;

        Texture texture = region.getTexture();
        if (texture != lastTexture) {
            switchTexture(texture);
        } else if (idx == vertices.length) flush();

        // bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        // scale
        if (scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        // construct corner points, start from top left and go counter clockwise
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        if (rotation != 0) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);

            x1 = cos * p1x - sin * p1y;
            y1 = sin * p1x + cos * p1y;

            x2 = cos * p2x - sin * p2y;
            y2 = sin * p2x + cos * p2y;

            x3 = cos * p3x - sin * p3y;
            y3 = sin * p3x + cos * p3y;

            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;

            x2 = p2x;
            y2 = p2y;

            x3 = p3x;
            y3 = p3y;

            x4 = p4x;
            y4 = p4y;
        }

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        float u1, v1, u2, v2, u3, v3, u4, v4;
        if (clockwise) {
            u1 = region.getU2();
            v1 = region.getV2();
            u2 = region.getU();
            v2 = region.getV2();
            u3 = region.getU();
            v3 = region.getV();
            u4 = region.getU2();
            v4 = region.getV();
        } else {
            u1 = region.getU();
            v1 = region.getV();
            u2 = region.getU2();
            v2 = region.getV();
            u3 = region.getU2();
            v3 = region.getV2();
            u4 = region.getU();
            v4 = region.getV2();
        }

        float color = this.colorPacked;
        int idx = this.idx;
        vertices[idx] = x1;
        vertices[idx + 1] = y1;
        vertices[idx + 2] = Z_2D;
        vertices[idx + 3] = color;
        vertices[idx + 4] = u1;
        vertices[idx + 5] = v1;

        vertices[idx + 6] = x2;
        vertices[idx + 7] = y2;
        vertices[idx + 8] = Z_2D;
        vertices[idx + 9] = color;
        vertices[idx + 10] = u2;
        vertices[idx + 11] = v2;

        vertices[idx + 12] = x3;
        vertices[idx + 13] = y3;
        vertices[idx + 14] = Z_2D;
        vertices[idx + 15] = color;
        vertices[idx + 16] = u3;
        vertices[idx + 17] = v3;

        vertices[idx + 18] = x4;
        vertices[idx + 19] = y4;
        vertices[idx + 20] = Z_2D;
        vertices[idx + 21] = color;
        vertices[idx + 22] = u4;
        vertices[idx + 23] = v4;
        this.idx = idx + SPRITE_SIZE;
    }

    @Override
    public void draw(
        TextureRegion region,
        float width,
        float height,
        Affine2 transform
    ) {
        if (!drawing) throw new IllegalStateException(
            "begin must be called before draw."
        );

        float[] vertices = this.vertices;

        Texture texture = region.getTexture();
        if (texture != lastTexture) {
            switchTexture(texture);
        } else if (idx == vertices.length) {
            flush();
        }

        // construct corner points
        float x1 = transform.m02;
        float y1 = transform.m12;
        float x2 = transform.m01 * height + transform.m02;
        float y2 = transform.m11 * height + transform.m12;
        float x3 =
            transform.m00 * width + transform.m01 * height + transform.m02;
        float y3 =
            transform.m10 * width + transform.m11 * height + transform.m12;
        float x4 = transform.m00 * width + transform.m02;
        float y4 = transform.m10 * width + transform.m12;

        float u = region.getU();
        float v = region.getV2();
        float u2 = region.getU2();
        float v2 = region.getV();

        float color = this.colorPacked;
        int idx = this.idx;
        vertices[idx] = x1;
        vertices[idx + 1] = y1;
        vertices[idx + 2] = Z_2D;
        vertices[idx + 3] = color;
        vertices[idx + 4] = u;
        vertices[idx + 5] = v;

        vertices[idx + 6] = x2;
        vertices[idx + 7] = y2;
        vertices[idx + 8] = Z_2D;
        vertices[idx + 9] = color;
        vertices[idx + 10] = u;
        vertices[idx + 11] = v2;

        vertices[idx + 12] = x3;
        vertices[idx + 13] = y3;
        vertices[idx + 14] = Z_2D;
        vertices[idx + 15] = color;
        vertices[idx + 16] = u2;
        vertices[idx + 17] = v2;

        vertices[idx + 18] = x4;
        vertices[idx + 19] = y4;
        vertices[idx + 20] = Z_2D;
        vertices[idx + 21] = color;
        vertices[idx + 22] = u2;
        vertices[idx + 23] = v;
        this.idx = idx + SPRITE_SIZE;
    }

    @Override
    public void flush() {
        if (idx == 0) return;

        int spritesInBatch = idx / SPRITE_SIZE;
        int count = spritesInBatch * 6;

        lastTexture.bind();
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, idx);

        // Only upload indices for the vertex array type
        if (currentDataType == VertexDataType.VertexArray) {
            Buffer indicesBuffer = (Buffer) mesh.getIndicesBuffer(true);
            indicesBuffer.position(0);
            indicesBuffer.limit(count);
        }

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1) Gdx.gl.glBlendFuncSeparate(
                blendSrcFunc,
                blendDstFunc,
                blendSrcFuncAlpha,
                blendDstFuncAlpha
            );
        }

        mesh.render(
            customShader != null ? customShader : shader,
            GL20.GL_TRIANGLES,
            0,
            count
        );

        idx = 0;
    }

    @Override
    public void disableBlending() {
        if (blendingDisabled) return;
        flush();
        blendingDisabled = true;
    }

    @Override
    public void enableBlending() {
        if (!blendingDisabled) return;
        flush();
        blendingDisabled = false;
    }

    @Override
    public void setBlendFunction(int srcFunc, int dstFunc) {
        setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }

    @Override
    public void setBlendFunctionSeparate(
        int srcFuncColor,
        int dstFuncColor,
        int srcFuncAlpha,
        int dstFuncAlpha
    ) {
        if (
            blendSrcFunc == srcFuncColor &&
            blendDstFunc == dstFuncColor &&
            blendSrcFuncAlpha == srcFuncAlpha &&
            blendDstFuncAlpha == dstFuncAlpha
        ) return;
        flush();
        blendSrcFunc = srcFuncColor;
        blendDstFunc = dstFuncColor;
        blendSrcFuncAlpha = srcFuncAlpha;
        blendDstFuncAlpha = dstFuncAlpha;
    }

    @Override
    public int getBlendSrcFunc() {
        return blendSrcFunc;
    }

    @Override
    public int getBlendDstFunc() {
        return blendDstFunc;
    }

    @Override
    public int getBlendSrcFuncAlpha() {
        return blendSrcFuncAlpha;
    }

    @Override
    public int getBlendDstFuncAlpha() {
        return blendDstFuncAlpha;
    }

    @Override
    public void dispose() {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    @Override
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public Matrix4 getTransformMatrix() {
        return transformMatrix;
    }

    @Override
    public void setProjectionMatrix(Matrix4 projection) {
        if (drawing) flush();
        projectionMatrix.set(projection);
        if (drawing) setupMatrices();
    }

    @Override
    public void setTransformMatrix(Matrix4 transform) {
        if (drawing) flush();
        transformMatrix.set(transform);
        if (drawing) setupMatrices();
    }

    protected void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        if (customShader != null) {
            customShader.setUniformMatrix("u_projTrans", combinedMatrix);
            customShader.setUniformi("u_texture", 0);
        } else {
            shader.setUniformMatrix("u_projTrans", combinedMatrix);
            shader.setUniformi("u_texture", 0);
        }
    }

    protected void switchTexture(Texture texture) {
        flush();
        lastTexture = texture;
        invTexWidth = 1.0f / texture.getWidth();
        invTexHeight = 1.0f / texture.getHeight();
    }

    @Override
    public void setShader(ShaderProgram shader) {
        if (
            shader == customShader // avoid unnecessary flushing in case we are drawing
        ) return;
        if (drawing) {
            flush();
        }
        customShader = shader;
        if (drawing) {
            if (customShader != null) customShader.bind();
            else this.shader.bind();
            setupMatrices();
        }
    }

    @Override
    public ShaderProgram getShader() {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }

    @Override
    public boolean isBlendingEnabled() {
        return !blendingDisabled;
    }

    public boolean isDrawing() {
        return drawing;
    }
}
