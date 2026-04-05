package io.github.example.presentation.input;

import io.github.example.domain.service.Direction;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Turn-based input queue for managing player actions.
 * Ensures only ONE action is processed per game turn, queuing additional inputs.
 * Supports multiple key types: movement, item use, wait, etc.
 */
public class InputQueue {
    private final Deque<GameAction> actionQueue = new LinkedList<>();
    private GameAction lastProcessedAction;

    /**
     * Represents a queued game action.
     */
    public interface GameAction {
        void execute();
        ActionType getType();
        String getDescription();
    }

    /**
     * Movement action.
     */
    public static class MoveAction implements GameAction {
        private final Direction direction;
        private final Runnable executor;

        public MoveAction(Direction direction, Runnable executor) {
            this.direction = direction;
            this.executor = executor;
        }

        @Override
        public void execute() {
            executor.run();
        }

        @Override
        public ActionType getType() {
            return ActionType.MOVE;
        }

        @Override
        public String getDescription() {
            return "Move " + direction;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    /**
     * Item use action.
     */
    public static class ItemUseAction implements GameAction {
        private final int slotIndex;
        private final Runnable executor;

        public ItemUseAction(int slotIndex, Runnable executor) {
            this.slotIndex = slotIndex;
            this.executor = executor;
        }

        @Override
        public void execute() {
            executor.run();
        }

        @Override
        public ActionType getType() {
            return ActionType.ITEM_USE;
        }

        @Override
        public String getDescription() {
            return "Use item slot " + slotIndex;
        }

        public int getSlotIndex() {
            return slotIndex;
        }
    }

    /**
     * Wait/no-op action.
     */
    public static class WaitAction implements GameAction {
        private final Runnable executor;

        public WaitAction(Runnable executor) {
            this.executor = executor;
        }

        @Override
        public void execute() {
            executor.run();
        }

        @Override
        public ActionType getType() {
            return ActionType.WAIT;
        }

        @Override
        public String getDescription() {
            return "Wait";
        }
    }

    public enum ActionType {
        MOVE,
        ITEM_USE,
        WAIT,
        INTERACT,
        SPECIAL
    }

    /**
     * Enqueue a movement action.
     */
    public void enqueueMove(Direction direction, Runnable executor) {
        actionQueue.offer(new MoveAction(direction, executor));
    }

    /**
     * Enqueue an item use action.
     */
    public void enqueueItemUse(int slotIndex, Runnable executor) {
        actionQueue.offer(new ItemUseAction(slotIndex, executor));
    }

    /**
     * Enqueue a wait action.
     */
    public void enqueueWait(Runnable executor) {
        actionQueue.offer(new WaitAction(executor));
    }

    /**
     * Enqueue a custom action.
     */
    public void enqueueAction(GameAction action) {
        actionQueue.offer(action);
    }

    /**
     * Get the next action to process this turn.
     * Returns null if queue is empty.
     */
    public GameAction getNextAction() {
        GameAction action = actionQueue.poll();
        if (action != null) {
            lastProcessedAction = action;
        }
        return action;
    }

    /**
     * Peek at the next action without removing it.
     */
    public GameAction peekNextAction() {
        return actionQueue.peek();
    }

    /**
     * Check if queue has pending actions.
     */
    public boolean hasActions() {
        return !actionQueue.isEmpty();
    }

    /**
     * Get the number of queued actions.
     */
    public int size() {
        return actionQueue.size();
    }

    /**
     * Clear all queued actions.
     */
    public void clear() {
        actionQueue.clear();
        lastProcessedAction = null;
    }

    /**
     * Get the last processed action (for debugging).
     */
    public GameAction getLastProcessedAction() {
        return lastProcessedAction;
    }

    /**
     * Get queue status for debugging.
     */
    public String getDebugInfo() {
        return "InputQueue: " + actionQueue.size() + " queued actions" +
               (lastProcessedAction != null ? ", Last: " + lastProcessedAction.getDescription() : "");
    }
}
