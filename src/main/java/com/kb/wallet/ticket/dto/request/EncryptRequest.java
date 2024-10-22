package com.kb.wallet.ticket.dto.request;

import static com.kb.wallet.global.common.status.ErrorCode.ENCRYPTION_ERROR;

import com.kb.wallet.global.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EncryptRequest {
  private String deviceId;

  public void validateDeviceId() {
    // TODO 현재는 temp로 가설정.. 이후 finger print 등으로 변경 예정
    if(!this.deviceId.equals("temp")) {
      throw new CustomException(ENCRYPTION_ERROR, "해당 기기로 저장된 티켓 내역이 없습니다.");
    }
  }
}
