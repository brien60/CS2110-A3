package cs2110;

public abstract class Mage extends Player{
    private final String spellName;

    public Mage(String name, GameEngine engine, String spellName) {
        super(name, engine);
        this.spellName = spellName;
    }

    @Override
    public boolean chooseAction() {
        boolean castSpell = engine.queryBoolean("Would you like to cast a " + spellName + "?");
        if (!castSpell) return true;
        castSpell();
        return false;
    }

    public abstract void castSpell();

}
