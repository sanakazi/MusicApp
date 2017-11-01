package com.musicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.musicapp.activities.AppIntroActivityNew;
import com.musicapp.activities.LoginActivity;
import com.musicapp.activities.SearchActivity;
import com.musicapp.adapters.ArtistsAdapter;
import com.musicapp.adapters.BrowseAdapter;
import com.musicapp.adapters.HomeDataAdapter;
import com.musicapp.adapters.RVBrowsAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;

import com.musicapp.pojos.BrowseJson;
import com.musicapp.pojos.BrowseJson;
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
public class BrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    RecyclerView rvSearchList;
    BrowseAdapter myAdapter;
    LinearLayout lnrtvSearch,lnrSearch;
    ProgressBar progressBar;
    String from="browse", url, deviceId;
    EditText edtSearch;
    ImageView ivSearch;
    int userId;
    private static final String TAG = BrowseFragment.class.getSimpleName();


    ArrayList<BrowseJson.Categories> main_categories_list;
    ArrayList<BrowseJson.Categories> main_categories_list_containing_elements;


    SwipeRefreshLayout refreshLayout;//changes by amol
    public boolean refreshmode = false;//changes by amol

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
        ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        lnrSearch = (LinearLayout) view.findViewById(R.id.lnrSearch);
        lnrtvSearch = (LinearLayout) view.findViewById(R.id.lnrtvSearch);
        rvSearchList = (RecyclerView) view.findViewById(R.id.rvSearchList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);//changes by amol
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BrowseFragment.this.getActivity().getApplicationContext());
        rvSearchList.setLayoutManager(mLayoutManager);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        refreshLayout.setOnRefreshListener(this);//changes by amol
        refreshLayout.setColorSchemeResources(R.color.header_bg_start, R.color.header_bg_end, R.color.home_icons_yellow); //changes by amol


    }

    private void setupViewAction() {


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edtSearch.getText().toString().trim().matches("")) {
                        // edtSearch.setError(getResources().getString(R.string.error_search_input));
                        edtSearch.requestFocus();
                    } else {

                        if (ComonHelper.checkConnection(getActivity())) {
                            Bundle bundle = new Bundle();
                            bundle.putString("from", "browse");
                            bundle.putString("search", edtSearch.getText().toString());
                            Intent intent = new Intent(getActivity(), SearchActivity.class);
                            //intent.putExtra("from","browse");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                        }

                    }
                    return true;
                }
                return false;
            }
        });


        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtSearch.getText().toString().trim().matches("")) {
                    // edtSearch.setError(getResources().getString(R.string.error_search_input));
                    edtSearch.requestFocus();
                }

                Bundle bundle = new Bundle();
                bundle.putString("from", "browse");
                bundle.putString("search", edtSearch.getText().toString());
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                //intent.putExtra("from","browse");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }



    private void maincategory_webservice()
    {
        refreshLayout.setRefreshing(true); //changes by amol
        main_categories_list_containing_elements = new ArrayList<>();
        Log.w(TAG, Utility.BROWSE_URL + "UserId=" + userId + "&DeviceId=" + deviceId);
        if (refreshmode) //changes by amol
        {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.BROWSE_URL + "UserId=" + userId + "&DeviceId=" + deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        BrowseJson jsonResponse = gson.fromJson(response, BrowseJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();


                            for (int i = 0; i<main_categories_list.size(); i++)
                            {
                                    main_categories_list_containing_elements.add(jsonResponse.getCategories().get(i));

                            }


                        //   myAdapter = new BrowseAdapter(getActivity(), main_categories_list_containing_elements,1);
                            setAdapter( main_categories_list_containing_elements);
                         //   MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            rvSearchList.setAdapter(myAdapter);
                        }
                        else if(jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page"))
                        {
                            PreferencesManager.getInstance(getActivity()).clearUserPreferences();
                            Intent i = new Intent(getActivity(), AppIntroActivityNew.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();;
                        }
                        myAdapter.notifyDataSetChanged();//changes by amol
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);//changes by amol
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
               //         Toast.makeText(BrowseFragment.this.getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);//changes by amol
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }


    private void setAdapter(ArrayList<BrowseJson.Categories> main_categories_list_containing_elements)
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

    //changes by amol
    @Override
    public void onRefresh() {
        refreshmode = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                maincategory_webservice();
            }
        }, 1000);
    }
}
