package com.nmusic.imageswitcher;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

/**
 * This is convenience implementation of the Switcher class for handling the desire to display
 * and switch between Images, shown in Fresco DraweeViews.
 * <p/>
 * Created by bam on 14/11/15.
 */
public class ImageSwitcher implements Switcher
{
    private ArrayList<Uri> imageUri;
    private ViewTracker    viewTracker;

    private int currentIndex = 0;

    // The Image that will be used as a placeholder for the DraweeViews.
    private int imagePlaceholder = R.drawable.image_placeholder;


    /**
     * Constructor for the class that takes an ArrayList of Uri (pointing to the desired
     * swipe images) and three DraweeViews that will be used to rotate through images.
     *
     * @param imageUri
     */
    public ImageSwitcher( ArrayList<Uri> imageUri, DraweeView imageA, DraweeView imageB, DraweeView imageC )
    {
        if( imageUri == null || imageUri.size() < 1 )
        {
            throw new IllegalArgumentException("Given Uri ArrayList was null! Error!");
        }

        if( imageA == null || imageB == null || imageC == null )
        {
            throw new IllegalArgumentException("Given DraweeViews were invalid! Error!");
        }

        this.imageUri = imageUri;
        this.viewTracker = new ViewTracker(imageA, imageB, imageC);
    }

    /**
     * Returns a boolean indicating whether there is a Next item.
     */
    @Override
    public boolean hasNext()
    {
        int nextIndex = ( currentIndex + 1 );

        // If invalid index position, return false.
        if( nextIndex < 0 && nextIndex >= imageUri.size() )
        {
            return false;
        }
        else
        {
            return imageUri.get(nextIndex) != null;
        }
    }

    /**
     * Returns a boolean indicating whether there is a Previous item.
     */
    @Override
    public boolean hasPrevious()
    {
        int previousIndex = ( currentIndex - 1 );

        // If invalid index position, return false.
        if( previousIndex < 0 && previousIndex >= imageUri.size() )
        {
            return false;
        }
        else
        {
            return imageUri.get(previousIndex) != null;
        }
    }

    /**
     * Performs the action required when going to the next item (expect transitioning to the next
     * Drawee view).
     * This action is performed at the end of a switch-to-next animation.
     * <p/>
     * This can be starting the next audio track, calling some API or any other logic action.
     * It can also be empty if no actions are needed.
     * <p/>
     * NOTE: Remember to make the "videoSurfaceContainer" Visible/Invisible as needed.
     */
    @Override
    public boolean nextAction()
    {
        currentIndex = currentIndex + 1;

        // No other actions needed here as the PrepareNext() takes care of preparing the DraweeView.

        return true;
    }

    /**
     * Performs the action required when going to the previous item (expect transitioning to the
     * previous Drawee view).
     * This action is performed at the end of a switch-to-prvious animation.
     * <p/>
     * This can be starting the previous audio track, calling some API or any other logic action.
     * It can also be empty if no actions are needed.
     * <p/>
     * NOTE: Remember to make the "videoSurfaceContainer" Visible/Invisible as needed.
     */
    @Override
    public boolean previousAction()
    {
        currentIndex = currentIndex - 1;

        // No other actions needed here as the PreparePrevious() takes care of preparing the DraweeView.

        return true;
    }

    /**
     * Perform any action necessary to prepare the Next View, such as loading the next image.
     * This action is performed any time a switch takes place, but after the
     * {@link ViewTracker#switchNext()} or {@link ViewTracker#switchPrevious()}, so it can perform
     * the prepare action on the correct View.
     */
    @Override
    public boolean prepareNextView()
    {
        // This will return the next Uri is there exists one.
        if( hasNext() )
        {
            Uri nextUri = imageUri.get(currentIndex + 1);

            DraweeView nextDrawee = (DraweeView) viewTracker.getNextImage();

            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(nextUri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(nextDrawee.getController())
                    .build();

            nextDrawee.setController(controller);
        }
        else // If no Uri is retrieved, the Next DraweeView source has to be set to null.
        {
            DraweeView nextDrawee = (DraweeView) viewTracker.getNextImage();
            nextDrawee.setImageResource(imagePlaceholder);
        }

        return true;
    }

    /**
     * Perform any action necessary to prepare the Previous View, such as loading the next image.
     * This action is performed any time a switch takes place, but after the
     * {@link ViewTracker#switchNext()} or {@link ViewTracker#switchPrevious()}, so it can perform
     * the prepare action on the correct View.
     */
    @Override
    public boolean preparePreviousView()
    {
        // This will return the next Uri is there exists one.
        if( hasPrevious() )
        {
            Uri nextUri = imageUri.get(currentIndex - 1);

            DraweeView nextDrawee = (DraweeView) viewTracker.getPreviousImage();

            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(nextUri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(nextDrawee.getController())
                    .build();

            nextDrawee.setController(controller);
        }
        else // If no Uri is retrieved, the Previous DraweeView source has to be set to null.
        {
            DraweeView nextDrawee = (DraweeView) viewTracker.getPreviousImage();
            nextDrawee.setImageResource(imagePlaceholder);
        }

        return true;
    }
}
