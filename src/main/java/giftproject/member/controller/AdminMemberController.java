package giftproject.member.controller;

import giftproject.member.dto.MemberRequestDto;
import giftproject.member.dto.MemberResponseDto;
import giftproject.member.dto.MemberUpdateRequestDto;
import giftproject.member.entity.Member;
import giftproject.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String showMemberList(Model model) {
        List<MemberResponseDto> members = memberService.findAll();
        model.addAttribute("members", members);
        return "member/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new MemberRequestDto("", ""));
        model.addAttribute("isEdit", false);
        return "member/form";
    }

    @PostMapping
    public String createMember(@Valid @ModelAttribute("member") MemberRequestDto requestDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "member/form";
        }
        memberService.save(requestDto);
        return "redirect:/admin/members";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MemberResponseDto member = memberService.findById(id);
        model.addAttribute("member", new MemberRequestDto(member.email(), ""));
        model.addAttribute("memberId", member.id());
        model.addAttribute("isEdit", true);
        return "member/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("member") MemberUpdateRequestDto requestDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("memberId", id);
            return "member/form";
        }
        if (requestDto.password() == null || requestDto.password().isEmpty()) {
            Member member = memberService.findEntityById(id);
            String password = member.getPassword();

            requestDto = new MemberUpdateRequestDto(requestDto.email(), password);
        }
        memberService.update(id, requestDto.email(), requestDto.password());
        return "redirect:/admin/members";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        memberService.delete(id);
        return "redirect:/admin/members";
    }
}
