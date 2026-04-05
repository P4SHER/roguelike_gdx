package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.renderer.LayerRenderer;
import io.github.example.presentation.renderer.culling.CullingSystem;

/**
 * Абстрактный базовый класс для всех слоев рендеринга.
 * Содержит общую логику для инициализации, рендеринга и отладки.
 */
public abstract class AbstractLayerRenderer implements LayerRenderer {
    protected final CullingSystem cullingSystem;
    protected final String layerName;
    protected boolean isVisible;

    public AbstractLayerRenderer(String layerName) {
        this.layerName = layerName;
        this.cullingSystem = new CullingSystem();
        this.isVisible = true;
    }

    @Override
    public void init() {
        System.out.println("Инициализирован слой: " + layerName);
    }

    @Override
    public abstract void render(SpriteBatch batch, OrthographicCamera camera, float delta);

    @Override
    public void dispose() {
        System.out.println("Слой очищен: " + layerName);
    }

    @Override
    public String getName() {
        return layerName;
    }

    /**
     * Устанавливает видимость слоя.
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        System.out.println("Слой " + layerName + " видимость: " + visible);
    }

    /**
     * Получает видимость слоя.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Получает систему отсечения.
     */
    public CullingSystem getCullingSystem() {
        return cullingSystem;
    }

    /**
     * Логирование для отладки.
     */
    protected void debugLog(String message) {
        System.out.println("[" + layerName + "] " + message);
    }
}
