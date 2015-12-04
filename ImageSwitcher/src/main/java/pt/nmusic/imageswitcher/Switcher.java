package pt.nmusic.imageswitcher;

/**
 * Created by bam on 14/11/15.
 */
public interface Switcher
{
    /**
     * Returns a boolean indicating whether there is a Next item.
     */
    boolean hasNext();

    /**
     * Returns a boolean indicating whether there is a Previous item.
     */
    boolean hasPrevious();

    /**
     * Performs the action required when going to the next item (expect transitioning to the next
     * Drawee view).
     * This action is performed at the end of a switch-to-next animation.
     * <p>
     * This can be starting the next audio track, calling some API or any other logic action.
     * It can also be empty if no actions are needed.
     * <p>
     * NOTE: Remember to make the "videoSurfaceContainer" Visible/Invisible as needed.
     *
     * @param userInitiated A boolean indicating whether the function has been called because of a
     *                      user performed action or called by the system in some other way.
     *                      Can be used to differentiate between Touch and System initiated events.
     */
    boolean nextAction( boolean userInitiated );

    /**
     * Performs the action required when going to the previous item (expect transitioning to the
     * previous Drawee view).
     * This action is performed at the end of a switch-to-prvious animation.
     * <p>
     * This can be starting the previous audio track, calling some API or any other logic action.
     * It can also be empty if no actions are needed.
     * <p>
     * NOTE: Remember to make the "videoSurfaceContainer" Visible/Invisible as needed.
     *
     * @param userInitiated A boolean indicating whether the function has been called because of a
     *                      user performed action or called by the system in some other way.
     *                      Can be used to differentiate between Touch and System initiated events.
     */
    boolean previousAction( boolean userInitiated );

    /**
     * Perform any action necessary to prepare the Next View, such as loading the next image.
     * This action is performed any time a switch takes place, but after the
     * {@link ViewTracker#switchNext()} or {@link ViewTracker#switchPrevious()}, so it can perform
     * the prepare action on the correct View.
     *
     * @param userInitiated A boolean indicating whether the function has been called because of a
     *                      user performed action or called by the system in some other way.
     *                      Can be used to differentiate between Touch and System initiated events.
     */
    boolean prepareNextView( boolean userInitiated );

    /**
     * Perform any action necessary to prepare the Previous View, such as loading the next image.
     * This action is performed any time a switch takes place, but after the
     * {@link ViewTracker#switchNext()} or {@link ViewTracker#switchPrevious()}, so it can perform
     * the prepare action on the correct View.
     *
     * @param userInitiated A boolean indicating whether the function has been called because of a
     *                      user performed action or called by the system in some other way.
     *                      Can be used to differentiate between Touch and System initiated events.
     */
    boolean preparePreviousView( boolean userInitiated );
}
