package com.github.ldjmthgh.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ImageDTO {
    @NotNull(message = "图片名称参数不得为空")
    private String imageName;
    private String imageType;
    private String imageSummary;
}
