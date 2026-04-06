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
 * Поддерживает:
 * - Направления (стрелки/WASD)
 * - Инвентарь (I)
 * - Пауза (P/ESC)
 * - Использование предметов (1-9)
 * - Ожидание (Spacebar)
 * - Выход (Q)
 */
public class InputHandler implements InputProcessor {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> previousPressedKeys = new HashSet<>();
    private InputListener listener;
    private final Set<Integer> itemSlotKeys = new HashSet<>();

    // Track direction changes to prevent key repeats
    private Direction lastProcessedDirection = Direction.NONE;

    public interface InputListener {
        void onMove(Direction direction);
        void onAction(Action action);
        void onMenuInput(MenuInput input);
        void onItemUse(int slotIndex);
        void onWait();
        void onToggleInventory();
        void onTogglePause();
        void onQuitToMenu();
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

    public InputHandler() {
        // Initialize item slot keys (1-9)
        itemSlotKeys.add(Input.Keys.NUM_1);
        itemSlotKeys.add(Input.Keys.NUM_2);
        itemSlotKeys.add(Input.Keys.NUM_3);
        itemSlotKeys.add(Input.Keys.NUM_4);
        itemSlotKeys.add(Input.Keys.NUM_5);
        itemSlotKeys.add(Input.Keys.NUM_6);
        itemSlotKeys.add(Input.Keys.NUM_7);
        itemSlotKeys.add(Input.Keys.NUM_8);
        itemSlotKeys.add(Input.Keys.NUM_9);
    }

    /**
     * Устанавливает слушателя ввода.
     */
    public void setInputListener(InputListener listener) {
        this.listener = listener;
    }

    /**
     * Получает текущое направление движения на основе нажатых клавиш.
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
     * Получает направление ТОЛЬКО если клавиша была нажата В ЭТОМ КАДРЕ (KEY_DOWN).
     * Возвращает Direction.NONE если клавиша была нажата в предыдущем кадре и продолжает держаться.
     * Это предотвращает автоматическое повторение движений при держании клавиши.
     */
    public Direction getDirectionOnKeyDown() {
        Direction currentDir = getCurrentDirection();

        // Если нет направления, нет никакого нажатия
        if (currentDir == Direction.NONE) {
            lastProcessedDirection = Direction.NONE;
            return Direction.NONE;
        }

        // Если направление ОТЛИЧАЕТСЯ от последнего, это новое нажатие
        if (currentDir != lastProcessedDirection) {
            lastProcessedDirection = currentDir;
            return currentDir;
        }

        // Иначе клавиша была нажата в предыдущем кадре - игнорируем
        return Direction.NONE;
    }

    /**
     * Обновляет состояние нажатых клавиш.
     * ДОЛЖНА вызываться в конце каждого кадра для правильного отслеживания KEY_DOWN.
     */
    public void updatePreviousState() {
        // This is called from GameScreen after processing input
    }

    /**
     * Проверяет, нажата ли клавиша.
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Преобразует код клавиши в индекс слота инвентаря (0-8 для 1-9)
     */
    private int getItemSlotIndex(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_1: return 0;
            case Input.Keys.NUM_2: return 1;
            case Input.Keys.NUM_3: return 2;
            case Input.Keys.NUM_4: return 3;
            case Input.Keys.NUM_5: return 4;
            case Input.Keys.NUM_6: return 5;
            case Input.Keys.NUM_7: return 6;
            case Input.Keys.NUM_8: return 7;
            case Input.Keys.NUM_9: return 8;
            default: return -1;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        pressedKeys.add(keycode);

        if (listener == null) {
            return false;
        }

        // Специальные клавиши (не движение)
        switch (keycode) {
            // Инвентарь (I)
            case Input.Keys.I:
                listener.onToggleInventory();
                return true;

            // Пауза (P только)
            case Input.Keys.P:
                listener.onTogglePause();
                return true;

            // Ожидание (Spacebar)
            case Input.Keys.SPACE:
                listener.onWait();
                return true;

            // Выход в меню (Q)
            case Input.Keys.Q:
                listener.onQuitToMenu();
                return true;

            // Использование предметов (1-9)
            case Input.Keys.NUM_1:
            case Input.Keys.NUM_2:
            case Input.Keys.NUM_3:
            case Input.Keys.NUM_4:
            case Input.Keys.NUM_5:
            case Input.Keys.NUM_6:
            case Input.Keys.NUM_7:
            case Input.Keys.NUM_8:
            case Input.Keys.NUM_9:
                int slotIndex = getItemSlotIndex(keycode);
                if (slotIndex >= 0) {
                    listener.onItemUse(slotIndex);
                }
                return true;

            // Направления (для меню)
            case Input.Keys.UP:
            case Input.Keys.W:
                listener.onMenuInput(MenuInput.UP);
                break;

            case Input.Keys.DOWN:
            case Input.Keys.S:
                listener.onMenuInput(MenuInput.DOWN);
                break;

            case Input.Keys.LEFT:
            case Input.Keys.A:
                listener.onMenuInput(MenuInput.LEFT);
                break;

            case Input.Keys.RIGHT:
            case Input.Keys.D:
                listener.onMenuInput(MenuInput.RIGHT);
                break;

            case Input.Keys.ENTER:
                listener.onMenuInput(MenuInput.SELECT);
                return true;

            // ESCAPE - CANCEL для меню (инвентарь и т.д.)
            case Input.Keys.ESCAPE:
                listener.onMenuInput(MenuInput.CANCEL);
                return true;
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
