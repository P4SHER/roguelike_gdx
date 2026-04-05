package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.renderer.LayerRenderer;
import io.github.example.presentation.renderer.culling.CullingSystem;
import io.github.example.presentation.util.Logger;

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
        Logger.debug("Инициализирован слой: " + layerName);
    }

    @Override
    public abstract void render(SpriteBatch batch, OrthographicCamera camera, float delta);

    @Override
    public void dispose() {
        Logger.debug("Слой очищен: " + layerName);
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
        Logger.debug("Слой " + layerName + " видимость: " + visible);
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
        Logger.debug("[" + layerName + "] " + message);
    }
}
