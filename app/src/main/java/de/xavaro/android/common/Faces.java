package de.xavaro.android.common;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.widget.Toast;
import android.util.SparseArray;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class Faces
{
    private static final String LOGTAG = Faces.class.getSimpleName();

    public FaceDetector detector;

    public Faces(Context context)
    {
        detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        Log.d(LOGTAG, "Faces: detector=" + detector);

        if (!detector.isOperational())
        {
            //
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            //

            Log.d(LOGTAG, "Face detector dependencies are not yet available.");

            //
            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            //

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage)
            {
                String mess = "Face detector low storage cannot load...";

                Log.d(LOGTAG, mess);

                Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
            }
        }
    }

    public SparseArray<Face> detect(Bitmap bitmap)
    {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = detector.detect(frame);

        if (faces != null)
        {
            for (int inx = 0; inx < faces.size(); ++inx)
            {
                Face face = faces.valueAt(inx);
                PointF pos = face.getPosition();

                Log.d(LOGTAG, "detect: face=" + inx + " id=" + face.getId() + " x=" + pos.x + " y=" + pos.y);

                /*
                Log.d(LOGTAG, "detect:"
                        + " smile=" + face.getIsSmilingProbability()
                        + " lauf=" + face.getIsLeftEyeOpenProbability()
                        + " rauf=" + face.getIsRightEyeOpenProbability());
                */

                for (Landmark landmark : face.getLandmarks())
                {
                    int cx = (int) landmark.getPosition().x;
                    int cy = (int) landmark.getPosition().y;

                    //Log.d(LOGTAG, "detect: face=" + inx + " cx=" + cx + " cy=" + cy);
                }
            }
        }

        return faces;
    }
}
