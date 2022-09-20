import com.alibaba.fastjson.JSON;

import com.caijy.plugin.model.TraceSegment;
import com.caijy.plugin.model.TraceSegmentModel;
import com.caijy.plugin.utils.TraceSegmentBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author liguang
 * @date 2022/8/17 星期三 8:53 下午
 */
public class SimpleTest {

    @Test
    public void test(){
//        TraceSegmentBuilder.add(TraceSegmentModel.builder().methodName("com.ywwl.foundation.teamwork.web.CommonsController.init").processFlag(1).build());
//        TraceSegmentBuilder.add(TraceSegmentModel.builder().methodName("com.ywwl.foundation.teamwork.web.CommonsController.init").processFlag(0).build());
//        TraceSegment traceSegment = TraceSegmentBuilder.buildTraceSegment();
//        System.out.println(JSON.toJSONString(traceSegment));

//        HashSet<Integer> hashSet = Sets.newHashSet(2, 3, 1, 4, 5);
//        System.out.println(hashSet.stream().sorted().collect(Collectors.toList()));
    }
}
