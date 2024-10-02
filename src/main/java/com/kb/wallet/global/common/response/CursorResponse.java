package com.kb.wallet.global.common.response;

import java.util.List;
import lombok.Getter;

@Getter
public class CursorResponse<T> {

  private List<T> data;         // 조회된 데이터 리스트
  private Long nextCursor;      // 다음 페이지를 요청하기 위한 커서 값

  public CursorResponse(List<T> data, Long nextCursor) {
    this.data = data;
    this.nextCursor = nextCursor;
  }
}
