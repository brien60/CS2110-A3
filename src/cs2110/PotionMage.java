package cs2110;

/**
 * Models a Mage who can cast a potion spell.
 */
public class PotionMage extends Mage {

    /**
     * Constructs a new PotionMage with the given `name`, a `spellName` of "potion spell", and
     * initializes their base health and power levels.
     */
    public PotionMage(String name, GameEngine engine) {
        super(name, "potion spell", engine);
    }

    @Override
    public String actorType() {
        return "potion mage";
    }

    /**
     * Adds three randomly generated potions to the potion inventory.
     */
    @Override
    public void castSpell() {
        String[] potionTypes = {"buff potion", "tuff potion", "invisibility potion"};

        for (int i = 0; i < 3; i++) {
            int potionRoll = engine.diceRoll(0, 2);
            engine.addPotion(potionRoll);

            System.out.printf("1 unit of %s added to the inventory.\n", potionTypes[potionRoll]);
        }
    }
}
