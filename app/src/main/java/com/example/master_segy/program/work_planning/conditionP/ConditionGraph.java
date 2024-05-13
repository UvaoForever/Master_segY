package com.example.master_segy.program.work_planning.conditionP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.View;

import com.example.master_segy.R;

public class ConditionGraph extends View {

    private Paint paint = new Paint();
    private Bitmap bitmap;

    public ConditionGraph(Context context) {
        super(context);

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.id.imageViewY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        // Draw Line
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        canvas.drawLine(0, 0, canvas.getWidth(), 0, paint);
    }
}
