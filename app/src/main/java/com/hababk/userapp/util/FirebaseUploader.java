package com.hababk.userapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;

/**
 * Created by Tayeb-Ali on 04-12-2017.
 */

public class FirebaseUploader {
    private String storageRef;
    private UploadListener uploadListener;
    private UploadTask uploadTask;
    private Uri fileUri;
    private StorageReference uploadRef;

    public FirebaseUploader(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public FirebaseUploader(UploadListener uploadListener, String storageRef) {
        this.uploadListener = uploadListener;
        this.storageRef = storageRef;
    }

    public void compressAndUpload(final Context context, final File file) {
        @SuppressLint("StaticFieldLeak") AsyncTask<File, Void, String> compressionTask = new AsyncTask<File, Void, String>() {
            @Override
            protected String doInBackground(File... files) {
                String filePathCompressed = null;
                Uri originalFileUri = Uri.fromFile(files[0]);
                File tempFile = new File(context.getCacheDir(), originalFileUri.getLastPathSegment());
                filePathCompressed = SiliCompressor.with(context).compress(originalFileUri.toString(), tempFile);
                return filePathCompressed;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                File compressed = new File(s);
                fileUri = Uri.fromFile(compressed.length() > 0 ? compressed : file);
                if (storageRef == null)
                    storageRef = fileUri.getLastPathSegment();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                uploadRef = storage.getReference().child("images").child(storageRef);

                upload();
            }
        };

        compressionTask.execute(file);
    }

    private void upload() {
        uploadTask = uploadRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                uploadListener.onUploadSuccess(downloadUrl);
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadListener.onUploadFail(e.getMessage());
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                uploadListener.onUploadProgress((int) progress);
            }
        });
    }

    public void cancelUpload() {
        if (uploadTask != null && uploadTask.isInProgress()) {
            uploadTask.cancel();
            uploadListener.onUploadCancelled();
        }
    }

    public interface UploadListener {
        void onUploadFail(String message);

        void onUploadSuccess(String downloadUrl);

        void onUploadProgress(int progress);

        void onUploadCancelled();
    }
}
