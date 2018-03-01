package com.p2p.p2pcamera;

import java.nio.FloatBuffer;

public class P2PVideoRenderContext
{
    public float alpha = 1.0f;

    public int alphaHandle;
    public int posCoordHandle;
    public int texCoordHandle;
    public int modelViewMatHandle;
    public int texSamplerHandle;

    public int shaderProgram;

    public float[] mModelViewMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    public FloatBuffer posVertices;
    public FloatBuffer texVertices;
}
