package com.kb.wallet.musical.controller;

import com.kb.wallet.musical.domain.Musical;
import com.kb.wallet.musical.dto.MusicalDTO;
//import com.kb.wallet.musical.repository.JpaMusicalRepository;
import com.kb.wallet.musical.service.MusicalService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@Slf4j
@RequestMapping("/musicals")
public class MusicalController {
    private final  MusicalService musicalService;

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
    public ResponseEntity<Musical> findById(@PathVariable(name="id") Long id) {
        Musical musical = musicalService.findById(id);
        return ResponseEntity.ok(musical);
    }

    @PostMapping("/create")
    public ResponseEntity<Musical> createMusical(@RequestBody MusicalDTO.MusicalRequest musicalRequest) {
        Musical savedMusical = musicalService.saveMusical(musicalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMusical);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable (name="id") Long id) {
        musicalService.deleteMusical(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/{title}")
    public ResponseEntity<Void> updateTitle(
        @PathVariable(name = "id") Long id,
        @PathVariable(name = "title") String title) {
        musicalService.updateTitle(id, title);
        return ResponseEntity.ok().build();
    }


}
