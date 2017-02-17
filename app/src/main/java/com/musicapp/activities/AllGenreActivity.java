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
import com.musicapp.pojos.AllGenresJson;
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

public class AllGenreActivity extends AppCompatActivity   {

    int from;
    private static final String TAG= AllGenreActivity.class.getSimpleName();
    @Bind(R.id.genre_recyclerview) RecyclerView genre_recyclerview;
    ProgressBar progressBar;
    static LinearLayout popup_layout;
    @Bind(R.id.btn_ok) TextView btn_ok;
    @Bind(R.id.lnrContinue) LinearLayout lnrContinue;

    ArrayList<AllGenresJson.GenreList> allGenresJsonArrayList ;
    HashMap<Integer,String> markfavGenre_hashMap = new HashMap<>();
    SelectGenreAdapter myAdapter;
    int userId;
    String deviceId;
    String output=" ";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_genre);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        from= intent.getIntExtra("from",0);
        LinearLayoutManager llm = new LinearLayoutManager(AllGenreActivity.this);
        genre_recyclerview.setLayoutManager(llm);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        popup_layout = (LinearLayout)findViewById(R.id.popup_layout);
        userId= PreferencesManager.getInstance(AllGenreActivity.this).getUserId();
        deviceId=PreferencesManager.getInstance(AllGenreActivity.this).getDeviceId();
        events();
    }

    private  void events()
    {
        getAllGenresService();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_layout.setVisibility(View.GONE);
            }
        });


        lnrContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContinue(v);
            }
        });

    }

    private void getAllGenresService()
    {
        allGenresJsonArrayList = new ArrayList<>();
        Log.w(TAG, Utility.GET_ALL_GENRES+"UserId="+ userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.GET_ALL_GENRES+"UserId="+ userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        AllGenresJson jsonResponse = gson.fromJson(response, AllGenresJson.class);
                        if (jsonResponse.getMessage().equalsIgnoreCase("Sucess")) {
                            allGenresJsonArrayList = jsonResponse.getGenreList();


                            for (int i = 0; i<allGenresJsonArrayList.size(); i++)
                            {
                                Log.w(TAG, allGenresJsonArrayList.get(i).getGenreName());
                               // markFav.add(allGenresJsonArrayList.get(i).getGenreId());
                                markfavGenre_hashMap.put(allGenresJsonArrayList.get(i).getGenreId(),allGenresJsonArrayList.get(i).getIsFav());

                            }





                            //  MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            setAdapter(allGenresJsonArrayList);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AllGenreActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }){


        };

        MySingleton.getInstance(AllGenreActivity.this).getRequestQueue().add(stringRequest);
    }

    public void onShowPop (Context context) {
        popup_layout.setVisibility(View.VISIBLE);
    }
    private void setAdapter(  ArrayList <AllGenresJson.GenreList> allGenresJsonArrayList)
    {
        myAdapter = new SelectGenreAdapter(AllGenreActivity.this, allGenresJsonArrayList,1);
        genre_recyclerview.setAdapter(myAdapter);
    }


    public void onClickContinue(View v)
    {
        ArrayList<Integer> arrayGenrId = new ArrayList<>();
        output=" ";

        Set<Integer> keySet = markfavGenre_hashMap.keySet();
        ArrayList<Integer> listOfGenreIdKeys = new ArrayList<Integer>(keySet);


        Collection<String> values = markfavGenre_hashMap.values();
        ArrayList<String> listOfFavValues = new ArrayList<String>(values);

       for(int i=0;i<listOfGenreIdKeys.size();i++)
       {
           if(listOfFavValues.get(i).equalsIgnoreCase("Y"))
           {
               arrayGenrId.add(listOfGenreIdKeys.get(i));
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
            onShowPop(AllGenreActivity.this);
        }
        else
            fav_genres_webservice();

    }

    private void fav_genres_webservice() {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", PreferencesManager.getInstance(AllGenreActivity.this).getUserId());
            jsonParams.put("genreId", output);
            jsonParams.put("deviceId", PreferencesManager.getInstance(AllGenreActivity.this).getDeviceId());

        }
        catch (JSONException e) {e.printStackTrace();}

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.ADD_TO_FAV_GENRES, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        Log.w("hello" ,response.toString());
                        progressBar.setVisibility(View.GONE);
                        if(from==1)
                        {
                            Intent intent=new Intent();
                            setResult(1,intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(AllGenreActivity.this, AllArtistsStartActivity.class);
                            intent.putExtra("from", 0);
                            startActivity(intent);
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
        MySingleton.getInstance(AllGenreActivity.this).getRequestQueue().add(request);
    }







    public class SelectGenreAdapter  extends RecyclerView.Adapter<SelectGenreAdapter.SingleItemRowHolder> {

        private ArrayList<AllGenresJson.GenreList> itemsList;

        private Context mContext;
        int category_Id;
        private ImageLoader mImageLoader;



        public SelectGenreAdapter(Context mContext, ArrayList<AllGenresJson.GenreList> itemsList ,int category_Id) {
            mImageLoader = MySingleton.getInstance(mContext).getImageLoader();
            this.itemsList = itemsList;
            this.mContext = mContext;
            this.category_Id=category_Id;


        }
        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_select_genre_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {

            holder.tvTitle.setText(itemsList.get(i).getGenreName());
          //  holder.itemImage.setImageUrl(itemsList.get(i).getCoverImage(), mImageLoader);
            Picasso.with(mContext).load(itemsList.get(i).getCoverImage()).into(holder.itemImage);
            if(markfavGenre_hashMap.get(itemsList.get(i).getGenreId()).equals("Y"))
            {
                holder.fav_icon.setImageResource(R.mipmap.ic_favorite_white_24dp);
            }
            else
                holder.fav_icon.setImageResource(R.mipmap.ic_favorite_border_white_24dp);

            holder.fav_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(markfavGenre_hashMap.get(itemsList.get(i).getGenreId()).equals("Y"))
                    {
                        markfavGenre_hashMap.put(itemsList.get(i).getGenreId(),"N");
                        notifyDataSetChanged();

                    }
                    else
                    {
                        markfavGenre_hashMap.put(itemsList.get(i).getGenreId(),"Y");
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
           // protected NetworkImageView itemImage;
            protected ImageView fav_icon;


            public SingleItemRowHolder(View view) {
                super(view);

                this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
               // this.itemImage.setDefaultImageResId(R.drawable.home_item3);
                this.fav_icon=(ImageView)view.findViewById(R.id.fav_icon);
            }

        }

    }

}
