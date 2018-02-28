package com.video.draw;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class EGLConfigChooser implements android.opengl.GLSurfaceView.EGLConfigChooser
{
    private static int[] s_configAttribs2 = new int[]{12324, 4, 12323, 4, 12322, 4, 12321, 4, 12326, -1, 12325, -1, 12352, 4, 12339, 1, 12344};
    protected int mAlphaSize;
    protected int mBlueSize;
    protected int mDepthSize;
    protected int mGreenSize;
    protected int mRedSize;
    protected int mStencilSize;
    private int[] mValue = new int[1];

    public EGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6)
    {
        this.mRedSize = i;
        this.mGreenSize = i2;
        this.mBlueSize = i3;
        this.mAlphaSize = i4;
        this.mDepthSize = i5;
        this.mStencilSize = i6;
    }

    private int findConfigAttrib(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2)
    {
        return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue) ? this.mValue[0] : i2;
    }

    private void printConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig)
    {
        int[] iArr = new int[]{12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354};
        String[] strArr = new String[]{"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"};
        int[] iArr2 = new int[1];
        for (int i = 0; i < iArr.length; i++)
        {
            int i2 = iArr[i];
            Object obj = strArr[i];
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i2, iArr2))
            {
                Log.d("EGLConfigChooser", String.format("  %s: %d\n", new Object[]{obj, Integer.valueOf(iArr2[0])}));
            }
            else
            {
                while (egl10.eglGetError() != 12288)
                {
                }
            }
        }
    }

    private void printConfigs(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr)
    {
        Log.d("EGLConfigChooser", String.format("%d configurations", new Object[]{Integer.valueOf(eGLConfigArr.length)}));

        for (EGLConfig printConfig : eGLConfigArr)
        {
            printConfig(egl10, eGLDisplay, printConfig);
        }
    }

    public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay)
    {
        int[] iArr = new int[1];
        egl10.eglChooseConfig(eGLDisplay, s_configAttribs2, null, 0, iArr);
        int i = iArr[0];
        if (i <= 0)
        {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] eGLConfigArr = new EGLConfig[i];
        egl10.eglChooseConfig(eGLDisplay, s_configAttribs2, eGLConfigArr, i, iArr);
        return chooseConfig(egl10, eGLDisplay, eGLConfigArr);
    }

    public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr)
    {
        for (EGLConfig eGLConfig : eGLConfigArr)
        {
            int findConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0);
            int findConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0);
            if (findConfigAttrib >= this.mDepthSize && findConfigAttrib2 >= this.mStencilSize)
            {
                findConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12324, 0);
                int findConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12323, 0);
                int findConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12322, 0);
                findConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12321, 0);
                if (findConfigAttrib == this.mRedSize && findConfigAttrib3 == this.mGreenSize && findConfigAttrib4 == this.mBlueSize && findConfigAttrib2 == this.mAlphaSize)
                {
                    printConfig(egl10, eGLDisplay, eGLConfig);
                    return eGLConfig;
                }
            }
        }
        return null;
    }
}
