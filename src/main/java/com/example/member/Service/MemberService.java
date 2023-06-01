package com.example.member.Service;

import com.example.member.DTO.MemberDTO;
import com.example.member.Entity.MemberEntity;
import com.example.member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Long save(MemberDTO memberDTO) {
        MemberEntity memberEntity = MemberEntity.toSaveEntity(memberDTO);
        return memberRepository.save(memberEntity).getId();
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (MemberEntity memberEntity: memberEntityList) {
            memberDTOList.add(MemberDTO.toDTO(memberEntity));
        }
        return memberDTOList;
    }

    public boolean login(MemberDTO memberDTO) {
        Optional<MemberEntity> memberEntity=memberRepository.findByMemberEmailAndMemberPassword(memberDTO.getMemberEmail(), memberDTO.getMemberPassword());
        if(memberEntity.isPresent()){
            return true;
        }else{
            return false;
        }
    }

//    public MemberDTO findById(Long id) {
//        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
//        if(optionalMemberEntity.isPresent()){
//            System.out.println("있음");
//            MemberEntity memberEntity = optionalMemberEntity.get();
//            MemberDTO memberDTO = MemberDTO.toDTO(memberEntity);
//            return memberDTO;
//        }else{
//            System.out.println("없음");
//            return null;
//        }
//    }
    public MemberDTO findById(Long id){
//    MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(()->new NoSuchElementException());
//    return MemberDTO.toDTO(memberEntity);
      Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
      if(optionalMemberEntity.isPresent()) {
          MemberEntity memberEntity = optionalMemberEntity.get();
          return MemberDTO.toDTO(memberEntity);
      }else{
          return null;
      }
    }

    public void loginAxios(MemberDTO memberDTO) {
        memberRepository.findByMemberEmailAndMemberPassword(memberDTO.getMemberEmail(), memberDTO.getMemberPassword())
                        .orElseThrow(() ->new NoSuchElementException("이메일 또는 비밀번호가 틀립니다"));
                // 메소드를 호출하고 또 . 찎고 또 메서드를 호출함. = chaining method(체이닝 메서드)
               // 보통 이런 경우 . 이 어딨는지, "나 '는 어딨는지 확인하여야 코드 확인이 편하다
     }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
    public MemberDTO detailAxios(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("회원이 없습니다"));
        return MemberDTO.toDTO(memberEntity);}

    public void update(Long id, MemberDTO memberDTO) {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(id);

        if(memberEntityOptional.isEmpty()){
            throw new EntityNotFoundException("회원이 없습니다.");
        }
        MemberEntity memberEntity = memberEntityOptional.get();

        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberMobile(memberDTO.getMemberMobile());

        memberRepository.save(memberEntity);
    }
}