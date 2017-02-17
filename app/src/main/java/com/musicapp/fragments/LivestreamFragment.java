package com.musicapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.LivestreamAdapter;
import com.musicapp.others.Utility;
import com.musicapp.pojos.LivestreamJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SanaKazi on 2/15/2017.
 */
public class LivestreamFragment  extends Fragment {
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.btn_changeloc)
    TextView btn_changeloc;
    Dialog dialog;


    RecyclerView rvPlayList;



    int userId,cityId=1;
    String deviceId;
    private static final String TAG=LivestreamFragment.class.getSimpleName();
    ArrayList<LivestreamJson.ConcertClass> concertClassArrayList =new ArrayList<>();
    ArrayList<LivestreamJson.DataListClass> dataListClassArrayList=new ArrayList<>();
    LivestreamAdapter livestreamAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream, container, false);
        ButterKnife.bind(this,view);
      //  rvPlayList = (RecyclerView)view.findViewById(R.id.rvPlayList);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userId = PreferencesManager.getInstance(getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(getActivity()).getDeviceId();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        maincategory_webservice();

        events();

    }

    private void events()
    {
        btn_changeloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupForlist();
            }
        });
    }
    private void maincategory_webservice()
    {


        progressBar.setVisibility(View.VISIBLE);
        Log.w(TAG, "live stream url is" + Utility.LIVE_STREAM_URL+"UserId="+userId+"&DeviceId="+deviceId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.LIVE_STREAM_URL+"UserId="+userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, "live stream response is " + response);
                        Gson gson = new Gson();
                        LivestreamJson jsonResponse = gson.fromJson(response, LivestreamJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                        concertClassArrayList = jsonResponse.getConcert();
                          data_fromcity(cityId);

                        }
                        else{

                        }

                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //         Toast.makeText(HomeFragment.this.getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                })


        //region volleycache
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
            try {
                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                if (cacheEntry == null) {
                    cacheEntry = new Cache.Entry();
                }
                final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                long now = System.currentTimeMillis();
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;
                cacheEntry.data = response.data;
                cacheEntry.softTtl = softExpire;
                cacheEntry.ttl = ttl;
                String headerValue;
                headerValue = response.headers.get("Date");
                if (headerValue != null) {
                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                headerValue = response.headers.get("Last-Modified");
                if (headerValue != null) {
                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                cacheEntry.responseHeaders = response.headers;
                final String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new String(jsonString), cacheEntry);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }

            @Override
            protected void deliverResponse(String response) {
            super.deliverResponse(response);
        }

            @Override
            public void deliverError(VolleyError error) {
            super.deliverError(error);
        }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }


        };
        //endregion

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

    private void data_fromcity(int cityId)
    {
        for(int i=0; i<concertClassArrayList.size();i++)
        {
            if(concertClassArrayList.get(i).getCityId()==cityId) {

                int childArraySize=concertClassArrayList.get(i).getDataList().size();
                dataListClassArrayList = concertClassArrayList.get(i).getDataList();
                for(int j=0;j<childArraySize;j++)
                {
                    Log.w(TAG, dataListClassArrayList.get(j).getConcertDate());
                }



                livestreamAdapter = new LivestreamAdapter(getActivity(),dataListClassArrayList);
                recyclerView.setAdapter(livestreamAdapter);
            }

        }
    }

    private void openPopupForlist( ) {


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_window_dim);
        dialog.setContentView(R.layout.popup_location);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        rvPlayList = (RecyclerView) dialog.findViewById(R.id.rvPlayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvPlayList.setLayoutManager(mLayoutManager);


        LivestreamLocationPopupAdapter  popup_adapter = new LivestreamLocationPopupAdapter(getActivity(),concertClassArrayList);
        rvPlayList.setAdapter(popup_adapter);


        dialog.show();
    }








    public class LivestreamLocationPopupAdapter extends RecyclerView.Adapter<LivestreamLocationPopupAdapter.SingleItemRowHolder> {

        private ArrayList<LivestreamJson.ConcertClass> itemsList;
        private Context mContext;
        int category_Id;
        private ImageLoader mImageLoader;



        public LivestreamLocationPopupAdapter(Context mContext, ArrayList<LivestreamJson.ConcertClass> itemsList ) {
            mImageLoader = MySingleton.getInstance(mContext).getImageLoader();
            this.itemsList = itemsList;
            this.mContext = mContext;

        }

        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popup_playlist, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SingleItemRowHolder holder,final int position) {

            holder.tvName.setText(itemsList.get(position).getCityName());
            Log.w(TAG,itemsList.get(position).getCityName());
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    data_fromcity(itemsList.get(position).getCityId());

                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class SingleItemRowHolder extends RecyclerView.ViewHolder {

            protected TextView tvName;


            public SingleItemRowHolder(View view) {
                super(view);

                this.tvName = (TextView) view.findViewById(R.id.tvName);


            }
        }
    }




}
