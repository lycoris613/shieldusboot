package mylab.bank.entity;

/**
 * 저축 계좌. 이자율을 가지며 이자 적용 기능을 제공한다.
 */
public class SavingsAccount extends Account {

    private double interestRate; // 이자율 (단위: %, 예: 3.0 은 3.0%)

    public SavingsAccount(String accountNumber, String ownerName, double balance, double interestRate) {
        super(accountNumber, ownerName, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    /** 현재 잔액에 이자율을 적용하여 이자를 입금한다. */
    public void applyInterest() {
        double interest = getBalance() * interestRate / 100;
        deposit(interest);
        System.out.println("이자 " + interest + "원이 적용되었습니다. 현재 잔액: " + getBalance() + "원");
    }

    @Override
    public String toString() {
        return super.toString() + ", 이자율: " + interestRate + "%";
    }
}
