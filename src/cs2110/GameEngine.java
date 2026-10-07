package cs2110;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Manages the state of our game simulation by creating and keeping track of players and monsters
 * and facilitating the turn order.
 */
public class GameEngine {

    /* *****************************************************************************
     * This first set of fields keeps track of the players and items in the game   *
     *******************************************************************************/

    /**
     * The players in this game simulation. Within this array, the first `numLivingPlayers` entries
     * reference distinct players with `health() > 0` and the remaining entries reference
     * distinct players with `health() == 0`.
     */
    private Player[] players;

    /**
     * The number of players who are currently alive in the game. Must have `0 <= numLivingPlayers
     * <= players.length`.
     */
    private int numLivingPlayers;

    /**
     * The monsters in this game simulation. Within this array, the first `numLivingMonsters`
     * entries reference distinct monsters with `health() > 0` and the remaining entries
     * reference distinct monsters with `health() == 0`.
     */
    private Monster[] monsters;

    /**
     * The number of monsters who are currently alive in the game. Must have `0 <= numLivingMonsters
     * <= monsters.length`.
     */
    private int numLivingMonsters;

    /**
     * The weapons that fighters can use during this game simulation. Within this array, the first
     * `numAvailableWeapons` entries reference distinct weapons that are not equipped by a
     * player, and the remaining entries reference distinct weapon objects that are equipped by
     * a player.
     */
    private Weapon[] weapons;

    /**
     * The number of weapons not currently equipped by players.
     * Must have `0 <= numAvailableWeapons <= weapons.length`
     */
    private int numAvailableWeapons;

    /**
     * The potion objects that can be used by a player (if available). Each entry is an instance
     * of a distinct potion type.
     */
    private final Potion[] potions;
    /* Note: Since `Potion` objects are immutable, we only need one instance of each that can be
     * re-used each time this potion is consumed. This is an example of the *singleton* pattern. */

    /**
     * The number of available units of each potion, where `potionQuantities[i]` is the number
     * of units of `potions[i]`. Must have `potionQuantities.length == potions.length`, and each
     * entry must be non-negative.
     */
    private final int[] potionQuantities;

    /**
     * Returns whether the class invariants on `players`, `numLivingPlayers`, `monsters`,
     * `numLivingMonsters`, `weapons`, `numAvailableWeapons`, `potions`, and `potionQuantities`
     * are all satisfied.
     */
    private boolean invariantSatisfied() {
        // player
        for (int i = 0; i < players.length; i++) {
            if (i < numLivingPlayers) {
                if (players[i].health() <= 0) return false;
            }
            else {
                if (players[i].health() != 0) return false;
            }
        }

        // numLivingPlayers
        if (numLivingPlayers < 0 || numLivingPlayers > players.length) return false;

        // monsters
        for (int i = 0; i < monsters.length; i++) {
            if (i < numLivingMonsters) {
                if (monsters[i].health() <= 0) return false;
            }
            else {
                if (monsters[i].health() != 0) return false;
            }
        }

        // numLivingMonsters
        if (numLivingMonsters < 0 || numLivingMonsters > monsters.length) return false;

        // weapons
        for (int i = 0; i < weapons.length; i++) {
            if (i < numAvailableWeapons) {
                for (int j = 0; j < players.length; j++) {
                    if (players[j].weapon() != null && players[j].weapon() == weapons[i]) {
                        /* invariant is broken if a weapon in weapons[..numAvailableWeapons)
                        is currently equipped by a character */
                        return false;
                    }
                }
            }
            else {
                for (int j = 0; j < players.length; j++) {
                    if (players[j].weapon() != null && players[j].weapon() == weapons[i]) {
                        break;
                    }
                    if (j == players.length - 1) return false;
                    /* invariant is broken if a weapon in weapons[numAvailableWeapons..]
                    is not currently equipped by a character */
                }
            }
        }

        // numAvailable
        if (numAvailableWeapons < 0 || numAvailableWeapons > weapons.length) return false;

        // potions
        // How distinct potion types?

        // potionQuantities
        if (potionQuantities.length != potions.length) return false;
        for (int i = 0; i < potionQuantities.length; i++) {
            if (potionQuantities[i] < 0) return false;
        }

        return true;

    }

