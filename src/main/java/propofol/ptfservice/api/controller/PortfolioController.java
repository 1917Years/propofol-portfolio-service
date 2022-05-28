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
     * 포트폴리오 수정 - 기본 정보 수정
     */
    @PostMapping("/{portfolioId}/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createUserInfo(@PathVariable(value = "portfolioId") Long portfolioId,
                                      @Validated @RequestBody BasicInfoCreateRequestDto requestDto) {

        portfolioService.updateBasicInfo(requestDto.getGithub(), requestDto.getJob(),
                requestDto.getContent(), portfolioId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "기본 정보 수정 성공", "ok");
    }

    /**
     * 포트폴리오 수정 시 새롭게 추가 - 스킬 정보 생성
     */
    @PostMapping("/{portfolioId}/skill")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createSkill(@Token Long memberId,
                                    @PathVariable(value = "portfolioId") Long portfolioId,
                                   @RequestParam(value = "skills") List<Long> skills) {
        portfolioTagService.deleteAllTags(portfolioId);
        portfolioService.saveTags(skills, portfolioService.getPortfolioInfo(memberId));

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "스킬 정보 수정 성공", "ok");
    }

    /**
     * 포트폴리오 수정 시 새롭게 추가 - 수상 경력 정보 생성
     */
    @PostMapping("/{portfolioId}/award")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createAward(@PathVariable(value = "portfolioId") Long portfolioId,
                                   @Validated @RequestBody AwardCreateRequestDto requestDto) {
        AwardDto awardDto = modelMapper.map(requestDto, AwardDto.class);
        Award award = portfolioService.createAward(awardDto, portfolioId);
        portfolioService.saveAward(award);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 정보 생성 성공", "ok");
    }

    /**
     * 포트폴리오 수정 시 새롭게 추가 - 경력 정보 생성
     */
    @PostMapping("/{portfolioId}/career")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createCareer(@PathVariable(value = "portfolioId") Long portfolioId,
                                    @Validated @RequestBody CareerCreateRequestDto requestDto) {
        CareerDto careerDto = modelMapper.map(requestDto, CareerDto.class);
        Career career = portfolioService.createCareer(careerDto, portfolioId);
        portfolioService.saveCareer(career);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 생성 성공", "ok");
    }

    /**
     * 포트폴리오 수정 시 새롭게 추가 - 프로젝트 정보 생성
     */
    @PostMapping("/{portfolioId}/project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createProject (@PathVariable(value = "portfolioId") Long portfolioId,
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
        Project project = portfolioService.createProject(projectDto, portfolioId);
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
    @PostMapping("/{portfolioId}/blog")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBlogPost(@PathVariable(value = "portfolioId") Long portfolioId,
                                      @RequestParam(value = "boardId") List<String> boardId,
                                      @RequestParam(value = "title") List<String> title,
                                      @RequestParam(value = "content") List<String> content,
                                      @RequestParam(value = "date") List<String> date,
                                      @RequestParam(value = "recommend") List<String> recommend,
                                      @Token Long memberId) {

        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);
        portfolioBoardService.deleteAllBoards(portfolioId);

        for(int i=0; i<title.size(); i++) {
            portfolioBoardService.saveBoard(boardId.get(i), title.get(i), content.get(i), date.get(i),
                    recommend.get(i), findPortfolio);
        }

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "블로그 정보 등록 성공!", "ok");
    }


    /**
     * 포트폴리오 생성 - 한 번에 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createPortfolio (
                                        @RequestParam(value="template") Template template,
                                        @RequestParam(value="github", required = false) String github,
                                        @RequestParam(value="job", required = false) String job,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "skills", required = false) List<Long> skills,
                                        @RequestParam(value="awardNames", required = false) List<String> awardNames,
                                        @RequestParam(value="awardDates", required = false) List<String> awardDates,
                                        @RequestParam(value = "careerTitles", required = false) List<String> careerTitles,
                                        @RequestParam(value = "careerContents", required = false) List<String> careerContents,
                                        @RequestParam(value = "careerStartTerms", required = false) List<String> careerStartTerms,
                                        @RequestParam(value = "careerEndTerms", required = false) List<String> careerEndTerms,
                                        @RequestParam(value = "prjTitles", required = false) List<String> prjTitles,
                                        @RequestParam(value = "prjContents", required = false) List<String> prjContents,
                                        @RequestParam(value = "prjJobs", required = false) List<String> prjJobs,
                                        @RequestParam(value = "prjStartTerms", required = false) List<String> prjStartTerms,
                                        @RequestParam(value = "prjEndTerms",required = false) List<String> prjEndTerms,
                                        @RequestParam(value = "prjSkillCount", required = false) List<Integer> prjSkillCount,
                                        @RequestParam(value = "prjSkills", required = false) List<Long> prjSkills,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @RequestParam(value = "projectId", required = false) Long projectId,
                                        @Token Long memberId) throws IOException {

        List<AwardDto> awardDtos = new ArrayList<>();
        if(awardNames != null) {
            for (int i = 0; i < awardNames.size(); i++) {
                AwardDto awardDto = getAwardDto(awardNames, awardDates, i);
                awardDtos.add(awardDto);
            }
        }

        List<CareerDto> careerDtos = new ArrayList<>();
        if(careerTitles != null) {
            for (int i = 0; i < careerTitles.size(); i++) {
                CareerDto careerDto = getCareerDto(careerTitles, careerContents, careerStartTerms, careerEndTerms, i);
                careerDtos.add(careerDto);
            }
        }

        List<ProjectDto> projectDtos = new ArrayList<>();
        if(prjTitles != null) {
            for (int i = 0; i < prjTitles.size(); i++) {
                ProjectDto projectDto = getProjectDto(prjTitles.get(i), prjContents.get(i), prjJobs.get(i),
                        prjStartTerms.get(i), prjEndTerms.get(i));
                projectDtos.add(projectDto);
            }
        }

        PortfolioDto portfolioDto = new PortfolioDto(template, github, job, content, awardDtos, careerDtos, projectDtos);

        Portfolio portfolio = portfolioService.createPortfolio(portfolioDto);
        portfolio.addMemberId(memberId);

        Portfolio savedPortfolio = portfolioService.savePortfolio(portfolio);
        List<Project> projects = savedPortfolio.getProjects();

        if(skills != null) {
            portfolioService.saveTags(skills, savedPortfolio);
        }

        List<String> storeImageNames = imageService.getStoreProjectImages(files, projectId, getProjectDir());
        if(storeImageNames != null) {
            imageService.changeImageProjects(storeImageNames, projects);
        }

        if(prjSkillCount != null) {
            for (int i = 0; i < prjSkillCount.size(); i++) {
                List<Long> projectTags = getProjectTags(prjSkillCount, prjSkills, i);

                if (projectTags != null)
                    projectService.saveTags(projectTags, projects.get(i));
            }
        }

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 생성 성공", "ok");
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
    @PostMapping("/{portfolioId}/template")
    public ResponseDto updateTemplate(@PathVariable(value = "portfolioId") Long portfolioId,
                                 @Token Long memberId,
                                 @RequestParam Template template) {
        portfolioService.updateTemplate(portfolioId, memberId, template);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "템플릿 수정 성공!", "ok");

    }

    /**
     * 포트폴리오 수정 - 수상 경력 수정
     */
    @PostMapping("/{portfolioId}/award/{awardId}")
    public ResponseDto updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "awardId") Long awardId,
                                  @Token Long memberId,
                                  @Validated @RequestBody AwardUpdateRequestDto requestDto) {

        AwardDto awardDto = modelMapper.map(requestDto, AwardDto.class);
        awardService.updateAward(portfolioId, awardId, memberId, awardDto);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 수정 성공!", "ok");
    }

    /**
     * 포트폴리오 수정 - 경력 수정
     */
    @PostMapping("/{portfolioId}/career/{careerId}")
    public ResponseDto updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "careerId") Long careerId,
                                  @Token Long memberId,
                                  @Validated @RequestBody CareerUpdateRequestDto requestDto) {
        CareerDto careerDto = modelMapper.map(requestDto, CareerDto.class);
        careerService.updateCareer(portfolioId, careerId, memberId, careerDto);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 수정 성공!", "ok");
    }


    /**
     * 포트폴리오 삭제 - 수상 경력 정보 삭제
     */
    @DeleteMapping("/{portfolioId}/award/{awardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteAward(@PathVariable(value = "portfolioId") Long portfolioId,
                                   @PathVariable(value = "awardId") Long awardId,
                                   @Token Long memberId) {
        awardService.deleteAward(portfolioId, awardId, memberId);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "수상 경력 정보 삭제 성공!", "ok");
    }

    /**
     * 포트폴리오 삭제 - 경력 정보 삭제
     */
    @DeleteMapping("/{portfolioId}/career/{careerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteCareer(@PathVariable(value = "portfolioId") Long portfolioId,
                                   @PathVariable(value = "careerId") Long careerId,
                                   @Token Long memberId) {
        careerService.deleteCareer(portfolioId, careerId, memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "경력 정보 삭제 성공!", "ok");
    }

    /**
     * 포트폴리오 삭제 - 프로젝트 정보 삭제
     */
    @DeleteMapping("/{portfolioId}/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteProject(@PathVariable(value = "portfolioId") Long portfolioId,
                                    @PathVariable(value = "projectId") Long projectId,
                                    @Token Long memberId) {
        projectService.deleteProject(portfolioId, projectId, memberId);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로젝트 정보 삭제 성공!", "ok");
    }

    private AwardDto getAwardDto(List<String> awardNames, List<String> awardDates, int i) {
        AwardDto awardDto = new AwardDto();
        awardDto.setName(awardNames.get(i));
        awardDto.setDate(awardDates.get(i));
        return awardDto;
    }

    private CareerDto getCareerDto(List<String> careerTitles, List<String> careerContents, List<String> careerStartTerms, List<String> careerEndTerms, int i) {
        CareerDto careerDto = new CareerDto();
        careerDto.setTitle(careerTitles.get(i));
        careerDto.setContent(careerContents.get(i));
        careerDto.setStartTerm(careerStartTerms.get(i));
        careerDto.setEndTerm(careerEndTerms.get(i));
        return careerDto;
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
        return responseDto;
    }


    private String getProjectDir() {
        return fileProperties.getProjectDir();
    }

    private List<Long> getProjectTags(List<Integer> skillCount, List<Long> skills, int idx) {
        List<Long> pjTags = new ArrayList<>();
        Integer count = skillCount.get(idx);

        int startIdx=0, endIdx=0;
        if(idx == 0) {
            startIdx = 0;
            endIdx = count;
        }
        else {
            startIdx = skillCount.get(idx - 1);
            endIdx = startIdx + count;
        }

        for(int i=startIdx; i<endIdx; i++) {
            pjTags.add(skills.get(i));
        }
        return pjTags;
    }

}
