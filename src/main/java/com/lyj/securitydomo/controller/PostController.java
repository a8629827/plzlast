package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.QPost;
import com.lyj.securitydomo.domain.pPhoto;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.RequestDTO;
import com.lyj.securitydomo.dto.upload.UploadFileDTO;
import com.lyj.securitydomo.dto.upload.UploadResultDTO;
import com.lyj.securitydomo.service.PostService;
import com.lyj.securitydomo.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/posting")
@RequiredArgsConstructor
public class PostController {

    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;

    private final PostService postService;
    private final RequestService requestService;

    /**
     * 게시글 목록을 조회하고 뷰에 전달하는 메서드
     */
    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model, @AuthenticationPrincipal PrincipalDetails principal) {

        // 기본 페이지 크기 설정
        if (pageRequestDTO.getSize() <= 0) {
            pageRequestDTO.setSize(10);
        }

        // 게시글 목록 조회
        PageResponseDTO<PostDTO> responseDTO = postService.list(pageRequestDTO);

        // 사용자 역할(관리자 여부) 확인
        boolean isAdmin = principal != null && principal.getUser().getRole().equals("ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        // 각 게시글에 대해 작성자인지 확인 (isAuthor 리스트 생성)
        List<Boolean> isAuthor = Collections.emptyList();
        if (principal != null) {
            isAuthor = responseDTO.getDtoList().stream()
                    .map(postDTO -> postDTO.getAuthor().equals(principal.getUser().getUsername())) // 작성자 여부 확인
                    .collect(Collectors.toList());
        }
        model.addAttribute("isAuthor", isAuthor);

        // 데이터 전달
        model.addAttribute("posts", responseDTO.getDtoList());
        model.addAttribute("totalPages", (int) Math.ceil(responseDTO.getTotal() / (double) pageRequestDTO.getSize()));
        model.addAttribute("currentPage", pageRequestDTO.getPage());

        log.info("게시글 목록 전달: {}", responseDTO.getDtoList());
        return "posting/list";
    }

    /**
     * 특정 게시글의 상세 정보를 조회하고 뷰에 전달하는 메서드
     */
    @GetMapping("/read/{postId}")
    public String read(@PathVariable Long postId, Model model,
                       @AuthenticationPrincipal PrincipalDetails principal) {
        // 게시글 상세 정보 조회
        PostDTO postDTO = postService.readOne(postId);
        model.addAttribute("post", postDTO);
        model.addAttribute("originalImages", postDTO.getOriginalImageLinks());
        model.addAttribute("isAuthor", true); // 작성자인 경우

        // 로그인한 사용자 정보 추가
        model.addAttribute("user", principal.getUser());

        // 관리자 여부 확인
        boolean isAdmin = "ADMIN".equals(principal.getUser().getRole());
        model.addAttribute("isAdmin", isAdmin);

        // 작성자 여부 확인
        boolean isAuthor = postDTO.getAuthor().equals(principal.getUser().getUsername());
        model.addAttribute("isAuthor", isAuthor);

        // 신청자리스트 추가 (작성자일 경우에만 조회)
        if (isAuthor) {
            List<RequestDTO> requestList = requestService.getRequestsByPostId(postId);
            model.addAttribute("requestList", requestList);
        }

        // 로그 출력
        log.info("게시글 상세 정보: {}", postDTO);
        log.info("isAdmin: {}, isAuthor: {}", isAdmin, isAuthor);

        return "posting/read";
    }

    /**
     * 게시글 등록 페이지를 보여주는 메서드
     */
    @GetMapping("/register")
    public void registerGET() {
        log.info("게시글 등록 페이지 로드");
    }

    @PostMapping("/register")
    public String registerPost(UploadFileDTO uploadFileDTO,
                               @RequestParam(value = "useRandomImage", required = false, defaultValue = "false") boolean useRandomImage,
                               @Valid PostDTO postDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        try {
            List<String> fileNames;

            if (useRandomImage) {
                // 랜덤 이미지를 사용
                String randomImage = UploadResultDTO.getRandomImage();
                fileNames = List.of(randomImage);
            } else if (uploadFileDTO.getFiles() != null && !uploadFileDTO.getFiles().isEmpty() &&
                    !uploadFileDTO.getFiles().get(0).getOriginalFilename().isEmpty()) {
                // 파일 업로드 처리
                fileNames = uploadFiles(uploadFileDTO);
            } else {
                // 파일이 없고 랜덤 이미지를 사용하지 않을 경우
                redirectAttributes.addFlashAttribute("error", "이미지가 필요합니다.");
                return "redirect:/posting/register";
            }

            postDTO.setFileNames(fileNames); // 파일 이름 설정
            Long postId = postService.register(postDTO); // 게시글 등록
            redirectAttributes.addFlashAttribute("result", postId); // 등록 성공 알림

            log.info("게시글 등록 성공: ID={}", postId);
        } catch (Exception e) {
            log.error("게시글 등록 오류: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글 등록 중 문제가 발생했습니다.");
            return "redirect:/posting/register";
        }

        return "redirect:/posting/list";
    }
    /**
     * 특정 게시글 수정 페이지를 로드하는 메서드
     */
    @GetMapping("/modify/{postId}")
    public String modify(@PathVariable Long postId, Model model) {
        PostDTO postDTO = postService.readOne(postId); // 기존 게시글 정보 읽기
        model.addAttribute("post", postDTO);
        model.addAttribute("originalImages", postDTO.getOriginalImageLinks()); // 기존 이미지

        log.info("수정할 게시글 정보: {}", postDTO);
        return "posting/modify";
    }

    /**
     * 게시글 수정 처리를 위한 메서드
     */
    @PostMapping("/modify/{postId}")
    public String modifyPost(@PathVariable Long postId, PageRequestDTO pageRequestDTO, UploadFileDTO uploadFileDTO,
                             @Valid PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("postId", postDTO.getPostId());
            return "redirect:/posting/modify/" + postId;
        }

        try {
            List<String> fileNames;

            if (uploadFileDTO.getFiles() != null && !uploadFileDTO.getFiles().isEmpty() &&
                    !uploadFileDTO.getFiles().get(0).getOriginalFilename().isEmpty()) {
                // 새로운 파일 업로드 처리
                fileNames = uploadFiles(uploadFileDTO);
            } else {
                // 기존 파일 유지
                fileNames = postService.readOne(postDTO.getPostId()).getFileNames();
            }

            postDTO.setFileNames(fileNames); // 파일 설정
            postService.modify(postDTO); // 게시글 수정 서비스 호출
            redirectAttributes.addFlashAttribute("result", "modified");

            log.info("게시글 수정 성공: ID={}", postId);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "이미지 파일 처리 중 문제가 발생했습니다.");
            log.error("게시글 수정 중 오류: {}", e.getMessage());
        }

        redirectAttributes.addAttribute("postId", postDTO.getPostId());
        return "redirect:/posting/read/{postId}";
    }

    /**
     * 게시글 삭제 처리 메서드
     */
    @GetMapping("/remove/{postId}")
    public String remove(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        postService.remove(postId);
        redirectAttributes.addFlashAttribute("result", "removed");
        log.info("게시글 삭제 성공: ID={}", postId);
        return "redirect:/posting/list";
    }

    /**
     * 게시글 비공개 처리 메서드
     * - 관리자가 신고된 게시글을 비공개로 처리
     */
    @PostMapping("/hide/{postId}")
    public String markPostAsInvisible(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        try {
            postService.makePostInvisible(postId);
            redirectAttributes.addFlashAttribute("message", "게시글이 비공개 처리되었습니다.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "해당 게시글을 찾을 수 없습니다.");
        }
        return "redirect:/posting/list";
    }

    /**
     * 게시글 공개 처리 메서드
     * - 관리자가 비공개 상태의 게시글을 다시 공개 처리
     */
    @PostMapping("/show/{postId}")
    public String markPostAsVisible(@PathVariable Long postId, RedirectAttributes redirectAttributes) {
        try {
            postService.makePostVisible(postId);
            redirectAttributes.addFlashAttribute("message", "게시글이 공개 처리되었습니다.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "해당 게시글을 찾을 수 없습니다.");
        }
        return "redirect:/posting/list";
    }

    private List<String> uploadFiles(UploadFileDTO uploadFileDTO) throws IOException {
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : uploadFileDTO.getFiles()) {
            String originalName = file.getOriginalFilename(); // 원본 파일 이름
            if (originalName == null || originalName.isEmpty()) {
                log.warn("빈 파일 이름이 감지되었습니다. 파일 처리 건너뜀.");
                continue;
            }

            String uuid = UUID.randomUUID().toString(); // 고유 식별자 생성
            Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

            try {
                // 파일 저장
                file.transferTo(savePath);
                log.info("파일 저장 성공: {}", savePath);

                // 이미지 포맷 검증 및 썸네일 생성
                try {
                    File thumbnail = new File(uploadPath, "s_" + uuid + "_" + originalName);
                    log.info("썸네일 생성 시도: {}", savePath);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbnail, 200, 200);
                    log.info("썸네일 생성 성공: {}", thumbnail.getAbsolutePath());

                    // 파일 이름 추가
                    fileNames.add("s_" + uuid + "_" + originalName); // 썸네일 파일 이름
                    fileNames.add(uuid + "_" + originalName);       // 원본 파일 이름
                } catch (IOException e) {
                    log.warn("썸네일 생성 실패: {}", e.getMessage());
                    throw new IOException("유효하지 않은 이미지 파일입니다: " + originalName, e);
                }
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
                throw e;
            }
        }

        log.info("업로드 처리 완료. 저장된 파일 수: {}", fileNames.size());
        return fileNames;
    }
}