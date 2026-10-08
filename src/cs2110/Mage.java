package cs2110;

/**
 * Models a player who can cast a spell.
 */
public abstract class Mage extends Player {

    /**
     * The name of the spell the mage can cast.
     */
    private final String spellName;

    /**
     * Constructs a new Mage with the given `name` and `spellName` and initializes their base health
     * and power levels.
     */
    public Mage(String name, String spellName, GameEngine engine) {
        super(name, engine);
        this.spellName = spellName;
    }

    /**
     * Queries whether the user would like this Mage to cast a spell. If so, then the corresponding
     * spell is cast and `false` is returned. Otherwise, nothing happens and `true` is returned.
     */
    @Override
    public boolean chooseAction() {
        boolean castSpell = engine.queryBoolean("Would you like to cast a " + spellName + "?");
        if (!castSpell) {
            return true;
        }
        castSpell();
        return false;
    }

    /**
     * Casts the corresponding spell associated with this mage.
     */
    public abstract void castSpell();

}
