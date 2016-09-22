
package es.espinr.gijonair;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class StationImageView extends ImageView
{

    public StationImageView(Context context)
    {
        super(context);
    }

    public StationImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    protected void onMeasure(int i, int j)
    {
        Drawable drawable = getDrawable();
        if (drawable != null)
        {
            int k = android.view.View.MeasureSpec.getSize(i);
            setMeasuredDimension(k, (int)Math.ceil(((float)k * (float)drawable.getIntrinsicHeight()) / (float)drawable.getIntrinsicWidth()));
            return;
        } else
        {
            super.onMeasure(i, j);
            return;
        }
    }
}
