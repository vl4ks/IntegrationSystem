package org.denisova.integrationapp.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CmsPageResponse {
    private List<CmsSpareDto> content;
    // если в API есть total/number/size — можно добавить
    public List<CmsSpareDto> getContent() { return content; }
    public void setContent(List<CmsSpareDto> content) { this.content = content; }
}
