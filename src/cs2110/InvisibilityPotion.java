package cs2110;

public class InvisibilityPotion extends Potion{
    public InvisibilityPotion() {
        super("invisibility potion", "makes player invisible");
    }

    public void applyTo(Player player) {
        player.consumeInvisibilityPotion();
    }
}
