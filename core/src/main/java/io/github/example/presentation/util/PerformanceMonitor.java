package io.github.example.presentation.util;

/**
 * Performance profiler for measuring game loop timing.
 * Tracks input, update, and render times to identify bottlenecks.
 */
public class PerformanceMonitor {
    private long inputStartTime;
    private long updateStartTime;
    private long renderStartTime;
    
    private long inputTime;
    private long updateTime;
    private long renderTime;
    private long totalTime;
    
    private static final int SAMPLE_SIZE = 60; // Average over 60 frames
    private int sampleCount = 0;
    
    private long totalInputTime = 0;
    private long totalUpdateTime = 0;
    private long totalRenderTime = 0;

    public void startInputPhase() {
        inputStartTime = System.nanoTime();
    }

    public void endInputPhase() {
        inputTime = System.nanoTime() - inputStartTime;
        totalInputTime += inputTime;
    }

    public void startUpdatePhase() {
        updateStartTime = System.nanoTime();
    }

    public void endUpdatePhase() {
        updateTime = System.nanoTime() - updateStartTime;
        totalUpdateTime += updateTime;
    }

    public void startRenderPhase() {
        renderStartTime = System.nanoTime();
    }

    public void endRenderPhase() {
        renderTime = System.nanoTime() - renderStartTime;
        totalRenderTime += renderTime;
    }

    public void recordFrameTime() {
        totalTime = inputTime + updateTime + renderTime;
        sampleCount++;
        
        if (sampleCount >= SAMPLE_SIZE) {
            sampleCount = 0;
        }
    }

    public long getAverageInputTime() {
        return sampleCount > 0 ? totalInputTime / sampleCount / 1_000_000 : 0;
    }

    public long getAverageUpdateTime() {
        return sampleCount > 0 ? totalUpdateTime / sampleCount / 1_000_000 : 0;
    }

    public long getAverageRenderTime() {
        return sampleCount > 0 ? totalRenderTime / sampleCount / 1_000_000 : 0;
    }

    public long getAverageTotalTime() {
        return sampleCount > 0 ? (totalInputTime + totalUpdateTime + totalRenderTime) / sampleCount / 1_000_000 : 0;
    }

    public String getDebugInfo() {
        return String.format("Input: %dms, Update: %dms, Render: %dms, Total: %dms",
            getAverageInputTime(),
            getAverageUpdateTime(),
            getAverageRenderTime(),
            getAverageTotalTime());
    }

    public void reset() {
        inputTime = 0;
        updateTime = 0;
        renderTime = 0;
        totalTime = 0;
        sampleCount = 0;
        totalInputTime = 0;
        totalUpdateTime = 0;
        totalRenderTime = 0;
    }
}
