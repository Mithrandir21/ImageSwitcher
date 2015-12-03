package pt.nmusic.imageswitcher;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by bam on 14/11/15.
 */
public class SwitchListener implements View.OnTouchListener
{
    private final GestureDetector swipeListener;

    private ViewTracker viewTracker;
    private Switcher    switcher;

    private int     animationDuration = 250;
    private boolean debug             = false;

    // The height and width of the screen.
    private int widthPixels;
    private int heightPixels;

    // SWIPE variables
    // A boolean indicating whether the user is touching the screen.
    private boolean touchInProgress = false;
    // A boolean indicating whether there is a Reset Swipe background process.
    private boolean resetInProgress = false;

    // The total amount of touch distance scrolled, either direction.
    private float distanceScrolled;

    // The distance (in pixels) that the scroll has to pass to count as a desire to switch.
    private int switchScrollThreshold;
    // This variable determines at percentage of the screen scroll the switch should happen.
    private float switchPercentage = 0.6f;

    /**
     * Constructor for the class with the necessary variables.
     * <p/>
     * This sets up the SwitcherListener with stock parameters.
     *
     * @param context
     * @param viewTracker A ViewTracker that contains the Drawee views that are to be switched.
     * @param switcher    The Switcher implementation that will be called when needed.
     */
    public SwitchListener( Context context, ViewTracker viewTracker, Switcher switcher )
    {
        if( context == null )
        {
            throw new IllegalArgumentException("Given Context was null! Error!");
        }

        if( viewTracker == null )
        {
            throw new IllegalArgumentException("Given ViewTracker was null! Error!");
        }

        if( switcher == null )
        {
            throw new IllegalArgumentException("Given Switcher was null! Error!");
        }


        this.viewTracker = viewTracker;
        this.switcher = switcher;

        swipeListener = new GestureDetector(context, new GestureListener(context));

        // Get the full width (and height) of the screen.
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;

        // The Threshold that needs to be passed for the simple scroll to register a switch.
        switchScrollThreshold = Math.round(widthPixels * switchPercentage);
    }

    /*******************
     * CONFIGURATION
     *******************/

    /**
     * Set the duration of the switch animation.
     * <p/>
     * Remember, this is the time it takes the animation to complete. This is affected by the space
     * remaining to travel or any other factors.
     */
    public void setAnimationDuration( int animationDuration )
    {
        this.animationDuration = animationDuration;
    }

    /**
     * Sets the boolean on whether debug information should be written to the log.
     */
    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    /**
     * Set the percentage of the screen at switch a scroll (not Fling) release should perform
     * a switch to the next/previous.
     */
    public void setSwitchPercentage( float switchPercentage )
    {
        this.switchPercentage = switchPercentage;

        // The Threshold that needs to be passed for the simple scroll to register a switch.
        switchScrollThreshold = Math.round(widthPixels * switchPercentage);
    }


    /*******************
     * TOUCH FUNCTIONS
     *******************/

    /**
     * @param viewTouched
     * @param event
     *
     * @return
     */
    public boolean onTouch( View viewTouched, MotionEvent event )
    {
        // If the MotionEvent IS NOT handled, for example because the movement speed was too slow.
        if( !swipeListener.onTouchEvent(event) )
        {
            return onUp(event);
        }

        // MotionEvent has been handled by the SwipeListener.
        return true;
    }

    /**
     * This function is placed here because the GestureDetector and Listener do not have a
     * onUp function to determine when the users finger has been removed form the screen.
     * <p/>
     * It is here the users actions will be determined and finalized.
     * <p/>
     * NOTE: This will only happen if the
     * {@link GestureListener#onFling(MotionEvent, MotionEvent, float, float)} is not called.
     *
     * @param event
     *
     * @return
     */
    public boolean onUp( MotionEvent event )
    {
        if( touchInProgress && event.getAction() == MotionEvent.ACTION_UP )
        {
            // A boolean to determine if the scrolling is passed the "switchScrollThreshold".
            boolean switchPlayable = false;

            // Determine if the distanceScrolled is passed the "switchScrollThreshold"
            if( distanceScrolled > switchScrollThreshold && switcher.hasNext() ) // Moved to the left.
            {
                moveBackgroundLeft();
                switchPlayable = true;
            }
            else if( distanceScrolled < -switchScrollThreshold && switcher.hasPrevious() ) // Moved to the right.
            {
                moveBackgroundRight();
                switchPlayable = true;
            }

            resetSwipeEvent(switchPlayable);
        }

        return true;
    }

