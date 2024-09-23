package com.kb.wallet.seat.constant;

import lombok.Getter;

@Getter
public enum Grade {
  R(0),
  S(1),
  A(2);

  private final int value;

  Grade(int value) {
    this.value = value;
  }

  public static Grade fromValue(int value) {
    for (Grade grade : Grade.values()) {
      if (grade.getValue() == value) {
        return grade;
      }
    }
    return null;
  }
}
