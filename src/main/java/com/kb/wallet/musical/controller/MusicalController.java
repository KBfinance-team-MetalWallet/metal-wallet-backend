package com.kb.wallet.musical.controller;

import com.kb.wallet.global.common.response.ApiResponse;
import com.kb.wallet.member.domain.Member;
import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.request.MusicalCreationRequest;
import com.kb.wallet.musical.dto.request.MusicalInfoUpdateRequest;
import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.musical.service.MusicalService;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

@Builder
@RestController
@Slf4j
@RequestMapping("/musicals")
public class MusicalController {

    private final MusicalService musicalService;

    @Autowired
    public MusicalController(MusicalService musicalService) {
        this.musicalService = musicalService;
    }

    @GetMapping
    public ResponseEntity<Page<Musical>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        Page<Musical> musicals = musicalService.findAllMusicals(page, size);
        return ResponseEntity.ok(musicals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Musical> findById(@PathVariable(name = "id") Long id) {
        Musical musical = musicalService.findById(id);
        return ResponseEntity.ok(musical);
    }

    @PostMapping
    public ResponseEntity<Musical> createMusical(@RequestBody MusicalCreationRequest request) {
        Musical savedMusical = musicalService.saveMusical(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMusical);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
        musicalService.deleteMusical(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMusicalInfo(
            @PathVariable(name = "id") Long id,
            @RequestBody MusicalInfoUpdateRequest request) {
        /**
         * TODO : Login Authentication 추가 예정
         */
        musicalService.updateMusicalInfo(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/seats-availability")
    public ApiResponse<List<MusicalSeatAvailabilityResponse>> checkSeatAvailability(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "id") Long id,
            @RequestParam("date") String date) {

        List<MusicalSeatAvailabilityResponse> responses = musicalService.checkSeatAvailability(id,
                date);

        return ApiResponse.ok(responses);
    }
}
