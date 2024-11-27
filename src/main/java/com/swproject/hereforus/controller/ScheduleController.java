package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.ScheduleDto;
import com.swproject.hereforus.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Schedule", description = "일정 및 할일 관리 REST API에 대한 명세를 제공합니다.")
@RestController
@RequestMapping("/api/auth/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(
            summary = "일정 생성",
            description = """
                    그룹의 일정을 생성하는 API입니다.
                    사용자가 입력한 내용과 일정을 저장하며, 반환 데이터는 생성된 일정의 세부 정보를 포함합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정 생성 성공",
                            content = @Content(schema = @Schema(implementation = ScheduleDto.class))),
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
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @RequestBody ScheduleDto scheduleDto
    ) {
        try {
            Object result = scheduleService.saveSchedule(scheduleDto);
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

    @Operation(
            summary = "일정 수정",
            description = """
                    기존 그룹 일정의 내용을 수정하는 API입니다.
                    요청한 일정 ID에 해당하는 데이터를 수정하며, 수정된 일정 정보를 반환합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정 수정 성공",
                            content = @Content(schema = @Schema(implementation = Page.class))),
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable("id") Long id,
            @RequestBody ScheduleDto scheduleDto
    ) {
        try {
            Object result = scheduleService.updateSchedule(scheduleDto, id);
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

    @Operation(
            summary = "일정 삭제",
            description = """
                    그룹의 특정 일정을 삭제하는 API입니다.\n
                    삭제된 일정의 상태를 반환하며, 성공 시 메시지를 제공합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정 삭제 성공",
                            content = @Content(schema = @Schema(example = "{ \"message\": \"일정이 삭제되었습니다.\" }"))),
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable("id") Long id
    ) {
        try {
            Object result = scheduleService.deleteSchedule(id);
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

    @Operation(
            summary = "일정 조회",
            description = """
                    그룹에 저장된 모든 일정을 조회하는 API입니다.\n
                    선택적으로 `date` 파라미터를 사용하여 특정 월의 일정만 조회할 수 있습니다.\n
                    - `date` 형식은 `YYYY-MM`이어야 하며, 해당 월의 모든 일정이 반환됩니다.\n
                    - `date` 파라미터를 제공하지 않으면 그룹의 모든 일정이 반환됩니다.\n
                    반환 데이터는 일정 목록과 세부 정보를 포함합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정 조회 성공",
                            content = @Content(schema = @Schema(example = """
                                        [
                                            {
                                                "id": 2,
                                                "content": "테스트",
                                                "scheduleDate": "2024-11-26"
                                            },
                                            {
                                                "id": 3,
                                                "content": "테스트2",
                                                "scheduleDate": "2024-11-25"
                                            },
                                            {
                                                "id": 4,
                                                "content": "테스트테스트",
                                                "scheduleDate": "2024-11-25"
                                            }
                                        ]
                                """))
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
    @GetMapping
    public ResponseEntity<?> getSchedule(@RequestParam(name = "date", required = false) String date) {
        try {
            List<ScheduleDto> result = scheduleService.selectSchedule(date);
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
