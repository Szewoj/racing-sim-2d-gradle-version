package szewoj.race2d.model;

public class LapTimer {
    private final int MAX_CHECKPOINT;
    private long currentLapStart;
    private int activatedCheckpoint;
    private boolean ready;
    private long finishedLapTime;

    public LapTimer( int numberOfCheckpoints ){
        MAX_CHECKPOINT = numberOfCheckpoints;
        currentLapStart = 0;
        activatedCheckpoint = -1;
        ready = false;
        finishedLapTime = 0;
    }

    public long getCurrentLapTime(){
        if( activatedCheckpoint > -1 ){
            long now = System.currentTimeMillis();
            long diff = now - currentLapStart;
            return diff;
        }
        return -1;
    }

    public void checkpointCrossed( int checkpointIndex ){
        if( activatedCheckpoint < 0 & checkpointIndex == 0 ){
            currentLapStart = System.currentTimeMillis();
            activatedCheckpoint = 1;
        } else if( activatedCheckpoint == checkpointIndex ) {
            if( activatedCheckpoint == 0 ){
                long now = System.currentTimeMillis();
                long diff = now - currentLapStart;
                finishedLapTime = diff;
                ready = true;
                currentLapStart = System.currentTimeMillis();
                ++activatedCheckpoint;
            } else {
                ++activatedCheckpoint;
                activatedCheckpoint %= MAX_CHECKPOINT;
            }

        }
    }

    public long getFinishedLapTime(){
        ready = false;
        return finishedLapTime;
    }

    public boolean isReady(){
        return ready;
    }

}
