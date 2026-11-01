public class Main {
    public static void main(String[] args) {
        BankAccount alice = new BankAccount("Alice", new Balance(100_000, Currency.RUB), 50_000);
        BankAccount bob = new BankAccount("Bob", new Balance(5_000, Currency.USD), 2_000);

        System.out.println("Start\n");
        System.out.println(alice);
        System.out.println(bob);

        System.out.println("\nDeposit and withdraw\n");
        alice.deposit(25_000);
        alice.withdraw(10_000);
        System.out.println(alice);

        System.out.println("\ntransfer with conversation\n");
        alice.transfer(bob, 48_000);
        System.out.println(alice);
        System.out.println(bob);
        bob.transfer(alice, 1_000);
        System.out.println(alice);
        System.out.println(bob);

        System.out.println("\nBalance Alice in dollars: " + alice.formatBalanceIn(Currency.USD));
        System.out.println("Balance Alice in euros: " + alice.formatBalanceIn(Currency.EUR));

        System.out.println("\nIncorrect operation\n");
        BankAccount charlie = new BankAccount("Charlie", new Balance(1_000, Currency.RUB));

        try {
            alice.deposit(-5);
        } catch (RuntimeException e) {
            printError("negative sum", e);
        }
        try {
            alice.deposit(0);
        } catch (RuntimeException e) {
            printError("null sum", e);
        }
        try {
            alice.withdraw(60_000);
        } catch (RuntimeException e) {
            printError("bigger than limit", e);
        }
        try {
            charlie.withdraw(5_000);
        } catch (RuntimeException e) {
            printError("bigger than balance", e);
        }
        try {
            alice.transfer(alice, 100);
        } catch (RuntimeException e) {
            printError("transfer to yourself", e);
        }
        try {
            alice.transfer(null, 100);
        } catch (RuntimeException e) {
            printError("transfer to null", e);
        }
        try {
            alice.transfer(bob, 1);
        } catch (RuntimeException e) {
            printError("1 kopeck -> dollars = 0", e);
        }
        try {
            new BankAccount("  ", new Balance());
        } catch (RuntimeException e) {
            printError("empty name", e);
        }
        try {
            new BankAccount("Eve", null);
        } catch (RuntimeException e) {
            printError("balance = null", e);
        }

        System.out.println("\nBlock\n");
        bob.block();
        try {
            alice.transfer(bob, 100);
        } catch (RuntimeException e) {
            printError("transfer to blocked", e);
        }
        try {
            bob.withdraw(100);
        } catch (RuntimeException e) {
            printError("withdraw from blocked", e);
        }
        bob.unblock();

        System.out.println("\nResults\n");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println(charlie);
    }

    private static void printError(String description, RuntimeException e) {
        System.out.println(description + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}