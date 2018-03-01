package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class P2PVideoFrameRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = P2PVideoFrameRenderer.class.getSimpleName();

    private P2PVideoRenderProgram renderContext;
    private P2PVideoStillImage image;

    public void setStillImage(P2PVideoStillImage image)
    {
        this.image = image;
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        renderContext = createProgram();
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2)
    {
        Log.d(LOGTAG, "onSurfaceChanged.");
    }

    public void onDrawFrame(GL10 gl10)
    {
        Log.d(LOGTAG, "onDrawFrame.");

        if (image != null)
        {
            //setRenderMatrix(image.width(), image.height());

            P2PVideoRenderUtils.renderTexture(renderContext, image.texture(), image.width(), image.height());
        }
    }

    //region GL shader programm.

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};

    private static final String FRAGMENT_SHADER = ""
        + "precision mediump float;"
        + "uniform sampler2D tex_sampler;\nuniform float alpha;\nvarying vec2 v_texcoord;\nvoid main() {\nvec4 color = texture2D(tex_sampler, v_texcoord);\ngl_FragColor = color;\n}\n";

    private static final String VERTEX_SHADER    = "attribute vec4 a_position;\nattribute vec2 a_texcoord;\nuniform mat4 u_model_view; \nvarying vec2 v_texcoord;\nvoid main() {\n  gl_Position = u_model_view*a_position;\n  v_texcoord = a_texcoord;\n}\n";

    private P2PVideoRenderProgram createProgram()
    {
        int vertexShader = P2PVideoRenderUtils.loadShader(35633, VERTEX_SHADER);

        if (vertexShader == 0)
        {
            throw new RuntimeException("Could not load vertex shader: " + VERTEX_SHADER);
        }

        Log.d(LOGTAG, "createProgram: VERTEX_SHADER created.");

        int fragentShader = P2PVideoRenderUtils.loadShader(35632, FRAGMENT_SHADER);

        if (fragentShader == 0)
        {
            throw new RuntimeException("Could not load fragment shader: " + FRAGMENT_SHADER);
        }

        Log.d(LOGTAG, "createProgram: FRAGMENT_SHADER created.");

        int programm = GLES20.glCreateProgram();

        if (programm != 0)
        {
            GLES20.glAttachShader(programm, vertexShader);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glAttachShader(programm, fragentShader);
            P2PVideoRenderUtils.checkGlError("glAttachShader");

            GLES20.glLinkProgram(programm);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(programm, 35714, iArr, 0);

            if (iArr[0] != 1)
            {
                String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(programm);
                GLES20.glDeleteProgram(programm);

                throw new RuntimeException("Could not link program: " + glGetProgramInfoLog);
            }
        }

        P2PVideoRenderProgram renderContext = new P2PVideoRenderProgram();

        renderContext.shaderProgram = programm;

        renderContext.alphaHandle = GLES20.glGetUniformLocation(programm, "alpha");
        renderContext.texCoordHandle = GLES20.glGetAttribLocation(programm, "a_texcoord");
        renderContext.posCoordHandle = GLES20.glGetAttribLocation(programm, "a_position");
        renderContext.texSamplerHandle = GLES20.glGetUniformLocation(programm, "tex_sampler");
        renderContext.modelViewMatHandle = GLES20.glGetUniformLocation(programm, "u_model_view");

        renderContext.texVertices = P2PVideoRenderUtils.createVerticesBuffer(TEX_VERTICES);
        renderContext.posVertices = P2PVideoRenderUtils.createVerticesBuffer(POS_VERTICES);

        return renderContext;
    }

    //endregion GL shader programm.
}