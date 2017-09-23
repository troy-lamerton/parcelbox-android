package de.parcelbox;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.parcelbox.manager.UploadManager;

public class CameraView extends SurfaceView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    private TextureView mTextureView;

    private boolean safeToTakePicture = false;

    public CameraView(Context context) {
        super(context);
    }

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
            return;
        }

        // rotate camera correctly
        mCamera.setDisplayOrientation(90);

        // flip the image
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(mTextureView.getWidth(), 0);
        mTextureView.setTransform(matrix);

        try {
            // when the surface is created, we can set the camera to draw images in this surfaceholder
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();

            safeToTakePicture = true;
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    public void takePicture() {
        if (safeToTakePicture) {
            mCamera.takePicture(null, null, mPictureCallback);
            safeToTakePicture = false;
        }
    }

    private String getUniqueFileName() {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        return s.format(new Date());
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera.startPreview();

                try {
                    String filename = getUniqueFileName();

                    File file = new File(Environment.getExternalStorageDirectory() + "/" + filename + ".png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();

                    Log.d("MAIN", "saved file");

                    new UploadManager().uploadImage(file);

                    Log.d("MAIN", "uploaded file");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //finished saving picture
                safeToTakePicture = true;
            } catch (IOException e) {
                //do something about it
            }
        }
    };

}