package com.kb.wallet.ticket.exception;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;

public class TicketException extends CustomException {

  public TicketException(ErrorCode errorCode) {
    super(errorCode);
  }

  public TicketException(ErrorCode errorCode, String customMessage) {
    super(errorCode, customMessage);
  }
}