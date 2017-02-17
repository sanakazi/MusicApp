package com.musicapp.adapters;

        import android.content.Context;
        import android.content.Intent;
        import android.support.v7.widget.CardView;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.android.volley.toolbox.ImageLoader;
        import com.android.volley.toolbox.NetworkImageView;
        import com.musicapp.R;

        import com.musicapp.activities.BrowseItemListActivity;
        import com.musicapp.pojos.HomeDetailsJson;
        import com.musicapp.singleton.MySingleton;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;

/**
 * Created by SanaKazi on 1/18/2017.
 */

public class BrowseAdapter  extends RecyclerView.Adapter<BrowseAdapter.SingleItemRowHolder> {


    private ArrayList<HomeDetailsJson.Categories> itemsList;
    private Context mContext;
    int category_Id;
    ArrayList<Integer> markFav;
    private ImageLoader mImageLoader;
    private static final String TAG=BrowseAdapter.class.getSimpleName();


    public BrowseAdapter(Context context, ArrayList<HomeDetailsJson.Categories> itemsList ,int category_Id) {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        this.itemsList = itemsList;
        this.mContext = context;
        this.category_Id=category_Id;
        this.markFav = markFav;

    }
    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_browse, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder,final int i) {


        holder.tvTitle.setText(itemsList.get(i).getCategoryName());
        //  holder.itemImage.setImageUrl(itemsList.get(i).getCoverImage(), mImageLoader);
      //  Picasso.with(mContext).load(itemsList.get(i).getCoverImage()).into(holder.itemImage);
        holder.browse_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, BrowseItemListActivity.class);
                intent.putExtra(BrowseItemListActivity.CAT_ID,itemsList.get(i).getCategoryId());
                intent.putExtra(BrowseItemListActivity.CATEGORY_NAME,itemsList.get(i).getCategoryName());
                mContext.startActivity(intent);
                Log.w(TAG,"CAT ID is"+itemsList.get(i).getCategoryId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected RelativeLayout browse_category;

        protected ImageView itemImage;



        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.browse_category = (RelativeLayout) view.findViewById(R.id.browse_category);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
            //  this.itemImage.setDefaultImageResId(R.drawable.home_item3);
        }

    }

}