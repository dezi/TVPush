package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlslFilter
{
    private static final String LOGTAG = GlslFilter.class.getSimpleName();

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final String FRAGMENT_SHADER = "precision mediump float;\nuniform sampler2D inputImageTexture;\nvarying vec2 textureCoordinate;\nvoid main() {\n  gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}\n";
    private static final String VERTEX_SHADER = "attribute vec4 a_position;\nattribute vec2 a_texcoord;\nuniform mat4 u_texture_mat; \nuniform mat4 u_model_view; \nvarying vec2 textureCoordinate;\nvoid main() {\n  gl_Position = u_model_view*a_position;\n  vec4 tmp = u_texture_mat*vec4(a_texcoord.x,a_texcoord.y,0.0,1.0);\n  textureCoordinate = tmp.xy;\n}\n";
    public static final int GL_TEXTURE_2D = 3553;
    public static final int GL_TEXTURE_EXTERNAL_OES = 36197;
    private static final float[] IDENTIFY_MATRIX = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] POS_VERTICES = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f};
    private static final float[] TEX_VERTICES = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private static final float[] TEX_VERTICES_SURFACE_TEXTURE = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f};
    private final String SURFACE_TEXTURE_FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES inputImageTexture;\nvarying vec2 textureCoordinate;\nvoid main() {\n  gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}\n";
    private int[] frameBufferObjectId = new int[]{0};
    boolean isInitialed = false;
    //protected Context mContext;
    int mInputTextureType = GL_TEXTURE_2D;
    P2PVideoGLImage mMiddlePhoto;
    final float[] mModelViewMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    GlslFilter mNextGlslFilter;
    final float[] mTextureMat = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private int modelViewMatHandle;
    private int posCoordHandle;
    private FloatBuffer posVertices;
    protected int shaderProgram;
    private int texCoordHandle;
    private int texCoordMatHandle;
    private int texSamplerHandle;
    private FloatBuffer texVertices;

    //public GlslFilter(Context context)

    public static void checkGlError(String str)
    {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0)
        {
            throw new RuntimeException(getError(str, glGetError));
        }
    }

    private static FloatBuffer createVerticesBuffer(float[] fArr)
    {
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        asFloatBuffer.put(fArr).position(0);
        return asFloatBuffer;
    }

    public static String getError(String str, int i)
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str).append(" - ");
        switch (i)
        {
            case 0:
                stringBuffer.append("No errors.");
                break;
            case 1280:
                stringBuffer.append("Invalid enum");
                break;
            case 1281:
                stringBuffer.append("Invalid value");
                break;
            case 1282:
                stringBuffer.append("Invalid operation");
                break;
            case 1286:
                stringBuffer.append("OpenGL invalid framebuffer operation.");
                break;
            case 36053:
                stringBuffer.append("Framebuffer complete.");
                break;
            case 36054:
                stringBuffer.append("OpenGL framebuffer attached images must have same format.");
                break;
            case 36055:
                stringBuffer.append("OpenGL framebuffer missing attachment.");
                break;
            case 36057:
                stringBuffer.append("OpenGL framebuffer attached images must have same dimensions.");
                break;
            case 36061:
                stringBuffer.append("OpenGL framebuffer format not supported. ");
                break;
            default:
                stringBuffer.append("OpenGL error: " + i);
                break;
        }
        return stringBuffer.toString();
    }

    private static int loadShader(int i, String str)
    {
        int glCreateShader = GLES20.glCreateShader(i);

        Log.d(LOGTAG, "loadShader: glCreateShader=" + glCreateShader + " errno=" + GLES20.glGetError());

        if (glCreateShader != 0)
        {
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0)
            {
                String glGetShaderInfoLog = GLES20.glGetShaderInfoLog(glCreateShader);
                GLES20.glDeleteShader(glCreateShader);
                throw new RuntimeException("Could not compile shader " + i + ":" + glGetShaderInfoLog);
            }
        }

        return glCreateShader;
    }

    private void processInner(P2PVideoGLImage photo, P2PVideoGLImage photo2)
    {
        if (this.shaderProgram != 0)
        {
            if (photo2 == null)
            {
                GLES20.glBindFramebuffer(36160, 0);
            }
            else
            {
                if (this.frameBufferObjectId[0] == 0)
                {
                    GLES20.glGenFramebuffers(1, this.frameBufferObjectId, 0);
                }
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(GL_TEXTURE_2D, photo2.getTexture());
                GLES20.glTexParameteri(GL_TEXTURE_2D, 10240, 9729);
                GLES20.glTexParameteri(GL_TEXTURE_2D, 10241, 9729);
                GLES20.glTexParameteri(GL_TEXTURE_2D, 10242, 33071);
                GLES20.glTexParameteri(GL_TEXTURE_2D, 10243, 33071);
                GLES20.glTexImage2D(GL_TEXTURE_2D, 0, 6408, photo2.getWidth(), photo2.getHeight(), 0, 6408, 5121, null);
                GLES20.glBindFramebuffer(36160, this.frameBufferObjectId[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, GL_TEXTURE_2D, photo2.getTexture(), 0);
                checkGlError("glBindFramebuffer");
            }
            GLES20.glUseProgram(this.shaderProgram);
            checkGlError("glUseProgram");
            GLES20.glViewport(0, 0, photo2.getWidth(), photo2.getHeight());
            checkGlError("glViewport");
            GLES20.glDisable(3042);
            GLES20.glVertexAttribPointer(this.texCoordHandle, 2, 5126, false, 0, this.texVertices);
            GLES20.glEnableVertexAttribArray(this.texCoordHandle);
            GLES20.glVertexAttribPointer(this.posCoordHandle, 3, 5126, false, 0, this.posVertices);
            GLES20.glEnableVertexAttribArray(this.posCoordHandle);
            checkGlError("vertex attribute setup");
            if (photo != null && this.texSamplerHandle >= 0)
            {
                GLES20.glActiveTexture(33984);
                checkGlError("glActiveTexture");
                GLES20.glBindTexture(this.mInputTextureType, photo.getTexture());
                checkGlError("glBindTexture");
                GLES20.glTexParameteri(this.mInputTextureType, 10240, 9729);
                GLES20.glTexParameteri(this.mInputTextureType, 10241, 9729);
                GLES20.glTexParameteri(this.mInputTextureType, 10242, 33071);
                GLES20.glTexParameteri(this.mInputTextureType, 10243, 33071);
                GLES20.glUniform1i(this.texSamplerHandle, 0);
                checkGlError("texSamplerHandle");
            }
            GLES20.glUniformMatrix4fv(this.texCoordMatHandle, 1, false, this.mTextureMat, 0);
            checkGlError("texCoordMatHandle");
            GLES20.glUniformMatrix4fv(this.modelViewMatHandle, 1, false, this.mModelViewMat, 0);
            checkGlError("modelViewMatHandle");
            updateParams();
            GLES20.glDrawArrays(6, 0, 4);
            checkGlError("glDrawArrays");
            GLES20.glFinish();
            if (photo2 != null)
            {
                GLES20.glFramebufferTexture2D(36160, 36064, GL_TEXTURE_2D, 0, 0);
                GLES20.glBindFramebuffer(36160, 0);
            }
            checkGlError("after process");
        }
    }

    protected void doRelease()
    {
    }

    public final void flipXModelView()
    {
        this.mModelViewMat[0] = -this.mModelViewMat[0];
        this.mModelViewMat[1] = -this.mModelViewMat[1];
        this.mModelViewMat[2] = -this.mModelViewMat[2];
        this.mModelViewMat[3] = -this.mModelViewMat[3];
    }

    public final void flipYModelView()
    {
        this.mModelViewMat[4] = -this.mModelViewMat[4];
        this.mModelViewMat[5] = -this.mModelViewMat[5];
        this.mModelViewMat[6] = -this.mModelViewMat[6];
        this.mModelViewMat[7] = -this.mModelViewMat[7];
    }

    public String fragmentShader()
    {
        return this.mInputTextureType == GL_TEXTURE_2D ? FRAGMENT_SHADER : "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES inputImageTexture;\nvarying vec2 textureCoordinate;\nvoid main() {\n  gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}\n";
    }

    public final void initial()
    {
        if (!this.isInitialed)
        {
            this.isInitialed = true;

            int loadShader1 = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader());

            if (loadShader1 == 0)
            {
                throw new RuntimeException("Could not load vertex shader: " + vertexShader());
            }

            int loadShader2 = loadShader(35632, fragmentShader());

            if (loadShader2 == 0)
            {
                throw new RuntimeException("Could not load fragment shader: " + fragmentShader());
            }

            this.shaderProgram = GLES20.glCreateProgram();
            if (this.shaderProgram != 0)
            {
                GLES20.glAttachShader(this.shaderProgram, loadShader1);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(this.shaderProgram, loadShader2);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(this.shaderProgram);
                int[] iArr = new int[1];
                GLES20.glGetProgramiv(this.shaderProgram, 35714, iArr, 0);
                if (iArr[0] != 1)
                {
                    String glGetProgramInfoLog = GLES20.glGetProgramInfoLog(this.shaderProgram);
                    GLES20.glDeleteProgram(this.shaderProgram);
                    this.shaderProgram = 0;
                    throw new RuntimeException("Could not link program: " + glGetProgramInfoLog);
                }
                this.texSamplerHandle = GLES20.glGetUniformLocation(this.shaderProgram, "inputImageTexture");
                this.texCoordHandle = GLES20.glGetAttribLocation(this.shaderProgram, "a_texcoord");
                this.posCoordHandle = GLES20.glGetAttribLocation(this.shaderProgram, "a_position");
                this.texCoordMatHandle = GLES20.glGetUniformLocation(this.shaderProgram, "u_texture_mat");
                this.modelViewMatHandle = GLES20.glGetUniformLocation(this.shaderProgram, "u_model_view");
                if (this.mInputTextureType == GL_TEXTURE_2D)
                {
                    this.texVertices = createVerticesBuffer(TEX_VERTICES);
                }
                else
                {
                    this.texVertices = createVerticesBuffer(TEX_VERTICES_SURFACE_TEXTURE);
                }
                this.posVertices = createVerticesBuffer(POS_VERTICES);
                prepareParams();
                if (this.mNextGlslFilter != null)
                {
                    this.mNextGlslFilter.initial();
                    return;
                }
                return;
            }
            throw new RuntimeException("Could not create program");
        }
    }

    protected void prepareParams()
    {
    }

    public void process(P2PVideoGLImage photo, P2PVideoGLImage photo2)
    {
        if (this.mNextGlslFilter == null)
        {
            processInner(photo, photo2);
            return;
        }
        if (this.mMiddlePhoto == null)
        {
            P2PVideoGLImage photo3 = photo == null ? photo2 : photo;
            if (photo3 != null)
            {
                this.mMiddlePhoto = new P2PVideoGLImage(photo3.getWidth(), photo3.getHeight());
            }
        }
        processInner(photo, this.mMiddlePhoto);
        this.mNextGlslFilter.processInner(this.mMiddlePhoto, photo2);
    }

    public final void release()
    {
        if (this.isInitialed)
        {
            this.isInitialed = false;
            if (this.mMiddlePhoto != null)
            {
                this.mMiddlePhoto.release();
                this.mMiddlePhoto = null;
            }
            if (this.mNextGlslFilter != null)
            {
                this.mNextGlslFilter.release();
            }
            doRelease();
            if (this.shaderProgram > 0)
            {
                GLES20.glDeleteProgram(this.shaderProgram);
                this.shaderProgram = 0;
            }
            if (this.frameBufferObjectId[0] > 0)
            {
                GLES20.glDeleteFramebuffers(1, this.frameBufferObjectId, 0);
                this.frameBufferObjectId[0] = 0;
            }
        }
    }

    public final void rotationModelView(int i)
    {
        updateModelViewMatrix(IDENTIFY_MATRIX);
        float cos = (float) Math.cos((((double) i) * 3.1415926d) / 180.0d);
        float sin = (float) Math.sin((((double) i) * 3.1415926d) / 180.0d);
        this.mModelViewMat[0] = cos;
        this.mModelViewMat[1] = -sin;
        this.mModelViewMat[4] = sin;
        this.mModelViewMat[5] = cos;
    }

    public void setNextFilter(GlslFilter glslFilter)
    {
        this.mNextGlslFilter = glslFilter;
    }

    public final void setTypeWeg(int i)
    {
        this.mInputTextureType = i;
    }

    public final void updateModelViewMatrix(float[] fArr)
    {
        for (int i = 0; i < fArr.length; i++)
        {
            this.mModelViewMat[i] = fArr[i];
        }
    }

    protected void updateParams()
    {
    }

    public final void updateTextureMatrix(float[] fArr)
    {
        for (int i = 0; i < fArr.length; i++)
        {
            this.mTextureMat[i] = fArr[i];
        }
    }

    public String vertexShader()
    {
        return VERTEX_SHADER;
    }
}
