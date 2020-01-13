package szewoj.race2d.model;

/**
 * Calculates lap times.
 * Uses -1 as time value to identify lack of it.
 */
public class LapTimer {
    private final int MAX_CHECKPOINT;
    private long currentLapStart;
    private int activatedCheckpoint;
    private boolean ready;
    private long finishedLapTime;

    /**
     * Constructor of LapTimer.
     * Initializes values for it to be waiting on first start checkpoint.
     * Needs the number of checkpoints to keep track of.
     *
     * @param numberOfCheckpoints   number of checkpoints placed on track
     */
    public LapTimer( int numberOfCheckpoints ){
        MAX_CHECKPOINT = numberOfCheckpoints;
        currentLapStart = 0;
        activatedCheckpoint = -1;
        ready = false;
        finishedLapTime = 0;
    }

    /**
     * Calculates current lap time if it has already started.
     *
     * @return  lap time in milliseconds
     */
    public long getCurrentLapTime(){
        if( activatedCheckpoint > -1 ){
            long now = System.currentTimeMillis();
            return now - currentLapStart;
        }
        return -1;
    }

    /**
     * Reacts to crossed checkpoint index. Ignores unknown indexes (they can be passed freely).
     * When start is crossed, if the lap previously started it readies the lap time value to be pulled.
     *
     * @param checkpointIndex   int in range 0 - MAX_CHECKPOINT
     */
    public void checkpointCrossed( int checkpointIndex ){
        if( activatedCheckpoint < 0 & checkpointIndex == 0 ){
            currentLapStart = System.currentTimeMillis();
            activatedCheckpoint = 1;
        } else if( activatedCheckpoint == checkpointIndex ) {
            if( activatedCheckpoint == 0 ){
                long now = System.currentTimeMillis();
                finishedLapTime = now - currentLapStart;
                ready = true;
                currentLapStart = System.currentTimeMillis();
                ++activatedCheckpoint;
            } else {
                ++activatedCheckpoint;
                activatedCheckpoint %= MAX_CHECKPOINT;
            }

        }
    }

    /**
     * Returns calculated finished lap time. The return value is credible ONLY when it was pulled while ready.
     *
     * @return      finished lap time in milliseconds
     */
    public long pullFinishedLapTime(){
        ready = false;
        return finishedLapTime;
    }

    /**
     * Informs about if finished lap time value can be pulled.
     *
     * @return  true if finished lap time value can be pulled.
     */
    public boolean isReady(){
        return ready;
    }

}
