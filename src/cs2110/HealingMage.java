package cs2110;

/**
 * Models a Mage who can cast a healing spell.
 */
public class HealingMage extends Mage {

    /**
     * Constructs a new HealingMage with the given `name`, a `spellName` of "healing spell", and
     * initializes their base health and power levels.
     */

    public HealingMage(String name, GameEngine engine) {
        super(name, "healing spell", engine);
    }

    @Override
    public String actorType() {
        return "healing mage";
    }

    /**
     * Selects a targetable player, including the possibility of selecting themself, to heal. The
     * number of health points sent to that player is a random roll between 0 and this healer's
     * power level.
     */
    @Override
    public void castSpell() {
        Player target = engine.selectPlayerTarget(this);
        int healPoints = engine.diceRoll(0, power());
        target.heal(healPoints);
    }
}
