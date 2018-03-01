package com.p2p.p2pcamera;

import android.opengl.GLES20;

public class YUVFilter extends GlslFilter
{
    private final String YUV_FRAGMENT_SHADER_STRING = "precision mediump float;\nvarying vec2 textureCoordinate;\nuniform sampler2D y_tex;\nuniform sampler2D u_tex;\nuniform sampler2D v_tex;\nvoid main() {\n  float y = texture2D(y_tex, textureCoordinate).r;\n  float u = texture2D(u_tex, textureCoordinate).r - 0.5;\n  float v = texture2D(v_tex, textureCoordinate).r - 0.5;\n  gl_FragColor = vec4(y + 1.403 * v,                       y - 0.344 * u - 0.714 * v,                       y + 1.77 * u, 1.0);\n}\n";

    int texUHandle;
    int texVHandle;
    int texYHandle;
    int[] textures;

    public String fragmentShader()
    {
        return YUV_FRAGMENT_SHADER_STRING;
    }

    protected void prepareParams()
    {
        super.prepareParams();

        this.texYHandle = GLES20.glGetUniformLocation(this.shaderProgram, "y_tex");
        this.texUHandle = GLES20.glGetUniformLocation(this.shaderProgram, "u_tex");
        this.texVHandle = GLES20.glGetUniformLocation(this.shaderProgram, "v_tex");
    }

    public void setYuvTextures(int[] iArr)
    {
        this.textures = iArr;
    }

    protected void updateParams()
    {
        super.updateParams();
        GlslFilter.checkGlError("setYuvTextures");
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, this.textures[0]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10241, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(this.texYHandle, 0);
        GlslFilter.checkGlError("glBindTexture y");
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, this.textures[1]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(this.texUHandle, 1);
        GlslFilter.checkGlError("glBindTexture u");
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(GlslFilter.GL_TEXTURE_2D, this.textures[2]);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10240, 9729.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10242, 33071.0f);
        GLES20.glTexParameterf(GlslFilter.GL_TEXTURE_2D, 10243, 33071.0f);
        GLES20.glUniform1i(this.texVHandle, 2);
        GlslFilter.checkGlError("glBindTexture v");
    }
}
