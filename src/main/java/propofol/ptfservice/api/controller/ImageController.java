package propofol.ptfservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.ptfservice.api.service.ImageService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolio/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public String getImages(@PathVariable("fileName") String fileName) {
        return imageService.getImageBytes(fileName);
    }
}
