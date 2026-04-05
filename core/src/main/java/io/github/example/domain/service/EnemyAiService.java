package io.github.example.domain.service;

import io.github.example.domain.entities.Enemy;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Level;
import io.github.example.domain.unittest.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * !!! НЕТ ЗАРДЛЕНИЯ ХОДА ДЛЯ КАЖДОГО ТИПА ВРАГА !!!
 * Потому что каждый имеет свои особенности передвижения и атаки.
 */
public class EnemyAiService {
    private static final Random random = new Random();

    public void processAllEnemies(GameSession session, CombatService combatService, MovementService movementService) {
        Logger.info("Все противники начала делать ход");
        Level level = session.getCurrentLevel();
        Map<Coordinates, Enemy> newEnemies = new HashMap<>(level.getEnemies());
        for(Enemy enemy : newEnemies.values()) {
            Coordinates newPos = calculationNewCoordinates(session, enemy);
            movementService.moveEnemy(session, enemy, newPos ,combatService);
        }

    }

    private Coordinates calculationNewCoordinates(GameSession session, Enemy enemy) {
        if (inAttackZone(session, enemy)) {
            // Противник двигается в сторону Игрока
            Direction directionMoveEnemy = determiningDirectionAttack(session, enemy);
            Coordinates newPos = new Coordinates(enemy.getCoordinates().getX() + directionMoveEnemy.dx(), enemy.getCoordinates().getY() + directionMoveEnemy.dy());
            Logger.info("PosPlayer: (" + session.getPlayer().getCoordinates().getX() + ", " + session.getPlayer().getCoordinates().getY() + ") PosEnemy: (" + enemy.getCoordinates().getX() + ", " + enemy.getCoordinates().getY() + "), " + "aNewPosEnemy: (" + newPos.getX() + ", " + newPos.getY() + ")");
            if (!session.getCurrentLevel().isNotRoomAndPassage(newPos)) return newPos;
            else {
                return definingCorrectNewRandomPos(session, enemy);
            }
        } else {
            // Случайное направление
            return definingCorrectNewRandomPos(session, enemy);
        }
    }

    private Coordinates definingCorrectNewRandomPos(GameSession session, Enemy enemy) {
        boolean flagCorrectPos = false;
        Coordinates newPos = new Coordinates(0, 0);
        while (!flagCorrectPos) {
            Direction directionMoveEnemy = determiningDirectionRandom();
            newPos = new Coordinates(enemy.getCoordinates().getX() + directionMoveEnemy.dx(), enemy.getCoordinates().getY() + directionMoveEnemy.dy());
            if (!session.getCurrentLevel().isNotRoomAndPassage(newPos)) {
                flagCorrectPos = true;
            }
        }
        return newPos;
    }

    private Direction determiningDirectionRandom() {
        double randomValue = random.nextDouble();
        if (randomValue <= 0.25) return Direction.LEFT;
        else if (randomValue <= 0.5) return Direction.UP;
        else if (randomValue <= 0.75) return Direction.RIGHT;
        else return Direction.DOWN;
    }

    private Direction determiningDirectionAttack(GameSession session, Enemy enemy) {
        Coordinates playerPos = session.getPlayer().getCoordinates();
        Coordinates enemyPos = enemy.getCoordinates();

        double dx = playerPos.getX() - enemyPos.getX();
        double dy = playerPos.getY() - enemyPos.getY();
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx < 0) return Direction.LEFT;
            return Direction.RIGHT;
        } else {
            if (dy < 0) return Direction.UP;
            return Direction.DOWN;
        }
    }

    private boolean inAttackZone(GameSession session, Enemy enemy) {
        Coordinates playerPos = session.getPlayer().getCoordinates();
        Coordinates enemyPos = enemy.getCoordinates();

        double dx = Math.pow(enemyPos.getX() - playerPos.getX(), 2);
        double dy = Math.pow(enemyPos.getY() - playerPos.getY(), 2);
        double distanceBetweenPlayerAndEnemy = Math.sqrt(dx + dy);

        return distanceBetweenPlayerAndEnemy < enemy.getHostilityRange();
    }
}
