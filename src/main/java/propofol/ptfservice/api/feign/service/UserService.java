package propofol.ptfservice.api.feign.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.ptfservice.api.feign.UserServiceFeignClient;
import propofol.ptfservice.api.feign.dto.MemberInfoResponseDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceFeignClient userServiceFeignClient;

    /**
     * 유저 정보 가져오기
     */
    public MemberInfoResponseDto getMemberInfo(String token, Long memberId) {
        return userServiceFeignClient.getMemberInfo(token, memberId);
    }

}
