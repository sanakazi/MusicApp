package com.musicapp.adapters;

/**
 * Created by pratap.kesaboyina on 24-12-2014.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.custom.HorizontalSpacingDecoration;
import com.musicapp.pojos.HomeDetailsJson;

import java.util.ArrayList;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.ItemRowHolder>  {

    private ArrayList<HomeDetailsJson.Categories> dataList_parent;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    private ArrayList<HomeDetailsJson.DataList> video_itemsList;
    private Context mContext;

    public HomeDataAdapter(Context context, ArrayList<HomeDetailsJson.Categories> dataList_parent,ArrayList<HomeDetailsJson.DataList> audio_itemsList, ArrayList<HomeDetailsJson.DataList> video_itemsList) {
        this.dataList_parent = dataList_parent;
        this.mContext = context;
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

      //  final String sectionName = dataList_parent.get(i).getHeaderTitle();
        final String sectionName =dataList_parent.get(i).getCategoryName();

     //   ArrayList singleSectionItems = dataList_parent;

        itemRowHolder.itemTitle.setText(sectionName);

        HomeItemAdapter itemListDataAdapter = new HomeItemAdapter(mContext, dataList_parent.get(i).getDataList(),audio_itemsList,video_itemsList, dataList_parent.get(i).getCategoryId());
        if(!dataList_parent.get(i).getDataList().isEmpty())
        {
            itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
        }

         itemRowHolder.recycler_view_list.setOnTouchListener(new View.OnTouchListener() {
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
        });

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
        return (null != dataList_parent ? dataList_parent.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        protected Button btnMore;

        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            recycler_view_list.setHasFixedSize(true);
           recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
          recycler_view_list.addItemDecoration(new HorizontalSpacingDecoration(10));
            recycler_view_list.setNestedScrollingEnabled(false);

        }

    }

}