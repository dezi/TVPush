package com.p2p.p2pcamera;

import android.graphics.Bitmap;

public class P2PVideoStillImage
{
    private int height;
    private int texture = -1;
    private int width;

    public P2PVideoStillImage(int i, int i2, int i3)
    {
        this.texture = i;
        this.width = i2;
        this.height = i3;
    }

    public static P2PVideoStillImage create(int i, int i2)
    {
        return new P2PVideoStillImage(P2PVideoRenderUtils.createTexture(), i, i2);
    }

    public static P2PVideoStillImage create(Bitmap bitmap)
    {
        return bitmap != null ? new P2PVideoStillImage(P2PVideoRenderUtils.createTexture(bitmap), bitmap.getWidth(), bitmap.getHeight()) : null;
    }

    public void changeDimension(int i, int i2)
    {
        this.width = i;
        this.height = i2;
        P2PVideoRenderUtils.clearTexture(this.texture);
        this.texture = P2PVideoRenderUtils.createTexture();
    }

    public void clear()
    {
        P2PVideoRenderUtils.clearTexture(this.texture);
        this.texture = -1;
    }

    public int height()
    {
        return this.height;
    }

    public boolean matchDimension(P2PVideoStillImage photo)
    {
        return photo.width == this.width && photo.height == this.height;
    }

    public Bitmap save()
    {
        return P2PVideoRenderUtils.saveTexture(this.texture, this.width, this.height);
    }

    public void setTexture(int i)
    {
        P2PVideoRenderUtils.clearTexture(this.texture);
        this.texture = i;
    }

    public void swap(P2PVideoStillImage photo)
    {
        int i = this.texture;
        this.texture = photo.texture;
        photo.texture = i;
    }

    public int texture()
    {
        return this.texture;
    }

    public void update(Bitmap bitmap)
    {
        this.texture = P2PVideoRenderUtils.createTexture(this.texture, bitmap);
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    public void updateSize(int i, int i2)
    {
        this.width = i;
        this.height = i2;
    }

    public int width()
    {
        return this.width;
    }
}
