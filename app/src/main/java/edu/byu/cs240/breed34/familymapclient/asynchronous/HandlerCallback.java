package edu.byu.cs240.breed34.familymapclient.asynchronous;

import android.os.Bundle;

/**
 * An interface for creating callback methods
 * for the handler base class to use.
 */
public interface HandlerCallback {

    /**
     * The method to be run.
     *
     * @param bundle the bundle received from the task.
     */
    void run(Bundle bundle);
}
