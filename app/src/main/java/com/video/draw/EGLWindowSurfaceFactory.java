package com.video.draw;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EGLWindowSurfaceFactory implements android.opengl.GLSurfaceView.EGLWindowSurfaceFactory
{
    private static int EGL_BACK_BUFFER = 12420;

    public EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj)
    {
        EGLSurface eGLSurface = null;
        int[] iArr = new int[]{12422, EGL_BACK_BUFFER, 12344};
        while (eGLSurface == null)
        {
            try
            {
                eGLSurface = egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, iArr);
                if (eGLSurface == null)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
            catch (Throwable th)
            {
                if (eGLSurface == null)
                {
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e2)
                    {
                    }
                }
            }
        }
        return eGLSurface;
    }

    public void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface)
    {
        egl10.eglDestroySurface(eGLDisplay, eGLSurface);
    }
}
