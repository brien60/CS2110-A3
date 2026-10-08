package cs2110;

/**
 * A `Potion` that doubles the base power of the player who
 * consumes it.
 */
public class BuffPotion extends Potion{
    public BuffPotion() {
        super("buff potion", "increases power");
    }

    public void applyTo(Player player) {
        player.consumeBuffPotion();
    }
}
