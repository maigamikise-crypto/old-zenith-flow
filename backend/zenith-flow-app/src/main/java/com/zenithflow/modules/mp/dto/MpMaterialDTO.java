package com.zenithflow.modules.mp.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 公众号素材
 *
 *
 */
@Data
@ApiModel(value = "公众号素材")
public class MpMaterialDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mediaId;


}
