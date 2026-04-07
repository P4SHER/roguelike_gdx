package io.github.example.presentation;

import com.badlogic.gdx.Screen;

public class PresentationLayer implements Screen {
    public PresentationLayer(int w, int h) {}
    public void setInitialScreen(Object screen) {}
    public void transitionToScreen(Object screen, float duration) {}
    public void render(float delta) {}
    public void resize(int width, int height) {}
    public void dispose() {}
    
    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
