//package com.lyj.securitydomo.repository;
//
//import com.lyj.securitydomo.domain.Report;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Date;
//
//@SpringBootTest
//@Log4j2
//public class ReportRepositoryTests {
//
//
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private ReportRepository reportRepository;
//
//    @Test
//    public void testInsertSingleReport() {
//        Report report = Report.builder()
//                .post(postRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Post not found")))
//                .category(Report.ReportCategory.SPAM) // enum으로 변경
//                .reason("Test reason")
//                .status(Report.ReportStatus.PENDING)
//                .createdAt(new Date())
//                .build();
//
//        Report result = reportRepository.save(report);
//        log.info("Report saved with ID: {}", result.getReportId());
//    }
//    }