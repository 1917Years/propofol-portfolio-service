package propofol.ptfservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.ptfservice.api.common.properties.FileProperties;
import propofol.ptfservice.domain.exception.NotFoundFileException;
import propofol.ptfservice.domain.exception.NotSaveFileException;
import propofol.ptfservice.domain.portfolio.entity.ProjectImage;
import propofol.ptfservice.domain.portfolio.entity.Project;
import propofol.ptfservice.domain.portfolio.repository.ImageRepository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final FileProperties fileProperties;

    public String getUploadDir() {
        return fileProperties.getProjectDir();
    }

    public String findProjectPath(String dir) {
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString() + "/" + dir;
        return path;
    }

    public ProjectImage saveImage(MultipartFile file) throws IOException {
        String path = createFolder();
        String originalFilename = file.getOriginalFilename();
        String extType = getExt(originalFilename);
        String storeFilename = createStoreFilename(extType);

        try {
            file.transferTo(new File(getFullPath(path, storeFilename)));
        } catch (IOException e) {
            throw new NotSaveFileException("파일을 저장할 수 없습니다.");
        }

        ProjectImage projectImage = ProjectImage
                .createImage().storeFileName(storeFilename)
                .contentType(file.getContentType())
                .uploadFileName(originalFilename)
                .build();

        return imageRepository.save(projectImage);
    }

    @Transactional
    public List<String> getStoreProjectImages(List<MultipartFile> files, Long projectId, String dir) throws IOException {
        if(projectId != null) {
            List<ProjectImage> projectImages = imageRepository.findAllByProjectId(projectId);
            projectImages.forEach(image -> {
                String pathName = findProjectPath(dir) + "/" + image.getStoreFileName();
                File newFile = new File(pathName);
                if(newFile.exists())
                    newFile.delete();
            });
            imageRepository.deleteImage(projectId);
        }

        if(files != null) {
//            String path = "http://localhost:8000/ptf-service/api/v1/portfolio/images";
            List<String> fileNames = new ArrayList<>();
            files.forEach(file -> {
                try {
                    ProjectImage savedImage = saveImage(file);
                    fileNames.add(savedImage.getStoreFileName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return fileNames;
        }
        return null;
    }



    @Transactional
    public void changeImageProject(String fileName, Project project) {
        ProjectImage findProjectImage = imageRepository.findImageByStoreFileName(fileName).orElseThrow(() -> {
            throw new NotFoundFileException("파일을 찾을 수 없습니다.");
        });
        findProjectImage.changeProject(project);
    }

    @Transactional
    public void changeImageProjects(List<String> fileNames, List<Project> projects) {
        for (int i = 0; i < fileNames.size(); i++) {
            ProjectImage findProjectImage = imageRepository.findImageByStoreFileName(fileNames.get(i)).orElseThrow(() -> {
                throw new NotFoundFileException("파일을 찾을 수 없습니다.");
            });
            findProjectImage.changeProject(projects.get(i));
        }
    }


    public String getImageBytes(String fileName){
        String path = findProjectPath(getUploadDir());
        byte[] bytes = null;

        try {
            String file = path + "/" + fileName;
            InputStream imageStream = new FileInputStream(file);
            bytes = IOUtils.toByteArray(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(bytes);
    }


    public String getFullPath(String path, String filename){
        return path + "/" + filename;
    }

    public ProjectImage findByProjectId(Long projectId) {
        return imageRepository.findImageByProjectId(projectId).orElse(null);
    }

    public String getImageType(ProjectImage projectImage){
        if(projectImage == null) { return null; }
        return projectImage.getContentType();
    }

    @Transactional
    public void deleteAllImages(Long projectId) {
        imageRepository.deleteImage(projectId);
    }


    private String createFolder() {
        String path = findProjectPath(getUploadDir());
        File parentFolder = new File(path);

        if (!parentFolder.exists()){
            parentFolder.mkdir();
        }
        return path;
    }

    private String getExt(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String createStoreFilename(String extType) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extType;
    }


}
