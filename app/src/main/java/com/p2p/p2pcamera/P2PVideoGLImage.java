package com.p2p.p2pcamera;

import android.graphics.Bitmap;

public class P2PVideoGLImage
{
    private int width;
    private int height;
    private int texture;

    public P2PVideoGLImage()
    {
        this(P2PVideoRenderUtils.createTexture(), 0, 0);
    }

    public P2PVideoGLImage(int width, int height)
    {
        this(P2PVideoRenderUtils.createTexture(), width, height);
    }

    public P2PVideoGLImage(int texture, int width, int height)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public P2PVideoGLImage(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();

            this.texture = P2PVideoRenderUtils.createTexture(bitmap);
        }
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

    public void release()
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
