package com.sweetcat.app_feedback.interfaces.facade;

import com.sweetcat.app_feedback.application.service.AppFeedbackApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Coder_Jarvis
 * @Description:
 * @Date: 2021-11-2021/11/6-19:30
 * @Version: 1.0
 */
@Component
public class AppFeedbackFacade {
    private AppFeedbackApplicationService feedbackApplicationService;

    @Autowired
    public void setFeedbackApplicationService(AppFeedbackApplicationService feedbackApplicationService) {
        this.feedbackApplicationService = feedbackApplicationService;
    }

    public void addAFeedback(Long userId, String content, String[] feedbackPics, Long feedbackTime) {
        feedbackApplicationService.addAFeedback(userId, content, feedbackPics, feedbackTime);
    }
}