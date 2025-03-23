package com.zhao.trans.dto.response;

import com.zhao.trans.dto.dto.GuideIndexDTO;
import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {
    List<T> items;
    GuideIndexDTO index;
}
