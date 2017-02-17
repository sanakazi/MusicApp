package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.others.Utility;
import com.musicapp.pojos.AllArtistsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AllArtistsActivity extends AppCompatActivity {

    @Bind(R.id.artist_recyclerview) RecyclerView artist_recyclerview;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.btn_ok) TextView btn_ok;
    static LinearLayout popup_layout;
    private static final String TAG= AllArtistsActivity.class.getSimpleName();
    ArrayList <AllArtistsJson.ArtistsList> allArtistsJsonArrayList;
    HashMap<Integer,String> markfavArtist_hashMap = new HashMap<>();
    SelectArtistAdapter myAdapter;
    int userId,from;
    String deviceId;String output=" ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_artists);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        from= intent.getIntExtra("from",0);
        userId= PreferencesManager.getInstance(AllArtistsActivity.this).getUserId();
        deviceId=PreferencesManager.getInstance(AllArtistsActivity.this).getDeviceId();
        popup_layout = (LinearLayout)findViewById(R.id.popup_layout);
        LinearLayoutManager llm = new LinearLayoutManager(AllArtistsActivity.this);
        artist_recyclerview.setLayoutManager(llm);
        
        events();
    }
    
    private void events(){
        getAllArtistsService();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_layout.setVisibility(View.GONE);
            }
        });
    }

    private void getAllArtistsService()
    {
        allArtistsJsonArrayList = new ArrayList<>();
        Log.w(TAG, Utility.GET_ALL_ARTISTS+"UserId="+ userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.GET_ALL_ARTISTS+"UserId="+ userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        AllArtistsJson jsonResponse = gson.fromJson(response, AllArtistsJson.class);
                        if (jsonResponse.getMessage().equalsIgnoreCase("Sucess")) {
                            allArtistsJsonArrayList = jsonResponse.getArtistList();

                            for (int i = 0; i<allArtistsJsonArrayList.size(); i++)
                            {
                                Log.w(TAG, allArtistsJsonArrayList.get(i).getArtistName());
                                markfavArtist_hashMap.put(allArtistsJsonArrayList.get(i).getArtistId(),allArtistsJsonArrayList.get(i).getIsFav());
                            }

                           setAdapter(allArtistsJsonArrayList);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AllArtistsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }){


        };

        MySingleton.getInstance(AllArtistsActivity.this).getRequestQueue().add(stringRequest);
    }

    private void setAdapter( ArrayList <AllArtistsJson.ArtistsList> allArtistsJsonArrayList)
    {
        myAdapter =new SelectArtistAdapter(AllArtistsActivity.this, allArtistsJsonArrayList,1 );
        artist_recyclerview.setAdapter(myAdapter);
    }

    public void onShowPopup(Context getContext) {
        popup_layout.setVisibility(View.VISIBLE);
    }

    public void onClickContinue(View v)
    {
        ArrayList<Integer> arrayGenrId = new ArrayList<>();
        output=" ";

        Set<Integer> keySet = markfavArtist_hashMap.keySet();
        ArrayList<Integer> listOfArtistsIdKeys = new ArrayList<Integer>(keySet);


        Collection<String> values = markfavArtist_hashMap.values();
        ArrayList<String> listOfFavValues = new ArrayList<String>(values);

        for(int i=0;i<listOfArtistsIdKeys.size();i++)
        {
            if(listOfFavValues.get(i).equalsIgnoreCase("Y"))
            {
                arrayGenrId.add(listOfArtistsIdKeys.get(i));
            }
        }


        Log.w(TAG, "arraylength is "+arrayGenrId.size());
        for(int i=0;i<arrayGenrId.size();i++)
        {
            output= output+String.valueOf(arrayGenrId.get(i));
            if(i!=(arrayGenrId.size())-1)
            {
                output=output+",";
            }
        }
        Log.w(TAG, "output is " +output);


        if(arrayGenrId.size()<3)
        {
            // mListener.onShowPopup();
            onShowPopup(AllArtistsActivity.this);
        }
        else
            fav_artists_webservice();

    }

    private void fav_artists_webservice() {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", PreferencesManager.getInstance(AllArtistsActivity.this).getUserId());
            jsonParams.put("artistId", output);
            jsonParams.put("deviceId", PreferencesManager.getInstance(AllArtistsActivity.this).getDeviceId());

        }
        catch (JSONException e) {e.printStackTrace();}

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.ADD_TO_FAV_Artists, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        Log.w(TAG ,response.toString());
                        progressBar.setVisibility(View.GONE);
                        if(from==1){

                            Intent intent=new Intent();
                            setResult(1,intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(AllArtistsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);

                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });
        MySingleton.getInstance(AllArtistsActivity.this).getRequestQueue().add(request);
    }

    public class SelectArtistAdapter  extends RecyclerView.Adapter<SelectArtistAdapter.SingleItemRowHolder> {


        private ArrayList<AllArtistsJson.ArtistsList> itemsList;
        private Context mContext;
        int category_Id;
        ArrayList<Integer> markFav;
        private ImageLoader mImageLoader;


        public SelectArtistAdapter(Context context, ArrayList<AllArtistsJson.ArtistsList> itemsList ,int category_Id) {
            mImageLoader = MySingleton.getInstance(context).getImageLoader();
            this.itemsList = itemsList;
            this.mContext = context;
            this.category_Id=category_Id;
            this.markFav = markFav;

        }
        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_select_artist_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SingleItemRowHolder holder,final int i) {


            holder.tvTitle.setText(itemsList.get(i).getArtistName());
          //  holder.itemImage.setImageUrl(itemsList.get(i).getCoverImage(), mImageLoader);
            Picasso.with(mContext).load(itemsList.get(i).getCoverImage()).into(holder.itemImage);
            if(markfavArtist_hashMap.get(itemsList.get(i).getArtistId()).equals("Y"))
            {
                holder.fav_icon.setImageResource(R.mipmap.ic_favorite_white_24dp);
            }
            else
                holder.fav_icon.setImageResource(R.mipmap.ic_favorite_border_white_24dp);

            holder.fav_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(markfavArtist_hashMap.get(itemsList.get(i).getArtistId()).equals("Y"))
                    {
                        markfavArtist_hashMap.put(itemsList.get(i).getArtistId(),"N");
                        notifyDataSetChanged();

                    }
                    else
                    {
                        markfavArtist_hashMap.put(itemsList.get(i).getArtistId(),"Y");
                        notifyDataSetChanged();

                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class SingleItemRowHolder extends RecyclerView.ViewHolder {

            protected TextView tvTitle;

            protected ImageView itemImage;
            protected ImageView fav_icon;


            public SingleItemRowHolder(View view) {
                super(view);

                this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
              //  this.itemImage.setDefaultImageResId(R.drawable.home_item3);
                this.fav_icon=(ImageView)view.findViewById(R.id.fav_icon);
            }

        }

    }


}
