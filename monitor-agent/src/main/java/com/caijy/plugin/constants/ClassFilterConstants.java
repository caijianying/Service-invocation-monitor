package com.caijy.plugin.constants;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author liguang
 * @date 2022/8/8 星期一 8:43 下午
 */
public interface ClassFilterConstants {

    List<String> MYBATIS_PLUS_IGNORED_METHODS = Lists.newArrayList(
        "getBaseMapper","lambdaUpdate","lambdaQuery"
    );

    List<String> JDK_IGNORED_METHODS = Lists.newArrayList(
        "toString","hashCode"
    );
}