    /**
     * Initialization of a Swipe event.
     */
    public void initSwipeEvent()
    {
        // Set the STATE
        touchInProgress = true;
        distanceScrolled = 0;

        View previousBackground = viewTracker.getPreviousImage();
        View showingBackground = viewTracker.getShowingImage();
        View nextBackground = viewTracker.getNextImage();
        View videoSurfaceContainer = viewTracker.getVideoSurfaceContainer();

        // Screen to the -X of the screen.
        previousBackground.setVisibility(View.VISIBLE);
        previousBackground.setTranslationX(-widthPixels);

        // Screen to the -X of the screen.
        showingBackground.setVisibility(View.VISIBLE);
        showingBackground.setTranslationX(0);

        if( videoSurfaceContainer != null )
        {
            videoSurfaceContainer.setVisibility(View.VISIBLE);
            videoSurfaceContainer.setTranslationX(0);
        }

        // Screen to the X of the screen.
        nextBackground.setVisibility(View.VISIBLE);
        nextBackground.setTranslationX(widthPixels);
    }


    /**
     * The reset of a Swipe event. This function will be called whenever a Swipe event finishes.
     * It is called whether the event is switched a next/previous or not. A boolean given indicates
     * whether a switch happened or not. If false, all the views will be returned to their original
     * positions.
     *
     * @param swipeSuccessful Whether a switch occurred or not. Reset View positions on False.
     */
    public void resetSwipeEvent( boolean swipeSuccessful )
    {
        if( !swipeSuccessful )
        {
            moveBackgroundCenter();
        }

        // Set the STATE
        touchInProgress = false;
        distanceScrolled = 0;
    }


