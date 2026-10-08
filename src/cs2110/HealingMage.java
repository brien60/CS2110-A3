package cs2110;

public class HealingMage extends Mage {

    public HealingMage(String name, GameEngine engine) {
        super(name, "healing spell", engine);
    }

    @Override
    public String actorType() {
        return "healing mage";
    }

    @Override
    public void castSpell() {
        Player target = engine.selectPlayerTarget(this);
        int healPoints = engine.diceRoll(0, power());
        target.heal(healPoints);
    }
}
