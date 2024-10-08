package com.example.mutsideout_mju.controller;

import com.example.mutsideout_mju.authentication.AuthenticatedUser;
import com.example.mutsideout_mju.dto.request.user.DeleteUserDto;
import com.example.mutsideout_mju.dto.request.user.UpdateUserDto;
import com.example.mutsideout_mju.dto.response.ResponseDto;
import com.example.mutsideout_mju.dto.response.user.UserInfoResponseDto;
import com.example.mutsideout_mju.entity.User;
import com.example.mutsideout_mju.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 유저 등급 조회
    @GetMapping("/grade")
    public ResponseEntity<ResponseDto<UserInfoResponseDto>> getUserGrade(@AuthenticatedUser User user) {
        UserInfoResponseDto userInfoResponseDto = userService.getUserGrade(user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "유저 등급 조회 완료", userInfoResponseDto), HttpStatus.OK);
    }

    // 유저 정보 수정
    @PatchMapping
    public ResponseEntity<ResponseDto<Void>> updateUser(@AuthenticatedUser User user,
                                                        @RequestBody @Valid UpdateUserDto updateUserDto) {
        userService.updateUser(user, updateUserDto);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "유저 정보 수정 완료"), HttpStatus.OK);
    }

    // 유저 탈퇴
    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteUser(@AuthenticatedUser User user,
                                                        @RequestBody @Valid DeleteUserDto deleteUserDto) {
        userService.deleteUser(user, deleteUserDto);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "회원 탈퇴 성공"), HttpStatus.OK);
    }

    // 유저 전체 정보(이메일, 이름, 등급) 조회
    @GetMapping("/mypage")
    public ResponseEntity<ResponseDto<UserInfoResponseDto>> getMyPage(@AuthenticatedUser User user) {
        UserInfoResponseDto userInfoResponseDto = userService.getMyPage(user);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "회원 정보 조회 완료", userInfoResponseDto), HttpStatus.OK);
    }
}
