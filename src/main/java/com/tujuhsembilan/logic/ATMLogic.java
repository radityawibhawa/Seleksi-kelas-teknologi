package com.tujuhsembilan.logic;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import data.constant.BankCompany;
import data.constant.TransactionType;
import data.model.ATM;
import data.model.Bank;
import data.model.Customer;
import data.model.Transaction;
import data.repository.BankRepo;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ATMLogic {
  static int attempts = 0; // Declare attempts here
  public static boolean isWithdrawalAllowed(BigDecimal balance, BigDecimal withdrawalAmount) {
    BigDecimal minimumResidualBalance = BigDecimal.valueOf(10000);
    return balance.compareTo(withdrawalAmount.add(minimumResidualBalance)) >= 0;
  }
  public static Customer login(Bank bank) {
      Scanner scanner = new Scanner(System.in);
  
      while (attempts < 3) {
          System.out.print("Masukkan nomor rekening: ");
          String account = scanner.nextLine();
  
          // Cek apakah input adalah nomor
          if (!account.matches("\\d+")) {
              System.out.println("Input harus berupa nomor!");
              continue;
          }
  
          System.out.print("Masukkan pin: ");
          String pin = scanner.nextLine();
  
          // Cek apakah input adalah nomor
          if (!pin.matches("\\d+")) {
              System.out.println("Input harus berupa nomor!");
              continue;
          }
  
          Optional<Customer> optCustomer = bank.findCustomerByAccount(account);
  
          if (optCustomer.isPresent() && optCustomer.get().getPin().equals(pin)) {
              System.out.println("Login berhasil!");
              return optCustomer.get();
          } else {
              System.out.println("Nomor rekening atau pin salah!");
              attempts++;
              if (attempts >= 3) {
                  System.out.println("Anda telah mencoba login 3 kali. Akun Anda telah diblokir.");
                  return null;
              }
          }
      }
      return null;
  }

  public static void accountBalanceInformation(Customer customer) {
      System.out.println("Saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
  }

  public static void moneyWithdrawal(Customer customer, ATM atm) {
    Scanner scanner = new Scanner(System.in);
    BigDecimal amount;
    BigDecimal denomination;

    while (true) {
        System.out.println("Masukkan nominal uang yang ingin Anda tarik:");
        System.out.print("> ");
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("Input harus berupa angka. Silakan coba lagi.");
            scanner.next(); // discard non-numeric input
        }
        amount = scanner.nextBigDecimal();

        // Add constraint for maximum amount per transaction
        if (amount.compareTo(BigDecimal.valueOf(2500000)) > 0) {
            System.out.println("Jumlah maksimum per transaksi adalah Rp2.500.000,00");
            continue;
        }

        System.out.println("Pilih pecahan uang yang ingin Anda tarik:");
        System.out.println("1. Rp10.000,00");
        System.out.println("2. Rp20.000,00");
        System.out.println("3. Rp50.000,00");
        System.out.println("4. Rp100.000,00");
        System.out.print("> ");
        int selection = scanner.nextInt();

        switch (selection) {
            case 1:
                denomination = BigDecimal.valueOf(10000);
                break;
            case 2:
                denomination = BigDecimal.valueOf(20000);
                break;
            case 3:
                denomination = BigDecimal.valueOf(50000);
                break;
            case 4:
                denomination = BigDecimal.valueOf(100000);
                break;
            default:
                System.out.println("Pilihan tidak valid!");
                continue;
        }

        // Check if the amount is divisible by the chosen denomination
        if (amount.remainder(denomination).compareTo(BigDecimal.ZERO) != 0) {
            System.out.println("Nominal penarikan harus kelipatan dari pecahan yang dipilih.");
            continue;
        }

        break;
    }

    BigDecimal balance = customer.getBalance();
    if (!isWithdrawalAllowed(balance, amount)) {
        System.out.println("Saldo Anda tidak mencukupi untuk melakukan penarikan ini.");
    } else {
        customer.setBalance(balance.subtract(amount));
        atm.subtract(amount); 
        System.out.println("Penarikan berhasil! Sisa saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
    }
  }

  public static void phoneCreditsTopUp(Transaction transaction, ATM atm) {
    Scanner scanner = new Scanner(System.in);
    String phoneNumber;

    while (true) {
        System.out.print("Masukkan nomor telepon yang ingin diisi pulsa: ");
        phoneNumber = scanner.nextLine();

        
        if (!phoneNumber.matches("\\d{3,15}")) {
            System.out.println("Nomor telepon harus antara 3 dan 15 digit!");
        } else {
            break;
        }
    }

    int selection;
    BigDecimal topUpAmount;
    while (true) {
        System.out.println("Pilih nominal pulsa:");
        System.out.println("1. Rp50.000,00");
        System.out.println("2. Rp100.000,00");
        System.out.println("3. Rp200.000,00");
        System.out.println("4. Rp500.000,00");
        System.out.print("> ");
        selection = scanner.nextInt();
        
        switch (selection) {
            case 1:
                topUpAmount = BigDecimal.valueOf(50000);
                break;
            case 2:
                topUpAmount = BigDecimal.valueOf(100000);
                break;
            case 3:
                topUpAmount = BigDecimal.valueOf(200000);
                break;
            case 4:
                topUpAmount = BigDecimal.valueOf(500000);
                break;
            default:
                System.out.println("Pilihan tidak valid");
                continue;
        }
        break;
    }

    Customer customer = transaction.getCustomer();
    BigDecimal balance = customer.getBalance();
    if (!isWithdrawalAllowed(balance, topUpAmount)) {
        System.out.println("Saldo Anda tidak mencukupi untuk pengisian pulsa ini.");
    } else {
        customer.setBalance(balance.subtract(topUpAmount));
        atm.subtract(topUpAmount); 
        transaction.setExpense(topUpAmount);
        transaction.setType(TransactionType.TOP_UP);
        System.out.println("Pengisian pulsa berhasil! Nomor telepon: " + phoneNumber + ", Jumlah pulsa: " + topUpAmount + ", Sisa saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
    }
  }

  public static void electricityBillsToken(Transaction transaction, ATM atm) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Masukkan nomor tagihan listrik: ");
    String billNumber = scanner.nextLine();

    int selection;
    BigDecimal tokenAmount;
    while (true) {
        System.out.println("Pilih nominal token:");
        System.out.println("1. Rp50.000,00");
        System.out.println("2. Rp100.000,00");
        System.out.println("3. Rp200.000,00");
        System.out.println("4. Rp500.000,00");
        System.out.print("> ");
        selection = scanner.nextInt();
        
        switch (selection) {
            case 1:
                tokenAmount = BigDecimal.valueOf(50000);
                break;
            case 2:
                tokenAmount = BigDecimal.valueOf(100000);
                break;
            case 3:
                tokenAmount = BigDecimal.valueOf(200000);
                break;
            case 4:
                tokenAmount = BigDecimal.valueOf(500000);
                break;
            default:
                System.out.println("Pilihan tidak valid");
                continue;
        }
        break;
    }

    Customer customer = transaction.getCustomer();
    BigDecimal balance = customer.getBalance();
    if (!isWithdrawalAllowed(balance, tokenAmount)) {
        System.out.println("Saldo Anda tidak mencukupi untuk pembelian token ini.");
    } else {
        customer.setBalance(balance.subtract(tokenAmount));
        atm.subtract(tokenAmount); 
        transaction.setExpense(tokenAmount);

        
        String uuid = UUID.randomUUID().toString();
        long epochMillis = System.currentTimeMillis();
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(Long.toString(epochMillis).getBytes(StandardCharsets.UTF_8));
            String sha256 = String.format("%064x", new BigInteger(1, hash)).substring(0, 8);
            
            String token = uuid + "_" + sha256 + "_" + tokenAmount;
            System.out.println("Pembelian token berhasil! Token: " + token + ", Nomor tagihan: " + billNumber + ", Jumlah token: " + tokenAmount + ", Sisa saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  }

  public static void accountMutation(Customer customer, Bank bank, ATM atm) {
    Scanner scanner = new Scanner(System.in);
    String targetBankName;
    String targetAccount;
    BigDecimal amount;

    while (true) {
        System.out.print("Masukkan nama bank tujuan (atau 0 untuk kembali ke menu utama): ");
        targetBankName = scanner.nextLine();
        if ("0".equals(targetBankName)) {
            return; 
        }
        Optional<Bank> targetBank = BankRepo.findBankByName(targetBankName);
        if (!targetBank.isPresent()) {
            System.out.println("Bank tujuan tidak ditemukan.");
            continue;
        }

        System.out.print("Masukkan nomor rekening tujuan (atau 0 untuk kembali ke menu utama): ");
        targetAccount = scanner.nextLine();
        if ("0".equals(targetAccount)) {
            return; 
        }
        Optional<Customer> targetCustomer = targetBank.get().findCustomerByAccount(targetAccount);
        if (!targetCustomer.isPresent()) {
            System.out.println("Nomor rekening tujuan tidak ditemukan.");
            continue;
        }

        System.out.print("Masukkan jumlah yang ingin ditransfer (atau 0 untuk kembali ke menu utama): ");
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("Input harus berupa angka. Silakan coba lagi.");
            scanner.next();
        }
        amount = scanner.nextBigDecimal();
        if (BigDecimal.ZERO.equals(amount)) {
            return; 
        }

        BigDecimal balance = customer.getBalance();
        BigDecimal totalAmount = amount.add(BigDecimal.valueOf(2500)); 
        if (!isWithdrawalAllowed(balance, totalAmount)) {
            System.out.println("Saldo Anda tidak mencukupi untuk melakukan transfer ini.");
            continue;
        } else {
            customer.setBalance(balance.subtract(totalAmount));
            atm.subtract(BigDecimal.valueOf(2500)); 
            targetCustomer.get().add(amount);
            System.out.println("Transfer berhasil! Bank tujuan: " + targetBank.get().getName() + ", Nomor rekening tujuan: " + targetAccount + ", Jumlah yang ditransfer: " + CurrencyUtil.formatRupiah(amount) + ", Biaya transfer: Rp2.500,00, Sisa saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
            break;
        }
      } 
  }

  public static void moneyDeposit(Customer customer, Bank bank) {
    if (!bank.getName().equals(BankCompany.BNI.getName()) && !bank.getName().equals(BankCompany.MANDIRI.getName())) {
        System.out.println("Fitur ini hanya tersedia untuk pengguna bank BNI dan Mandiri.");
        return;
    }

    Scanner scanner = new Scanner(System.in);
    BigDecimal amount;

    while (true) {
        System.out.print("Masukkan jumlah uang yang ingin Anda setor: ");
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("Input harus berupa angka. Silakan coba lagi.");
            System.out.print("Masukkan jumlah uang yang ingin Anda setor: ");
            scanner.next(); // discard non-numeric input
        }
        amount = scanner.nextBigDecimal();

        if (amount.compareTo(BigDecimal.valueOf(2500000)) > 0) {
            System.out.println("Jumlah maksimum per transaksi adalah Rp2.500.000,00");
            continue;
        }

        if (amount.remainder(BigDecimal.valueOf(10000)).compareTo(BigDecimal.ZERO) != 0) {
            System.out.println("Jumlah uang harus kelipatan 10.000!");
        } else {
            break;
        }
    }

    customer.add(amount);
    System.out.println("Penyetoran berhasil! Saldo Anda saat ini adalah: " + CurrencyUtil.formatRupiah(customer.getBalance()));
}

}
