//package com.LetMeDoWith.LetMeDoWith.integration.task;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
//import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
//import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
//import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
//import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
//import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
//import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
//import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
//import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
//import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
//import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.DowithTaskJpaRepository;
//import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.hamcrest.core.IsEqual;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.util.LinkedMultiValueMap;
//
//@Slf4j
//@SpringBootTest
//@ActiveProfiles("local")
//@AutoConfigureMockMvc
//public class DowithTaskIntegrationTest {
//
//  static final String BASE_URL = "/api/v1/task/dowith";
//  static final String CREATE_DOWITH_TASK = "";
//
//  @Autowired
//  ObjectMapper objectMapper;
//  @Autowired
//  MockMvc mockMvc;
//  @Autowired
//  MemberJpaRepository memberJpaRepository;
//  @Autowired
//  AccessTokenProvider accessTokenProvider;
//  @Autowired
//  DowithTaskJpaRepository dowithTaskJpaRepository;
//
//
//  private Member member;
//  private AccessToken memberAccessToken;
//
//  @BeforeEach
//  void beforeEach() {
//    memberJpaRepository.deleteAll();
//    dowithTaskJpaRepository.deleteAll();
//
//    member = memberJpaRepository.save(Member.builder()
//        .status(MemberStatus.NORMAL)
//        .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
//        .nickname("test")
//        .selfDescription("test description")
//        .gender(Gender.MALE)
//        .dateOfBirth(LocalDate.of(1995, 11, 4))
//        .type(MemberType.USER)
//        .build());
//    memberAccessToken = accessTokenProvider.createAccessToken(member.getId());
//  }
//
//  private ResultActions requestCreateDowithTask(AccessToken accessToken, CreateDowithTaskReqDto requestBody) throws Exception {
//    LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
//    headerMap.add("AUTHORIZATION", "Bearer" + accessToken.getToken());
//
//    return mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + CREATE_DOWITH_TASK)
//        .headers(new HttpHeaders(headerMap))
//        .contentType(MediaType.APPLICATION_JSON)
//        .accept(MediaType.APPLICATION_JSON)
//        .characterEncoding(StandardCharsets.UTF_8)
//        .content(objectMapper.writeValueAsString(requestBody)))
//        .andDo(System.out::println);
//  }
//
//  @Test
//  @DisplayName("[SUCCESS] Dowith Task 등록")
//  void createDowithTask() throws Exception {
//    // given
//    LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
//    CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime, Boolean.FALSE,
//        null);
//
//    // when
//    ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
//
//    // then
//    resultActions.andExpect(status().is2xxSuccessful())
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].id").exists())
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].taskCategoryId").value(requestBody.taskCategoryId()))
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].title").value(requestBody.title()))
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].status").value(DowithTaskStatus.WAIT.getCode()))
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].date").value(DateTimeUtil.toFormatString(startDateTime.toLocalDate())))
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].startTime").value(DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
//        .andExpect(jsonPath("$.data.dowithTaskDtos[0].isRoutine").value(Boolean.FALSE))
//        .andDo(System.out::println);
//
//  }
//
//  @Test
//  @DisplayName("[SUCCESS] Dowith Task 등록")
//  void createDowithTaskWithRoutine() throws Exception {
//    // given
//    LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
//    LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
//    LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
//    ArrayList<LocalDate> targetDates = new ArrayList<>();
//    targetDates.add(startDateTime.toLocalDate());
//    targetDates.add(routineDate1);
//    targetDates.add(routineDate2);
//    Collections.sort(targetDates);
//    CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime, Boolean.TRUE,
//        List.of(routineDate1, routineDate2));
//
//    // when
//    ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
//
//    // then
//    for (int i = 0; i < targetDates.size(); i++) {
//      resultActions.andExpect(status().is2xxSuccessful())
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].id").exists())
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].taskCategoryId").value(requestBody.taskCategoryId()))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].title").value(requestBody.title()))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].status").value(DowithTaskStatus.WAIT.getCode()))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].date").value(DateTimeUtil.toFormatString(targetDates.get(i))))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].startTime").value(DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].isRoutine").value(Boolean.TRUE))
//          .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].routineDates").value(new IsEqual<>(List.of(DateTimeUtil.toFormatString(targetDates.get(0)), DateTimeUtil.toFormatString(targetDates.get(1)), DateTimeUtil.toFormatString(targetDates.get(2)))), List.class))
//          .andDo(System.out::println);
//    }
//
//    // TODO - 등록 실패 케이스 - 화면설계서 구체화되면 작성
//  }
//}