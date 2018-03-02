package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.util.Log;

public class P2PVideoShaderRGB2SUR extends P2PVideoShader
{
    private final String LOGTAG = P2PVideoShader.class.getSimpleName();

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};

    private static final String FRAGMENT_SHADER_SOURCE = ""
            + "precision mediump float;"
            + "uniform sampler2D tex_sampler;"
            + "uniform float alpha;"
            + "varying vec2 v_texcoord;"
            + "void main()"
            + "{"
            + "  vec4 color = texture2D(tex_sampler, v_texcoord);"
            + "  gl_FragColor = color;"
            + "}"
            ;

    private static final String VERTEX_SHADER_SOURCE = ""
            + "attribute vec4 a_position;"
            + "attribute vec2 a_texcoord;"
            + "uniform mat4 u_model_view; "
            + "varying vec2 v_texcoord;"
            + "void main()"
            + "{"
            + "  gl_Position = u_model_view*a_position;"
            + "  v_texcoord = a_texcoord;"
            + "}"
            ;

    public P2PVideoShaderRGB2SUR()
    {
        int vertexShader = P2PVideoRenderUtils.loadShader(35633, VERTEX_SHADER_SOURCE);

        if (vertexShader == 0)
        {
            throw new RuntimeException("Could not load vertex shader: " + VERTEX_SHADER_SOURCE);
        }

        Log.d(LOGTAG, "createProgram: VERTEX_SHADER created.");

        int fragentShader = P2PVideoRenderUtils.loadShader(35632, FRAGMENT_SHADER_SOURCE);

        if (fragentShader == 0)
        {
            throw new RuntimeException("Could not load fragment shader: " + FRAGMENT_SHADER_SOURCE);
        }

        Log.d(LOGTAG, "createProgram: FRAGMENT_SHADER created.");

        program = GLES20.glCreateProgram();

        if (program != 0)
        {
            GLES20.glAttachShader(program, vertexShader);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glAttachShader(program, fragentShader);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glLinkProgram(program);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(program, 35714, iArr, 0);

            if (iArr[0] != 1)
            {
                String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);

                throw new RuntimeException("Could not link program: " + glGetProgramInfoLog);
            }

            alphaHandle = GLES20.glGetUniformLocation(program, "alpha");
            texCoordHandle = GLES20.glGetAttribLocation(program, "a_texcoord");
            posCoordHandle = GLES20.glGetAttribLocation(program, "a_position");
            modelViewMatHandle = GLES20.glGetUniformLocation(program, "u_model_view");

            texVertices = P2PVideoRenderUtils.createVerticesBuffer(TEX_VERTICES);
            posVertices = P2PVideoRenderUtils.createVerticesBuffer(POS_VERTICES);
        }
    }

    public boolean process(P2PVideoGLImage rgb, int width, int height)
    {
        if (program == 0)
        {
            Log.d(LOGTAG, "process: program failed.");

            return false;
        }

        if (rgb == null)
        {
            Log.d(LOGTAG, "process: no RGB image given.");

            return false;
        }

        if ((rgb.getTexture() == 0) || (rgb.getWidth() == 0) || (rgb.getHeight() == 0))
        {
            Log.d(LOGTAG, "process: RGB image not yet ready.");

            return false;
        }

        GLES20.glViewport(0, 0, width, height);
        checkGlError("glViewport");

        GLES20.glDisable(3042);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, 5126, false, 0, texVertices);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(posCoordHandle, 2, 5126, false, 0, posVertices);
        GLES20.glEnableVertexAttribArray(posCoordHandle);
        checkGlError("vertex attribute setup");
        GLES20.glActiveTexture(33984);
        checkGlError("glActiveTexture");
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, rgb.getTexture());
        GLES20.glTexParameteri(GlslFilter.GL_TEXTURE_2D, 10240, 9729);
        GLES20.glTexParameteri(GlslFilter.GL_TEXTURE_2D, 10241, 9729);
        GLES20.glTexParameteri(GlslFilter.GL_TEXTURE_2D, 10242, 33071);
        GLES20.glTexParameteri(GlslFilter.GL_TEXTURE_2D, 10243, 33071);
        checkGlError("glBindTexture");
        GLES20.glUniform1f(alphaHandle, alpha);
        GLES20.glUniformMatrix4fv(modelViewMatHandle, 1, false, mModelViewMat, 0);
        checkGlError("modelViewMatHandle");
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glFinish();

        return true;
    }
}
