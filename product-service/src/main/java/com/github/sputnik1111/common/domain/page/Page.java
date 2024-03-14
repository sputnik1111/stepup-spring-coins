package com.github.sputnik1111.common.domain.page;

import java.util.List;

public class Page<T>{
    private final List<T> content;

    public Page(List<T> content) {
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }
}
