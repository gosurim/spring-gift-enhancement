package giftproject.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import giftproject.member.dto.MemberRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void register_Success() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto("email@email.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        MemberRequestDto registerDto = new MemberRequestDto("email@email.com", "password");
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        MemberRequestDto loginDto = new MemberRequestDto("email@email.com", "password");
        String jsonRequest = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("잘못된 비밀번호")
    void login_Fail_WrongPassword() throws Exception {
        MemberRequestDto registerDto = new MemberRequestDto("email@email.com", "password");
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        MemberRequestDto loginDto = new MemberRequestDto("email@email.com", "wrongpassword");
        String jsonRequest = objectMapper.writeValueAsString(loginDto);

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("이메일 또는 비밀번호가 일치하지 않습니다."));
    }
}
