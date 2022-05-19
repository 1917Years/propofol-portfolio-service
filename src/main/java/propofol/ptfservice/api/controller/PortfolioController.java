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
import propofol.ptfservice.api.controller.dto.portfolio.PortfolioCreateRequestDto;
import propofol.ptfservice.api.controller.dto.portfolio.PortfolioDetailResponseDto;
import propofol.ptfservice.api.controller.dto.portfolio.PortfolioResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectCreateRequestDto;
import propofol.ptfservice.api.controller.dto.project.ProjectResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectUpdateRequestDto;
import propofol.ptfservice.api.service.ImageService;
import propofol.ptfservice.domain.portfolio.entity.*;
import propofol.ptfservice.api.service.AwardService;
import propofol.ptfservice.api.service.CareerService;
import propofol.ptfservice.domain.portfolio.service.PortfolioService;
import propofol.ptfservice.api.service.ProjectService;
import propofol.ptfservice.domain.portfolio.service.dto.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 프로젝트 이미지 저장
     */
    @PostMapping("/project/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveImage(@RequestParam(value = "file") MultipartFile file,
                                 @RequestParam(value = "boardId", required = false) Long projectId) throws IOException {
        String storeImageNames = imageService.getStoreProjectImageName(file, projectId, getProjectDir());
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "이미지 생성 성공!", storeImageNames);
    }

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
                                      @Validated @RequestBody ProjectCreateRequestDto requestDto) {
        ProjectDto projectDto = modelMapper.map(requestDto, ProjectDto.class);
        Project project = portfolioService.createProject(projectDto, portfolioId);
        portfolioService.saveProject(project);
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로젝트 정보 생성 성공", "ok");
    }


    /**
     * 포트폴리오 생성 - 한 번에 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createPortfolio (@Validated @RequestBody PortfolioCreateRequestDto requestDto) {
        List<AwardDto> awardDtos = new ArrayList<>();

        requestDto.getAwards().forEach(award -> {
            awardDtos.add(modelMapper.map(award, AwardDto.class));
        });

        List<CareerDto> careerDtos = new ArrayList<>();
        requestDto.getCareers().forEach(career -> {
            careerDtos.add(modelMapper.map(career, CareerDto.class));
        });

        List<ProjectDto> projectDtos = createProjectDtos(requestDto.getProjects());

        PortfolioDto portfolioDto = new PortfolioDto(requestDto.getTemplate(), requestDto.getGithub(),
                requestDto.getJob(), requestDto.getContent(), awardDtos, careerDtos, projectDtos);

        Portfolio portfolio = portfolioService.createPortfolio(portfolioDto);
        portfolioService.savePortfolio(portfolio);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "포트폴리오 생성 성공", "ok");
    }

    /**
     * 포트폴리오 조회
     */
    @GetMapping("/myPortfolio")
    public PortfolioResponseDto getPortfolio (@RequestHeader(name = "Authorization") String token,
                                              @Token Long memberId) {
        PortfolioResponseDto responseDto = new PortfolioResponseDto();

        MemberInfoResponseDto memberInfo = portfolioService.getMemberInfo(token);

        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        responseDto.setEmail(memberInfo.getEmail());
        responseDto.setUsername(memberInfo.getUsername());
        responseDto.setPhoneNumber(memberInfo.getPhoneNumber());

        PortfolioDetailResponseDto portfolioDto = new PortfolioDetailResponseDto();

        // 하나의 포트폴리오 내부 필드
        List<CareerResponseDto> careerDtos = new ArrayList<>();
        List<ProjectResponseDto> projectDtos = new ArrayList<>();
        List<AwardResponseDto> awardDtos = new ArrayList<>();

        findPortfolio.getCareers().forEach(career -> {
            CareerResponseDto careerDto = modelMapper.map(career, CareerResponseDto.class);
            careerDtos.add(careerDto);
        });

        findPortfolio.getProjects().forEach(project -> {
            ProjectResponseDto projectDto = modelMapper.map(project, ProjectResponseDto.class);
            projectDtos.add(projectDto);
        });

        findPortfolio.getAwards().forEach(archive -> {
            AwardResponseDto archiveDto = modelMapper.map(archive, AwardResponseDto.class);
            awardDtos.add(archiveDto);
        });


        portfolioDto.setCareers(careerDtos);
        portfolioDto.setProjects(projectDtos);
        portfolioDto.setAwards(awardDtos);
        portfolioDto.setTemplate(findPortfolio.getTemplate());
        portfolioDto.setGithub(findPortfolio.getGithub());
        portfolioDto.setJob(findPortfolio.getJob());
        portfolioDto.setContent(findPortfolio.getContent());
        portfolioDto.setTemplate(findPortfolio.getTemplate());

        responseDto.setPortfolio(portfolioDto);
        return responseDto;
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
     * 포트폴리오 수정 - 프로젝트 수정
     */
    @PostMapping("/{portfolioId}/project/{projectId}")
    public ResponseDto updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "projectId") Long projectId,
                                  @Token Long memberId,
                                  @Validated @RequestBody ProjectUpdateRequestDto requestDto) {

        ProjectDto projectDto = modelMapper.map(requestDto, ProjectDto.class);
        projectService.updateProject(portfolioId, projectId, memberId, projectDto);

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "프로젝트 수정 성공!", "ok");
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

    private String getProjectDir() {
        return fileProperties.getProjectDir();
    }

    private List<ProjectDto> createProjectDtos(List<ProjectCreateRequestDto> requestDtos) {
        List<ProjectDto> projectDtos = new ArrayList<>();

        requestDtos.forEach(project -> {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setTitle(project.getTitle());
            projectDto.setContent(project.getContent());
            projectDto.setJob(project.getJob());
            projectDto.setStartTerm(project.getStartTerm());
            projectDto.setEndTerm(project.getEndTerm());

            List<SkillDto> skillDtos = new ArrayList<>();

            project.getProjectSkills().forEach(skillRequestDto -> {
                skillDtos.add(new SkillDto(skillRequestDto.getName()));
            });

            projectDto.setProjectSkills(skillDtos);

            /** TODO <일단 빼둠> 파일 의무적으로 첨부하도록 */
//            imageService.changeImageProjectId(project.getFileName(), project);
            projectDtos.add(projectDto);
        });

        return projectDtos;
    }

}
