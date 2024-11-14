package com.swproject.hereforus.config.error;

public enum ErrorCode {
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    NOT_FOUND(404, "접근이 거부되었습니다."),
    FORBIDDEN(403, "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "서버에 문제가 발생했습니다.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
