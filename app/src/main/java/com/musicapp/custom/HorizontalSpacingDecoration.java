package com.musicapp.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SanaKazi on 2/13/2017.
 */
public class HorizontalSpacingDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public HorizontalSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = spacing;
    }
}
