package ru.stepup.spring.coins.core.dtos;

import java.util.List;

public class PageDto<T> {
    private List<T> content;

    public PageDto(List<T> content) {
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }
}
