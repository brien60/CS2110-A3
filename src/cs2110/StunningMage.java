package cs2110;

public class StunningMage extends Mage {

    public StunningMage(String name, GameEngine engine) {
        super(name, engine, "stunning spell");
    }

    @Override
    public String actorType() {
        return "stunning mage";
    }

    @Override
    public void castSpell() {
        Monster target = engine.selectMonsterTarget();
        target.stun();
        System.out.printf("%s has been stunned!\n", target.name());
    }

}
