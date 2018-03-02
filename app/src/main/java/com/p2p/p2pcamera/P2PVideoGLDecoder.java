package com.p2p.p2pcamera;

import java.nio.ByteBuffer;

public abstract class P2PVideoGLDecoder
{
    public abstract boolean decodeBufferDecoder(ByteBuffer byteBuffer, int i, long j);

    public abstract boolean decodeDecoder(byte[] bArr, int i, long j);

    public abstract int getHeightDecoder();

    public abstract int getWidthDecoder();

    public abstract void initDecoder(int i);

    public abstract void releaseDecoder();

    public abstract int toTextureDecoder(int i, int i2, int i3);
}
