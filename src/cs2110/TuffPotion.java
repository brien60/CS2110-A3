package cs2110;

/**
 * A `Potion` that doubles the base toughness of the player that consumes it.
 */
public class TuffPotion extends Potion {

    /**
     * Constructs a new TuffPotion as a `Potion` with `name` "tuff potion" and `effect` "increases
     * toughness".
     */
    public TuffPotion() {
        super("tuff potion", "increases toughness");
    }

    @Override
    public void applyTo(Player player) {
        player.consumeTuffPotion();
    }
}
