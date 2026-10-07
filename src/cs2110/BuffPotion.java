package cs2110;

public class BuffPotion extends Potion{
    public BuffPotion() {
        super("buff potion", "increases power");
    }

    public void applyTo(Player player) {
        player.consumeBuffPotion();
    }
}
