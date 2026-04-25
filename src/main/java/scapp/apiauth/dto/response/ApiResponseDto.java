package scapp.apiauth.dto.response;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ApiResponseDto<T> {

        private OffsetDateTime timestamp;
        private boolean status;
        private String message;
        private T data;
        private Map<String, List<String>> errors;

        public ApiResponseDto() {
            this.timestamp = OffsetDateTime.now();
        }

        public static <T> ApiResponseDto<T> success(String message, T data) {
            ApiResponseDto<T> response = new ApiResponseDto<>();
            response.setStatus(true);
            response.setMessage(message);
            response.setData(data);
            response.setErrors(Collections.emptyMap());
            return response;
        }

        public static <T> ApiResponseDto<T> success(T data) {
            return success("Operación exitosa.", data);
        }

        public static <T> ApiResponseDto<T> fail(String message, Map<String, List<String>> errors, T data) {
            ApiResponseDto<T> response = new ApiResponseDto<>();
            response.setStatus(false);
            response.setMessage(message);
            response.setData(data);
            response.setErrors(errors == null ? Collections.emptyMap() : errors);
            return response;
        }

        public static <T> ApiResponseDto<T> fail(String message, Map<String, List<String>> errors) {
            return fail(message, errors, null);
        }

        public OffsetDateTime getTimestamp() {
            return timestamp;
        }

        public boolean isStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }

        public Map<String, List<String>> getErrors() {
            return errors;
        }

        public void setTimestamp(OffsetDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setData(T data) {
            this.data = data;
        }

        public void setErrors(Map<String, List<String>> errors) {
            this.errors = errors;
        }
    }


