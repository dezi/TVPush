package de.xavaro.android.cam.util;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class CAMMP4Parser
{
    private static final String LOGTAG = CAMMP4Parser.class.getSimpleName();

    private HashMap<String, Long> mBoxes = new HashMap<>();
    private final RandomAccessFile mFile;
    private long mPos = 0;

    public static CAMMP4Parser parse(String path) throws IOException
    {
        return new CAMMP4Parser(path);
    }

    private CAMMP4Parser(final String path) throws IOException, FileNotFoundException
    {
        mFile = new RandomAccessFile(new File(path), "r");
        try
        {
            parse("", mFile.length());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException("Parse error: malformed mp4 file");
        }
    }

    public void close()
    {
        try
        {
            mFile.close();
        }
        catch (Exception e)
        {
        }
        ;
    }

    public long getBoxPos(String box) throws IOException
    {
        Long r = mBoxes.get(box);

        if (r == null) throw new IOException("Box not found: " + box);
        return mBoxes.get(box);
    }

    public CAMStsdBox getStsdBox() throws IOException
    {
        try
        {
            return new CAMStsdBox(mFile, getBoxPos("/moov/trak/mdia/minf/stbl/stsd"));
        }
        catch (IOException e)
        {
            throw new IOException("stsd box could not be found");
        }
    }

    private void parse(String path, long len) throws IOException
    {
        ByteBuffer byteBuffer;
        long sum = 0, newlen = 0;
        byte[] buffer = new byte[8];
        String name = "";

        if (!path.equals("")) mBoxes.put(path, mPos - 8);

        while (sum < len)
        {
            mFile.read(buffer, 0, 8);
            mPos += 8;
            sum += 8;

            if (validBoxName(buffer))
            {
                name = new String(buffer, 4, 4);

                if (buffer[3] == 1)
                {
                    // 64 bits atom size
                    mFile.read(buffer, 0, 8);
                    mPos += 8;
                    sum += 8;
                    byteBuffer = ByteBuffer.wrap(buffer, 0, 8);
                    newlen = byteBuffer.getLong() - 16;
                }
                else
                {
                    // 32 bits atom size
                    byteBuffer = ByteBuffer.wrap(buffer, 0, 4);
                    newlen = byteBuffer.getInt() - 8;
                }

                // 1061109559+8 correspond to "????" in ASCII the HTC Desire S seems to write that sometimes, maybe other phones do
                // "wide" atom would produce a newlen == 0, and we shouldn't throw an exception because of that
                if (newlen < 0 || newlen == 1061109559) throw new IOException();

                //Log.d(LOGTAG, "Atom -> name: " + name + " position: " + mPos + ", length: " + newlen);
                sum += newlen;
                parse(path + '/' + name, newlen);

            }
            else
            {
                if (len < 8)
                {
                    mFile.seek(mFile.getFilePointer() - 8 + len);
                    sum += len - 8;
                }
                else
                {
                    int skipped = mFile.skipBytes((int) (len - 8));
                    if (skipped < ((int) (len - 8)))
                    {
                        throw new IOException();
                    }
                    mPos += len - 8;
                    sum += len - 8;
                }
            }
        }
    }

    private boolean validBoxName(byte[] buffer)
    {
        for (int i = 0; i < 4; i++)
        {
            // If the next 4 bytes are neither lowercase letters nor numbers
            if ((buffer[i + 4] < 'a' || buffer[i + 4] > 'z') && (buffer[i + 4] < '0' || buffer[i + 4] > '9'))
                return false;
        }
        return true;
    }

    static String toHexString(byte[] buffer, int start, int len)
    {
        String c;
        StringBuilder s = new StringBuilder();
        for (int i = start; i < start + len; i++)
        {
            c = Integer.toHexString(buffer[i] & 0xFF);
            s.append(c.length() < 2 ? "0" + c : c);
        }
        return s.toString();
    }

}
