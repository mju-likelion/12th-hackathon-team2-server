package com.example.mutsideout_mju.service;

import com.example.mutsideout_mju.dto.response.user.UserGradeResponseDto;
import com.example.mutsideout_mju.entity.Grade;
import com.example.mutsideout_mju.entity.SurveyOption;
import com.example.mutsideout_mju.entity.User;
import com.example.mutsideout_mju.entity.UserSurvey;
import com.example.mutsideout_mju.repository.UserRepository;
import com.example.mutsideout_mju.repository.UserSurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSurveyRepository userSurveyRepository;

    @Transactional
    public UserGradeResponseDto calculateUserGrade(User user) {
        List<UserSurvey> userSurveyList = userSurveyRepository.findByUserId(user.getId());

        long count = userSurveyList.stream()
                .filter(userSurvey -> isValidSurveyOption(userSurvey))
                .count();
        Grade grade = user.determineGrade(count);

        user.setUserGrade(grade);
        userRepository.save(user);

        return UserGradeResponseDto.of(user.getName(), grade);
    }

    public UserGradeResponseDto getUserGrade(User user) {
        return UserGradeResponseDto.from(user.getUserGrade());
    }

    public static boolean isValidSurveyOption(UserSurvey userSurvey) {
        Long questionNumber = userSurvey.getSurvey().getNumber();
        SurveyOption option = userSurvey.getSurveyOption();

        return (questionNumber >= 1 && questionNumber <= 3 && (option == SurveyOption.NORMAL || option == SurveyOption.YES))
                || (questionNumber >= 4 && questionNumber <= 6 && option == SurveyOption.YES);
    }
}
