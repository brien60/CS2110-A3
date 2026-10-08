package cs2110;

/**
 * Models a fighter that can equip a Weapon
 */
public class Fighter extends Player {

    /**
     * Equals `null` if the Fighter does not currently have a weapon, otherwise stores the reference
     * to the `Weapon` the Fighter has currently equipped.
     */
    private Weapon equippedWeapon;

    /**
     * Constructs a new Fighter with the given `name` and initializes their base health and power
     * levels.
     */
    public Fighter(String name, GameEngine engine) {
        super(name, engine);
    }

    /**
     * Queries the user whether they want this fighter to change their equipment, takes the
     * appropriate action (could be to do nothing) based on the user's input, and returns true.
     */
    @Override
    public boolean chooseAction() {
        boolean changeEquipment = engine.queryBoolean(
                "Would you like to change your current equipment?");
        if (!changeEquipment) {
            return true;
        }

        int selection = engine.queryWeaponSelection(equippedWeapon);
        equippedWeapon = engine.updateWeapons(selection, equippedWeapon);

        return true;
    }

    @Override
    public String actorType() {
        return "fighter";
    }

    @Override
    public int power() {
        if (equippedWeapon != null) {
            return super.power() + equippedWeapon.power();
        }
        return super.power();
    }

    @Override
    public int toughness() {
        if (equippedWeapon != null) {
            return super.toughness() + equippedWeapon.toughness();
        }
        return super.toughness();
    }

    @Override
    public Weapon weapon() {
        return equippedWeapon;
    }


}
