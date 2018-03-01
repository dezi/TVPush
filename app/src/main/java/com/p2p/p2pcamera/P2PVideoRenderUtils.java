package com.p2p.p2pcamera;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class P2PVideoRenderUtils
{
    private static final String LOGTAG = P2PVideoRenderUtils.class.getSimpleName();

    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};

    private static final String FRAGMENT_SHADER  = "precision mediump float;\nuniform sampler2D tex_sampler;\nuniform float alpha;\nvarying vec2 v_texcoord;\nvoid main() {\nvec4 color = texture2D(tex_sampler, v_texcoord);\ngl_FragColor = color;\n}\n";
    private static final String VERTEX_SHADER    = "attribute vec4 a_position;\nattribute vec2 a_texcoord;\nuniform mat4 u_model_view; \nvarying vec2 v_texcoord;\nvoid main() {\n  gl_Position = u_model_view*a_position;\n  v_texcoord = a_texcoord;\n}\n";

    public static void renderTexture(P2PVideoShader renderContext, int i, int i2, int i3)
    {
        GLES20.glUseProgram(renderContext.shaderProgram);

        GLES20.glViewport(0, 0, i2, i3);

        checkGlError("glViewport");

        GLES20.glDisable(3042);
        GLES20.glVertexAttribPointer(renderContext.texCoordHandle, 2, 5126, false, 0, renderContext.texVertices);
        GLES20.glEnableVertexAttribArray(renderContext.texCoordHandle);
        GLES20.glVertexAttribPointer(renderContext.posCoordHandle, 2, 5126, false, 0, renderContext.posVertices);
        GLES20.glEnableVertexAttribArray(renderContext.posCoordHandle);

        checkGlError("vertex attribute setup");

        GLES20.glActiveTexture(33984);

        checkGlError("glActiveTexture");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10240, 9729);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10241, 9729);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10242, 33071);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10243, 33071);

        checkGlError("glBindTexture");

        GLES20.glUniform1i(renderContext.texSamplerHandle, 0);
        GLES20.glUniform1f(renderContext.alphaHandle, renderContext.alpha);
        GLES20.glUniformMatrix4fv(renderContext.modelViewMatHandle, 1, false, renderContext.mModelViewMat, 0);

        checkGlError("modelViewMatHandle");

        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glFinish();

        checkGlError("glDrawArrays");
    }

    public static P2PVideoShader createProgram()
    {
        return createProgram(POS_VERTICES, TEX_VERTICES);
    }

    private static P2PVideoShader createProgram(float[] fArr, float[] fArr2)
    {
        int loadShader1 = loadShader(35633, VERTEX_SHADER);

        if (loadShader1 == 0)
        {
            throw new RuntimeException("Could not load vertex shader: " + VERTEX_SHADER);
        }

        Log.d(LOGTAG, "createProgram: VERTEX_SHADER created.");

        int loadShader2 = loadShader(35632, FRAGMENT_SHADER);

        if (loadShader2 == 0)
        {
            throw new RuntimeException("Could not load fragment shader: " + FRAGMENT_SHADER);
        }

        Log.d(LOGTAG, "createProgram: FRAGMENT_SHADER created.");

        int glCreateProgram = GLES20.glCreateProgram();

        if (glCreateProgram != 0)
        {
            GLES20.glAttachShader(glCreateProgram, loadShader1);
            checkGlError("glAttachShader");

            GLES20.glAttachShader(glCreateProgram, loadShader2);
            checkGlError("glAttachShader");

            GLES20.glLinkProgram(glCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);

            if (iArr[0] != 1)
            {
                String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(glCreateProgram);
                GLES20.glDeleteProgram(glCreateProgram);

                throw new RuntimeException("Could not link program: " + glGetProgramInfoLog);
            }
        }

        P2PVideoShader renderContext = new P2PVideoShader();

        renderContext.texSamplerHandle = GLES20.glGetUniformLocation(glCreateProgram, "tex_sampler");
        renderContext.alphaHandle = GLES20.glGetUniformLocation(glCreateProgram, "alpha");
        renderContext.texCoordHandle = GLES20.glGetAttribLocation(glCreateProgram, "a_texcoord");
        renderContext.posCoordHandle = GLES20.glGetAttribLocation(glCreateProgram, "a_position");
        renderContext.modelViewMatHandle = GLES20.glGetUniformLocation(glCreateProgram, "u_model_view");
        renderContext.texVertices = createVerticesBuffer(fArr2);
        renderContext.posVertices = createVerticesBuffer(fArr);
        renderContext.shaderProgram = glCreateProgram;

        return renderContext;
    }

    public static FloatBuffer createVerticesBuffer(float[] fArr)
    {
        if (fArr.length != 8)
        {
            throw new RuntimeException("Number of vertices should be four.");
        }

        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        asFloatBuffer.put(fArr).position(0);

        return asFloatBuffer;
    }

    public static int loadShader(int type, String source)
    {
        int glCreateShader = GLES20.glCreateShader(type);

        if (glCreateShader != 0)
        {
            GLES20.glShaderSource(glCreateShader, source);
            GLES20.glCompileShader(glCreateShader);

            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);

            if (iArr[0] == 0)
            {
                String glGetShaderInfoLog = GLES20.glGetShaderInfoLog(glCreateShader);
                GLES20.glDeleteShader(glCreateShader);

                throw new RuntimeException("Could not compile shader " + type + ":" + glGetShaderInfoLog);
            }
        }

        return glCreateShader;
    }

    public static void clearTexture(int texture)
    {
        int[] iArr = new int[]{texture};

        GLES20.glDeleteTextures(iArr.length, iArr, 0);

        checkGlError("glDeleteTextures");
    }

    public static int createTexture()
    {
        int[] iArr = new int[1];

        GLES20.glGenTextures(iArr.length, iArr, 0);

        checkGlError("glGenTextures");

        return iArr[0];
    }

    public static int createTexture(Bitmap bitmap)
    {
        return createTexture(createTexture(), bitmap);
    }

    public static int createTexture(int texture, Bitmap bitmap)
    {
        if (texture < 0) texture = createTexture();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bitmap), bitmap, GLUtils.getType(bitmap), 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10240, 9729);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10241, 9729);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10242, 33071);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, 10243, 33071);

        checkGlError("texImage2D");

        return texture;
    }

    public static Bitmap saveTexture(int i, int i2, int i3)
    {
        int[] iArr = new int[1];

        GLES20.glGenFramebuffers(1, iArr, 0);

        checkGlError("glGenFramebuffers");

        GLES20.glBindFramebuffer(36160, iArr[0]);

        checkGlError("glBindFramebuffer");

        GLES20.glFramebufferTexture2D(36160, 36064, GLES20.GL_TEXTURE_2D, i, 0);

        checkGlError("glFramebufferTexture2D");

        Buffer allocate = ByteBuffer.allocate((i2 * i3) * 4);
        GLES20.glReadPixels(0, 0, i2, i3, 6408, 5121, allocate);

        checkGlError("glReadPixels");

        Bitmap bitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(allocate);
        GLES20.glBindFramebuffer(36160, 0);

        checkGlError("glBindFramebuffer");

        GLES20.glDeleteFramebuffers(1, iArr, 0);

        checkGlError("glDeleteFramebuffer");

        return bitmap;
    }

    public static void checkGlError(String str)
    {
        int glGetError = GLES20.glGetError();

        if (glGetError != 0)
        {
            Log.e(LOGTAG, str + ": glError " + getEGLErrorString(glGetError));

            for (StackTraceElement stackTraceElement : Thread.getAllStackTraces().get(Thread.currentThread()))
            {
                Log.e(LOGTAG, "SS: " + stackTraceElement.toString());
            }
        }
    }

    public static String getEGLErrorString(int glErr)
    {
        switch (glErr)
        {
            case 12288:
                return "EGL_SUCCESS";
            case 12289:
                return "EGL_NOT_INITIALIZED";
            case 12290:
                return "EGL_BAD_ACCESS";
            case 12291:
                return "EGL_BAD_ALLOC";
            case 12292:
                return "EGL_BAD_ATTRIBUTE";
            case 12293:
                return "EGL_BAD_CONFIG";
            case 12294:
                return "EGL_BAD_CONTEXT";
            case 12295:
                return "EGL_BAD_CURRENT_SURFACE";
            case 12296:
                return "EGL_BAD_DISPLAY";
            case 12297:
                return "EGL_BAD_MATCH";
            case 12298:
                return "EGL_BAD_NATIVE_PIXMAP";
            case 12299:
                return "EGL_BAD_NATIVE_WINDOW";
            case 12300:
                return "EGL_BAD_PARAMETER";
            case 12301:
                return "EGL_BAD_SURFACE";
            case 12302:
                return "EGL_CONTEXT_LOST";
        }

        return Integer.toString(glErr);
    }
}
