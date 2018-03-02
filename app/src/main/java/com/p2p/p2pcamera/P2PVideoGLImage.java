package com.p2p.p2pcamera;

import android.graphics.Bitmap;

public class P2PVideoGLImage
{
    private int width;
    private int height;
    private int texture = -1;

    public P2PVideoGLImage(int texture, int width, int height)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public static P2PVideoGLImage create(int width, int height)
    {
        return new P2PVideoGLImage(P2PVideoRenderUtils.createTexture(), width, height);
    }

    public static P2PVideoGLImage create(Bitmap bitmap)
    {
        return (bitmap != null) ? new P2PVideoGLImage(P2PVideoRenderUtils.createTexture(bitmap), bitmap.getWidth(), bitmap.getHeight()) : null;
    }


    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getTexture()
    {
        return texture;
    }

    public void setTexture(int texture)
    {
        P2PVideoRenderUtils.clearTexture(texture);

        this.texture = texture;
    }

    public void clear()
    {
        P2PVideoRenderUtils.clearTexture(texture);
        texture = -1;
    }

    public void updateSize(int width, int height)
    {
        if ((this.width != width) || (this.height != height))
        {
            changeSize(width, height);
        }
    }

    public void changeSize(int width, int height)
    {
        this.width = width;
        this.height = height;

        P2PVideoRenderUtils.clearTexture(texture);
        texture = P2PVideoRenderUtils.createTexture();
    }

    public Bitmap save()
    {
        return P2PVideoRenderUtils.saveTexture(texture, width, height);
    }
}
