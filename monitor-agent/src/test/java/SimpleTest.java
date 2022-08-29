import com.alibaba.fastjson.JSON;

import com.caijy.plugin.TraceSegment;
import com.caijy.plugin.TraceSegmentModel;
import com.caijy.plugin.utils.TraceSegmentBuilder;
import org.junit.Test;

/**
 * @author liguang
 * @date 2022/8/17 星期三 8:53 下午
 */
public class SimpleTest {

    @Test
    public void test(){
        TraceSegmentBuilder.add(TraceSegmentModel.builder().methodName("com.ywwl.foundation.teamwork.web.CommonsController.init").processFlag(1).build());
        TraceSegmentBuilder.add(TraceSegmentModel.builder().methodName("com.ywwl.foundation.teamwork.web.CommonsController.init").processFlag(0).build());
        TraceSegment traceSegment = TraceSegmentBuilder.buildTraceSegment();
        System.out.println(JSON.toJSONString(traceSegment));
    }
}
