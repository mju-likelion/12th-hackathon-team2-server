package com.example.mutsideout_mju.service;

import com.example.mutsideout_mju.authentication.PasswordHashEncryption;
import com.example.mutsideout_mju.dto.request.user.DeleteUserDto;
import com.example.mutsideout_mju.dto.request.user.UpdateUserDto;
import com.example.mutsideout_mju.dto.response.user.UserInfoResponseDto;
import com.example.mutsideout_mju.entity.UserGrade;
import com.example.mutsideout_mju.entity.User;
import com.example.mutsideout_mju.exception.ConflictException;
import com.example.mutsideout_mju.exception.ForbiddenException;
import com.example.mutsideout_mju.exception.errorCode.ErrorCode;
import com.example.mutsideout_mju.repository.UserRepository;
import com.example.mutsideout_mju.repository.usersurvey.UserSurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSurveyRepository userSurveyRepository;
    private final PasswordHashEncryption passwordHashEncryption;

    /**
     * 설문조사 유효 응답갯수로 유저 등급 계산
     */
    @Transactional
    public UserInfoResponseDto calculateUserGrade(User user) {
        long count = userSurveyRepository.countValidSurveyResponse(user.getId());
        UserGrade userGrade = user.determineGrade(count);

        user.setUserGrade(userGrade);
        userRepository.save(user);

        return UserInfoResponseDto.of(user.getName(), userGrade);
    }

    /**
     * 유저 등급 반환
     */
    public UserInfoResponseDto getUserGrade(User user) {
        return UserInfoResponseDto.from(user.getUserGrade());
    }

    /**
     * 유저 탈퇴
     */
    public void deleteUser(User user, DeleteUserDto deleteUserDto) {
        validatePassword(deleteUserDto.getPassword(), user.getPassword());
        userRepository.delete(user);
    }

    /**
     * 유저 정보 수정
     */
    @Transactional
    public void updateUser(User user, UpdateUserDto updateUserDto) {
        validatePassword(updateUserDto.getOriginPassword(), user.getPassword());
        if (updateUserDto.getNewName() != null && !updateUserDto.getNewName().isEmpty()) {
            //중복된 이름이 있을 경우
            if (userRepository.findByName(updateUserDto.getNewName()).isPresent()) {
                throw new ConflictException(ErrorCode.DUPLICATED_NAME);
            }
            user.setName(updateUserDto.getNewName());
        }
        if (updateUserDto.getNewPassword() != null && !updateUserDto.getNewPassword().isEmpty()) {
            user.setPassword(passwordHashEncryption.encrypt(updateUserDto.getNewPassword()));
        }
        userRepository.save(user);
    }

    /**
     * 유저 전체 정보(이메일, 이름, 등급) 조회
     */
    public UserInfoResponseDto getMyPage(User user){
        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.of(user.getEmail(), user.getName(), user.getUserGrade());
        return userInfoResponseDto;
    }

    /**
     * 비밀번호 일치 여부 확인
     */
    public void validatePassword(String plainPassword, String hashedPassword) {
        if (!passwordHashEncryption.matches(plainPassword, hashedPassword)) {
            throw new ForbiddenException(ErrorCode.NO_ACCESS, "비밀번호 정보가 일치하지 않습니다.");
        }
    }
}
