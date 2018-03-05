package zz.top.p2p.video;

import java.nio.FloatBuffer;

public class VideoGLShader
{
    private final static String LOGTAG = VideoGLShader.class.getSimpleName();

    public int program;

    public float alpha = 1.0f;

    public int alphaHandle;
    public int posCoordHandle;
    public int texCoordHandle;
    public int texCoordMatHandle;

    public int modelViewMatHandle;

    public int frameBufferHandle;

    public float[] mModelViewMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    public FloatBuffer posVertices;
    public FloatBuffer texVertices;

    public static boolean checkGlError(String str)
    {
        return VideoGLUtils.checkGlError(LOGTAG + ":" + str);
    }
}
