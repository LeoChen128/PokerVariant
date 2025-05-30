public class Balance {

    private int balance;

    public Balance() {
        this.balance = 10000;
    }

    public int getBalance() {
        return balance;
    }

    public void deduct(int amount) {
        if (balance < amount) {
            System.out.println("You don't have enough cash!");
        } else {
            balance -= amount;
        }
    }

   
    public void redChip() {
        deduct(5);
    }

    public void greenChip() {
        deduct(25);
    }

    public void blackChip() {
        deduct(100);
    }
}
