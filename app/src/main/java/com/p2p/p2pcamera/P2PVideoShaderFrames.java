package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.util.Log;

public class P2PVideoShaderFrames extends P2PVideoShader
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

    public P2PVideoShaderFrames()
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
}
