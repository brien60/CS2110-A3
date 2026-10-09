package cs2110;

/**
 * Models a player that can equip a Weapon.
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
     * appropriate action (could be to do nothing and return) based on the user's input, and returns
     * true.
     */
    @Override
    public boolean chooseAction() {
        boolean changeEquipment = engine.queryBoolean(
                "Would you like to change your current equipment?");
        if (!changeEquipment) {
            return true;
        }

        int selection = engine.queryWeaponSelection(equippedWeapon);

        /* A copy of `equippedWeapon` is necessary as `equippedWeapon` must be modified before the
        `updateWeapons` call so that the weapons invariant is true when exiting `updateWeapons`. */
        Weapon equippedWeaponCopy = equippedWeapon;

        if (selection == -1) {
            equippedWeapon = null;
        } else {
            equippedWeapon = engine.getWeaponAtIndex(selection);
        }

        engine.updateWeapons(selection, equippedWeaponCopy);

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

    /**
     * Performs the same behavior as `takeDamage()` for `Actor`, but additionally sets
     * `equippedWeapon` to null if the fighter dies from the damage.
     */
    @Override
    public void takeDamage(int damageAmount) {
        super.takeDamage(damageAmount);
        if (health() == 0) {
            equippedWeapon = null;
        }
    }

}
