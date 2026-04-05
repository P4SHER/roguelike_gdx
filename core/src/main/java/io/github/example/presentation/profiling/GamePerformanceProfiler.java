package io.github.example.presentation.profiling;

import io.github.example.presentation.util.Logger;

/**
 * Profiler for measuring gameplay performance.
 * Tracks frame time, input latency, and action execution time.
 * Target: 60 FPS consistently, <5ms input latency.
 */
public class GamePerformanceProfiler {
    private static final int SAMPLE_SIZE = 60;
    private static final long TARGET_FRAME_TIME_NS = 16_666_667; // ~16.67ms for 60 FPS
    private static final long TARGET_INPUT_LATENCY_NS = 5_000_000; // 5ms
    
    private long[] frameTimes = new long[SAMPLE_SIZE];
    private long[] inputLatencies = new long[SAMPLE_SIZE];
    private long[] updateTimes = new long[SAMPLE_SIZE];
    private long[] renderTimes = new long[SAMPLE_SIZE];
    
    private int frameIndex = 0;
    private int sampleCount = 0;
    
    private long frameStartTime;
    private long inputStartTime;
    private long updateStartTime;
    private long renderStartTime;
    
    private boolean recordingComplete = false;
    
    public void startFrame() {
        frameStartTime = System.nanoTime();
    }
    
    public void startInputMeasure() {
        inputStartTime = System.nanoTime();
    }
    
    public void endInputMeasure() {
        long inputLatency = System.nanoTime() - inputStartTime;
        inputLatencies[frameIndex] = inputLatency;
    }
    
    public void startUpdateMeasure() {
        updateStartTime = System.nanoTime();
    }
    
    public void endUpdateMeasure() {
        long updateTime = System.nanoTime() - updateStartTime;
        updateTimes[frameIndex] = updateTime;
    }
    
    public void startRenderMeasure() {
        renderStartTime = System.nanoTime();
    }
    
    public void endRenderMeasure() {
        long renderTime = System.nanoTime() - renderStartTime;
        renderTimes[frameIndex] = renderTime;
    }
    
    public void endFrame() {
        long frameTime = System.nanoTime() - frameStartTime;
        frameTimes[frameIndex] = frameTime;
        
        frameIndex = (frameIndex + 1) % SAMPLE_SIZE;
        if (frameIndex == 0) {
            sampleCount++;
            recordingComplete = true;
        }
    }
    
    public double getAverageFrameTimeMs() {
        long total = 0;
        for (long time : frameTimes) {
            total += time;
        }
        return total / (double) SAMPLE_SIZE / 1_000_000;
    }
    
    public double getAverageInputLatencyMs() {
        long total = 0;
        for (long time : inputLatencies) {
            total += time;
        }
        return total / (double) SAMPLE_SIZE / 1_000_000;
    }
    
    public double getAverageUpdateTimeMs() {
        long total = 0;
        for (long time : updateTimes) {
            total += time;
        }
        return total / (double) SAMPLE_SIZE / 1_000_000;
    }
    
    public double getAverageRenderTimeMs() {
        long total = 0;
        for (long time : renderTimes) {
            total += time;
        }
        return total / (double) SAMPLE_SIZE / 1_000_000;
    }
    
    public double getAverageFPS() {
        double frameTimeMs = getAverageFrameTimeMs();
        if (frameTimeMs > 0) {
            return 1000 / frameTimeMs;
        }
        return 0;
    }
    
    public long getMaxFrameTimeMs() {
        long max = 0;
        for (long time : frameTimes) {
            if (time > max) max = time;
        }
        return max / 1_000_000;
    }
    
    public long getMinFrameTimeMs() {
        long min = Long.MAX_VALUE;
        for (long time : frameTimes) {
            if (time > 0 && time < min) min = time;
        }
        return min == Long.MAX_VALUE ? 0 : min / 1_000_000;
    }
    
    public boolean meetsPerformanceTargets() {
        return getAverageFrameTimeMs() <= 16.67 && getAverageInputLatencyMs() <= 5;
    }
    
    public String getPerformanceReport() {
        if (!recordingComplete) {
            return "Performance data still collecting (" + (frameIndex + 1) + "/" + SAMPLE_SIZE + " frames)...";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("\n=== PERFORMANCE REPORT ===\n");
        report.append(String.format("Target: 60 FPS (16.67ms), <5ms input latency\n"));
        report.append(String.format("Average Frame Time: %.2f ms (FPS: %.1f)\n", getAverageFrameTimeMs(), getAverageFPS()));
        report.append(String.format("Min Frame Time: %d ms\n", getMinFrameTimeMs()));
        report.append(String.format("Max Frame Time: %d ms\n", getMaxFrameTimeMs()));
        report.append(String.format("Average Input Latency: %.2f ms\n", getAverageInputLatencyMs()));
        report.append(String.format("Average Update Time: %.2f ms\n", getAverageUpdateTimeMs()));
        report.append(String.format("Average Render Time: %.2f ms\n", getAverageRenderTimeMs()));
        
        if (meetsPerformanceTargets()) {
            report.append("Status: ✓ PASSES PERFORMANCE TARGETS\n");
        } else {
            report.append("Status: ✗ FAILS PERFORMANCE TARGETS\n");
        }
        
        report.append("=========================\n");
        return report.toString();
    }
    
    public void reset() {
        frameTimes = new long[SAMPLE_SIZE];
        inputLatencies = new long[SAMPLE_SIZE];
        updateTimes = new long[SAMPLE_SIZE];
        renderTimes = new long[SAMPLE_SIZE];
        frameIndex = 0;
        sampleCount = 0;
        recordingComplete = false;
    }
    
    public void printReport() {
        Logger.info(getPerformanceReport());
    }
}
