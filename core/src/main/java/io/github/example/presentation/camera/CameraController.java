package io.github.example.presentation.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import io.github.example.presentation.util.Constants;

/**
 * Управляет ортографической камерой для 2D вида сверху.
 * Следит за персонажем, ограничивает границы уровня.
 */
public class CameraController {
    private final OrthographicCamera camera;
    private final float levelWidth;
    private final float levelHeight;

    private float targetX;
    private float targetY;
    private static final float LERP_SPEED = 0.1f;

    public CameraController(float levelWidth, float levelHeight) {
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        // Создаем камеру с размерами в пикселях
        float w = Constants.SCREEN_WIDTH;
        float h = Constants.SCREEN_HEIGHT;
        this.camera = new OrthographicCamera(w, h);
        this.camera.position.set(w / 2, h / 2, 0);
        this.camera.update();

        this.targetX = this.camera.position.x;
        this.targetY = this.camera.position.y;
    }

    /**
     * Устанавливает целевую позицию для камеры (обычно позиция персонажа).
     * Камера будет мягко двигаться к этой позиции.
     */
    public void setTarget(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    /**
     * Обновляет позицию камеры.
     * Вызывается каждый кадр перед render().
     */
    public void update() {
        // Мягкое движение камеры к целевой позиции (lerp)
        camera.position.x = MathUtils.lerp(camera.position.x, targetX, LERP_SPEED);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, LERP_SPEED);

        // Ограничиваем границы просмотра
        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, levelWidth - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, levelHeight - halfHeight);

        camera.update();
    }

    /**
     * Устанавливает зум камеры.
     *
     * @param zoom зум (1.0 = нормальный размер)
     */
    public void setZoom(float zoom) {
        camera.zoom = MathUtils.clamp(zoom, 0.5f, 2f);
    }

    /**
     * Получает текущий зум.
     */
    public float getZoom() {
        return camera.zoom;
    }

    /**
     * Преобразует мировые координаты в экранные.
     */
    public com.badlogic.gdx.math.Vector2 worldToScreen(float worldX, float worldY) {
        com.badlogic.gdx.math.Vector3 worldPos = new com.badlogic.gdx.math.Vector3(worldX, worldY, 0);
        com.badlogic.gdx.math.Vector3 screenPos = camera.project(worldPos);
        return new com.badlogic.gdx.math.Vector2(screenPos.x, screenPos.y);
    }

    /**
     * Преобразует экранные координаты в мировые.
     */
    public com.badlogic.gdx.math.Vector2 screenToWorld(float screenX, float screenY) {
        com.badlogic.gdx.math.Vector3 screenPos = new com.badlogic.gdx.math.Vector3(screenX, screenY, 0);
        com.badlogic.gdx.math.Vector3 worldPos = camera.unproject(screenPos);
        return new com.badlogic.gdx.math.Vector2(worldPos.x, worldPos.y);
    }

    /**
     * Возвращает OrthographicCamera для использования в SpriteBatch.
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Получает видимые границы камеры.
     * Используется для оптимизации (culling).
     */
    public com.badlogic.gdx.math.Rectangle getVisibleBounds() {
        float x = camera.position.x - camera.viewportWidth / 2;
        float y = camera.position.y - camera.viewportHeight / 2;
        return new com.badlogic.gdx.math.Rectangle(
            x,
            y,
            camera.viewportWidth,
            camera.viewportHeight
        );
    }

    public void dispose() {
        // Камера не требует dispose
    }
}
