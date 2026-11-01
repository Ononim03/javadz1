public final class BankAccount {

    public static final long NO_LIMIT = Long.MAX_VALUE;

    private final String owner;
    private Balance balance;
    private long withdrawalLimit;
    private boolean blocked;

    public BankAccount(String owner, Balance balance, long withdrawalLimit) {
        if (owner == null || owner.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner must not be empty");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Balance must not be null");
        }
        if (withdrawalLimit <= 0) {
            throw new IllegalArgumentException("Withdrawal limit must be positive");
        }
        this.owner = owner.trim();
        this.balance = balance;
        this.withdrawalLimit = withdrawalLimit;
        this.blocked = false;
    }

    public BankAccount(String owner, Balance balance) {
        this(owner, balance, NO_LIMIT);
    }


    public void deposit(long amount) {
        checkNotBlocked();
        checkPositive(amount);
        balance = balance.add(amount);
    }

    public void withdraw(long amount) {
        checkNotBlocked();
        checkPositive(amount);
        checkLimit(amount);
        balance = balance.subtract(amount);
    }

    public void transfer(BankAccount to, long amount) {
        if (to == null) {
            throw new IllegalArgumentException("Target account must not be null");
        }
        if (to == this) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        checkNotBlocked();
        to.checkNotBlocked();
        checkPositive(amount);
        checkLimit(amount);

        Balance credit = new Balance(amount, balance.getCurrency()).convert_to(to.balance.getCurrency());
        long creditAmount = credit.getAmount();
        if (creditAmount == 0) {
            throw new IllegalArgumentException("Amount is too small to convert");
        }

        Balance newFrom = balance.subtract(amount);
        Balance newTo = to.balance.add(creditAmount);
        balance = newFrom;
        to.balance = newTo;
    }


    public void setWithdrawalLimit(long withdrawalLimit) {
        if (withdrawalLimit <= 0) {
            throw new IllegalArgumentException("Withdrawal limit must be positive");
        }
        this.withdrawalLimit = withdrawalLimit;
    }

    public void block() {
        blocked = true;
    }

    public void unblock() {
        blocked = false;
    }


    public String getOwner() {
        return owner;
    }

    public Balance getBalance() {
        return balance;
    }

    public long getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String formatBalanceIn(Currency currency) {
        Balance converted = balance.convert_to(currency);
        return format(converted.getAmount(), converted.getCurrency());
    }

    private void checkNotBlocked() {
        if (blocked) {
            throw new IllegalStateException("Account of " + owner + " is blocked");
        }
    }

    private static void checkPositive(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void checkLimit(long amount) {
        if (amount > withdrawalLimit) {
            throw new IllegalArgumentException("Withdrawal limit exceeded: limit is "
                    + format(withdrawalLimit, balance.getCurrency()));
        }
    }

    private static String format(long amount, Currency currency) {
        return String.format("%d.%02d %s", amount / 100, amount % 100, currency.getSymbol());
    }

    @Override
    public String toString() {
        String limit = withdrawalLimit == NO_LIMIT ? "none" : format(withdrawalLimit, balance.getCurrency());
        return "BankAccount{owner=" + owner
                + ", balance=" + format((long) balance.getAmount(), balance.getCurrency())
                + ", limit=" + limit
                + ", blocked=" + blocked + "}";
    }
}