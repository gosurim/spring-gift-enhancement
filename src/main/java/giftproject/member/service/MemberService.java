package giftproject.member.service;

import giftproject.member.dto.MemberRequestDto;
import giftproject.member.dto.MemberResponseDto;
import giftproject.member.entity.Member;
import giftproject.member.repository.MemberRepository;
import giftproject.member.util.JwtTokenProvider;
import giftproject.member.util.PasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String register(MemberRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.password());

        Member newMember = new Member(requestDto.email(), encodedPassword);
        Member savedMember = memberRepository.save(newMember);

        return jwtTokenProvider.generateToken(savedMember.getId(), savedMember.getEmail());
    }

    public String login(MemberRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generateToken(member.getId(), member.getEmail());
    }

    public List<MemberResponseDto> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    public MemberResponseDto save(MemberRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        Member member = new Member(requestDto.email(), encodedPassword);
        Member saveMember = memberRepository.save(member);

        return MemberResponseDto.from(saveMember);
    }

    public MemberResponseDto update(Long id, String email, String password) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        member.update(email, passwordEncoder.encode(password));
        memberRepository.update(member);

        return MemberResponseDto.from(member);
    }

    public MemberResponseDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return MemberResponseDto.from(member);
    }

    public Member findEntityById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void delete(Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        memberRepository.delete(id);
    }
}
