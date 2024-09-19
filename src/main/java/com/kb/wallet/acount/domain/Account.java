package com.kb.wallet.acount.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id")
  private Long memberId;

  @Column(name = "number")
  private String accountNumber;

  @Column()
  private Integer balance;

  @Column(name = "created_at")
  @CreatedDate
  private LocalDateTime createdAt;
}