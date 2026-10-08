package cs2110;

/**
 * A `Potion` that doubles the base power of the player that consumes it.
 */
public class BuffPotion extends Potion {

    /**
     * Constructs a new BuffPotion as a `Potion` with `name` "buff potion" and `effect` "increases
     * power".
     */
    public BuffPotion() {
        super("buff potion", "increases power");
    }

    @Override
    public void applyTo(Player player) {
        player.consumeBuffPotion();
    }
}
