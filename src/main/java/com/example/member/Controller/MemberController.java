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
    public String loginForm(@RequestParam(value="redirectURI", defaultValue = "/member/myPage") String redirectURI,
                            Model model){
        // 위에서 value=redirectURI는 LoginCheckInterceptor의 리데이렉트URI를 받기위해 쓴 것이다.

        model.addAttribute("redirectURI", redirectURI);


        return "/memberPages/memberLogin";
    }
    @PostMapping("member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session,
                        @RequestParam("redirectURI") String redirectURI){
        boolean memberLoginResult = memberService.login(memberDTO);
        if(memberLoginResult){
            session.setAttribute("loginEmail", memberDTO.getMemberEmail());
//            return "/memberPages/memberMain";

            //로그인 성공하면 사용자가 직전에 요청한 주소로 redirect
            // 인터셉터에 걸리지 않고 처음부터 로그인하는 사용자였다면
            // redirect:/member/myPage 로 요청되며, memberMain 화면으로 전환됨.
            return "redirect:"+redirectURI;
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
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        // 세션에 담긴 값 전체 삭제
//     List<ArticleDTO> searchByContent(Map<String, Object> contentMap) {
//        return sql.selectList("Article.searchByContent", contentMap);
//    }   session.invalidate();
        // 특정 파라미터만 삭제
        session.removeAttribute("loginEmail");
        return "redirect:/";
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