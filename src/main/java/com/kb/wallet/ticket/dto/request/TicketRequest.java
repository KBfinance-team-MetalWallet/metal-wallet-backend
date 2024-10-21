package com.kb.wallet.ticket.dto.request;

import com.kb.wallet.global.common.status.ErrorCode;
import com.kb.wallet.global.exception.CustomException;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketRequest {

  @NotEmpty(message = "좌석 ID는 필수입니다.")
  private List<Long> seatId;

  @NotEmpty(message = "디바이스 ID는 필수입니다.")
  private String deviceId;
}
