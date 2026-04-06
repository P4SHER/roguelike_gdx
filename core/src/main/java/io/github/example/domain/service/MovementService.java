package io.github.example.domain.service;

import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.Item;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Level;
import io.github.example.domain.unittest.Logger;
public class MovementService {
    /**
     * Обрабатывает перемещение игрока в рамках игровой сессии.
     *
     * @param session       Для получения данных об игре
     * @param direction     Направление движения
     * @param combatService Для атаки
     * @return true, если перемещение успешно, false если заблокировано
     */
    public MoveResult movePlayer(GameSession session, Direction direction, CombatService combatService) {
        if (session.getState() != GameState.PLAYING) {
            return MoveResult.GAME_STATUS_NOT_PLAYING;
        }

        Player player = session.getPlayer();
        Level level = session.getCurrentLevel();
        Coordinates currentPos = player.getCoordinates();
        Coordinates newPos = currentPos.translate(direction);

        // Проверка на клетку перехода на новый уровень
        if (level.isExit(newPos)) {
            session.transitionToNextLevel();
            player.setPosition(newPos);
            return MoveResult.EXIT_REACHED;
        }

        // Проверка на границы карты
        if (level.isNotRoomAndPassage(newPos)) return MoveResult.OUT_OF_BOUNDS;

        // Проверка на наличие врага(бой)
        if (level.getEnemies().containsKey(newPos)) {
            Enemy enemy = level.getEnemyAtPos(newPos);
            CombatResult attackResult = combatService.attack(player, enemy, session);
            if (attackResult == CombatResult.KILL) {
                level.removeEnemy(newPos);
            }
            return MoveResult.HIT;
        }

        // Проверка на наличие предмета на новой клетке(авто-подбор)
        if (level.getItems().containsKey(newPos)) {
            Item itemCopy = level.getItemCopyAtPos(newPos);
            if (pickUpItem(session, itemCopy)) {
                level.removeItem(newPos);
            }
        }

        player.setPosition(newPos);

        return MoveResult.SUCCESS;
    }

    // Проверка, есть ли свободное место в рюкзаке. Если да, то кладем туда Item
    private boolean pickUpItem(GameSession session, Item item) {
        return session.getPlayer().getBackpack().addItem(item);
    }

    /**
     * Метод для перемещения Enemy
     *
     * @param session   Для получения данных об игре
     * @param enemy     - Враг, который будет ходить и атаковать
     * @param targetPos - координаты на которые хотим перейти
     * @return boolean. False - не сделали ход. True - ход был сделан.
     */
    public MoveResult moveEnemy(GameSession session, Enemy enemy, Coordinates targetPos, CombatService combatService) {
        if (session.getState() != GameState.PLAYING) {
            return MoveResult.GAME_STATUS_NOT_PLAYING;
        }

        Level level = session.getCurrentLevel();
        Player player = session.getPlayer();

        if (level.isNotRoomAndPassage(targetPos)) return MoveResult.OUT_OF_BOUNDS;

        if (!level.isWalkable(targetPos)) return MoveResult.NOT_WALKABLE;

        // Если совпадают координаты хода противника и текущего положения игрока, то выполняется атака вместо хода
        if (Coordinates.equals(player.getCoordinates(), targetPos)) {
            combatService.attack(enemy, player, session);
            Logger.info("Враг наносит урон игроку");
            return MoveResult.HIT;

        }

        level.setPositionEnemy(enemy, enemy.getCoordinates(), targetPos);

        return MoveResult.SUCCESS;
    }


}
