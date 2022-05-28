package propofol.ptfservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.ptfservice.api.feign.dto.MemberInfoResponseDto;

@FeignClient(name="user-service")
public interface UserServiceFeignClient {

    /**
     * 유저 정보 요청
     */
    @GetMapping("/api/v1/members/userInfo")
    MemberInfoResponseDto getMemberInfo(
            @RequestHeader("Authorization") String token,
            @RequestParam("memberId") Long memberId);


}
