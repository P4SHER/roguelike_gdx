package io.github.example.domain.service;

import io.github.example.datalayer.SaveService;
import io.github.example.domain.entities.Character;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.EnemyType;
import io.github.example.domain.entities.Player;
import io.github.example.domain.unittest.Logger;

import java.util.Random;

/**
 * Расчет боя, урона.
 */
public class CombatService {
    private static final Random random = new Random();

    public CombatResult attack(Character attacker, Character defender, GameSession session) {
        // Проверка на попадание(ловкость)
        Logger.info("Начало атаки");
        if (!isHitSuccessful(attacker, defender)) {
            Logger.info("Промах");
            return CombatResult.MISS;

        }

        // Расчет удара
        int damage = calculateDamage(attacker);

        // Наносим урон
        defender.takeDamage(damage);
        Logger.info("Атака завершена, урона нанесен");

        // Обновляем статистику
        if (attacker instanceof Player) {
            session.addDamageDealt(damage);
        } else if (defender instanceof Player) {
            session.addDamageTaken(damage);
        }

        if (!defender.isAlive()) {
            handleDeath(attacker, defender, session);
            if (defender instanceof Player) {
                Logger.error("Игрок умер");
            } else
                Logger.info("Противник умер");
            return CombatResult.KILL;

        }

        return CombatResult.HIT;
    }

    /**
     * Обрабатывает смерть персонажа
     */
    private void handleDeath(Character attacker, Character defender, GameSession session) {
        // Если убит враг
        if (defender instanceof Enemy enemy) {
            session.getRecord().incrementNumberDefeatedEnemies();

            // Выпадение сокровищ
            int treasureDrop = calculateTreasureDrop(enemy);
            if (attacker instanceof Player player) {
                player.getBackpack().addTotalTreasureValue(treasureDrop);
            }
        }
        // Если убит игрок
        else if (defender instanceof Player) {
            session.setState(GameState.GAME_OVER);
            if (session.getCurrentLevelNumber() == 1) {
                session.getRecord().setNumberTreasures(session.getPlayer().getBackpack().getTotalTreasureValue());
                session.getLeaderboard().updateLastRecord(session.getRecord());
//                session.getLeaderboard().addRecord(session.getRecord());
                SaveService.clearJsonFile();
            } else
                session.getLeaderboard().updateLastRecord(session.getRecord());
            session.getSaveService().saveLeaderBoard(session);
        }
    }

    private static int calculateDamage(Character attacker) {
        int baseDamage = attacker.getStats().getStrength();

        // Если есть оружие, то добавляем урон
        if (attacker instanceof Player player && player.getCurrentWeapon() != null) {
            baseDamage += player.getCurrentWeapon().getStrengthBonus();
        }

        // По желанию можно добавить небольшой разброс урона

        return baseDamage;
    }

    private int calculateTreasureDrop(Enemy enemy) {
        // Формула зависит от характеристик врага
        int baseTreasure = 5;
        int bonus = (enemy.getStats().getStrength() + enemy.getStats().getAgility() + enemy.getStats().getMaxHealth()) / 10;
        return baseTreasure + bonus + random.nextInt(5);
    }

    private static Boolean isHitSuccessful(Character attacker, Character defender) {
        double hitChance = (double) attacker.getStats().getAgility() / (attacker.getStats().getAgility() + defender.getStats().getAgility());

        if (attacker instanceof Enemy enemy && enemy.getType() == EnemyType.VAMPIRE){
            if (!enemy.getCanAttack()) {
                enemy.setCanAttack(true);
                return false;
            }
        }

        return random.nextDouble() < hitChance;
    }
}
