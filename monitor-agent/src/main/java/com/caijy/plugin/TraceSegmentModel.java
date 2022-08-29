package com.caijy.plugin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liguang
 * @date 2022/8/17 星期三 7:44 下午
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TraceSegmentModel implements Serializable {

    /**
     * 全方法名
     **/
    private String methodName;

    /**
     * 1=enter或0=exit
     **/
    private Integer processFlag;

}
