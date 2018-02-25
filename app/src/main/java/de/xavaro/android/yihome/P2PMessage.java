package de.xavaro.android.yihome;

public class P2PMessage
{
    public byte[] data;
    public int error;
    public boolean needWaitResponse;
    public int reqId;
    public int resId;
    public IMessageResponse resp;

    public interface IMessageResponse
    {
        void onError(int i);

        boolean onResponse(byte[] bArr);
    }

    public P2PMessage(int i, int i2, byte[] bArr, IMessageResponse iMessageResponse)
    {
        this.reqId = i;
        this.resId = i2;
        this.data = bArr;
        if (iMessageResponse != null)
        {
            this.resp = iMessageResponse;
        }
        this.needWaitResponse = true;
    }

    public P2PMessage(int i, byte[] bArr)
    {
        this.reqId = i;
        this.data = bArr;
        this.needWaitResponse = false;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        return this.reqId == ((P2PMessage) obj).reqId;
    }

    public int hashCode()
    {
        return this.reqId + 31;
    }
}
