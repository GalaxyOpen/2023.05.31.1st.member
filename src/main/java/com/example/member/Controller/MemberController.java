package com.example.member.Controller;


import com.example.member.DTO.MemberDTO;
import com.example.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequiredArgsConstructor

public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member/save")
    public String saveForm() {
        return "/memberPages/memberSave";
    }

    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        memberService.save(memberDTO);
        return "/index";
    }

    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        model.addAttribute("memberList", memberDTOList);
        return "/memberPages/memberList";
    }
    @GetMapping("/member/login")
    public String loginForm(){
        return "/memberPages/memberLogin";
    }
    @PostMapping("member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session){
        boolean memberLoginResult = memberService.login(memberDTO);
        if(memberLoginResult){
            session.setAttribute("loginEmail", memberDTO.getMemberEmail());
            return "/memberPages/memberMain";
        }else{
            return "/memberPages/memberLogin";
        }
    }

    @PostMapping("/member/login/axios")
    public ResponseEntity loginAxios(@RequestBody MemberDTO memberDTO, HttpSession session) throws Exception{
        memberService.loginAxios(memberDTO);
        session.setAttribute("loginEmail", memberDTO.getMemberEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/member/myPage")
    public String myPage(){
        return "/memberPages/memberMain";
    }

//    @GetMapping("/member/{id}")
//    public String findById(@PathVariable Long id, Model model){
//        MemberDTO memberDTO = memberService.findById(id);
//        model.addAttribute("member",memberDTO);
//        return "/memberPages/memberDetail";
//    }




    @GetMapping("/member/axios/{id}")
    public ResponseEntity detailAxios(@PathVariable Long id) throws Exception{
        MemberDTO memberDTO = memberService.findById(id);
        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }
    @DeleteMapping("/member/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model){
        String loginEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.findByMemberEmail(loginEmail);
        model.addAttribute("member", memberDTO);
//        model.addAttribute("member", memberService.findByMemberEmail(loginEmail));
        // 이렇게 한줄로 쓰기도 함. 더 극단적인 케이스는 String loginEmail 선언 없이도 하지만 글쎄...? 하지만 읽을순 있어야함.
        return "memberPages/memberUpdate";
    }
    @PutMapping("/member/{id}")
    public ResponseEntity update(@RequestBody MemberDTO memberDTO){
        memberService.update(memberDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/member/{id}")
    public String detail(@PathVariable Long id, Model model){
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member",memberDTO);
        return "/memberPages/memberDetail";
    }

    @PostMapping("/member/dup-check")
    public ResponseEntity emailCheck(@RequestBody MemberDTO memberDTO){
//        memberService.findByMemberEmail(memberDTO.getMemberEmail());
//        return new ResponseEntity<>(HttpStatus.OK);
        boolean result = memberService.emailCheck(memberDTO.getMemberEmail());
        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}