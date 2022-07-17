package com.caijy.plugin.constants;

/**
 * @author caijy
 * @date 2022/7/17 星期日 10:11 上午
 */
public interface TraceConstant {
    /**
     * Junit Test filter
     **/
    String TEST_KEY_UPPERCASE = "Test";
    String TEST_KEY_LOWERCASE = "test";

    /**
     * Spring AOP filter
     **/
    String BY_SPRING_CGLIB = "BySpringCGLIB";
    String ENHANCER_BY_SPRING_CGLIB = "$$EnhancerBySpringCGLIB";
    String INVOKE = "invoke";
    String GET_INDEX = "getIndex";

    /**
     * exclude in cglib
     **/
    String GET_TARGET_CLASS = "getTargetClass";
    String IS_FROZEN = "isFrozen";
    String SET_CALLBACKS = "setCallbacks";
    String STATIC_HOOK = "STATICHOOK";
    String SET_BEAN_FACTORY = "setBeanFactory";
    String BIND_CALLBACKS = "BIND_CALLBACKS";
    String SET_STATIC_CALLBACKS = "SET_STATIC_CALLBACKS";
}