    /* *****************************************************************************
     * The remaining fields are utilities that are used by the GameEngine          *
     *******************************************************************************/

    /**
     * The random number generator that is used to model random events in this game.
     */
    private final Random rng;

    /**
     * The Scanner used to accept player inputs
     */
    private final Scanner sc;

    /**
     * Whether user inputs should be echoed to the output stream (true for file mode, false for
     * console mode)
     */
    private final boolean echo;

    /* *****************************************************************************
     * Methods to construct the GameEngine and initialize the game                 *
     *******************************************************************************/

    /**
     * Constructs a new game engine with the given Scanner `sc` to process user inputs.
     */
    public GameEngine(Scanner sc, boolean echo) {
        this(sc, echo, new Random());
    }

    /**
     * Constructs a new game engine with a reference to the supplied random number generator, `rng`
     * and the given Scanner `sc` to process user inputs.
     */
    public GameEngine(Scanner sc, boolean echo, Random rng) {
        this.sc = sc;
        this.echo = echo;
        this.rng = rng;

        players = new Player[0];
        numLivingPlayers = 0;
        monsters = new Monster[0];
        numLivingMonsters = 0;
        weapons = new Weapon[0];
        numAvailableWeapons = 0;

        potions = new Potion[] {new BuffPotion(), new TuffPotion(), new InvisibilityPotion()};
        potionQuantities = new int[potions.length];
    }

    /**
     * Carries out the simulation of our dungeon battle game by initializing the players and
     * monsters before entering the main game loop.
     */
    protected void initializeGame() {
        System.out.println("*** Welcome to the Dungeons of Dragon Day! ***");
        System.out.println(); // extra line break

        initializePlayers();
        initializeWeapons();
        initializeMonsters();
    }

    /**
     * Queries for console input to set up the players of this game simulation.
     */
    private void initializePlayers() {
        System.out.print("How many players will you have? ");
        try {
            numLivingPlayers = Integer.parseInt(getInputLine());
            players = new Player[numLivingPlayers];
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize players: " + e.getMessage());
        }

        System.out.println("Enter the player names, one at a time.");
        for (int i = 0; i < numLivingPlayers; i++) {
            System.out.print((i + 1) + ": ");
            String name = getInputLine();

//            int type = -1;
            int type = querySelection("Player Type", "What type of player is " + name + "?",
                new String[]{ "Fighter", "HealingMage", "StunningMage", "PotionMage"}, null);
            players[i] = createPlayer(name, type);
            System.out.println();
        }
    }