    public void moveBackgroundLeft()
    {
        // Re-align backgrounds.
        final View previousBackground = viewTracker.getPreviousImage();
        final View showingBackground = viewTracker.getShowingImage();
        final View nextBackground = viewTracker.getNextImage();
        final View videoSurfaceContainer = viewTracker.getVideoSurfaceContainer();


        // Set the animators in the set of animations
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(animationDuration);


        // MOVE LEFT - PREVIOUS BACKGROUND
        ObjectAnimator moveX1 = ObjectAnimator.ofFloat(previousBackground, "x", -widthPixels * 2);

        // MOVE LEFT - SHOWING BACKGROUND
        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(showingBackground, "x", -widthPixels);

        // MOVE LEFT - NEXT BACKGROUND
        ObjectAnimator moveX3 = ObjectAnimator.ofFloat(nextBackground, "x", 0);

        if( videoSurfaceContainer != null )
        {
            // MOVE LEFT - VIDEO SURFACE
            ObjectAnimator moveXvideo = ObjectAnimator.ofFloat(videoSurfaceContainer, "x", -widthPixels);

            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3, moveXvideo);
        }
        else
        {
            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3);
        }


        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart( Animator animation )
            {
                resetInProgress = true;
            }

            @Override
            public void onAnimationEnd( Animator animation )
            {
                previousBackground.setVisibility(View.GONE);
                nextBackground.setVisibility(View.GONE);
                resetInProgress = false;

                if( videoSurfaceContainer != null )
                {
                    // This returns the Video to X=0.
                    ObjectAnimator.ofFloat(videoSurfaceContainer, "x", 0).setDuration(1).start();
                }

                switcher.nextAction();
                viewTracker.switchNext(); // This rotates the View to be in the correct order.

                // Perform Prepare actions
                switcher.prepareNextView();
                switcher.preparePreviousView();
            }

            @Override
            public void onAnimationCancel( Animator animation )
            {
                resetInProgress = false;
            }

            @Override
            public void onAnimationRepeat( Animator animation )
            {
            }
        });
        animatorSet.start();
    }


    public void moveBackgroundCenter()
    {
        // Re-align backgrounds.
        final View previousBackground = viewTracker.getPreviousImage();
        final View showingBackground = viewTracker.getShowingImage();
        final View nextBackground = viewTracker.getNextImage();
        final View videoSurfaceContainer = viewTracker.getVideoSurfaceContainer();


        // Set the animators in the set of animations
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(animationDuration);


        // MOVE LEFT - PREVIOUS BACKGROUND
        ObjectAnimator moveX1 = ObjectAnimator.ofFloat(previousBackground, "x", -widthPixels);

        // MOVE CENTER - SHOWING BACKGROUND
        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(showingBackground, "x", 0);

        // MOVE RIGHT - NEXT BACKGROUND
        ObjectAnimator moveX3 = ObjectAnimator.ofFloat(nextBackground, "x", widthPixels);

        if( videoSurfaceContainer != null )
        {
            // MOVE CENTER - VIDEO SURFACE
            ObjectAnimator moveXvideo = ObjectAnimator.ofFloat(videoSurfaceContainer, "x", 0);

            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3, moveXvideo);
        }
        else
        {
            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3);
        }


        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart( Animator animation )
            {
                resetInProgress = true;
            }

            @Override
            public void onAnimationEnd( Animator animation )
            {
                previousBackground.setVisibility(View.GONE);
                nextBackground.setVisibility(View.GONE);
                resetInProgress = false;
            }

            @Override
            public void onAnimationCancel( Animator animation )
            {
                resetInProgress = false;
            }

            @Override
            public void onAnimationRepeat( Animator animation )
            {
            }
        });
        animatorSet.start();
    }


    public void moveBackgroundRight()
    {
        // Re-align backgrounds.
        final View previousBackground = viewTracker.getPreviousImage();
        final View showingBackground = viewTracker.getShowingImage();
        final View nextBackground = viewTracker.getNextImage();
        final View videoSurfaceContainer = viewTracker.getVideoSurfaceContainer();


        // Set the animators in the set of animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(animationDuration);


        // MOVE RIGHT - PREVIOUS BACKGROUND
        ObjectAnimator moveX1 = ObjectAnimator.ofFloat(previousBackground, "x", 0);

        // MOVE RIGHT - SHOWING BACKGROUND
        ObjectAnimator moveX2 = ObjectAnimator.ofFloat(showingBackground, "x", widthPixels);

        // MOVE RIGHT - NEXT BACKGROUND
        ObjectAnimator moveX3 = ObjectAnimator.ofFloat(nextBackground, "x", widthPixels * 2);

        if( videoSurfaceContainer != null )
        {
            // MOVE RIGHT - VIDEO SURFACE
            ObjectAnimator moveXvideo = ObjectAnimator.ofFloat(videoSurfaceContainer, "x", widthPixels);

            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3, moveXvideo);
        }
        else
        {
            // Play together
            animatorSet.playTogether(moveX1, moveX2, moveX3);
        }


        animatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart( Animator animation )
            {
                resetInProgress = true;
            }

            @Override
            public void onAnimationEnd( Animator animation )
            {
                previousBackground.setVisibility(View.GONE);
                nextBackground.setVisibility(View.GONE);
                resetInProgress = false;

                if( videoSurfaceContainer != null )
                {
                    // This returns the Video to X=0.
                    ObjectAnimator.ofFloat(videoSurfaceContainer, "x", 0).setDuration(1).start();
                }

                switcher.previousAction();
                viewTracker.switchPrevious(); // This rotates the View to be in the correct order.

                // Perform Prepare actions
                switcher.prepareNextView();
                switcher.preparePreviousView();
            }

            @Override
            public void onAnimationCancel( Animator animation )
            {
                resetInProgress = false;
            }

            @Override
            public void onAnimationRepeat( Animator animation )
            {
            }
        });
        animatorSet.start();
    }

    /**
     * This function will use the given X-coordinate to move all the Background.
     * The previous-, showing- and next-backgrounds will be moved according to the -screenWidth,
     * 0 and +screenWidth.
     *
     * @param x
     */
    public void moveBackgrounds( float x )
    {
        View previousBackground = viewTracker.getPreviousImage();
        View showingBackground = viewTracker.getShowingImage();
        View nextBackground = viewTracker.getNextImage();
        View videoSurfaceContainer = viewTracker.getVideoSurfaceContainer();

        previousBackground.setTranslationX(-widthPixels + x);
        showingBackground.setTranslationX(x);
        nextBackground.setTranslationX(widthPixels + x);

        if( videoSurfaceContainer != null )
        {
            videoSurfaceContainer.setTranslationX(x);
        }
    }

    /**
     * Gesture controls will function 2 ways, either scrolling to above the change threshold or
     * flinging to any direction from any position on the screen (regardless of scrolling).
     */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private int FLING_DISTANCE_THRESHOLD;
        private int FLING_VELOCITY_THRESHOLD;

        public GestureListener( Context context )
        {
            FLING_DISTANCE_THRESHOLD = Utils.dpToPx(50, context);
            FLING_VELOCITY_THRESHOLD = Utils.dpToPx(500, context);

            if( debug )
            {
                Log.d(getClass().getCanonicalName(), "Distance:" + FLING_DISTANCE_THRESHOLD);
                Log.d(getClass().getCanonicalName(), "Velocity:" + FLING_VELOCITY_THRESHOLD);
            }
        }

        /*******************
         * CONFIGURATION
         *******************/

        public void setFlingDistanceThreshold( int FLING_DISTANCE_THRESHOLD )
        {
            this.FLING_DISTANCE_THRESHOLD = FLING_DISTANCE_THRESHOLD;
        }

        public void setFlingVelocityThreshold( int FLING_VELOCITY_THRESHOLD )
        {
            this.FLING_VELOCITY_THRESHOLD = FLING_VELOCITY_THRESHOLD;
        }

        /*********************
         * OnGestureListener
         *********************/

        /**
         * Notified when a motion occurs with the down {@link MotionEvent}
         * that triggered it. This will be triggered immediately for
         * every down event. All other events should be preceded by this.
         *
         * @param e The down motion event.
         */
        @Override
        public boolean onDown( MotionEvent e )
        {
            // If the reset of background are in progress, skip any action.
            if( resetInProgress )
            {
                return false;
            }

            // First determine if another touch event has not already started, INVALID STATE.
            if( touchInProgress )
            {
                if( debug )
                {
                    Log.d(getClass().getCanonicalName(), "A Touch event has already started, but not finished. Invalid State!");
                }

                // RESET
                touchInProgress = false;

                return false;
            }

            // Start Swipe event that will set the necessary variables.
            initSwipeEvent();
            return true;
        }

        /**
         * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
         * current move {@link MotionEvent}. The distance in x and y is also supplied for
         * convenience.
         *
         * @param e1        The first down motion event that started the scrolling.
         * @param e2        The move motion event that triggered the current onScroll.
         * @param distanceX The distance along the X axis that has been scrolled since the last
         *                  call to onScroll. This is NOT the distance between {@code e1}
         *                  and {@code e2}.
         * @param distanceY The distance along the Y axis that has been scrolled since the last
         *                  call to onScroll. This is NOT the distance between {@code e1}
         *                  and {@code e2}.
         *
         * @return true if the event is consumed, else false
         */
        @Override
        public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
        {
            // If the reset of background are in progress, skip any action.
            if( resetInProgress )
            {
                return false;
            }

            if( touchInProgress )
            {
                // The user has scrolled here, but has not let go or is performing a FLING.

                // If the potential new distanceScrolled is not wider than width and -width.
                if( widthPixels > ( distanceScrolled + distanceX )
                        && ( distanceScrolled + distanceX ) > -widthPixels )
                {
                    // Register Distance scrolled (add to any previous scroll).
                    distanceScrolled += distanceX;

                    moveBackgrounds(-distanceScrolled);
                }
                else
                {
                    if( debug )
                    {
                        Log.d(getClass().getCanonicalName(), "Scrolled to far! Ignore.");
                    }
                }

                return true;
            }

            return false;
        }

        /**
         * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
         * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
         * the x and y axis in pixels per second.
         *
         * @param e1        The first down motion event that started the fling.
         * @param e2        The move motion event that triggered the current onFling.
         * @param velocityX The velocity of this fling measured in pixels per second
         *                  along the x axis.
         * @param velocityY The velocity of this fling measured in pixels per second
         *                  along the y axis.
         *
         * @return true if the event is consumed, else false
         */
        @Override
        public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
        {
            // If the reset of background are in progress, skip any action.
            if( resetInProgress )
            {
                return false;
            }

            boolean swipeSuccessful = false;

            if( touchInProgress )
            {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();

                if( debug )
                {
                    Log.d(getClass().getCanonicalName(), "Distance:" + Math.abs(distanceX));
                    Log.d(getClass().getCanonicalName(), "Velocity:" + Math.abs(velocityX));
                }

                if( Math.abs(distanceX) > Math.abs(distanceY)
                        && Math.abs(distanceX) > FLING_DISTANCE_THRESHOLD
                        && Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD )
                {
                    if( velocityX < 0 && switcher.hasNext() )
                    {
                        moveBackgroundLeft();
                        swipeSuccessful = true;
                    }
                    else if( velocityX > 0 && switcher.hasPrevious() )
                    {
                        moveBackgroundRight();
                        swipeSuccessful = true;
                    }
                }

                // Reset the swipe variable. If SwipeSuccessful false, re-align the backgrounds.
                resetSwipeEvent(swipeSuccessful);
            }

            return swipeSuccessful;
        }
    }
}
