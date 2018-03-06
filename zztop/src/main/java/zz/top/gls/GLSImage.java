package zz.top.gls;

import android.graphics.Bitmap;

import java.nio.Buffer;

public class GLSImage
{
    private int width;
    private int height;
    private int texture;

    public GLSImage()
    {
        this(GLSUtils.createTexture(), 0, 0);
    }

    public GLSImage(int width, int height)
    {
        this(GLSUtils.createTexture(), width, height);
    }

    public GLSImage(int texture, int width, int height)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public GLSImage(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();

            this.texture = GLSUtils.createTexture(bitmap);
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
        GLSUtils.clearTexture(texture);

        this.texture = texture;
    }

    public void release()
    {
        GLSUtils.clearTexture(texture);
        texture = -1;
    }

    public void updateSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public Bitmap save()
    {
        return GLSUtils.saveTexture(texture, width, height);
    }

    public Bitmap save(int div)
    {
        return GLSUtils.saveTexture(texture, width / div, height / div);
    }

    public void save(Buffer buffer)
    {
        GLSUtils.saveTextureToBuffer(texture, width, height, buffer);
    }
}
