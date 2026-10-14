package mylab.bank.entity;

import mylab.bank.exception.InsufficientBalanceException;

/**
 * 모든 계좌의 기본이 되는 추상 클래스.
 * 계좌번호, 소유자 이름, 잔액을 관리하고 입금/출금 기능을 제공한다.
 */
public abstract class Account {

    private String accountNumber; // 계좌번호
    private String ownerName;     // 계좌 소유자 이름
    private double balance;       // 현재 잔액

    public Account(String accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /** 입금 */
    public void deposit(double amount) {
        balance += amount;
        System.out.println(amount + "원이 입금되었습니다. 현재 잔액: " + balance + "원");
    }

    /**
     * 출금. 잔액이 부족하면 InsufficientBalanceException 을 발생시킨다.
     * 하위 클래스에서 추가 제약(출금 한도 등)을 위해 재정의할 수 있다.
     */
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("잔액이 부족합니다. 현재 잔액: " + balance + "원");
        }
        balance -= amount;
        System.out.println(amount + "원이 출금되었습니다. 현재 잔액: " + balance + "원");
    }

    @Override
    public String toString() {
        return "계좌번호: " + accountNumber + ", 소유자: " + ownerName + ", 잔액: " + balance + "원";
    }
}
