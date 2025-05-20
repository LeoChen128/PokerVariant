
public class Balance{

    private int balance;

    public Balance(){
        this.balance = 10000;
    }

    public int getBalance() {
        return balance;
    }

    public void whiteChip(){
        if (balance < 1){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 1;
        }
    }

    public void redChip(){
        if (balance < 5){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 5;
        }
    }

    public void greenChip(){
        if (balance < 25){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 25;
        }
    }

    public void blackChip(){
        if (balance < 100){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 100;
        }
    }

    public void purpleChip(){
        if (balance < 500){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 500;
        }
    }

    public void yellowChip(){
        if (balance < 1000){
            System.out.println("You don't have enough cash!");
        }
        else{
            balance -= 1000;
        }
    }

}

