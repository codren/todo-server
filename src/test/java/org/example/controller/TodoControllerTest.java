package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.TodoEntity;
import org.example.model.TodoRequest;
import org.example.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    // Spring 자동으로 MockMvc bean 생성
    @Autowired
    MockMvc mvc;

    //  ApplicationContext 내에서 수행될 Mock 객체 지정
    @MockBean
    TodoService todoService;

    private TodoEntity expected;

    @BeforeEach
    void setup() {
        this.expected = new TodoEntity();
        this.expected.setId(123L);
        this.expected.setTitle("Test Title");
        this.expected.setOrder(0L);
        this.expected.setCompleted(false);
    }


    @Test
    void create() throws Exception {
        when(this.todoService.add(any(TodoRequest.class)))
                .then((i) -> {  // add() 메소드로 들어오는 매개변수를  i로 받음
                    // i로 넘어오는 매개변수에서 0번 째
                    TodoRequest request = i.getArgument(0, TodoRequest.class);
                    return new TodoEntity(this.expected.getId(),
                            request.getTitle(),
                            this.expected.getOrder(),
                            this.expected.getCompleted());
                });

        TodoRequest request = new TodoRequest();
        request.setTitle("ANY TITLE");

        // 객체를 String 문자열로 변환 (serialize)
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        // MVC 동작 수행
        // post Method 로 "/" 요청
        // 요청 타입은 Json 형식
        // 반환되는 MockMvc(TodoEntity) 가 200ok 이고
        // 반환되는 Json 문자열에서 key = title 인 것의 value = ANY TITLE 이라면 OK
        this.mvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)).andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("ANY TITLE"));

    }
}