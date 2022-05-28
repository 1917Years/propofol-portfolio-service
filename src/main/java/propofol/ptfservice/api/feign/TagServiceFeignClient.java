package propofol.ptfservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.ptfservice.api.feign.dto.TagsDto;

import java.util.List;
import java.util.Set;

@FeignClient("tag-service")
public interface TagServiceFeignClient {
    /**
     * 태그 정보 요청
     */
    @GetMapping("/api/v1/tags/ids")
    TagsDto getTagsByTagId (@RequestHeader("Authorization") String token,
                            @RequestParam("ids") Set<Long> ids);
}
