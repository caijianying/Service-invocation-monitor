package com.caijy.agent.core.plugin.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author liguang
 * @date 2023/1/3 星期二 11:50 上午
 */
public abstract class AbstractJunction<V> implements ElementMatcher.Junction<V> {
    @Override
    public <U extends V> Junction<U> and(ElementMatcher<? super U> other) {
        return new Conjunction<U>(this, other);
    }

    @Override
    public <U extends V> Junction<U> or(ElementMatcher<? super U> other) {
        return new Disjunction<U>(this, other);
    }
}
