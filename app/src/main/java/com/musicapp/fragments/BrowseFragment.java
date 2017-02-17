package com.musicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.musicapp.activities.LoginActivity;
import com.musicapp.activities.SearchActivity;
import com.musicapp.adapters.ArtistsAdapter;
import com.musicapp.adapters.BrowseAdapter;
import com.musicapp.adapters.HomeDataAdapter;
import com.musicapp.adapters.RVBrowsAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;

import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.SearchListItem;
import com.musicapp.pojos.SearchListSectionItem;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/21/2016.
 */
public class BrowseFragment extends Fragment{

    View view;
    RecyclerView rvSearchList;
    BrowseAdapter myAdapter;
    LinearLayout lnrtvSearch,lnrSearch;
    ProgressBar progressBar;
    String from="browse", url, deviceId;
    int userId;
    private static final String TAG = BrowseFragment.class.getSimpleName();


    ArrayList <HomeDetailsJson.Categories> main_categories_list;
    ArrayList<HomeDetailsJson.Categories> main_categories_list_containing_elements;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse, container, false);
        initializer();
        userId = PreferencesManager.getInstance(BrowseFragment.this.getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(BrowseFragment.this.getActivity()).getDeviceId();
        setupViewAction();
      //  getAllArtistsService();

      /*  main_categories_list_containing_elements = MySingleton.getInstance(getActivity()).getHome_categories_list();
        if (main_categories_list_containing_elements.isEmpty()) {
            maincategory_webservice();
        }
        else {
            setAdapter( main_categories_list_containing_elements);
        }*/
        maincategory_webservice();
        return view;
    }

    private void initializer() {
        lnrSearch = (LinearLayout) view.findViewById(R.id.lnrSearch);
        lnrtvSearch = (LinearLayout) view.findViewById(R.id.lnrtvSearch);
        rvSearchList = (RecyclerView) view.findViewById(R.id.rvSearchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BrowseFragment.this.getActivity().getApplicationContext());
        rvSearchList.setLayoutManager(mLayoutManager);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

    }

    private void setupViewAction() {

        lnrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
           //     intent.putExtra("from","browse");
                startActivity(intent);
            }
        });


    }



    private void maincategory_webservice()
    {
        main_categories_list_containing_elements = new ArrayList<>();
        Log.w(TAG, Utility.home2+"UserId="+userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.home2+"UserId="+userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();


                            for (int i = 0; i<main_categories_list.size(); i++)
                            {
                                if(main_categories_list.get(i).getDataList().isEmpty())
                                {
                                    Log.w(TAG, "Values for Child are empty " );

                                }else
                                {
                                    main_categories_list_containing_elements.add(jsonResponse.getCategories().get(i));
                                }

                            }


                        //   myAdapter = new BrowseAdapter(getActivity(), main_categories_list_containing_elements,1);
                            setAdapter( main_categories_list_containing_elements);
                         //   MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            rvSearchList.setAdapter(myAdapter);
                        }
                        else if(jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page"))
                        {
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();;
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
               //         Toast.makeText(BrowseFragment.this.getActivity(),error.toString(),Toast.LENGTH_LONG).show();
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


    private void setAdapter( ArrayList <HomeDetailsJson.Categories> main_categories_list_containing_elements)
    {
        myAdapter =new BrowseAdapter(getActivity(), main_categories_list_containing_elements,1 );
        rvSearchList.setAdapter(myAdapter);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = BrowseFragment.this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) BrowseFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
