package propofol.ptfservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.ptfservice.api.common.annotation.Token;
import propofol.ptfservice.api.common.properties.FileProperties;
import propofol.ptfservice.api.controller.dto.*;
import propofol.ptfservice.api.controller.dto.award.AwardCreateRequestDto;
import propofol.ptfservice.api.controller.dto.award.AwardResponseDto;
import propofol.ptfservice.api.controller.dto.award.AwardUpdateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerCreateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerResponseDto;
import propofol.ptfservice.api.controller.dto.career.CareerUpdateRequestDto;
import propofol.ptfservice.api.controller.dto.portfolio.PortfolioDetailResponseDto;
import propofol.ptfservice.api.controller.dto.portfolio.PortfolioResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectResponseDto;
import propofol.ptfservice.api.feign.dto.MemberInfoResponseDto;
import propofol.ptfservice.api.feign.dto.TagDto;
import propofol.ptfservice.api.feign.service.TagService;
import propofol.ptfservice.api.feign.service.UserService;
import propofol.ptfservice.api.service.*;
import propofol.ptfservice.domain.portfolio.entity.*;
import propofol.ptfservice.domain.portfolio.service.dto.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final AwardService awardService;
    private final CareerService careerService;
    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final FileProperties fileProperties;
    private final ProjectTagService projectTagService;
    private final TagService tagService;
    private final PortfolioTagService portfolioTagService;
    private final UserService userService;
    private final PortfolioBoardService portfolioBoardService;

    /**
     * 포트폴리오 - 기본 정보
     */
    @PostMapping("/{memberId}/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createUserInfo(@PathVariable(value = "memberId") Long memberId,
                                      @Validated @RequestBody BasicInfoCreateRequestDto requestDto) {

        portfolioService.updateBasicInfo(requestDto.getGithub(), requestDto.getJob(),
                requestDto.getContent(), memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "기본 정보 수정 성공", "ok");
    }

    /**
     * 포트폴리오 - 스킬 정보
     */
    @PostMapping("/{memberId}/skill")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createSkill(@PathVariable(value = "memberId") Long memberId,
                                   @RequestParam(value = "skills") List<Long> skills) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        portfolioTagService.deleteAllTags(findPortfolio.getId());
        portfolioService.saveTags(skills, findPortfolio);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "스킬 정보 수정 성공", "ok");
    }

    /**
     * 포트폴리오 - 수상 경력
     */
    @PostMapping("/{memberId}/award")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createAward(@PathVariable(value = "memberId") Long memberId,
                                   @Validated @RequestBody AwardCreateRequestDto requestDto) {
        AwardDto awardDto = modelMapper.map(requestDto, AwardDto.class);
        Award award = portfolioService.createAward(awardDto, memberId);
        portfolioService.saveAward(award);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 정보 생성 성공", "ok");
    }

    /**
     * 포트폴리오 - 경력
     */
    @PostMapping("/{memberId}/career")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createCareer(@PathVariable(value = "memberId") Long memberId,
                                    @Validated @RequestBody CareerCreateRequestDto requestDto) {
        CareerDto careerDto = modelMapper.map(requestDto, CareerDto.class);
        Career career = portfolioService.createCareer(careerDto, memberId);
        portfolioService.saveCareer(career);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 생성 성공", "ok");
    }

    /**
     * 포트폴리오 - 프로젝트
     */
    @PostMapping("/{memberId}/project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createProject (@PathVariable(value = "memberId") Long memberId,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "content", required = false) String content,
                                      @RequestParam(value = "job", required = false) String job,
                                      @RequestParam(value = "startTerm", required = false) String startTerm,
                                      @RequestParam(value = "endTerm",required = false) String endTerm,
                                      @RequestParam(value = "skills", required = false) List<Long> skills,
                                      @RequestParam(value = "file", required = false) List<MultipartFile> files,
                                      @RequestParam(value = "projectId", required = false) Long projectId
    ) throws IOException {
        ProjectDto projectDto = getProjectDto(title, content, job, startTerm, endTerm);
        Project project = portfolioService.createProject(projectDto, memberId);
        Project savedProject = portfolioService.saveProject(project);

        if(skills != null)
            projectService.saveTags(skills, savedProject);

        List<String> storeProjectImages = imageService.getStoreProjectImages(files, projectId, getProjectDir());
        imageService.changeImageProject(storeProjectImages.get(0), savedProject);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로젝트 정보 생성 성공", "ok");
    }

    /**
     * 블로그 정보 등록
     */
    @PostMapping("/{memberId}/blog")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBlogPost(@PathVariable(value = "memberId") Long memberId,
                                      @RequestParam(value = "boardId") List<String> boardId,
                                      @RequestParam(value = "title") List<String> title,
                                      @RequestParam(value = "content") List<String> content,
                                      @RequestParam(value = "date") List<String> date,
                                      @RequestParam(value = "recommend") List<String> recommend) {

        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        portfolioBoardService.deleteAllBoards(findPortfolio.getId());

        for(int i=0; i<title.size(); i++) {
            portfolioBoardService.saveBoard(boardId.get(i), title.get(i), content.get(i), date.get(i),
                    recommend.get(i), findPortfolio);
        }

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "블로그 정보 등록 성공!", "ok");
    }


    /**
     * 회원의 포트폴리오가 이미 있는지 판단
     */
    @GetMapping("/checkPortfolio")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto checkPortfolio(@Token Long memberId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        if(findPortfolio == null)
            return new ResponseDto(HttpStatus.OK.value(), "success",
                    "포트폴리오 존재하지 않음!", "no");
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 존재함!", findPortfolio.getId());
    }


    /**
     * 포트폴리오 조회
     */
    @GetMapping("/myPortfolio")
    public ResponseDto getPortfolio (@RequestHeader(name = "Authorization") String token,
                                              @Token Long memberId) {
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 조회 성공!", getPortfolioResponseDto(token, memberId));
    }

    /**
     * 회원의 자기소개, 직무 조회
     */
    @GetMapping("/getMemberInfo")
    public ResponseDto getMemberMatchingInfo (@RequestParam(value = "memberId") Long memberId) {
        Portfolio portfolioInfo = portfolioService.getPortfolioInfo(memberId);
        if(portfolioInfo == null)
            return new ResponseDto(HttpStatus.OK.value(), "success", "포트폴리오 존재하지 않음!", "no");
        else {
            MatchingResponseDto matchingResponseDto = new MatchingResponseDto();
            matchingResponseDto.setContent(portfolioInfo.getContent());
            matchingResponseDto.setJob(portfolioInfo.getJob());
            return new ResponseDto(HttpStatus.OK.value(), "success", "매칭 정보 조회 성공!",
                    matchingResponseDto);
        }
    }

    /**
     * 다른 회원의 포트폴리오 조회
     */
    @GetMapping("/memberPortfolio")
    public ResponseDto getMemberPortfolio (@RequestHeader(name = "Authorization") String token,
                                              @RequestParam(value = "memberId") Long memberId) {
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 조회 성공!", getPortfolioResponseDto(token, memberId));
    }

    /**
     * 템플릿 조회
     */
    @GetMapping("/myPortfolio/template")
    public ResponseDto getTemplate(@Token Long memberId) {
        Portfolio findPorfolio = portfolioService.getPortfolioInfo(memberId);
        Template template = findPorfolio.getTemplate();
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "템플릿 조회 성공!", template);
    }

    /**
     * 스킬 정보 조회
     */
    @GetMapping("/myPortfolio/skills")
    public ResponseDto getSkill (@RequestHeader(name = "Authorization") String token,
                                 @Token Long memberId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 스킬 사항 조회 성공!", getTagResponseDtos(token, findPortfolio));
    }

    /**
     * 경력 사항 조회
     */
    @GetMapping("/myPortfolio/career")
    public ResponseDto getCareer (@Token Long memberId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 경력 사항 조회 성공!", getCareerResponseDtos(findPortfolio));
    }

    /**
     * 수상 경력 조회
     */
    @GetMapping("/myPortfolio/award")
    public ResponseDto getAward(@Token Long memberId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 수상 경력 조회 성공!", getAwardResponseDtos(findPortfolio));
    }


    /**
     * 프로젝트 조회
     */
    @GetMapping("/myPortfolio/project")
    public ResponseDto getProject (@RequestHeader(name = "Authorization") String token,
                                   @Token Long memberId) {
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 프로젝트 조회 성공!", getProjectResponseDtos(token, findPortfolio));
    }


    /**
     * 포트폴리오 수정 - 템플릿 수정
     */
    @PostMapping("/{memberId}/template")
    public ResponseDto updateTemplate(@PathVariable(value = "memberId") Long memberId,
                                 @RequestParam Template template) {
        portfolioService.updateTemplate(memberId, template);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "템플릿 수정 성공!", "ok");
    }

    /**
     * 포트폴리오 수정 - 수상 경력 수정
     */
    @PostMapping("/{memberId}/award/{awardId}")
    public ResponseDto updatePortfolio(@PathVariable(value = "memberId") Long memberId,
                                  @PathVariable(value = "awardId") Long awardId,
                                  @Validated @RequestBody AwardUpdateRequestDto requestDto) {

        AwardDto awardDto = modelMapper.map(requestDto, AwardDto.class);
        awardService.updateAward(memberId, awardId, awardDto);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 수정 성공!", "ok");
    }

    /**
     * 포트폴리오 수정 - 경력 수정
     */
    @PostMapping("/{memberId}/career/{careerId}")
    public ResponseDto updatePortfolio(@PathVariable(value = "memberId") Long memberId,
                                  @PathVariable(value = "careerId") Long careerId,
                                  @Validated @RequestBody CareerUpdateRequestDto requestDto) {
        CareerDto careerDto = modelMapper.map(requestDto, CareerDto.class);
        careerService.updateCareer(memberId, careerId, careerDto);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 수정 성공!", "ok");
    }


    /**
     * 포트폴리오 삭제 - 수상 경력 정보 삭제
     */
    @DeleteMapping("/{memberId}/award/{awardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteAward(@PathVariable(value = "memberId") Long memberId,
                                   @PathVariable(value = "awardId") Long awardId) {
        awardService.deleteAward(memberId, awardId);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 정보 삭제 성공!", "ok");
    }

    /**
     * 포트폴리오 삭제 - 경력 정보 삭제
     */
    @DeleteMapping("/{memberId}/career/{careerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteCareer(@PathVariable(value = "memberId") Long memberId,
                                   @PathVariable(value = "careerId") Long careerId) {
        careerService.deleteCareer(memberId, careerId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 삭제 성공!", "ok");
    }

    /**
     * 포트폴리오 삭제 - 프로젝트 정보 삭제
     */
    @DeleteMapping("/{memberId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteProject(@PathVariable(value = "memberId") Long memberId,
                                    @PathVariable(value = "projectId") Long projectId) {
        projectService.deleteProject(memberId, projectId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로젝트 정보 삭제 성공!", "ok");
    }

    private ProjectDto getProjectDto(String title, String content, String job, String startTerm, String endTerm) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setTitle(title);
        projectDto.setContent(content);
        projectDto.setJob(job);
        projectDto.setStartTerm(startTerm);
        projectDto.setEndTerm(endTerm);

        return projectDto;
    }

    private List<BoardResponseDto> getBoardResponseDtos(String token, Portfolio findPortfolio) {
        List<BoardResponseDto> boardDtos = new ArrayList<>();
        List<PortfolioBoard> findBoards = portfolioBoardService.findByPortfolioId(findPortfolio.getId());
        if(findBoards != null) {
            findBoards.forEach(board-> {
                BoardResponseDto boardResponseDto = new BoardResponseDto();
                boardResponseDto.setBoardId(board.getBoardId());
                boardResponseDto.setTitle(board.getTitle());
                boardResponseDto.setContent(board.getContent());
                boardResponseDto.setDate(board.getDate());
                boardResponseDto.setRecommend(board.getRecommend());
                boardDtos.add(boardResponseDto);
            });
        }
        return boardDtos;
    }

    private List<TagResponseDto> getTagResponseDtos(String token, Portfolio findPortfolio) {
        List<TagResponseDto> skillDtos = new ArrayList<>();
        Set<Long> skillIds = new HashSet<>();
        List<PortfolioTag> findTags = portfolioTagService.findAllByPortfolioId(findPortfolio.getId());
        if(findTags!=null) {
            findTags.forEach(findTag -> {
                skillIds.add(findTag.getTagId());
            });

            List<TagDto> skillList = tagService.getTagsByTagIds(token, skillIds);
            skillList.forEach(skill -> {
                skillDtos.add(modelMapper.map(skill, TagResponseDto.class));
            });
        }
        return skillDtos;
    }

    private List<AwardResponseDto> getAwardResponseDtos(Portfolio findPortfolio) {
        List<AwardResponseDto> awardDtos = new ArrayList<>();

        if(findPortfolio.getAwards() != null) {
            findPortfolio.getAwards().forEach(archive -> {
                AwardResponseDto awardDto = modelMapper.map(archive, AwardResponseDto.class);
                awardDtos.add(awardDto);
            });
        }
        return awardDtos;
    }

    private List<CareerResponseDto> getCareerResponseDtos(Portfolio findPortfolio) {
        List<CareerResponseDto> careerDtos = new ArrayList<>();

        if(findPortfolio.getCareers() != null) {
            findPortfolio.getCareers().forEach(career -> {
                CareerResponseDto careerDto = modelMapper.map(career, CareerResponseDto.class);
                careerDtos.add(careerDto);
            });
        }
        return careerDtos;
    }

    private List<ProjectResponseDto> getProjectResponseDtos(String token, Portfolio findPortfolio) {
        List<ProjectResponseDto> projectDtos = new ArrayList<>();

        if(findPortfolio.getProjects() != null) {
            findPortfolio.getProjects().forEach(project -> {
                ProjectResponseDto projectDto = modelMapper.map(project, ProjectResponseDto.class);
                ProjectImage findImage = imageService.findByProjectId(project.getId());

                if (findImage != null) {
                    projectDto.setImageBytes(imageService.getImageBytes(findImage.getStoreFileName()));
                    projectDto.setImageType(imageService.getImageType(findImage));
                }

                Set<Long> tagIds = new HashSet<>();
                List<ProjectTag> findTags = projectTagService.findAllByProjectId(project.getId());
                findTags.forEach(findTag -> {
                    tagIds.add(findTag.getTagId());
                });

                List<TagDto> tagDtoList = tagService.getTagsByTagIds(token, tagIds);
                tagDtoList.forEach(tag -> {
                    projectDto.getTagId().add(modelMapper.map(tag, TagResponseDto.class));
                });

                projectDtos.add(projectDto);
            });
        }
        return projectDtos;
    }

    private PortfolioResponseDto getPortfolioResponseDto(String token, Long memberId) {
        PortfolioResponseDto responseDto = new PortfolioResponseDto();

        MemberInfoResponseDto memberInfo = userService.getMemberInfo(token, memberId);
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        responseDto.setEmail(memberInfo.getEmail());
        responseDto.setUsername(memberInfo.getUsername());
        responseDto.setPhoneNumber(memberInfo.getPhoneNumber());

        PortfolioDetailResponseDto portfolioDto = new PortfolioDetailResponseDto();
        if(findPortfolio != null) {

            // 하나의 포트폴리오 내부 필드
            List<AwardResponseDto> awardDtos = getAwardResponseDtos(findPortfolio);
            List<CareerResponseDto> careerDtos = getCareerResponseDtos(findPortfolio);
            List<ProjectResponseDto> projectDtos = getProjectResponseDtos(token, findPortfolio);
            List<TagResponseDto> skillDtos = getTagResponseDtos(token, findPortfolio);
            List<BoardResponseDto> boardDtos = getBoardResponseDtos(token, findPortfolio);

            portfolioDto.setBoards(boardDtos);
            portfolioDto.setCareers(careerDtos);
            portfolioDto.setProjects(projectDtos);
            portfolioDto.setAwards(awardDtos);
            portfolioDto.setSkills(skillDtos);
            portfolioDto.setTemplate(findPortfolio.getTemplate());
            portfolioDto.setGithub(findPortfolio.getGithub());
            portfolioDto.setJob(findPortfolio.getJob());
            portfolioDto.setContent(findPortfolio.getContent());
            portfolioDto.setTemplate(findPortfolio.getTemplate());

            responseDto.setPortfolio(portfolioDto);
        }
        return responseDto;
    }


    private String getProjectDir() {
        return fileProperties.getProjectDir();
    }

}
