package com.kb.wallet.ticket.dto.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketRequest {
  @NotBlank(message = "좌석 ID는 필수입니다.")
  private List<Long> seatId;

  @NotBlank(message = "디바이스 ID는 필수입니다.")
  private String deviceId;
}
