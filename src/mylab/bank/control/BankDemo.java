package mylab.bank.control;

import mylab.bank.entity.Bank;
import mylab.bank.entity.SavingsAccount;
import mylab.bank.exception.AccountNotFoundException;
import mylab.bank.exception.InsufficientBalanceException;

/**
 * [실습3] 은행 계좌 관리 시스템 테스트 클래스.
 * 계좌 생성, 입금/출금, 이자 적용, 계좌 이체, 예외 처리를 테스트한다.
 */
public class BankDemo {

    public static void main(String[] args) {
        Bank bank = new Bank();

        System.out.println("=== 계좌 생성 ===");
        SavingsAccount hong = bank.createSavingsAccount("홍길동", 10000, 3.0);
        bank.createCheckingAccount("김철수", 20000, 5000);
        bank.createSavingsAccount("이영희", 30000, 2.0);

        bank.printAllAccounts();

        try {
            System.out.println("=== 입금/출금 테스트 ===");
            bank.deposit("AC1000", 5000);
            bank.withdraw("AC1001", 3000);

            System.out.println("=== 이자 적용 테스트 ===");
            hong.applyInterest();

            System.out.println("=== 계좌 이체 테스트 ===");
            bank.transfer("AC1002", "AC1001", 5000);
        } catch (AccountNotFoundException | InsufficientBalanceException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        bank.printAllAccounts();

        // 출금 한도 초과 예외
        try {
            bank.withdraw("AC1001", 6000);
        } catch (AccountNotFoundException | InsufficientBalanceException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        // 이체 중 출금 한도 초과 예외
        try {
            bank.transfer("AC1001", "AC1000", 7000);
        } catch (AccountNotFoundException | InsufficientBalanceException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }

        // 존재하지 않는 계좌 조회 예외
        try {
            bank.findAccount("AC9999");
        } catch (AccountNotFoundException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }
    }
}
