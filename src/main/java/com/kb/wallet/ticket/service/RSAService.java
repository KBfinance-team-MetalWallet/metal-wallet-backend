package com.kb.wallet.ticket.service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSAService {
  PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException;

  PrivateKey getPrivateKey();

  String decrypt(String encryptedTicketInfo);
}
