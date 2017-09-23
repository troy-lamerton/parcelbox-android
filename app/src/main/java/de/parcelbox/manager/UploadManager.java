package de.parcelbox.manager;

import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class UploadManager {

    interface Service {
        @Multipart
        @POST("/1jx4fnb1")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image);
    }

    public void uploadImage(File image) {
        Log.d("UPLOAD", "upload start");

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image", reqFile);

        Log.d("UPLOAD", "upload file: " + image.getAbsolutePath());

        // start the API call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://requestb.in/")
                .build();

        Service service = retrofit.create(Service.class);

        Call<ResponseBody> req = service.postImage(body);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("UPLOAD", "upload successful");
                } else {
                    Log.d("UPLOAD", "upload failed with " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UPLOAD", "upload failed", t);
            }
        });
    }

}
