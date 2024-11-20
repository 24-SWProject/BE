package com.swproject.hereforus.controller;

import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.dto.FestivalDto;
import com.swproject.hereforus.dto.JwtDto;
import com.swproject.hereforus.entity.Festival;
import com.swproject.hereforus.entity.Performance;
import com.swproject.hereforus.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Tag(name = "Event", description = "서울특별시의 행사 관련 REST API에 대한 명세를 제공합니다. 사용자가 요청한 날짜를 기준으로 진행 중인 행사를 조회합니다.")
@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(
            summary = "축제 조회",
            description = "사용자가 요청한 날짜로부터 서울특별시에서 진행 중인 축제 정보를 조회합니다. 데이터는 서울문화포털에서 제공되며, 축제의 제목, 장소, 시작일, 종료일 등의 정보를 포함합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "축제 데이터 조회 성공.", content = @Content(schema = @Schema(implementation = Festival.class))),
                    @ApiResponse(
                            responseCode = "404", description = "축제 데이터 조회 실패.", content = @Content(schema = @Schema(example = "{\"error\":\"축제 데이터 조회를 실패하였습니다.\"}"))
                    )
            },
            parameters = {
                    @Parameter(
                            name = "date",
                            description = "요청 날짜(yyyy-MM-dd 형식). 해당 날짜를 기준으로 축제 정보를 반환합니다.",
                            example = "2024-11-14"
                    )
            }
    )
    @GetMapping("/festival")
    public ResponseEntity<?> getFestival(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date) throws Exception {
        try {
            List<Festival> festivals = eventService.getFestivalsByDate(date);
            return ResponseEntity.ok(festivals);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto("축제 데이터 조회를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Operation(
            summary = "공연 조회",
            description = "사용자가 요청한 날짜로부터 서울특별시에서 진행 중인 공연 정보를 조회합니다. 데이터는 공연예술통합전산망에서 제공되며, 공연의 제목, 장소, 시작일, 종료일, 카테고리(예: 뮤지컬, 연극) 및 포스터 정보를 포함합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "공연 데이터 조회 성공.", content = @Content(schema = @Schema(implementation = Festival.class))),
                    @ApiResponse(
                            responseCode = "404", description = "공연 데이터 조회 실패.", content = @Content(schema = @Schema(example = "{\"error\":\"공연 데이터 조회를 실패하였습니다.\"}"))
                    )
            },
            parameters = {
                    @Parameter(
                            name = "date",
                            description = "요청 날짜(yyyy-MM-dd 형식). 해당 날짜를 기준으로 공연 정보를 반환합니다.",
                            example = "2024-11-14"
                    )
            }
    )
    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date) throws Exception {
        try {
            List<Performance> performances = eventService.getPerformanceByDate(date);
            return ResponseEntity.ok(performances);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDto errorResponse = new ErrorDto("축제 데이터 조회를 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}