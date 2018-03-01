package com.p2p.p2pcamera;

import java.nio.FloatBuffer;

public class P2PVideoShader
{
    private final String LOGTAG = P2PVideoShader.class.getSimpleName();

    public int program;

    public float alpha = 1.0f;

    public int alphaHandle;
    public int posCoordHandle;
    public int texSamplerHandle;
    public int texCoordHandle;
    public int texCoordMatHandle;

    public int modelViewMatHandle;

    public float[] mModelViewMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    public FloatBuffer posVertices;
    public FloatBuffer texVertices;
}
