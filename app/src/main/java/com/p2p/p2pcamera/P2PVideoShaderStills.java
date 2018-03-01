package com.p2p.p2pcamera;

import android.opengl.GLES20;

public class P2PVideoShaderStills extends P2PVideoShader
{
    private final String LOGTAG = P2PVideoShader.class.getSimpleName();

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};

    private final String YUV_FRAGMENT_SHADER_SOURCE = ""
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

    public int texUHandle;
    public int texVHandle;
    public int texYHandle;

    public int[] textures;

    public P2PVideoShaderStills()
    {
        int loadShader1 = P2PVideoRenderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE);

        if (loadShader1 == 0)
        {
            throw new RuntimeException("Could not load vertex shader: " + VERTEX_SHADER_SOURCE);
        }

        int loadShader2 = P2PVideoRenderUtils.loadShader(35632, YUV_FRAGMENT_SHADER_SOURCE);

        if (loadShader2 == 0)
        {
            throw new RuntimeException("Could not load fragment shader: " + YUV_FRAGMENT_SHADER_SOURCE);
        }

        program = GLES20.glCreateProgram();
        
        if (program != 0)
        {
            GLES20.glAttachShader(program, loadShader1);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glAttachShader(program, loadShader2);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glLinkProgram(program);

            int[] iArr = new int[1];
            GLES20.glGetProgramiv(program, 35714, iArr, 0);
            
            if (iArr[0] != 1)
            {
                String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);
                program = 0;
                throw new RuntimeException("Could not link program: " + glGetProgramInfoLog);
            }

            texSamplerHandle = GLES20.glGetUniformLocation(program, "inputImageTexture");
            texCoordHandle = GLES20.glGetAttribLocation(program, "a_texcoord");
            posCoordHandle = GLES20.glGetAttribLocation(program, "a_position");
            texCoordMatHandle = GLES20.glGetUniformLocation(program, "u_texture_mat");
            modelViewMatHandle = GLES20.glGetUniformLocation(program, "u_model_view");

            texVertices = P2PVideoRenderUtils.createVerticesBuffer(TEX_VERTICES);
            posVertices = P2PVideoRenderUtils.createVerticesBuffer(POS_VERTICES);

            prepareParams();
        }
        else
        {
            throw new RuntimeException("Could not create program");
        }
    }

    public void setYuvTextures(int[] iArr)
    {
        textures = iArr;
    }

    protected void prepareParams()
    {
        texYHandle = GLES20.glGetUniformLocation(program, "y_tex");
        texUHandle = GLES20.glGetUniformLocation(program, "u_tex");
        texVHandle = GLES20.glGetUniformLocation(program, "v_tex");
    }

    protected void updateParams()
    {
        GlslFilter.checkGlError("setYuvTextures");

        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10241, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(texYHandle, 0);

        GlslFilter.checkGlError("glBindTexture y");

        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, textures[1]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(texUHandle, 1);

        GlslFilter.checkGlError("glBindTexture u");

        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, textures[2]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(texVHandle, 2);

        GlslFilter.checkGlError("glBindTexture v");
    }
}
