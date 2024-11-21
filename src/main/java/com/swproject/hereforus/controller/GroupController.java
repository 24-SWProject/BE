package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.GroupCodeDto;
import com.swproject.hereforus.dto.GroupDto;
import com.swproject.hereforus.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@Tag(name = "Group", description = "그룹 관련 REST API에 대한 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/group")
public class GroupController {

    private final GroupService groupService;


    /* 그룹 코드 조회 */
    @Operation(
            summary = "그룹 코드 조회",
            description = "그룹 생성 시 발급된 고유 그룹 코드를 조회합니다. 이 코드는 다른 사용자를 그룹에 초대하거나 초대를 받을 때 사용됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "그룹 코드 조회 성공",
                            content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @ResponseBody
    @GetMapping("/code")
    public ResponseEntity<?> getGroupCode() {
        try {
            Optional<GroupCodeDto> code = groupService.fetchGroupCode();
            return ResponseEntity.ok(code);
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /* 그룹 프로필 조회 */
    @Operation(
            summary = "그룹 프로필 조회",
            description = "현재 그룹의 프로필 정보를 확인합니다. 그룹 닉네임, 기념일, 대표 이미지와 같은 세부 정보를 포함합니다",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "그룹 프로필 조회 성공",
                            content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> getGroupProfile() {
        try {
            Optional<GroupDto> profile = groupService.fetchGroupProfile();
            return ResponseEntity.ok(profile);
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /* 그룹 프로필 수정 */
    @Operation(
            summary = "그룹 프로필 수정",
            description = "그룹 초대자가 그룹 닉네임, 기념일, 대표 이미지를 수정할 수 있습니다. 선택적으로 원하는 항목만 변경할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "그룹 프로필 수정 성공",
                            content = @Content(schema = @Schema(implementation = GroupDto.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }
    )
    @PutMapping
    public ResponseEntity<?> updateGroupProfile(@RequestBody GroupDto groupDto) {
        try {
            GroupDto updatedProfile = groupService.saveGroupProfile(groupDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /* 그룹 참여 */
    @Operation(
            summary = "그룹 참여",
            description = "다른 사용자로부터 제공받은 그룹 코드를 사용하여 그룹에 참여합니다. 그룹의 최대 구성원은 2명입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "그룹 참여를 위한 코드 요청 바디",
                    content = @Content(
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{ \"code\": \"bb050618-53b5-4e09-8b35-d96d856109ce\" }",
                                    summary = "그룹 참여 요청"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "그룹 참여 성공",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 200, \"message\": \"그룹 참여에 성공하였습니다.\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(schema = @Schema(example = "{\"error\":\"사용자 인증에 실패하였습니다.\"}"))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "그룹 중복 참여 시도 또는 최대 인원 초과",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 409, \"message\": \"이미 그룹에 참여하였거나, 그룹 인원이 초과되었습니다.\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(schema = @Schema(example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"))
                    )
            }

    )
    @PostMapping("/join")
    public ResponseEntity<?> joinGroupByCode(@RequestBody Map<String, String> code) {
        try {
            groupService.saveInviteeByCode(code.get("code"));
            return ResponseEntity.ok("그룹 참여에 성공하였습니다.");
        } catch (CustomException e) {
            ErrorDto errorResponse = new ErrorDto(e.getStatus().value(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
