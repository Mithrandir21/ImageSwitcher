package pt.nmusic.imageswitcher;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by bam on 14/11/15.
 */
public class Utils
{
    public static int dpToPx( int dp, Context context )
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * ( displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT ));
    }
}
