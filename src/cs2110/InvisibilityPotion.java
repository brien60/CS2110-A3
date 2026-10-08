package cs2110;

/**
 * A `Potion` that make the player that consumes it invisible.
 */
public class InvisibilityPotion extends Potion {

    /**
     * Constructs a new InvisibilityPotion as a `Potion` with `name` "invisibility potion" and
     * `effect` "makes player invisible".
     */
    public InvisibilityPotion() {
        super("invisibility potion", "makes player invisible");
    }

    @Override
    public void applyTo(Player player) {
        player.consumeInvisibilityPotion();
    }
}
