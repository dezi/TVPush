package com.video.draw;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class EGLContextFactory implements android.opengl.GLSurfaceView.EGLContextFactory
{
    private static int EGL_CONTEXT_CLIENT_VERSION = 12440;

    private static void checkEglError(String str, EGL10 egl10)
    {
        while (egl10.eglGetError() != 12288)
        {
            Log.d("EGLContextFactory", String.format("%s: EGL error: 0x%x", new Object[]{str, Integer.valueOf(egl10.eglGetError())}));
        }
    }

    public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig)
    {
        int[] iArr = new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
        checkEglError("Before eglCreateContext", egl10);
        EGLContext eglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, iArr);
        checkEglError("After eglCreateContext", egl10);
        return eglCreateContext;
    }

    public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext)
    {
        egl10.eglDestroyContext(eGLDisplay, eGLContext);
    }
}
