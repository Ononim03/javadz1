public final class Balance {

    private final long amount;
    private final Currency currency;

    public Balance(long amount, Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Balance() {
        this(0, Currency.RUB);
    }

    public Balance(long amount) {
        this(amount, Currency.RUB);
    }

    public Balance(Currency currency) {
        this(0, currency);
    }


    public Balance add(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount");
        }
        try {
            return new Balance(Math.addExact(this.amount, delta), this.currency);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Balance overflow");
        }
    }

    public Balance subtract(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Cannot subtract a negative amount");
        }
        if (this.amount < delta) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Balance(Math.subtractExact(amount, delta), this.currency);
    }

    public Balance convert_to(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        if (currency == this.currency) {
            return this;
        }
        try {
            return new Balance(Math.multiplyExact(this.amount, this.currency.getPriceInRublesKopecks()) / currency.getPriceInRublesKopecks(), currency);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Amount is too big");
        }
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
