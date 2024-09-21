package com.kb.wallet.global.exception;

public class DatabaseAccessException extends RuntimeException {

  public DatabaseAccessException() {
    super("Database access error occurred.");
  }

  public DatabaseAccessException(String message) {
    super(message);
  }

  public DatabaseAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}