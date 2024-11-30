package com.swproject.hereforus.controller;

import com.swproject.hereforus.config.error.CustomException;
import com.swproject.hereforus.dto.ErrorDto;
import com.swproject.hereforus.service.RecommendService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Recommend", description = "데이트 코스 추천 REST API에 대한 명세를 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    @Operation(
            summary = "AI 코스 추천",
            description = """
                          사용자가 입력한 키워드를 바탕으로 AI가 데이트 코스를 추천하는 API입니다.\n
                          키워드에는 지역, 분위기, 음식 스타일, 선호하는 활동 등이 포함될 수 있으며, 정확한 정보를 조회하기 위한 ID값과 함께 반환됩니다.
                          예: {"keyword": ["중구", "실내", "조용한", "저녁", "한식", "축제", "야경"]}
                          """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "코스 추천 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "festival_results": [
                                                            {
                                                                "collection": "festival_hereforus",
                                                                "distance": 840.2509765625,
                                                                "id": 574,
                                                                "text": "카테고리는 축제입니다. 축제 이름은 '[은평역사한옥박물관] Blue, Hidden nature', 축제 장소는 삼각산금암미술관 기획전시실, 축제 시작 일자는 2024-11-07, 축제 종료 일자는 2024-12-08, 축제 대상은 누구나입니다. "
                                                            }
                                                        ],
                                                        "food_results": [
                                                            {
                                                                "collection": "food_hereforus",
                                                                "distance": 844.4337158203125,
                                                                "id": 843,
                                                                "text": "카테고리는 음식점, 종류는 한식입니다. 음식점의 이름은 '1974', 음식점의 전화번호는 , 자치구는 강북구, 상세 주소는 서울특별시 강북구 4.19로 45, 2층 (수유동)에 위치해있습니다."
                                                            }
                                                        ],
                                                        "llm_response": "데이트하기 딱 좋은 저녁 시간대가 다가오는데 어디서 뭘 하면 좋을지 고민이군. 내가 괜찮은 코스를 알려줄게!  \\n\\n먼저 첫 번째로는 19:00에 시작하는 함께하는 사랑밭 감사콘서트: 사랑의 시간, 온기를 더하다 공연을 보러 가는 건 어때? 이 공연은 다양한 장르의 음악을 들을 수 있고 가수들도 많이 출연한다고 하니까 눈과 귀가 모두 즐거울 거야. \\n\\n두 번째로는 20:00에 한식집인 1974 에서 밥을 먹는 게 좋겠어. 여기는 반찬도 맛있고 메인 메뉴도 맛있다고 하니 든든하게 배를 채우기에 딱 좋아. 그리고 가게 내부 인테리어도 한옥 스타일이라 한국 전통 느낌이 물씬 나서 기분 좋게 식사할 수 있을 거야.  \\n\\n마지막으로는 21:00에 시작하는 별별산책 인문학 콘서트를 보러 가는 건 어때? 산책이라는 이름처럼 야외에서 진행되는 콘서트인데 잔잔한 음악과 함께 이야기를 듣는 거니까 차분하게 마무리하기 좋을 거야.  \\n\\n이렇게 가면 하루 종일 재밌게 놀 수 있을 거야!",
                                                        "performance_results": [
                                                            {
                                                                "collection": "performance_hereforus",
                                                                "distance": 839.177490234375,
                                                                "id": 475,
                                                                "text": "카테고리는 공연, 장르는 복합입니다. 공연의 제목은 '함께하는 사랑밭 감사콘서트: 사랑의 시간, 온기를 더하다', 공연 장소는 성신여자대학교 운정그린캠퍼스, 공연 시작 일자는 2024-11-30, 공연 종료 일자는 2024-11-30입니다. "
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 형식",
                            content = @Content(
                                    schema = @Schema(
                                            example = "{ \"statusCode\": 400, \"message\": \"잘못된 요청 데이터입니다.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "리소스에 접근할 권한이 없거나 인증 정보가 유효하지 않음",
                            content = @Content(
                                    schema = @Schema(
                                            example = "{ \"statusCode\": 403, \"message\": \"사용자 인증에 실패하였습니다.\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러 발생",
                            content = @Content(
                                    schema = @Schema(
                                            example = "{ \"statusCode\": 500, \"message\": \"서버에 문제가 발생했습니다.\" }"
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "keyword": ["강북구", "실내", "자연친화적인", "저녁", "한식", "공연"]
                                    }
                                    """
                            )
                    )
            )
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecommend(@RequestBody Map<String, List<String>> request) {
        try {
            String response = recommendService.fetchRecommendByAI(request);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8")
                    .body(response);
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
