package com.kb.wallet.ticket.service;

import com.kb.wallet.member.domain.Member;
import com.kb.wallet.ticket.constant.TicketStatus;
import com.kb.wallet.ticket.domain.Ticket;
import com.kb.wallet.ticket.dto.request.TicketExchangeRequest;
import com.kb.wallet.ticket.dto.request.TicketRequest;
import com.kb.wallet.ticket.dto.request.VerifyTicketRequest;
import com.kb.wallet.ticket.dto.response.ProposedEncryptResponse;
import com.kb.wallet.ticket.dto.response.TicketExchangeResponse;
import com.kb.wallet.ticket.dto.response.TicketListResponse;
import com.kb.wallet.ticket.dto.response.TicketResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface TicketService {

  TicketResponse findTicket(String email, Long ticketId);

  Ticket findTicketById(Long id);

  Page<TicketListResponse> findAllBookedTickets(String email, TicketStatus ticketStatus, int page,
    int size);

  List<TicketResponse> saveTicket(String email, TicketRequest ticketRequest);

  void cancelTicket(String email, Long ticketId);


  void updateStatusChecked(Ticket ticket);

  TicketExchangeResponse createTicketExchange(Member member,
    TicketExchangeRequest exchangeRequest);

  void cancelTicketExchange(String email, Long ticketId);

  Page<TicketExchangeResponse> getUserExchangedTickets(Member member, int page, int size);

  void updateToCheckedStatus(VerifyTicketRequest request);

  ProposedEncryptResponse provideEncryptElement(Long ticketId, String email);

  interface RSAService {
    PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException;

    PrivateKey getPrivateKey();

    String decrypt(String encryptedTicketInfo, PrivateKey privateKey);
  }

  @Service
  @Slf4j
  class RSAServiceImpl implements RSAService {

    private static final String ALGORITHM = "RSA";
    private static final String PROVIDER = "BC";
    private static final String OAEP_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private KeyPair keyPair;

    static {
      try {
        if (Security.getProvider(PROVIDER) == null) {
          Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
      } catch (Exception e) {
        log.error("Failed to add Bouncy Castle provider", e);
      }
    }

    @PostConstruct
    public void init() {
      try {
        if (this.keyPair == null) {
          this.keyPair = generateKeyPair();
        }
      } catch (Exception e) {
        log.error("Failed to generate key pair", e);
        throw new RuntimeException("Failed to initialize RSA service", e);
      }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
      KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
      keyPairGen.initialize(2048);
      return keyPairGen.generateKeyPair();
    }

    @Override
    public PublicKey getPublicKey() {
      return keyPair.getPublic();
    }

    @Override
    public PrivateKey getPrivateKey() {
      return keyPair.getPrivate();
    }

    @Override
    public String decrypt(String base64EncryptedData, PrivateKey privateKey) {
      try {
        Cipher cipher = Cipher.getInstance(OAEP_PADDING, PROVIDER);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            new MGF1ParameterSpec("SHA-256"),
            PSource.PSpecified.DEFAULT
        );
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64EncryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new RuntimeException("복호화 실패", e);
      }
    }
  }
}