package com.kb.wallet.ticket.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.jwt.TokenProvider;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.member.service.MemberService;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.SignedTicketRequest;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.QrCreationResponse;
import com.kb.wallet.ticket.dto.response.SignedTicketResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import com.kb.wallet.ticket.service.TicketService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

  private final TicketService ticketService;
  private final MemberService memberService;
  @Lazy
  private final TokenProvider tokenProvider;

  @PostMapping
  public ApiResponse<List<TicketResponse>> createTicket(
    @AuthenticationPrincipal Member member,
    @RequestBody TicketRequest ticketRequest) {
    List<TicketResponse> tickets = ticketService.saveTicket(member.getEmail(), ticketRequest
    );
    return ApiResponse.created(tickets);
  }

  @PostMapping("/sign")
  public ResponseEntity<SignedTicketResponse> signTicket(@RequestBody SignedTicketRequest request)
    throws Exception {
    SignedTicketResponse signedTicket = ticketService.signTicket(request.getTicketId());
    return ResponseEntity.ok(signedTicket);
  }

  @GetMapping
  public ApiResponse<Page<TicketResponse>> getUserTickets(
    @AuthenticationPrincipal Member member,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size) {
    Page<TicketResponse> tickets = ticketService.findAllBookedTickets(member.getEmail(), page,
      size);
    return ApiResponse.ok(tickets);
  }

  @PostMapping("/verify")
  public ApiResponse<Boolean> verifyTicket(
    @AuthenticationPrincipal Member member,
    @RequestBody VerifyTicketRequest request)
    throws Exception {
    boolean isValid = ticketService.verifyTicketSignature(request.getTicket(),
      request.getSignature(), request.getDeviceId());
    return ApiResponse.ok(isValid);
  }

  @GetMapping("/{ticketId}")
  public ApiResponse<TicketResponse> getTicket(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId) {
    TicketResponse response = ticketService.findTicket(member.getEmail(), ticketId);
    System.out.println("**********************" + "ticketId = " + ticketId);
    return ApiResponse.ok(response);
  }

  @PostMapping("encrypt/{ticketId}")
  public ResponseEntity<QrCreationResponse> generateEncryptData(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId,
    @RequestParam String deviceID) throws Exception {
    QrCreationResponse response = ticketService.generateQRCodeData(member.getEmail(), ticketId,
      deviceID);
    //TODO: byte[] -> String으로 변환할 것
    //  String encryptedData = util.encrypt()
    //TODO: qr 생성은 클라이언트에서 처리하도록 변경할 것
    //TODO: qrCreationResponse로 반환해야함 = qrCreationResponse
    //  이 DTO에는 token, qrBytes, secord값이 담김
    return ResponseEntity.ok(response);
  }


  //  @PutMapping("/use")
////  @PreAuthorize("hasRole('ADMIN')")
//  // 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
//  public ResponseEntity<DecryptionResponse> updateTicket(
//      @AuthenticationPrincipal Member member,
//      @RequestBody DecryptionRequest decryptionRequest) throws Exception {
//    //TODO: QrCreationResponse qrCreationResponse 를 request로 받는다
//    //  복호화
//    //  qr 해서 받는 데이터 token, qrBytes, second
//    //  qrBytes 디코딩 -> 예약자의 memberId, 티켓 ID
//    //TODO: 동시성 처리
//    Ticket ticket = ticketService.findTicketById(2L);
//
//    if (ticketService.isTicketAvailable(2L, TicketResponse.toTicketResponse(ticket))) {
//      ticketService.updateStatusChecked(ticket);
//    }
//    DecryptionResponse decryptionResponse = ticketService.useTicket(member, decryptionRequest);
//    return ResponseEntity.ok(decryptionResponse);
//  }
  @PutMapping("use/{ticketId}")
// TODO : @PreAuthorize("hasRole('ADMIN')")
// TODO : 시큐리티 필터 없어서 아직 여긴 role에 따른 인가 구분 못함
  public ResponseEntity<TicketResponse> updateTicket(
    @AuthenticationPrincipal Member member,
    @PathVariable(name = "ticketId") Long ticketId
  ) throws Exception {
    System.out.println(" ************************");
    //TODO: QrCreationResponse qrCreationResponse 를 request로 받는다
    //  복호화
    //  qr 해서 받는 데이터 token, encryptedData, second
    //  encryptedData 디코딩 -> 예약자의 memberId, 티켓 ID
    //TODO: 동시성 처리

    TicketResponse updatedTicket = ticketService.useTicket(member, ticketId);
    Ticket ticket = ticketService.findTicketById(member.getId());
    //TODO: isTicketAvailable paran 변경, TicketUseValidationResponse 변경
    if (ticketService.isTicketAvailable(TicketResponse.toTicketResponse(ticket))) {
      ticketService.updateStatusChecked(ticket);
    }

    return ResponseEntity.ok(updatedTicket);
  }

  @DeleteMapping("/{ticketId}")
  public ApiResponse<Void> cancelTicket(
    @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.cancelTicket(member.getEmail(), ticketId);
    return ApiResponse.ok();
  }

  @GetMapping("/exchange")
  public ResponseEntity<Page<TicketExchangeResponse>> getUserExchangedTickets(
    @AuthenticationPrincipal Member member,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    Page<TicketExchangeResponse> userExchangedTickets = ticketService.getUserExchangedTickets(
      member, page, size);
    return ResponseEntity.ok(userExchangedTickets);
  }

  @PostMapping("/exchange")
  public ResponseEntity<TicketExchangeResponse> createTicketExchange(
    @AuthenticationPrincipal Member member,
    @RequestBody TicketExchangeRequest exchangeRequest) {
    TicketExchangeResponse ticketExchange = ticketService.createTicketExchange(member,
      exchangeRequest);
    return ResponseEntity.ok(ticketExchange);
  }

  @DeleteMapping("/exchange/{ticketId}")
  public ApiResponse<Void> cancelTicketExchange(
    @AuthenticationPrincipal Member member, @PathVariable(name = "ticketId") long ticketId) {
    ticketService.cancelTicketExchange(member.getEmail(), ticketId);
    return ApiResponse.ok();
  }
}
