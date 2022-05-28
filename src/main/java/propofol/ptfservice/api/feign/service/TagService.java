package propofol.ptfservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.ptfservice.api.feign.TagServiceFeignClient;
import propofol.ptfservice.api.feign.dto.TagDto;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagServiceFeignClient tagServiceFeignClient;

    public List<TagDto> getTagsByTagIds(String token, Set<Long> ids) {
        return tagServiceFeignClient.getTagsByTagId(token, ids).getTags();
    }
}
