package com.sopt.rescat.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDto {
    public final static int NAME_PARSE_INDEX = 8;
    public final static int CODES_FIELD_STRING_INDEX = 1;

    @ApiModelProperty(example = "nickname", position = 1)
    private String field;

    @ApiModelProperty(example = "닉네임은 특수문자 제외 2~20자이어야 합니다.", position = 2)
    private String message;

    public static ExceptionDto toDto(ObjectError validError) {
        return ExceptionDto.builder()
                .message(validError.getDefaultMessage())
                .field(validError.getCodes()[CODES_FIELD_STRING_INDEX]
                        .substring(NAME_PARSE_INDEX)).build();
    }
}