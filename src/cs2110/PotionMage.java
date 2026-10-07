package cs2110;

public class PotionMage extends Mage{
    public PotionMage(String name, GameEngine engine) {
        super(name, engine, "potion spell");
    }

    @Override
    public String actorType() {
        return "potion mage";
    }

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
