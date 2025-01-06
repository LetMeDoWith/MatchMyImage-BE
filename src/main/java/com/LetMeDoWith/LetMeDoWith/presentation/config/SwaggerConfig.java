package com.LetMeDoWith.LetMeDoWith.presentation.config;

import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.dto.FailResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

@Configuration
@Slf4j
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme().type(Type.HTTP)
                                                            .scheme("bearer")
                                                            .bearerFormat("JWT")
                                                            .in(In.HEADER)
                                                            .name("Authorization");
        
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");
        
        return new OpenAPI().components(
                                new Components().addSecuritySchemes("bearerAuth", securityScheme))
                            .security(Collections.singletonList(securityRequirement));
        
    }
    
    /**
     * Swagger를 통한 API Docs 작성시 기본 기능으로 지원되지 않는 부분을 직접 Custom 하는 Bean.
     *
     * 현재 구현된 Custom 기능 목록:
     * 1. FailResponseStatus 목록을 지정하기만 해도 에러 응답 예시를 자동으로 생성.
     *
     * @return OperationCustomizer 구현체
     */
    @Bean
    public OperationCustomizer customOperation() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorResponses errorResponses = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
            
            if (errorResponses != null) {
                ApiResponses responses = operation.getResponses();
                
                // HTTP Status 별 Fail Status 그룹화
                Map<HttpStatus, List<FailResponseStatus>> failStatusesByHttpStatus =
                    Arrays.stream(errorResponses.errors())
                          .collect(Collectors.groupingBy(FailResponseStatus::getHttpStatusCode));
                
                // HTTP Status 별 응답 예시 생성
                failStatusesByHttpStatus.forEach((httpStatus, failStatusList) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();
                    
                    failStatusList.forEach(failStatus -> {
                        mediaType.addExamples(failStatus.getStatusCode(),
                                              getResponseExample(failStatus));
                    });
                    
                    content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                         mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(httpStatus.value()),
                                             apiResponse);
                });
                
            }
            return operation;
        };
    }
    
    /**
     * 각 FailResponseStatus 별 Example 객체를 생성한다.
     *
     * @param status
     * @return Example 객체
     */
    private Example getResponseExample(FailResponseStatus status) {
        FailResponseDto response = FailResponseDto.builder()
                                                  .statusCode(status.getStatusCode())
                                                  .message(status.getMessage())
                                                  .build();
        
        return new Example()
            .value(response)
            .summary(status.getStatusCode() + " (" + status.getStatusName() + ")")
            .description(status.getMessage());
    }
    
}