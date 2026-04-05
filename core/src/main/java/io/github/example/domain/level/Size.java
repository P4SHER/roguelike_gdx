package io.github.example.domain.level;

/**
 * Размеры объека (NxM)
 */
public class Size {
    private int height;
    private int width;
    public Size(int height, int width) {
        this.height = height;
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
