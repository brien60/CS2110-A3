package cs2110;

public class TuffPotion extends Potion{
    public TuffPotion() {
        super("tuff potion", "increases toughness");
    }

    public void applyTo(Player player) {
        player.consumeTuffPotion();
    }
}
