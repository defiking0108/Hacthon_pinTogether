package org.topnetwork.pintogether.widgets.recycler;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.topnetwork.pintogether.utils.LogUtils;
import org.topnetwork.pintogether.utils.UIUtils;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "GridSpaceItemDecoration";
    private int spanCount; //
    private int spacing;
    private boolean includeEdge;

    public GridSpaceItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = UIUtils.dip2px(spacing);
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
            LogUtils.dTag(TAG, "position == " + position + " , outRect.left == " + outRect.left + " , outRect.right == " + outRect.right + " , view.getLeft == " + view.getLeft());

            int[] locations = new int[2];
            view.getLocationOnScreen(locations);
            LogUtils.iTag(TAG, "position == " + position + " , child view location on screen left == " + locations[0] + " top == " + locations[1]);
            outRect.top = spacing;
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            outRect.top = spacing;
        }
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return pos + spanCount >= childCount;
        }
        return false;
    }

}

