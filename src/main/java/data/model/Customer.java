package data.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  private String id;
  private BigDecimal dailyTransactionAmount = BigDecimal.ZERO;
  private String account;
  private String pin;

  private String fullName;

  private BigDecimal balance;

  private Integer invalidTries;

  /**
   * Use this function to add balance to Customer
   *
   * @param amount
   */
  public void add(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
  public BigDecimal getDailyTransactionAmount() {
    return dailyTransactionAmount;
  }

  public void addTransactionAmount(BigDecimal amount) {
      this.dailyTransactionAmount = this.dailyTransactionAmount.add(amount);
  }

  public void resetDailyTransactionAmount() {
      this.dailyTransactionAmount = BigDecimal.ZERO;
  }
}
