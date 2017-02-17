package com.musicapp.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.musicapp.R;
import com.musicapp.custom.HorizontalSpacingDecoration;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.HomeDetailsJson;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/22/2016.
 */
public class HomeItemClickListDataAdapter extends RecyclerView.Adapter<HomeItemClickListDataAdapter.ItemRowHolder> {

    private ArrayList<HomeDetailsJson.Categories> dataList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    private ArrayList<HomeDetailsJson.DataList> video_itemsList;
    private Context mContext;
    int cat_id_forAllSongs, type_id_forAllSongs,subCategoryId;


    public HomeItemClickListDataAdapter(Context context, ArrayList<HomeDetailsJson.Categories> dataList,ArrayList<HomeDetailsJson.DataList> audio_itemsList, ArrayList<HomeDetailsJson.DataList> video_itemsList,int cat_id_forAllSongs,int type_id_forAllSongs,int subCategoryId) {
        this.dataList = dataList;
        this.mContext = context;
        this.cat_id_forAllSongs=cat_id_forAllSongs;
        this.type_id_forAllSongs=type_id_forAllSongs;
        this.subCategoryId=subCategoryId;
        this.audio_itemsList=audio_itemsList;
        this.video_itemsList=video_itemsList;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_list, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, int i) {

        //  final String sectionName = dataList.get(i).getHeaderTitle();
        final String sectionName =dataList.get(i).getCategoryName();

        ArrayList singleSectionItems =  dataList.get(i).getDataList();

        itemRowHolder.itemTitle.setText(sectionName);


            HomeItemClickListItemAdapter itemListDataAdapter = new HomeItemClickListItemAdapter(mContext, singleSectionItems,audio_itemsList,video_itemsList, dataList.get(i).getCategoryId(), cat_id_forAllSongs, type_id_forAllSongs, subCategoryId);

            itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);


        itemRowHolder.recycler_view_list.setNestedScrollingEnabled(false);


       /*  itemRowHolder.recycler_view_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        //Allow ScrollView to intercept touch events once again.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle RecyclerView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });*/

       /* itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(v.getContext(), "click event on more, "+sectionName , Toast.LENGTH_SHORT).show();



            }
        });*/







       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;


        protected RecyclerView recycler_view_list;

        protected Button btnMore;


        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.recycler_view_list.setHasFixedSize(true);
            this.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            this.recycler_view_list.addItemDecoration(new HorizontalSpacingDecoration(10));

        }

    }
}

