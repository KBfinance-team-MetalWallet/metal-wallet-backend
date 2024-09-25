package com.kb.wallet.ticket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.TicketDTO;
import com.kb.wallet.ticket.model.TicketQrInfo;
import com.kb.wallet.ticket.repository.TicketMapper;
import com.kb.wallet.ticket.repository.TicketRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.kb.wallet.ticket.dto.response.TicketUsageResponse;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService{

  private final TicketRepository ticketRepository;
  private final TicketMapper ticketMapper;

  @Autowired
  public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
    this.ticketRepository = ticketRepository;
    this.ticketMapper = ticketMapper;
  }

  @Override
  public Ticket saveTicket(Member member, TicketDTO.TicketRequest ticketRequest) {
    // 일정 테이블에서 일정 찾아서 넣어줘야 함.
    // TODO : 임의 member 생성.. 로그인 구현 시 삭제 해야 함
    Member temp = new Member();
    temp.setId(1L);

    // TODO : 뮤지컬이 유효한지 검사

    // TODO : 일정이 유효한지 검사

    // 티켓 엔티티 생성
    Ticket ticket = Ticket.builder()
        .member(temp)
        .ticketStatus(TicketStatus.BOOKED)
        .build();
    ticketRepository.save(ticket);
    return ticket;
  }

  @Override
  public Ticket findTicket(Long memberId, Long ticketId) {
    return ticketRepository.findByIdAndMemberId(memberId, ticketId).orElseThrow(() -> new RuntimeException("해당 id의 티켓이 없습니다."));
  }

  @Override
  public Page<Ticket> findAllUserTicket(Long id, int page, int size) {
    id = 1L; // TODO: 이거 로그인 구현 시 지워야 함
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ticketRepository.findTicketsByMemberId(id, pageable);
  }


  @Override
  @Async("taskExecutor")
  public CompletableFuture<Void> checkTicket(Long memberId, Long ticketId) {
    isTicketAvailable(memberId, ticketId);

    // TODO : GlobalException로 바꿔 주세요.
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    ticket.setTicketStatus(TicketStatus.CHECKED);
    ticketRepository.save(ticket);

    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void deleteTicket(Member member, long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
        .orElseThrow(() -> new RuntimeException());

    checkTicketOwner(ticket, member);
    checkIfTicketIsBooked(ticket);

    // soft delete
    ticket.setTicketStatus(TicketStatus.CANCELED);
    ticketRepository.save(ticket);
  }

  private void checkTicketOwner(Ticket ticket, Member member) {
    if(ticket.getMember().getId() != member.getId()) {
      throw new RuntimeException();
    }
  }

  private void checkIfTicketIsBooked(Ticket ticket) {
    if(ticket.getTicketStatus() != TicketStatus.BOOKED) {
      throw new RuntimeException();
    }
  }

  @Override
  public void isTicketAvailable(Long memberId, Long ticketId) {
    boolean isTicketAvailable = ticketRepository.existsByMemberIdAndIdAndTicketStatus(memberId, ticketId,
        TicketStatus.BOOKED);
    log.info("isTicketAvailable");
    log.info(String.valueOf(isTicketAvailable));
    if(!isTicketAvailable) {
      log.info(String.valueOf(isTicketAvailable) + ": false");
      throw new IllegalArgumentException("The ticket is not available for use.");
    }
  }

  @Override
  public String generateTicketQRCode(Long memberId, Long ticketId) throws IOException, WriterException {
    ObjectMapper objectMapper = new ObjectMapper();
    String qrData = objectMapper.writeValueAsString(new TicketQrInfo(memberId, ticketId));
    int qrImagewidth = 250;
    int qrImageheight = 250;

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    //QR px 정보 저장
    BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, qrImagewidth, qrImageheight);
    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

    // 이미지를 Base64로 인코딩
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(qrImage, "png", baos);
    byte[] qrBytes = baos.toByteArray();
    String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);

    return qrBase64;
  }
}
