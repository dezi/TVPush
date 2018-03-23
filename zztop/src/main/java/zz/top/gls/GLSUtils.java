package zz.top.gls;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import zz.top.utl.Log;

public class GLSUtils
{
    private static final String LOGTAG = GLSUtils.class.getSimpleName();

    public static FloatBuffer createVerticesBuffer(float[] fArr)
    {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(fArr).position(0);

        return floatBuffer;
    }

    public static int loadShader(int type, String source)
    {
        int shader = GLES20.glCreateShader(type);

        if (shader != 0)
        {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            int[] iArr = new int[1];
            GLES20.glGetShaderiv(shader, 35713, iArr, 0);

            if (iArr[0] == 0)
            {
                String glGetShaderInfoLog = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);

                throw new RuntimeException("Could not compile shader " + type + ":" + glGetShaderInfoLog);
            }
        }

        return shader;
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

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bitmap),
                bitmap, GLUtils.getType(bitmap), 0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        checkGlError("texImage2D");

        return texture;
    }

    public static Bitmap saveTexture(int texture, int width, int height)
    {
        int[] iArr = new int[1];

        GLES20.glGenFramebuffers(1, iArr, 0);

        checkGlError("glGenFramebuffers");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, iArr[0]);

        checkGlError("glBindFramebuffer");

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

        checkGlError("glFramebufferTexture2D");

        Buffer allocate = ByteBuffer.allocate((width * height) * 4);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, allocate);

        checkGlError("glReadPixels");

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(allocate);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        checkGlError("glBindFramebuffer");

        GLES20.glDeleteFramebuffers(1, iArr, 0);

        checkGlError("glDeleteFramebuffer");

        return bitmap;
    }

    public static void saveTextureToBuffer(int texture, int width, int height, Buffer buffer)
    {
        int[] iArr = new int[1];

        GLES20.glGenFramebuffers(1, iArr, 0);

        checkGlError("glGenFramebuffers");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, iArr[0]);

        checkGlError("glBindFramebuffer");

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

        checkGlError("glFramebufferTexture2D");

        buffer.rewind();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

        checkGlError("glReadPixels");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        checkGlError("glBindFramebuffer");

        GLES20.glDeleteFramebuffers(1, iArr, 0);

        checkGlError("glDeleteFramebuffer");
    }

    public static boolean checkGlError(String str)
    {
        int glGetError = GLES20.glGetError();

        if (glGetError != 0)
        {
            Log.e(LOGTAG, str + ": glError=" + getGLErrorString(glGetError));

            for (StackTraceElement stackTraceElement : Thread.getAllStackTraces().get(Thread.currentThread()))
            {
                Log.e(LOGTAG, "SS: " + stackTraceElement.toString());
            }

            return true;
        }

        return false;
    }

    public static String getGLErrorString(int glErr)
    {
        switch (glErr)
        {
            case 0x0500:
                return "GL_INVALID_ENUM";
            case 0x0501:
                return "GL_INVALID_VALUE";
            case 0x0502:
                return "GL_INVALID_OPERATION";
            case 0x0503:
                return "GL_STACK_OVERFLOW";
            case 0x0504:
                return "GL_STACK_UNDERFLOW";
            case 0x0505:
                return "GL_OUT_OF_MEMORY";
            case 0x0506:
                return "GL_INVALID_FRAMEBUFFER_OPERATION";
            case 0x0507:
                return "GL_CONTEXT_LOST";

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

        return "DEZI_UNKNOWN_ERROR_" + Integer.toString(glErr);
    }
}
