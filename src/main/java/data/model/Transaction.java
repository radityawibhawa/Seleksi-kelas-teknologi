package data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import data.constant.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
public class Transaction {
  private String id;

  private LocalDateTime timestamp;

  private Customer customer;
  private TransactionType type;

  private BigDecimal expense;

  public void setExpense(BigDecimal expense) {
    this.expense = expense;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public void setCustomer(Customer customer){
    this.customer=customer;
  }
}