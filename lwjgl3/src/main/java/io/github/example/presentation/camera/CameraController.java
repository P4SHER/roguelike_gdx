package io.github.example.presentation.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;

/**
 * Контроллер камеры - управляет следованием камеры за игроком
 */
public class CameraController {
    private final OrthographicCamera camera;
    private final Level level;
    private final float viewportWidth;
    private final float viewportHeight;

    // Параметры камеры
    private static final float LEVEL_WIDTH = 200f;
    private static final float LEVEL_HEIGHT = 200f;
    private float smoothness = 0.1f;  // Плавность следования (0.1 = медленно, 1.0 = мгновенно)

    public CameraController(OrthographicCamera camera, Level level, float viewportWidth, float viewportHeight) {
        this.camera = camera;
        this.level = level;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    /**
     * Обновить позицию камеры для следования за игроком
     */
    public void update(Player player, float delta) {
        if (player == null) return;

        // Целевая позиция (центр на игроке)
        float targetX = player.getCoordinates().getX() + 0.5f;
        float targetY = player.getCoordinates().getY() + 0.5f;

        // Плавное движение камеры
        float currentX = camera.position.x;
        float currentY = camera.position.y;

        camera.position.x = MathUtils.lerp(currentX, targetX, smoothness);
        camera.position.y = MathUtils.lerp(currentY, targetY, smoothness);

        // Ограничиваем камеру границами уровня
        clampCameraToLevel();

        camera.update();
    }

    /**
     * Ограничить камеру границами уровня
     */
    private void clampCameraToLevel() {
        float minX = viewportWidth / 2f;
        float maxX = LEVEL_WIDTH - viewportWidth / 2f;
        float minY = viewportHeight / 2f;
        float maxY = LEVEL_HEIGHT - viewportHeight / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
    }

    /**
     * Установить плавность следования (0.0-1.0)
     * 0.0 = очень плавно, 1.0 = мгновенно
     */
    public void setSmoothness(float smoothness) {
        this.smoothness = MathUtils.clamp(smoothness, 0f, 1f);
    }

    public float getSmoothness() {
        return smoothness;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setPosition(float x, float y) {
        camera.position.x = x;
        camera.position.y = y;
        clampCameraToLevel();
        camera.update();
    }
}