    /**
     * A factory method to produce players of different types.
     */
    private Player createPlayer(String name, int type) {
        // TODO: Uncomment the case lines as you implement these player subtypes
        return switch (type) {
            case -1 -> new BasicPlayer(name, this);
             case 0 -> new Fighter(name, this);
             case 1 -> new HealingMage(name, this);
             case 2 -> new StunningMage(name, this);
             case 3 -> new PotionMage(name, this);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Creates the monsters of this game simulation and prints their information to the console.
     */
    private void initializeMonsters() {
        numLivingMonsters = (players.length / 2) + 1;

        System.out.printf("%nYou'll battle against %d monster%s:%n", numLivingMonsters,
            numLivingMonsters > 1 ? "s" : "");

        monsters = new Monster[numLivingMonsters];
        for (int i = 0; i < numLivingMonsters; i++) {
            monsters[i] = new Monster(getRandomName("monsters.txt", "Monster"), this);
            System.out.printf("  %d. %s%n", i, monsters[i]);
        }
        System.out.println();
    }

    /**
     * Creates the weapons for this game simulation and prints their information to the console.
     */
    private void initializeWeapons() {
        System.out.println("Your fighters will have access to these weapons for their battle.");

        weapons = new Weapon[numLivingPlayers + 1];
        numAvailableWeapons = weapons.length;
        for (int i = 0; i < numLivingPlayers + 1; i++) {
            String name = getRandomName("weapons.txt", "Weapon");
            int powerMod = diceRoll(1, 12) - 5; // -4 to 6
            int toughnessMod = 2 - powerMod;
            Weapon weapon = new Weapon(name, powerMod, toughnessMod);
            weapons[i] = weapon;
            System.out.printf("  %d. %s%n", i, weapons[i]);
        }
    }

    /* *****************************************************************************
     * Game state mutator methods                                                  *
     *******************************************************************************/

    /**
     * Processes an assignment of `damageAmount` points of damage to the given `actor`,
     * updating the state of the game engine appropriately if this causes that actor to die.
     * When a Player or Monster dies, swap it with the last currently living actor in its
     * corresponding array to restore that invariant. Thus, the newly-deceased actor becomes
     * the first dead actor in its array.
     */
    public void assignDamageTo(Actor actor, int damageAmount) {
        actor.takeDamage(damageAmount);

        if (actor.health() == 0) {
            if (actor.actorType().equals("monster")) {
                for (int i = 0; i < numLivingMonsters; i++) {
                    if (actor == monsters[i]) {
                        swap(monsters, numLivingMonsters-1, i);
                        break;
                    }
                }
                numLivingMonsters--;
            }
            else { // A player
                if (actor.actorType().equals("fighter")) {
                    Weapon equippedWeapon = actor.weapon();
                    if (equippedWeapon != null) {
                        updateWeapons(-1, equippedWeapon);
                    }
                }

                for (int i = 0; i < numLivingPlayers; i++) {
                    if (actor == players[i]) {
                        swap(players, numLivingPlayers-1, i);
                        break;
                    }
                }
                numLivingPlayers--;
            }
        }
        assert invariantSatisfied();

    }


    /**
     * Prompts the player to select a potion type from the available inventory (or decline to use
     * a potion). If a potion is selected, then the inventory is updated and that potion is
     * returned. Otherwise, null is returned and no changes are made to the potion inventory.
     */
    public Potion selectPotion() {
        int l = potions.length;
        int[] available = new int[l]; // index `j` stores index of the `j`'th available type in `potions`
        String[] potionOptions = new String[l]; // Option strings for query
        int numPotionTypes = 0; // number of available potion types
        for (int i = 0; i < l; i++) {
            if (potionQuantities[i] > 0) { // potion is available
                available[numPotionTypes] = i; // add to availability map
                potionOptions[numPotionTypes] = potions[i] + " (quantity = " + potionQuantities[i] + ")";
                numPotionTypes++;
            }
        }

        if (numPotionTypes == 0) { // no potions available
            System.out.println("There are no potions available.");
            assert invariantSatisfied();
            return null;
        }

        int selection = querySelection("Potion", "Select a potion to use this turn, if you'd like:",
                Arrays.copyOf(potionOptions, numPotionTypes), "Don't use potion this turn");

        if (selection == -1) { // no potion selected
            assert invariantSatisfied();
            return null;
        }
        Potion selected = potions[available[selection]];
        potionQuantities[available[selection]]--; // update potions inventory
        assert invariantSatisfied();
        return selected;
    }

    public Weapon updateWeapons(int selection, Weapon equippedWeapon) {
        if (equippedWeapon != null) { // unequip current weapon
            for(int i = numAvailableWeapons; i < weapons.length; i++) {
                if (weapons[i] == equippedWeapon) {
                    swap(weapons, i, numAvailableWeapons);
                    numAvailableWeapons++;
                }
            }
        }

        if (selection == -1) {
            assert invariantSatisfied();
            return null;
        }

        // Equip a new weapon
        swap(weapons, selection, numAvailableWeapons-1);
        numAvailableWeapons--;
        assert invariantSatisfied();
        return weapons[numAvailableWeapons];
    }

    /**
     * Increments potionQuantities[i] by 1.
     * Requires `0 <= i < potionQuantities.length`.
     */
    public void addPotion(int i) {
        potionQuantities[i]++;
        assert invariantSatisfied();
    }

    /* *****************************************************************************
     * Game state accessor methods                                                 *
     *******************************************************************************/

    /**
     * Returns a reference to an array copy containing references to all players who
     * can currently be targeted by spells and monster attacks, in their order in `players`.
     */
    public Player[] targetablePlayers() {
        int numTargetablePlayers = 0;
        Player[] targetablePlayers = new Player[numLivingPlayers];

        for (int i = 0; i < numLivingPlayers; i++) {
            Player currentPlayer = players[i];

            if (!currentPlayer.isInvisible()) {
                targetablePlayers[numTargetablePlayers++] = currentPlayer;
            }
        }
        // return a copy that excludes potential null entries at the end.
        return Arrays.copyOf(targetablePlayers, numTargetablePlayers);
    }

    /**
     * Returns a reference to an array copy containing references to all living monsters.
     */
    private Monster[] livingMonsters() {
        return Arrays.copyOf(monsters, numLivingMonsters);
    }

    /**
     * Prompts the user to select a monster from the list of all living monsters, and returns
     * a reference to the chosen monster.
     */
    public Monster selectMonsterTarget() {
        int index = querySelection("Monster Target", "Select the number of the monster you'd like "
            + "to target:", livingMonsters(), null);
        return livingMonsters()[index];
    }

    /**
     * Prompts the user to select a player from the list of all currently targetable players (as
     * well as the `actingPlayer` if they are not currently targetable). Returns a reference to
     * the chosen player.
     */
    public Player selectPlayerTarget(Player actingPlayer) {
        int index = querySelection("Player Target", "Select the number of the player you'd like to target:",
                targetablePlayers(), null);
        return targetablePlayers()[index];
    }

    /* *****************************************************************************
     * Utility methods                                                             *
     *******************************************************************************/

    /**
     * Returns the random result of a dice roll between `min` and `max` (inclusive).
     */
    public int diceRoll(int min, int max) {
        return rng.nextInt(min, max + 1);
    }

    /**
     * Presents the user with a yes/no `query`, prompting them to input a number to make a
     * selection. Validates the input before returning `true` if "yes" was selected and returning
     * `false` if "no" was selected.
     */
    public boolean queryBoolean(String query) {
        return querySelection("Yes/No", query, new String[]{"No", "Yes"}, null) == 1;
    }

    public int queryWeaponSelection(Weapon currentWeapon) {
        String query = "Select the weapon that you'd like to equip:";
        Weapon[] options = Arrays.copyOfRange(weapons, 0, numAvailableWeapons);

        String defaultOption = currentWeapon == null ? null : "Unequip " + currentWeapon.name();
        return querySelection("Weapon Selection", query, options, defaultOption);
    }


    /**
     * Presents the user with a list of numbered `options` with the given `query`, prompting them
     * to input a number to make a selection. Validates the input before returning it. If
     * `defaultOption != null`, then this is presented as the final option numbered `-1`. When an
     * invalid selection is made, the `queryName` is written into the exception message for
     * debugging.
     */
    private int querySelection(String queryName, String query, Object[] options, String defaultOption) {
        System.out.println(query);

        for (int i = 0; i < options.length; i++) {
            System.out.printf("  [%d] %s%n", i, options[i]);
        }
        if (defaultOption != null) {
            System.out.println("-----------------------");
            System.out.printf("  [-1] %s%n", defaultOption);
        }

        System.out.print("Selection: ");
        try {
            int selection = Integer.parseInt(getInputLine());
            if (selection >= options.length || selection < -1 || (selection == -1 && defaultOption == null)) {
                throw new IllegalArgumentException("invalid selection " + selection);
            }
            return selection;
        } catch (Exception e) { // either input was not a number or was an invalid index
            throw new RuntimeException("Query of " + queryName + " unsuccessful: " + e.getMessage());
        }
    }

    /**
     * Returns the next line of input from the user.
     */
    protected String getInputLine() {
        String line = sc.nextLine();
        if (echo) {
            System.out.println(line);
        }
        return line;
    }

    /**
     * Performs a Fisher-Yates shuffle on the given `arr`ay
     */
    private void shuffle(Object[] arr) {
        for (int i = 0; i < arr.length; i += 1) {
            int j = rng.nextInt(i, arr.length);
            swap(arr, i, j);
        }
    }

    /**
     * Swaps the Objects at positions `x` and `y` in the given array.
     */
    private void swap(Object[] objects, int x, int y) {
        Object temp = objects[x];
        objects[x] = objects[y];
        objects[y] = temp;
    }

    /**
     * Returns a randomly selected name from among the first 100 in the given `file`, or returns
     * the given `defaultName` if there's an IOException.
     */
    private String getRandomName(String file, String defaultName) {
        try {
            int line = rng.nextInt(100);
            return Files.readAllLines(Paths.get(file)).get(line + 1);
        } catch (IOException e) {
            return defaultName;
        }
    }

    /* *****************************************************************************
     * Methods for running the game simulation                                     *
     *******************************************************************************/

    /**
     * Runs the main game loop. Terminates when the players have won (all monsters are dead) or
     * lost (all players are dead). While there are still living players and monsters, constructs
     * an unshuffled turn-order array with living Players in their order in `players`, followed by
     * living Monsters in their order in `monsters`, then generates and executes a random turn
     * order from that array at the start of each round. At the end of the game, if the players
     * have won, the message "Congratulations! You defeated the monsters!" is printed. Otherwise,
     * if the players have lost, the message "The monsters defeated you. Better luck next time!"
     * is printed.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void runMainGameLoop() {
        int round = 1;

        while (numLivingPlayers > 0 && numLivingMonsters > 0) {
            System.out.println("==========================================");
            System.out.printf("Starting Round %d%n%n", round);

            Actor[] actors; // contains living Actors in their turn order for this round
            actors = new Actor[numLivingPlayers + numLivingMonsters];
            System.arraycopy(players, 0, actors, 0, numLivingPlayers);
            System.arraycopy(monsters, 0, actors, numLivingPlayers, numLivingMonsters);

            shuffle(actors); // randomize the turn order within this round
            System.out.println("The turn order will be:");
            for (int i = 0; i < actors.length; i++) {
                System.out.println((i + 1) + ": " + actors[i]);
            }

            for (int i = 0; i < actors.length; i++) { // have the actors take their turns
                if (numLivingPlayers == 0 || numLivingMonsters == 0) break;

                if (actors[i].health() != 0) {
                    System.out.println("-------------------------------------------------");
                    System.out.printf("Starting %s's Turn:\n\n", actors[i].name());
                    actors[i].takeTurn();
                }
            }

            round++;
        }

        if (numLivingPlayers == 0) {
            System.out.println("The monsters defeated you. Better luck next time!");
        }
        else
            System.out.println("Congratulations! You defeated the monsters!");
        }

    /**
     * Runs this game simulation.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // default to an interactive game with console inputs
        if (args.length > 0) {
            File commandFile = new File(args[0]);
            try {
                sc = new Scanner(commandFile);
            } catch (FileNotFoundException e) {
                System.out.println("ERROR! File not found: " + commandFile);
                System.exit(1);
            }
        }

        GameEngine engine = new GameEngine(sc, args.length > 0);
        // Uncomment the following line to manually run through the simulation in End2EndTest.java
        // GameEngine engine = new GameEngine(sc, args.length > 0, new Random(123456L));
        engine.initializeGame();
        engine.runMainGameLoop();
    }
}
