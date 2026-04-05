package io.github.example.presentation.profiling;

/**
 * Performance profiling utility for measuring frame rates and render times.
 * Tracks FPS, frame times, and performance metrics.
 */
public class PerformanceProfiler {
    private float deltaTime;
    private float frameTime;
    private int frameCount;
    private float totalTime;
    private float fps;
    private float minFrameTime = Float.MAX_VALUE;
    private float maxFrameTime = 0;
    
    private static final float UPDATE_INTERVAL = 1.0f; // Update FPS every second
    private float updateTimer;

    public PerformanceProfiler() {
        this.deltaTime = 0;
        this.frameTime = 0;
        this.frameCount = 0;
        this.totalTime = 0;
        this.fps = 0;
        this.updateTimer = 0;
    }

    /**
     * Called at the start of each frame to measure frame time.
     */
    public void beginFrame() {
        // Start timing this frame
    }

    /**
     * Called at the end of each frame to record timing.
     * @param delta Time elapsed in this frame (seconds)
     */
    public void endFrame(float delta) {
        this.deltaTime = delta;
        this.frameTime = delta * 1000f; // Convert to milliseconds
        this.frameCount++;
        this.totalTime += delta;
        
        // Update min/max frame times
        if (frameTime > 0) {
            minFrameTime = Math.min(minFrameTime, frameTime);
            maxFrameTime = Math.max(maxFrameTime, frameTime);
        }
        
        // Update FPS every UPDATE_INTERVAL seconds
        updateTimer += delta;
        if (updateTimer >= UPDATE_INTERVAL) {
            fps = frameCount / updateTimer;
            frameCount = 0;
            updateTimer = 0;
        }
    }

    /**
     * Get current frames per second.
     */
    public float getFPS() {
        return fps;
    }

    /**
     * Get current frame time in milliseconds.
     */
    public float getFrameTime() {
        return frameTime;
    }

    /**
     * Get current delta time in seconds.
     */
    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Get minimum frame time recorded (milliseconds).
     */
    public float getMinFrameTime() {
        return minFrameTime == Float.MAX_VALUE ? 0 : minFrameTime;
    }

    /**
     * Get maximum frame time recorded (milliseconds).
     */
    public float getMaxFrameTime() {
        return maxFrameTime;
    }

    /**
     * Get total elapsed time (seconds).
     */
    public float getTotalTime() {
        return totalTime;
    }

    /**
     * Get average frame time in milliseconds.
     */
    public float getAverageFrameTime() {
        if (totalTime <= 0) return 0;
        return (totalTime / frameCount) * 1000f;
    }

    /**
     * Get performance summary string.
     */
    public String getPerformanceSummary() {
        return String.format(
            "FPS: %.1f | Frame: %.2fms | Min: %.2fms | Max: %.2fms | Avg: %.2fms",
            fps,
            frameTime,
            getMinFrameTime(),
            getMaxFrameTime(),
            getAverageFrameTime()
        );
    }

    /**
     * Reset all profiling data.
     */
    public void reset() {
        frameCount = 0;
        totalTime = 0;
        fps = 0;
        minFrameTime = Float.MAX_VALUE;
        maxFrameTime = 0;
        updateTimer = 0;
    }

    /**
     * Get whether current FPS is below target (60 FPS).
     */
    public boolean isPerformanceIssue() {
        return fps < 55.0f;
    }

    /**
     * Get whether frame time exceeds 16.67ms (60 FPS target).
     */
    public boolean isFrameDropping() {
        return frameTime > 16.67f;
    }
}
