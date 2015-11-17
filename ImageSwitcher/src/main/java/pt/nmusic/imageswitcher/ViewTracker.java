package pt.nmusic.imageswitcher;

import android.view.View;

/**
 * Created by bam on 14/11/15.
 */
public class ViewTracker
{
    private View previousImage;
    private View showingImage;
    private View nextImage;
    private View videoSurfaceContainer;

    public ViewTracker( View imageA, View imageB, View imageC)
    {
        previousImage = imageA;
        showingImage = imageB;
        nextImage = imageC;
    }

    public ViewTracker( View imageA, View imageB, View imageC, View videoSurfaceContainer )
    {
        previousImage = imageA;
        showingImage = imageB;
        nextImage = imageC;
        this.videoSurfaceContainer = videoSurfaceContainer;
    }

    public View getPreviousImage()
    {
        return previousImage;
    }

    public View getShowingImage()
    {
        return showingImage;
    }

    public View getNextImage()
    {
        return nextImage;
    }

    public View getVideoSurfaceContainer()
    {
        return videoSurfaceContainer;
    }

    /**
     * This function needs to replace the Previous with Showing, the Showing with Next and
     * the Next with Previous. So one shift to the next and using the former Previous as Next.
     */
    public void switchNext()
    {
        View tempHolder = getPreviousImage();

        previousImage = getShowingImage();
        previousImage.setVisibility(View.GONE);

        showingImage = getNextImage();
        showingImage.setVisibility(View.VISIBLE);

        nextImage = tempHolder;
        nextImage.setVisibility(View.GONE);
    }


    /**
     * This function needs to replace the Next with Showing, the Showing with Previous and
     * the Previous with Next. So one shift to the previous and using the former Next as Previous.
     */
    public void switchPrevious()
    {
        View tempHolder = getNextImage();

        nextImage = getShowingImage();
        nextImage.setVisibility(View.GONE);

        showingImage = getPreviousImage();
        showingImage.setVisibility(View.VISIBLE);

        previousImage = tempHolder;
        previousImage.setVisibility(View.GONE);
    }
}
