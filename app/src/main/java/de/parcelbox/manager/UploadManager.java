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

    public static final String BASE_URL = "http://172.16.2.231:5000";

    interface Service {
        @Multipart
        @POST("/")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image);
    }

    public void uploadImage(File image, String filename) {
        Log.d("UPLOAD", "upload start");

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpg"), image);
        MultipartBody.Part body = MultipartBody.Part.createFormData("pic", filename, reqFile);

        Log.d("UPLOAD", "upload file: " + image.getAbsolutePath());

        // start the API call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
