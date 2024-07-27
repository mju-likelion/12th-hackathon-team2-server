package com.example.mutsideout_mju.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateUserDto {
    @Size(min = 1, max = 10, message = "이름은 최소 1자에서 최대 10자까지 입력 가능합니다.")
    private String newName;

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$", message = "비밀번호는 영문과 숫자,특수기호를 조합하여 8~20글자 미만으로 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자에서 최대 20자까지 입력 가능합니다.")
    private String originPassword;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$", message = "비밀번호는 영문과 숫자,특수기호를 조합하여 8~20글자 미만으로 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자에서 최대 20자까지 입력 가능합니다.")
    private String newPassword;
}
