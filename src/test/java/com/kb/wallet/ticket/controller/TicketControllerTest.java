package com.kb.wallet.ticket.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.global.common.response.CursorResponse;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.dto.request.EncryptRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.service.TicketService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;

class TicketControllerTest {

  @InjectMocks
  private TicketController ticketController;

  @Mock
  private TicketService ticketService;

  private Member member;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    member = new Member();
    member.setEmail("test@example.com");
  }

  @Test
  @DisplayName("티켓 생성 성공")
  void createTicket_Success() {
    TicketRequest ticketRequest = new TicketRequest();
    TicketResponse ticketResponse = TicketResponse.builder().id(1L).build();
    List<TicketResponse> ticketResponses = Collections.singletonList(ticketResponse);

    // given
    when(ticketService.bookTicket(member.getEmail(), ticketRequest)).thenReturn(ticketResponses);

    // when
    ApiResponse<List<TicketResponse>> response = ticketController.createTicket(member,
        ticketRequest);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getResult()).isEqualTo(ticketResponses);
  }

  @Test
  @DisplayName("사용자 티켓 조회 성공")
  void getUserTickets_Success() {
    Long cursor = null;
    int size = 10;
    TicketListResponse ticketResponse = TicketListResponse.builder().id(1L)
        .ticketStatus(TicketStatus.BOOKED).build();
    List<TicketListResponse> responses = Collections.singletonList(ticketResponse);
    Long nextCursor = 1L;

    // given
    when(ticketService.getTickets(member.getEmail(), null, 0, size, cursor)).thenReturn(responses);

    CursorResponse<TicketListResponse> cursorResponse = new CursorResponse<>(responses, nextCursor);
    ApiResponse<CursorResponse<TicketListResponse>> apiResponse = ApiResponse.ok(cursorResponse);

    // when
    ApiResponse<CursorResponse<TicketListResponse>> result = ticketController.getUserTickets(member,
        cursor, size, null);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getResult()).isNotNull();
    assertThat(result.getResult().getData()).isEqualTo(responses);
    assertThat(result.getResult().getNextCursor()).isEqualTo(nextCursor);
  }


  @Test
  @DisplayName("암호화 데이터 생성 성공")
  void generateEncryptData_Success() {
    Long ticketId = 1L;
    EncryptRequest encryptRequest = new EncryptRequest();
    ProposedEncryptResponse encryptResponse = new ProposedEncryptResponse();

    // given
    when(ticketService.provideEncryptElement(ticketId, member.getEmail(),
        encryptRequest)).thenReturn(encryptResponse);

    // when
    ResponseEntity<ProposedEncryptResponse> response = ticketController.generateEncryptData(member,
        ticketId, encryptRequest);

    // then
    assertThat(response.getBody()).isEqualTo(encryptResponse);
  }

  @Test
  @DisplayName("티켓 사용 업데이트 성공")
  void updateTicket_Success() {
    VerifyTicketRequest request = new VerifyTicketRequest();

    // when
    ResponseEntity<Void> response = ticketController.updateTicket(member, request);

    // then
    assertThat(response.getStatusCode()).isEqualTo(ResponseEntity.ok().build().getStatusCode());

    verify(ticketService, times(1)).updateToCheckedStatus(request);

  }

  @Test
  @DisplayName("티켓 취소 성공")
  void cancelTicket_Success() {
    long ticketId = 1L;

    // given: cancelTicket이 정상적으로 동작하도록 설정
    doNothing().when(ticketService).cancelTicket(member.getEmail(), ticketId);

    // when
    ApiResponse<Void> response = ticketController.cancelTicket(member, ticketId);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getResult()).isNull();

    verify(ticketService, times(1)).cancelTicket(member.getEmail(), ticketId);
  }
}
