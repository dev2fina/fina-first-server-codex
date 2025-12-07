package net.fina.server.returns.entity;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ReturnItemLiteTest {

    @Test
    public void valueLengthTest() {

        // null
        ReturnItemLite t1 = new ReturnItemLite();
        Assert.assertNull(t1.getValue());

        // 1000
        ReturnItemLite t2 = new ReturnItemLite();
        t2.setValue(RandomStringUtils.random(1000), MDTNodeDataTypes.TEXT);
        Assert.assertEquals(t2.getValue().length(), 1000);

        // 4000
        ReturnItemLite t3 = new ReturnItemLite();
        t3.setValue(RandomStringUtils.random(4000), MDTNodeDataTypes.TEXT);
        Assert.assertEquals(t3.getValue().length(), 4000);

        // 5000
        ReturnItemLite t4 = new ReturnItemLite();
        t4.setValue(RandomStringUtils.random(5000), MDTNodeDataTypes.TEXT);
        Assert.assertEquals(t4.getValue().length(), 4000);

    }
}
