package com.kb.wallet.account.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kb.wallet.account.constant.TransactionType;
import com.kb.wallet.account.domain.TransactionRecord;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionRecordResponse {

  private Long transactionId;
  private Long amount;
  private TransactionType transactionType;
  private Long currentBalance;
  private String vendor;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  public static TransactionRecordResponse toTransactionRecordResponse(
    TransactionRecord transactionRecord) {
    return TransactionRecordResponse.builder()
      .transactionId(transactionRecord.getId())
      .amount(transactionRecord.getAmount())
      .transactionType(transactionRecord.getTransactionType())
      .currentBalance(transactionRecord.getCurrentBalance())
      .vendor(transactionRecord.getVendor())
      .createdAt(transactionRecord.getCreatedAt())
      .build();
  }
}
