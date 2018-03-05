package com.p2p.p2pcamera;

import android.graphics.Bitmap;

public class P2PVideoGLImage
{
    private int width;
    private int height;
    private int texture;

    public P2PVideoGLImage()
    {
        this(P2PVideoGLUtils.createTexture(), 0, 0);
    }

    public P2PVideoGLImage(int width, int height)
    {
        this(P2PVideoGLUtils.createTexture(), width, height);
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

            this.texture = P2PVideoGLUtils.createTexture(bitmap);
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
        P2PVideoGLUtils.clearTexture(texture);

        this.texture = texture;
    }

    public void release()
    {
        P2PVideoGLUtils.clearTexture(texture);
        texture = -1;
    }

    public void updateSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public Bitmap save()
    {
        return P2PVideoGLUtils.saveTexture(texture, width, height);
    }

    public Bitmap saveSmall(int div)
    {
        return P2PVideoGLUtils.saveTexture(texture, width / div, height / div);
    }
}
