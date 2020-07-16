package com.MiniDouyin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.MiniDouyin.MainActivity;
import com.MiniDouyin.NewVideoActivity;
import com.MiniDouyin.R;
import com.MiniDouyin.api.IMiniDouyinService;

import com.MiniDouyin.api.IMiniDouyinService;
import com.MiniDouyin.api.IMiniDouyinService;
import com.MiniDouyin.model.GetVideosResponse;
import com.MiniDouyin.model.PostVideoResponse;
import com.MiniDouyin.model.Video;
import com.MiniDouyin.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "MainActivity";
    private static final int RESULT_OK = -1;
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public ImageButton mBtn;
    private ImageButton Btnrefresh;
    private Button mBtnRefresh;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(IMiniDouyinService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_i, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
        initBtns();
    }

    private void initBtns() {
        mBtn = getView().findViewById(R.id.btni);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getActivity().startActivityForResult(new Intent(getActivity(), NewVideoActivity.class),123);
            }
        });



        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if (response.body() != null && response.body().videos != null) {
                    mVideos = response.body().videos;
                    Iterator<Video> iterator = mVideos.iterator();
                    while(iterator.hasNext()){
                        Video video = iterator.next();
                        Log.i(TAG, "onResponse: "+video.studentId+" "+video.userName);
                        if(!video.studentId.equals("147258369")){
                            iterator.remove();
                        }
                    }
                    mRv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initRecyclerView() {
        mRv = getView().findViewById(R.id.rvi);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRv.setAdapter(new RecyclerView.Adapter<MainActivity.MyViewHolder>() {
            @NonNull
            @Override
            public MainActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MainActivity.MyViewHolder(
                        LayoutInflater.from(getActivity()).inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MainActivity.MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(getActivity(), video);
            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 )
        {
            onResume();
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(getActivity(), uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        //@TODO 4下面的id和名字替换成自己的
        miniDouyinService.postVideo("147258369", "Kingdom", coverImagePart, videoPart).enqueue(
                new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        if (response.body() != null) {
                            Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
