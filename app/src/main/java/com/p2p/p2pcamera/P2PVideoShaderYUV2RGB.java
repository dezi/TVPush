package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.util.Log;

public class P2PVideoShaderYUV2RGB extends P2PVideoShader
{
    private final String LOGTAG = P2PVideoShader.class.getSimpleName();

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};

    private final float[] mTextureMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    private static final String YUV_FRAGMENT_SHADER_SOURCE = ""
            + "precision mediump float;"
            + "varying vec2 textureCoordinate;"
            + "uniform sampler2D y_tex;"
            + "uniform sampler2D u_tex;"
            + "uniform sampler2D v_tex;"
            + "void main()"
            + "{"
            + "  float y = texture2D(y_tex, textureCoordinate).r;"
            + "  float u = texture2D(u_tex, textureCoordinate).r - 0.5;"
            + "  float v = texture2D(v_tex, textureCoordinate).r - 0.5;"
            + "  gl_FragColor = vec4(y + 1.403 * v, y - 0.344 * u - 0.714 * v, y + 1.77 * u, 1.0);"
            + "}"
            ;

    private static final String VERTEX_SHADER_SOURCE = ""
            + "attribute vec4 a_position;"
            + "attribute vec2 a_texcoord;"
            + "uniform mat4 u_texture_mat; "
            + "uniform mat4 u_model_view; "
            + "varying vec2 textureCoordinate;"
            + "void main()"
            + "{"
            + "  gl_Position = u_model_view*a_position;"
            + "  vec4 tmp = u_texture_mat*vec4(a_texcoord.x,a_texcoord.y,0.0,1.0);"
            + "  textureCoordinate = tmp.xy;"
            + "}"
            ;

    private int texUHandle;
    private int texVHandle;
    private int texYHandle;

    private int[] yuvTextures;

    public P2PVideoShaderYUV2RGB()
    {
        int vertexShader = P2PVideoRenderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE);

        if (vertexShader == 0)
        {
            throw new RuntimeException(LOGTAG + ": Could not load vertex shader: " + VERTEX_SHADER_SOURCE);
        }

        int fragmentShader = P2PVideoRenderUtils.loadShader(35632, YUV_FRAGMENT_SHADER_SOURCE);

        if (fragmentShader == 0)
        {
            throw new RuntimeException(LOGTAG + ": Could not load fragment shader: " + YUV_FRAGMENT_SHADER_SOURCE);
        }

        program = GLES20.glCreateProgram();

        if (program == 0)
        {
            throw new RuntimeException(LOGTAG + ": Could not create program");
        }

        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");

        GLES20.glAttachShader(program, fragmentShader);
        checkGlError("glAttachShader");

        GLES20.glLinkProgram(program);

        int[] iArr = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, iArr, 0);

        if (iArr[0] != GLES20.GL_TRUE)
        {
            String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(program);
            GLES20.glDeleteProgram(program);

            program = 0;

            throw new RuntimeException(LOGTAG + ":Could not link program: " + glGetProgramInfoLog);
        }

        GLES20.glGenFramebuffers(1, iArr, 0);
        checkGlError("glGenFramebuffers");
        frameBufferHandle = iArr[0];

        yuvTextures = new int[3];
        GLES20.glGenTextures(yuvTextures.length, yuvTextures, 0);

        texCoordHandle = GLES20.glGetAttribLocation(program, "a_texcoord");
        posCoordHandle = GLES20.glGetAttribLocation(program, "a_position");
        texCoordMatHandle = GLES20.glGetUniformLocation(program, "u_texture_mat");
        modelViewMatHandle = GLES20.glGetUniformLocation(program, "u_model_view");

        texYHandle = GLES20.glGetUniformLocation(program, "y_tex");
        texUHandle = GLES20.glGetUniformLocation(program, "u_tex");
        texVHandle = GLES20.glGetUniformLocation(program, "v_tex");

        texVertices = P2PVideoRenderUtils.createVerticesBuffer(TEX_VERTICES);
        posVertices = P2PVideoRenderUtils.createVerticesBuffer(POS_VERTICES);
    }

    public int[] getYUVTextures()
    {
        return yuvTextures;
    }

    public boolean process(P2PVideoGLImage rgb)
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

        //Log.d(LOGTAG, " rgb tex=" + rgb.getTexture() + " wid=" + rgb.getWidth() + " hei=" + rgb.getHeight());
        //Log.d(LOGTAG, " yuv ytex=" + yuvTextures[0] + " utex=" + yuvTextures[1] + " ytex=" + yuvTextures[2]);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rgb.getTexture());

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, rgb.getWidth(), rgb.getHeight(),
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, rgb.getTexture(), 0);

        checkGlError("glBindFramebuffer");

        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");

        GLES20.glViewport(0, 0, rgb.getWidth(), rgb.getHeight());
        checkGlError("glViewport");

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glVertexAttribPointer(this.texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, this.texVertices);
        GLES20.glEnableVertexAttribArray(this.texCoordHandle);

        GLES20.glVertexAttribPointer(this.posCoordHandle, 3, GLES20.GL_FLOAT, false, 0, this.posVertices);
        GLES20.glEnableVertexAttribArray(this.posCoordHandle);

        checkGlError("vertex attribute setup");

        GLES20.glUniformMatrix4fv(this.texCoordMatHandle, 1, false, this.mTextureMat, 0);
        checkGlError("texCoordMatHandle");

        GLES20.glUniformMatrix4fv(this.modelViewMatHandle, 1, false, this.mModelViewMat, 0);
        checkGlError("modelViewMatHandle");

        //
        // Attach YUV textures.
        //

        checkGlError("setYUVTextures");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 9729.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 9729.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, 33071.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, 33071.0f);
        GLES20.glUniform1i(texYHandle, 0);

        checkGlError("glBindTexture y");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[1]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 9729.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, 33071.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, 33071.0f);
        GLES20.glUniform1i(texUHandle, 1);

        checkGlError("glBindTexture u");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextures[2]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 9729.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, 33071.0f);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, 33071.0f);
        GLES20.glUniform1i(texVHandle, 2);

        checkGlError("glBindTexture v");

        //
        // Draw stuff,
        //

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        checkGlError("glDrawArrays");

        GLES20.glFinish();
        checkGlError("after draw");

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        checkGlError("after process");

        return true;
    }
}
