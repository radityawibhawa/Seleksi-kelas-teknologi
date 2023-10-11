package com.tujuhsembilan;

import static com.tujuhsembilan.logic.ConsoleUtil.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tujuhsembilan.logic.ATMLogic;

import data.constant.BankCompany;
import data.model.ATM;
import data.model.Bank;
import data.model.Customer;
import data.model.Transaction;
import data.repository.ATMRepo;
import data.repository.BankRepo;

public class App {
    
    public static void main(String[] args) {
        boolean loop = true;
        while (loop) {
            printClear();
            printDivider();
            int num = 1;
            for (String menu : Arrays.asList(BankCompany.values()).stream()
                    .map(item -> "ATM " + item.getName())
                    .collect(Collectors.toList())) {
                System.out.println(" " + num + ". " + menu);
                num++;
            }
            printDivider('-');
            System.out.println(" 0. EXIT");
            printDivider();

            System.out.print(" > ");
            int selection = in.nextInt() - 1;
            if (selection >= 0 && selection < BankCompany.values().length) {
                new App(BankCompany.getByOrder(selection).getName()).start();
            } else if (selection == -1) {
                loop = false;
            } else {
                System.out.println("Invalid input");
                delay();
            }
        }
    }

    /// --- --- --- --- ---

    final Bank bank;
    final ATM atm;

    public App(String bankName) {
        Bank lBank = null;
        ATM lAtm = null;

        Optional<Bank> qBank = BankRepo.findBankByName(bankName);
        if (qBank.isPresent()) {
            Optional<ATM> qAtm = ATMRepo.findATMByBank(qBank.get());
            if (qAtm.isPresent()) {
                lBank = qBank.get();
                lAtm = qAtm.get();
            }
        }

        this.bank = lBank;
        this.atm = lAtm;
    }
    
    public void start() {
        if (bank != null && atm != null) {
            Customer currentCustomer = ATMLogic.login(bank);
            if (currentCustomer == null) {
                System.out.println("Login failed");
                return;
            }
            Transaction transaction = new Transaction();
            transaction.setCustomer(currentCustomer);
            boolean loop = true;
            while (loop) {
                System.out.println("Pilih operasi yang ingin Anda lakukan:");
                System.out.println("1. Informasi Saldo");
                System.out.println("2. Penarikan Uang");
                System.out.println("3. Top Up Pulsa");
                System.out.println("4. Token Listrik");
                System.out.println("5. Mutasi Rekening");
                System.out.println("6. Deposit Uang");
                System.out.println("0. Keluar");
    
                System.out.print("> ");
                int selection = in.nextInt();
                switch (selection) {
                    case 1:
                        ATMLogic.accountBalanceInformation(currentCustomer);
                        break;
                    case 2:
                        ATMLogic.moneyWithdrawal(currentCustomer, atm);
                        break;
                    case 3:
                        ATMLogic.phoneCreditsTopUp(transaction, atm);
                        break;
                    case 4:
                        ATMLogic.electricityBillsToken(transaction, atm);
                        break;
                    case 5:
                        ATMLogic.accountMutation(currentCustomer, bank, atm);
                        break;
                    case 6:
                        ATMLogic.moneyDeposit(currentCustomer, bank);
                        break;
                    case 0:
                        loop = false;
                        break;
                    default:
                        System.out.println("Pilihan tidak valid");
                }
            }
        } else {
            System.out.println("Cannot find Bank or ATM");
            delay();
        }
    }

}
