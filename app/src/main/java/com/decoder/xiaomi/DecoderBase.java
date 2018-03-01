package com.decoder.xiaomi;

import java.nio.ByteBuffer;

public abstract class DecoderBase
{
    public static final String MODEL_D11 = "d11";
    public static final String MODEL_H19 = "h19";
    public static final String MODEL_H20 = "h20";
    public static final String MODEL_H30 = "h30";
    public static final String MODEL_M20 = "yunyi.camera.mj1";
    public static final String MODEL_V1 = "yunyi.camera.v1";
    public static final String MODEL_V2 = "yunyi.camera.htwo1";
    public static final String MODEL_Y10 = "y10";
    public static final String MODEL_Y20 = "yunyi.camera.y20";
    public static final String MODEL_Y31 = "y31";

    protected int mNativeContext;

    public String model = null;

    public abstract boolean decodeBufferDecoder(ByteBuffer byteBuffer, int i, long j);

    public abstract boolean decodeDecoder(byte[] bArr, int i, long j);

    public abstract int getHeightDecoder();

    public abstract int getWidthDecoder();

    public abstract void initDecoder(int i);

    public abstract void releaseDecoder();

    public abstract int toTextureDecoder(int i, int i2, int i3);
}
