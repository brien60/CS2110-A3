package cs2110;

/**
 * Models a Mage who can cast a stunning spell.
 */
public class StunningMage extends Mage {

    /**
     * Constructs a new StunningMage with the given `name`, a `spellName` of "stunning spell", and
     * initializes their base health and power levels.
     */
    public StunningMage(String name, GameEngine engine) {
        super(name, "stunning spell", engine);
    }

    @Override
    public String actorType() {
        return "stunning mage";
    }

    /**
     * Selects a living monster to stun. When a monster is stunned, it becomes dazed and its next
     * turn is skipped.
     */
    @Override
    public void castSpell() {
        Monster target = engine.selectMonsterTarget();
        target.stun();
        System.out.printf("%s has been stunned!\n", target.name());
    }

}
