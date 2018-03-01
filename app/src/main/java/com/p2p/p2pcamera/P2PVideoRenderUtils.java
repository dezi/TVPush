package com.p2p.p2pcamera;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class P2PVideoRenderUtils
{
    private static final String LOGTAG = P2PVideoRenderUtils.class.getSimpleName();

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
}
