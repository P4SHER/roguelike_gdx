package io.github.example.presentation.renderer.culling;

import com.badlogic.gdx.math.Rectangle;
import io.github.example.domain.level.Coordinates;
import io.github.example.presentation.util.Constants;

/**
 * Система отсечения видимых элементов для оптимизации рендеринга.
 * Проверяет, находятся ли объекты в видимой области камеры.
 */
public class CullingSystem {
    private final int maxRenderDistance;
    private final int cullingMargin;

    public CullingSystem() {
        this.maxRenderDistance = Constants.TILE_SIZE * 30; // Примерно 30 тайлов
        this.cullingMargin = Constants.CULLING_MARGIN;
    }

    /**
     * Проверяет, видим ли объект в границах камеры.
     *
     * @param x координата X объекта
     * @param y координата Y объекта
     * @param width ширина объекта
     * @param height высота объекта
     * @param visibleBounds видимые границы камеры
     * @return true если объект видим
     */
    public boolean isVisible(float x, float y, float width, float height, Rectangle visibleBounds) {
        if (visibleBounds == null) {
            return true;
        }

        return visibleBounds.overlaps(
            new Rectangle(
                x - cullingMargin,
                y - cullingMargin,
                width + cullingMargin * 2,
                height + cullingMargin * 2
            )
        );
    }

    /**
     * Проверяет, видима ли координата в границах камеры.
     *
     * @param coord координаты объекта
     * @param visibleBounds видимые границы камеры
     * @return true если видим
     */
    public boolean isVisible(Coordinates coord, Rectangle visibleBounds) {
        return isVisible(
            coord.getX() * Constants.TILE_SIZE,
            coord.getY() * Constants.TILE_SIZE,
            Constants.TILE_SIZE,
            Constants.TILE_SIZE,
            visibleBounds
        );
    }

    /**
     * Вычисляет расстояние между двумя точками.
     *
     * @param x1 первая X координата
     * @param y1 первая Y координата
     * @param x2 вторая X координата
     * @param y2 вторая Y координата
     * @return расстояние в пиксельных единицах
     */
    public int getDistance(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Проверяет, находится ли объект в пределах расстояния рендеринга.
     *
     * @param x1 первая X координата
     * @param y1 первая Y координата
     * @param x2 вторая X координата
     * @param y2 вторая Y координата
     * @return true если в пределах расстояния
     */
    public boolean isWithinRenderDistance(int x1, int y1, int x2, int y2) {
        return getDistance(x1, y1, x2, y2) <= maxRenderDistance;
    }

    /**
     * Получает максимальное расстояние рендеринга.
     */
    public int getMaxRenderDistance() {
        return maxRenderDistance;
    }

    /**
     * Получает margin для culling.
     */
    public int getCullingMargin() {
        return cullingMargin;
    }
}
