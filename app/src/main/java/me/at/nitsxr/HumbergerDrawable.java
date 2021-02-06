package me.at.nitsxr;

import android.content.Context;
import android.graphics.Canvas;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import in.co.iamannitian.iamannitian.R;

public class HumbergerDrawable extends DrawerArrowDrawable
{
    public HumbergerDrawable(Context context)
    {
        super(context);
        setColor(context.getResources().getColor(R.color.textColor1));
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        setBarLength(46.0f);
        setBarThickness(5.0f);
        setGapSize(11.0f);
    }
}
