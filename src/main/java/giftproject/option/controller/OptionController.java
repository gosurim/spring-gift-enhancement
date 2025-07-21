package giftproject.option.controller;

import giftproject.option.dto.OptionRequestDto;
import giftproject.option.dto.OptionResponseDto;
import giftproject.option.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/products/{productId}/options")
    public ResponseEntity<OptionResponseDto> create(
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequestDto requestDto) {
        OptionResponseDto responseDto = optionService.create(productId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/products/{productId}/options")
    public ResponseEntity<Page<OptionResponseDto>> find(
            @PathVariable Long productId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<OptionResponseDto> optionsPage = optionService.find(productId, pageable);
        return ResponseEntity.ok(optionsPage);
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<OptionResponseDto> update(
            @PathVariable Long optionId,
            @Valid @RequestBody OptionRequestDto requestDto
    ) {
        OptionResponseDto responseDto = optionService.update(optionId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long optionId,
            @RequestParam Long productId) {
        optionService.delete(optionId, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
