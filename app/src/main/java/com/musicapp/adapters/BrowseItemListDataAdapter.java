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

import com.musicapp.R;
import com.musicapp.custom.HorizontalSpacingDecoration;
import com.musicapp.pojos.HomeDetailsJson;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 2/9/2017.
 */

public class BrowseItemListDataAdapter extends RecyclerView.Adapter<BrowseItemListDataAdapter.ItemRowHolder> {

    private ArrayList<HomeDetailsJson.Categories> dataList;
    ArrayList <HomeDetailsJson.DataList> detail_categories_list=new ArrayList<>();
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList =new ArrayList<>();
    private ArrayList<HomeDetailsJson.DataList> video_itemsList= new ArrayList<>();
    private Context mContext;
    int cat_id_forAllSongs, type_id_forAllSongs,subCategoryId,categoryId;
    private static final String TAG=BrowseItemListDataAdapter.class.getSimpleName();


    public BrowseItemListDataAdapter(Context context, ArrayList<HomeDetailsJson.Categories> dataList,int categoryId) {
        this.dataList = dataList;
        this.mContext = context;
        this.categoryId=categoryId;


    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_list, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, int position) {

        //  final String sectionName = dataList.get(i).getHeaderTitle();
        final String sectionName =dataList.get(position).getCategoryName();

        ArrayList singleSectionItems =  dataList.get(position).getDataList();

        itemRowHolder.itemTitle.setText(sectionName);


        //region for all songs

        if(dataList.get(position).getCategoryId()!=0)
        {
            detail_categories_list =  dataList.get(position).getDataList();
            for (int j = 0; j<detail_categories_list.size(); j++)
            {

                if (detail_categories_list.get(j).getColumns().getSongTypeId() == 1) {

                    //audio list
                    audio_itemsList.add(dataList.get(position).getDataList().get(j));
                    Log.w(TAG, "in audio list " + audio_itemsList.size());
                } else if (detail_categories_list.get(j).getColumns().getSongTypeId() == 2) {

                    //video list
                    video_itemsList.add(dataList.get(position).getDataList().get(j));
                    Log.w(TAG, "IN VIDEO LIST " + video_itemsList.size());
                }
            }


        }
        //endregion

        BrowseItemListItemAdapter itemListDataAdapter = new BrowseItemListItemAdapter(mContext, singleSectionItems,audio_itemsList,video_itemsList, categoryId);
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);



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
            this.recycler_view_list.setNestedScrollingEnabled(false);

        }

    }
}

