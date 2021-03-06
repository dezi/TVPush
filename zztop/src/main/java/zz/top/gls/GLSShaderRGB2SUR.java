package zz.top.gls;

import android.opengl.GLES20;

import zz.top.utl.Log;

public class GLSShaderRGB2SUR extends GLSShader
{
    private final String LOGTAG = GLSShaderRGB2SUR.class.getSimpleName();

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};

    private static final float[] TEX_VERTICES_NORM = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private static final float[] TEX_VERTICES_FlIP = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f};

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

    public GLSShaderRGB2SUR()
    {
        int vertexShader = GLSUtils.loadShader(35633, VERTEX_SHADER_SOURCE);

        if (vertexShader == 0)
        {
            throw new RuntimeException("Could not load vertex shader: " + VERTEX_SHADER_SOURCE);
        }

        Log.d(LOGTAG, "createProgram: VERTEX_SHADER created.");

        int fragentShader = GLSUtils.loadShader(35632, FRAGMENT_SHADER_SOURCE);

        if (fragentShader == 0)
        {
            throw new RuntimeException("Could not load fragment shader: " + FRAGMENT_SHADER_SOURCE);
        }

        Log.d(LOGTAG, "createProgram: FRAGMENT_SHADER created.");

        program = GLES20.glCreateProgram();

        if (program == 0)
        {
            throw new RuntimeException(LOGTAG + ": Could not create program");
        }

        GLES20.glAttachShader(program, vertexShader);
        GLSUtils.checkGlError("glAttachShader");

        GLES20.glAttachShader(program, fragentShader);
        GLSUtils.checkGlError("glAttachShader");

        GLES20.glLinkProgram(program);

        int[] iArr = new int[1];

        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, iArr, 0);

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
        texSamplerHandle = GLES20.glGetUniformLocation(program, "tex_sampler");

        posVertices = GLSUtils.createVerticesBuffer(POS_VERTICES);

        texVerticesFlip = GLSUtils.createVerticesBuffer(TEX_VERTICES_FlIP);
        texVerticesNorm = GLSUtils.createVerticesBuffer(TEX_VERTICES_NORM);
    }

    public boolean process(GLSImage rgb, int width, int height)
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

        //
        // Setup program and view.
        //

        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");

        GLES20.glViewport(0, 0, width, height);
        checkGlError("glViewport");

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, this.texVerticesFlip);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(posCoordHandle, 3, GLES20.GL_FLOAT, false, 0, this.posVertices);
        GLES20.glEnableVertexAttribArray(posCoordHandle);

        checkGlError("vertex attribute setup");

        //
        // Attach RGB texture.
        //

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rgb.getTexture());
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        checkGlError("glBindTexture");

        GLES20.glUniform1i(texSamplerHandle, 0);
        GLES20.glUniform1f(alphaHandle, alpha);

        GLES20.glUniformMatrix4fv(this.modelViewMatHandle, 1, false, this.mModelViewMat, 0);
        checkGlError("modelViewMatHandle");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        checkGlError("glDrawArrays");

        GLES20.glFinish();
        checkGlError("after draw");

        return true;
    }
}
