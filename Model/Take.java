package Model;

/**
 * Take represents a single take for a Scene.
 */
class Take {

    /* Object attributes */
    private int takeID;
    private boolean done = false;
    private UIPlacement placement;

    /* Constructor */
    Take(int takeID, UIPlacement placement)
    {
        this.takeID = takeID;
        this.placement = placement;
    }

    /* Accessors */
    int getTakeID() {
        return takeID;
    }

    UIPlacement getPlacement() {
        return placement;
    }

    boolean isDone() {
        return done;
    }

    void setDone(boolean done) {
        this.done = done;
    }
}
