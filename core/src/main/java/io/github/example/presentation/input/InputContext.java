package io.github.example.presentation.input;

/**
 * Контекст для маршрутизации ввода.
 * Определяет, какой обработчик ввода активен в данный момент.
 */
public class InputContext {
    private InputHandler.InputListener currentListener;
    private ContextType currentType;

    public enum ContextType {
        GAME,      // Обработка движения персонажа
        MENU,      // Обработка меню
        INVENTORY, // Обработка инвентаря
        PAUSED,    // Паузировано
        DIALOG     // Диалог
    }

    public void setContext(ContextType type, InputHandler.InputListener listener) {
        this.currentType = type;
        this.currentListener = listener;
    }

    public ContextType getContextType() {
        return currentType;
    }

    public InputHandler.InputListener getListener() {
        return currentListener;
    }

    public boolean isGameContext() {
        return currentType == ContextType.GAME;
    }

    public boolean isMenuContext() {
        return currentType == ContextType.MENU;
    }

    public void clear() {
        currentType = null;
        currentListener = null;
    }
}
