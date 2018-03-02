package com.p2p.p2pcamera;

import android.graphics.Bitmap;

public class P2PVideoStillImage
{
    private int width;
    private int height;
    private int texture = -1;

    public P2PVideoStillImage(int texture, int width, int height)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public static P2PVideoStillImage create(int width, int height)
    {
        return new P2PVideoStillImage(P2PVideoRenderUtils.createTexture(), width, height);
    }

    public static P2PVideoStillImage create(Bitmap bitmap)
    {
        return (bitmap != null) ? new P2PVideoStillImage(P2PVideoRenderUtils.createTexture(bitmap), bitmap.getWidth(), bitmap.getHeight()) : null;
    }

    public void changeDimension(int width, int height)
    {
        this.width = width;
        this.height = height;

        P2PVideoRenderUtils.clearTexture(texture);
        texture = P2PVideoRenderUtils.createTexture();
    }

    public void clear()
    {
        P2PVideoRenderUtils.clearTexture(texture);
        texture = -1;
    }

    public boolean matchDimension(P2PVideoStillImage photo)
    {
        return (photo.width == width) && (photo.height == height);
    }

    public Bitmap save()
    {
        return P2PVideoRenderUtils.saveTexture(texture, width, height);
    }

    public void setTexture(int texture)
    {
        P2PVideoRenderUtils.clearTexture(texture);

        this.texture = texture;
    }

    public void swap(P2PVideoStillImage photo)
    {
        int swap = texture;
        texture = photo.texture;
        photo.texture = swap;
    }

    public int texture()
    {
        return texture;
    }

    public void update(Bitmap bitmap)
    {
        texture = P2PVideoRenderUtils.createTexture(texture, bitmap);

        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    public void updateSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }


}
