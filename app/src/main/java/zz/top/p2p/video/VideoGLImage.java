package zz.top.p2p.video;

import android.graphics.Bitmap;

public class VideoGLImage
{
    private int width;
    private int height;
    private int texture;

    public VideoGLImage()
    {
        this(VideoGLUtils.createTexture(), 0, 0);
    }

    public VideoGLImage(int width, int height)
    {
        this(VideoGLUtils.createTexture(), width, height);
    }

    public VideoGLImage(int texture, int width, int height)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public VideoGLImage(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();

            this.texture = VideoGLUtils.createTexture(bitmap);
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
        VideoGLUtils.clearTexture(texture);

        this.texture = texture;
    }

    public void release()
    {
        VideoGLUtils.clearTexture(texture);
        texture = -1;
    }

    public void updateSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public Bitmap save()
    {
        return VideoGLUtils.saveTexture(texture, width, height);
    }

    public Bitmap saveSmall(int div)
    {
        return VideoGLUtils.saveTexture(texture, width / div, height / div);
    }
}
