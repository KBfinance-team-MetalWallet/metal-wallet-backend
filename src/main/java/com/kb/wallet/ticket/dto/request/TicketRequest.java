package com.kb.wallet.ticket.dto.request;

import java.util.List;
import javax.validation.constraints.*;
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
  @Size(min = 1, message = "최소 1개의 좌석 ID가 필요합니다.")
  private List<Long> seatId;

  @NotBlank(message = "디바이스 ID는 필수입니다.")
  private String deviceId;
}
