package com.example.master_segy.program;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class CustomDividerItemDecoration extends DividerItemDecoration {

    private int mDividerColor;

    public CustomDividerItemDecoration(Context context, int orientation, int dividerColor) {
        super(context, orientation);
        mDividerColor = dividerColor;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        // создаем кисть для рисования разделителя
        Paint paint = new Paint();
        paint.setColor(mDividerColor);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // рисуем разделитель на каждой строке, кроме последней *
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + 2;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }
}