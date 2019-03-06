package com.zacsolutions.videoplayer;

/**
 * Created by Hassan on 2/28/2019.
 */

public interface VideoGestureListener {

    void onSingleTap();

    /**
     * Horizontal scroll to control progress of video
     *
     * @param seekForward seek to forward or not
     */
    void onHorizontalScroll(boolean seekForward);

    /**
     * vertical scroll listen
     *
     * @param percent   swipe percent
     * @param direction left or right edge for control brightness or volume
     */
    void onVerticalScroll(float percent, int direction);
}
