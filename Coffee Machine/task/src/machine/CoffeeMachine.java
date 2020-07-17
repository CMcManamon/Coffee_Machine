// This simple coffee machine takes orders and pays out

package machine;
import java.util.Scanner;

enum MachineState {
    CHOOSING_AN_ACTION,
    CHOOSING_A_COFFEE,
    FILLING_WATER,
    FILLING_MILK,
    FILLING_BEANS,
    FILLING_CUPS
}

class Inventory {
    private int water;
    private int milk;
    private int beans;
    private int cups;
    private int money;

    // Getters
    public int getWater() {
        return water;
    }

    public int getMilk() {
        return milk;
    }

    public int getBeans() {
        return beans;
    }

    public int getCups(){
        return cups;
    }

    public int getMoney() {
        return money;
    }

    // Setters (fillers) no limit assumed
    public void modifyWater(int input) {
        water += input;
    }

    public void modifyMilk(int input) {
        milk += input;
    }

    public void modifyBeans(int input) {
        beans += input;
    }

    public void modifyCups(int input) {
        cups += input;
    }

    public void addMoney(int input) {
        money += input;
    }

    public void withdrawMoney(int amount) {
        if (amount <= money) {
            money -= amount;
        } else {
            money = 0;
        }
    }
}

abstract class Beverage {
    final private int waterCost;
    final private int beanCost;
    final private int milkCost;
    final private int cupCost;
    final private int price;

    protected Beverage(int water, int bean, int milk, int cup, int price) {
        waterCost = water;
        beanCost = bean;
        milkCost = milk;
        cupCost = cup;
        this.price = price;
    }

    public boolean haveIngredients(Inventory inventory) {
        return inventory.getWater() >= waterCost
                && inventory.getBeans() >= beanCost
                && inventory.getMilk() >= milkCost
                && inventory.getCups() >= cupCost;
    }

    public int getWaterCost() {
        return waterCost;
    }
    public int getBeanCost() {
        return beanCost;
    }
    public int getMilkCost() {
        return milkCost;
    }
    public int getCupCost() {
        return cupCost;
    }
    public int getPrice() {
        return price;
    }
}

class Espresso extends Beverage {

    public Espresso () {
        super(250, 16, 0, 1, 4);
    }
}

class Latte extends Beverage {

    public Latte() {
        super(350, 20, 75, 1, 7);
    }
}

class Cappuccino extends Beverage {

    public Cappuccino() {
        super(200, 12, 100, 1, 6);
    }
}

public class CoffeeMachine {

    static Inventory inventory = new Inventory();
    boolean programRunning = true;

    MachineState machineState = MachineState.CHOOSING_AN_ACTION;

    public void processCommand(String input) {
        switch (machineState) {
            case CHOOSING_AN_ACTION:
                action(input);
                break;
            case CHOOSING_A_COFFEE:
                buy(input);
                break;
            case FILLING_WATER:
                inventory.modifyWater(Integer.parseInt(input));
                machineState = MachineState.FILLING_MILK;
                System.out.println("Write how many ml of milk do you want to add:");
                break;
            case FILLING_MILK:
                inventory.modifyMilk(Integer.parseInt(input));
                machineState = MachineState.FILLING_BEANS;
                System.out.println("Write how many grams of coffee beans do you want to add:");
                break;
            case FILLING_BEANS:
                inventory.modifyBeans(Integer.parseInt(input));
                machineState = MachineState.FILLING_CUPS;
                System.out.println("Write how many disposable cups of coffee do you want to add:");
                break;
            case FILLING_CUPS:
                inventory.modifyCups(Integer.parseInt(input));
                machineState = MachineState.CHOOSING_AN_ACTION;
                prompt();
                break;
            default:
                System.out.println("Invalid input");
        }
    }


    public void prompt() {
        System.out.println("\nWrite action (buy, fill, " +
                "take, remaining, exit):");
    }
    private void action(String input) {
        switch (input) {
            case "buy":
                System.out.println("\nWhat do you want to buy? 1 - espresso, 2 - latte, " +
                        "3 - cappuccino, back - to main menu:");
                machineState = MachineState.CHOOSING_A_COFFEE;
                break;
            case "fill":
                System.out.println("\nWrite how many ml of water do you want to add:");
                machineState = MachineState.FILLING_WATER;
                break;
            case "take":
                takeMoney();
                break;
            case "exit":
                programRunning = false;
                break;
            case "remaining":
                showCurrentState();
                prompt();
                break;
            default:
                System.out.println("Invalid input.");
        }
    }

    private void showCurrentState() {
        System.out.println("\nThe coffee machine has:");
        System.out.println(inventory.getWater() + " of water");
        System.out.println(inventory.getMilk() + " of milk");
        System.out.println(inventory.getBeans() + " of coffee beans");
        System.out.println(inventory.getCups() + " of disposable cups");
        System.out.println("$" + inventory.getMoney() + " of money");
    }

    private void buy(String buyChoice) {
        switch (buyChoice) {
            case "1":
                processOrder(1);
                break;
            case "2":
                processOrder(2);
                break;
            case "3":
                processOrder(3);
                break;
            case "back":
                break;
            default:
                System.out.println("Invalid Input");
        }
        machineState = MachineState.CHOOSING_AN_ACTION;
        prompt();
    }

    private void takeMoney() {
        System.out.println("\nI gave you $" + inventory.getMoney());
        inventory.withdrawMoney(inventory.getMoney());
        machineState = MachineState.CHOOSING_AN_ACTION;
        prompt();
    }

    private Beverage pickedBeverage(int option) {
        Beverage beverage;

        switch (option) {
            case 1: // espresso
                beverage = new Espresso();
                break;
            case 2: // latte
                beverage = new Latte();
                break;
            case 3: // cappuccino
                beverage = new Cappuccino();
                break;
            default:
                beverage = null;
                System.out.println("Not a valid beverage choice");
                break;
        }
        return beverage;
    }

    public void deductIngredients(Beverage beverage) {
        inventory.modifyWater(beverage.getWaterCost() * -1);
        inventory.modifyBeans(beverage.getBeanCost() * -1);
        inventory.modifyMilk(beverage.getMilkCost() * -1);
        inventory.modifyCups(beverage.getCupCost() * -1);
    }

    public void processOrder(int opt) {
        Beverage beverage = pickedBeverage(opt);

        if (beverage.haveIngredients(inventory)) {
            System.out.println("I have enough resources, making you a coffee!");
            deductIngredients(beverage);
            inventory.addMoney(beverage.getPrice());
        } else {
            if (beverage.getWaterCost() > inventory.getWater()) {
                System.out.println("Sorry, not enough water!");
            } else if (beverage.getBeanCost() > inventory.getBeans()) {
                System.out.println("Sorry, not enough coffee beans!");
            } else if (beverage.getMilkCost() > inventory.getMilk()) {
                System.out.println("Sorry, not enough milk!");
            } else if (beverage.getCupCost() > inventory.getCups()) {
                System.out.println("Sorry, not enough disposable cups!");
            }
        }
    }

    public static void main(String[] args) {
        CoffeeMachine machine = new CoffeeMachine();
        inventory.modifyWater(400); // Set starting values
        inventory.modifyBeans(120);
        inventory.modifyMilk(540);
        inventory.modifyCups(9);
        inventory.addMoney(550);

        machine.prompt();
        Scanner scanner = new Scanner(System.in);
        while (machine.programRunning) {
            if (scanner.hasNext()) {
                machine.processCommand(scanner.next());
            }
        }
    }
}
