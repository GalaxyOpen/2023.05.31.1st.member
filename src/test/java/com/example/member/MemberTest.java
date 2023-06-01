package com.example.member;

import com.example.member.DTO.MemberDTO;
import com.example.member.Repository.MemberRepository;
import com.example.member.Service.MemberService;
import org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class MemberTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("repository method 테스트")
    public void repositoryTest() {
        memberRepository.findByMemberEmail("aaa");
        memberRepository.findByMemberEmailAndMemberPassword("aaa","1234");
    }

    private MemberDTO newMember(int i){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMemberEmail("testEmail"+i);
        memberDTO.setMemberPassword("testPass"+i);
        memberDTO.setMemberName("testName"+i);
        memberDTO.setMemberBirth("2000-01-10");
        memberDTO.setMemberMobile("010-1111-1212");
        return memberDTO;
    }
    @Test
    public void testData(){
        for(int i=1; i<=20; i++){
            memberService.save(newMember(i));
        }
    }
    
    // 회원가입테스트
    @Test@Transactional
    @Rollback
    @DisplayName("회원가입 테스트")
    public void memberSaveTest(){
        MemberDTO memberDTO = newMember(999);
        Long savedId = memberService.save(memberDTO);
        MemberDTO dto = memberService.findById(savedId);
        assertThat(memberDTO.getMemberEmail()).isEqualTo(dto.getMemberEmail());
    }
    @Test
    @Transactional
    @Rollback
    @DisplayName("로그인 테스트")
    public void loginTest() {
        MemberDTO memberDTO = newMember(999);
        MemberDTO loginDTO = new MemberDTO();
        loginDTO.setMemberEmail("wrong email");
        loginDTO.setMemberPassword("1234");
        // 예외처리가 발생했는지
        assertThatThrownBy(() -> memberService.loginAxios(memberDTO))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    public void deleteTest() {
        MemberDTO memberDTO = newMember(999);
        Long savedId = memberService.save(memberDTO);
        memberService.delete(savedId);
        assertThatThrownBy(() -> memberService.detailAxios(savedId))
                .isInstanceOf(NoSuchElementException.class);
    }
}
