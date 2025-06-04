public class Balance {
    private int balance;

    public Balance() {
        this.balance = 200;
    }

    public int getBalance() {
        return balance;
    }

    public boolean canDeduct(int amount) {
        return balance >= amount;
    }

    public void deduct(int amount) {
        if (balance >= amount) {
            balance -= amount;
        }
    }

    public void add(int amount) {
        balance += amount;
    }
}