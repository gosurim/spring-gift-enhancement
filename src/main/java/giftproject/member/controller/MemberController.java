package giftproject.member.controller;

import giftproject.member.dto.MemberRequestDto;
import giftproject.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody MemberRequestDto requestDto) {
        String token = String.valueOf(memberService.register(requestDto));
        return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody MemberRequestDto requestDto) {
        String token = memberService.login(requestDto);

        return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
    }
}
