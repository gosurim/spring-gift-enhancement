package giftproject.member.dto;

import giftproject.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequestDto(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        String password
) {

    public Member toEntity() {
        return new Member(email, password);
    }
}
