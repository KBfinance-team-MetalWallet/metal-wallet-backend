package com.kb.wallet.musical.domain;

import com.kb.wallet.ticket.domain.Schedule;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actor")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String characterName;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

}
