package com.cywl.launcher.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public LinearLayoutItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        int position = parent.getChildAdapterPosition(view);
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildPosition(view) == 0) {
            outRect.top = space;
        }
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.setEmpty();
        }
    }
}
