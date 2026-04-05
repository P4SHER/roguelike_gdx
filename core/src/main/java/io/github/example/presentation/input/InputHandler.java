package io.github.example.presentation.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import io.github.example.domain.service.Direction;
import java.util.HashSet;
import java.util.Set;

/**
 * Обработчик ввода (клавиатура и мышь) для игры.
 * Реализует InputProcessor из LibGDX.
 * Кэширует нажатые клавиши для плавного движения (4 направления: UP, DOWN, LEFT, RIGHT).
 */
public class InputHandler implements InputProcessor {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private InputListener listener;

    public interface InputListener {
        void onMove(Direction direction);
        void onAction(Action action);
        void onMenuInput(MenuInput input);
    }

    public enum Action {
        ATTACK,
        INTERACT,
        SPECIAL
    }

    public enum MenuInput {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SELECT,
        CANCEL,
        MENU_TOGGLE
    }

    /**
     * Устанавливает слушателя ввода.
     */
    public void setInputListener(InputListener listener) {
        this.listener = listener;
    }

    /**
     * Получает текущее направление движения на основе нажатых клавиш.
     * Приоритет: последняя нажатая клавиша выигрывает.
     * Поддерживает только 4 направления: UP, DOWN, LEFT, RIGHT (как в domain).
     */
    public Direction getCurrentDirection() {
        boolean up = isKeyPressed(Input.Keys.W) || isKeyPressed(Input.Keys.UP);
        boolean down = isKeyPressed(Input.Keys.S) || isKeyPressed(Input.Keys.DOWN);
        boolean left = isKeyPressed(Input.Keys.A) || isKeyPressed(Input.Keys.LEFT);
        boolean right = isKeyPressed(Input.Keys.D) || isKeyPressed(Input.Keys.RIGHT);

        // Только одно направление за раз (нет диагоналей)
        // Приоритет: if-else cascade
        if (up) return Direction.UP;
        if (down) return Direction.DOWN;
        if (left) return Direction.LEFT;
        if (right) return Direction.RIGHT;

        return Direction.NONE;
    }

    /**
     * Проверяет, нажата ли клавиша.
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    @Override
    public boolean keyDown(int keycode) {
        pressedKeys.add(keycode);

        // Специальные клавиши (не движение)
        switch (keycode) {
            case Input.Keys.I:
                if (listener != null) listener.onMenuInput(MenuInput.MENU_TOGGLE);
                return true;

            case Input.Keys.P:
            case Input.Keys.ESCAPE:
                if (listener != null) listener.onMenuInput(MenuInput.MENU_TOGGLE);
                return true;

            case Input.Keys.ENTER:
            case Input.Keys.SPACE:
                if (listener != null) listener.onMenuInput(MenuInput.SELECT);
                return true;

            case Input.Keys.UP:
                if (listener != null) listener.onMenuInput(MenuInput.UP);
                break;

            case Input.Keys.DOWN:
                if (listener != null) listener.onMenuInput(MenuInput.DOWN);
                break;

            case Input.Keys.LEFT:
                if (listener != null) listener.onMenuInput(MenuInput.LEFT);
                break;

            case Input.Keys.RIGHT:
                if (listener != null) listener.onMenuInput(MenuInput.RIGHT);
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (listener != null && button == Input.Buttons.LEFT) {
            listener.onMenuInput(MenuInput.SELECT);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    /**
     * Очищает все нажатые клавиши.
     * Используется при смене экрана.
     */
    public void clearInput() {
        pressedKeys.clear();
    }

    /**
     * Получает информацию о нажатых клавишах для отладки.
     */
    public String getDebugInfo() {
        return "Нажатых клавиш: " + pressedKeys.size() + ", Направление: " + getCurrentDirection();
    }
}
