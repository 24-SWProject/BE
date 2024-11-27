package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.group.GroupCodeDto;
import com.swproject.hereforus.dto.group.GroupDto;
import com.swproject.hereforus.dto.group.GroupOutputDto;
import com.swproject.hereforus.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            description = "그룹 생성 시 발급된 고유 그룹 코드 8글자를 조회합니다. 이 코드는 다른 사용자를 그룹에 초대하거나 초대를 받을 때 사용됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "그룹 코드 조회 성공",
                            content = @Content(schema = @Schema(implementation = GroupCodeDto.class, description = "8글자 길이의 그룹 코드. 소문자, 특수문자, 숫자가 포함됩니다."))),
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
            description = """
            현재 그룹의 프로필 정보를 확인합니다. 그룹 닉네임, 기념일, 대표 이미지와 같은 세부 정보를 포함합니다.
            프로필 이미지는 데이터베이스에 Blob 형태로 저장되며, 클라이언트에 반환될 때는 Base64 인코딩된 문자열로 반환됩니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = """
                            그룹 프로필 조회 성공.
                            반환되는 프로필 이미지(`profileImg`) 필드는 Base64로 인코딩된 문자열입니다.
                            """,
                            content = @Content(schema = @Schema(implementation = GroupOutputDto.class))),
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
            GroupOutputDto profile = groupService.fetchGroupProfile();
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
                            content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = GroupOutputDto.class))),
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
    public ResponseEntity<?> updateGroupProfile(@ModelAttribute GroupDto groupDto) {
        try {
            GroupOutputDto updatedProfile = groupService.saveGroupProfile(groupDto);
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
                            description = "그룹 참여 실패",
                            content = @Content(examples = {
                                    @ExampleObject(name = "그룹에 중복 참여하거나 최대 인원을 초과한 경우", value = "이미 그룹에 참여하였거나, 그룹 인원이 초과되었습니다."),
                                    @ExampleObject(name = "그룹의 초대자가 그룹에 참여한 경우", value = "그룹의 초대자는 참여할 수 없습니다.")}
                            )),
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


    /* 기념일 조회 */
    @Operation(
            summary = "기념일 조회",
            description = "현재 디데이와 함께 100일 단위 및 주년 단위의 주요 기념일 정보를 제공합니다. 반환된 데이터는 기념일 날짜와 해당 날짜까지 남은 일수를 포함합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기념일 조회 성공",
                            content = @Content(examples = {
                                    @ExampleObject(name = "기념일 등록되어 있는 경우", value = """
                                            {
                                                "currentDay": 330,
                                                "milestones": [
                                                    {
                                                        "day": 365,
                                                        "date": "2025-01-03",
                                                        "remain": 37
                                                    },
                                                    {
                                                        "day": 400,
                                                        "date": "2025-02-05",
                                                        "remain": 70
                                                    },
                                                    {
                                                        "day": 500,
                                                        "date": "2025-05-16",
                                                        "remain": 170
                                                    },
                                                    {
                                                        "day": 600,
                                                        "date": "2025-08-24",
                                                        "remain": 270
                                                    },
                                                    {
                                                        "day": 700,
                                                        "date": "2025-12-02",
                                                        "remain": 370
                                                    },
                                                    {
                                                        "day": 730,
                                                        "date": "2026-01-03",
                                                        "remain": 402
                                                    },
                                                    {
                                                        "day": 800,
                                                        "date": "2026-03-12",
                                                        "remain": 470
                                                    },
                                                    {
                                                        "day": 900,
                                                        "date": "2026-06-20",
                                                        "remain": 570
                                                    },
                                                    {
                                                        "day": 1000,
                                                        "date": "2026-09-28",
                                                        "remain": 670
                                                    },
                                                    {
                                                        "day": 1095,
                                                        "date": "2027-01-03",
                                                        "remain": 767
                                                    },
                                                    {
                                                        "day": 1100,
                                                        "date": "2027-01-06",
                                                        "remain": 770
                                                    },
                                                    {
                                                        "day": 1200,
                                                        "date": "2027-04-16",
                                                        "remain": 870
                                                    }
                                                ]
                                            }
                                            """
                                    ),
                                    @ExampleObject(name="기념일 등록되어 있지 않은 경우", value= """
                                            {}
                                            """)
                            })
                    ),
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
    @GetMapping("/anniv")
    public ResponseEntity<?> getGroupAnnivarsary() {
        try {
            Map<?, ?> result = groupService.selectGroupAnniversary();
            return ResponseEntity.ok(result);
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
