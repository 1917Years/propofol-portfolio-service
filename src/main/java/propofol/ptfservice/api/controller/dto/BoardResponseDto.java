package propofol.ptfservice.api.controller.dto;

import lombok.Data;

@Data
public class BoardResponseDto {
    private String boardId;
    private String title;
    private String recommend;
    private String content;
    private String date;
}
