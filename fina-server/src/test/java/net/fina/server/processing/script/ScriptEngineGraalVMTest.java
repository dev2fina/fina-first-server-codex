package net.fina.server.processing.script;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.script.js.JSTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ScriptEngineGraalVMTest {

    @Before
    public void before() {
        System.setProperty(PropertyKeys.SCRIPT_ENGINE_PROVIDER, ScriptEngineProvider.GRAALVM.name());
    }

    public Map<String, ProcessItem> createProcessItems(String codePrefix, int range) {
        Map<String, ProcessItem> packageReturnItemsByCode = new HashMap<>();

        final long returnId = 1;

        List<ProcessItem> items = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ProcessItem item = new ProcessItem();
            item.returnId = returnId;
            item.code = codePrefix + i;
            item.dataType = MDTNodeDataTypes.NUMERIC;
            item.tableId = 1;
            item.tableType = ReturnTableType.VCT;
            item.tableEvalType = MDTNodeEvalMethods.SUM;

            Dependent dependent = new Dependent();
            dependent.dependentIds = new ArrayList<>();

            item.nodeId = i;
            item.sequence = i;

            for (int j = 0; j < 5; j++) {
                item.idByRowNumber.put(j, (long) j);
                item.values.put(j, j + i + range + "");
            }

            items.add(item);
        }

        for (ProcessItem item : items) {
            packageReturnItemsByCode.put(item.code, item);
        }

        return packageReturnItemsByCode;
    }


    @Test
    public void test() throws Exception {

        int threadCount = 50; // Number of concurrent threads
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ScriptEngineBase scriptEngineBase = ScriptEngineFactory.get();

        for (int i = 1; i <= threadCount; i++) {
            String s = "return tree.lookup('%s')+tree.lookup('%s')+tree.sumifs('%s','%s','>1')";
            String code = "code" + i;
            s = String.format(s, code + "1", code + "2", code + "1", code + "2");
            s = ScriptEngineBase.createFunction(s);
            for (int j = 0; j < 1; j++) {
                scriptEngineBase.compile(s, null);
            }
        }


        for (int i = 1; i <= threadCount; i++) {
            final int index = i;
            executor.execute(() -> {
                ScriptEngineBase se = ScriptEngineFactory.get();

                String s = "return tree.lookup('%s')+tree.lookup('%s')+tree.sumifs('%s','%s','>1')";
                String code = "code" + index;
                s = String.format(s, code + "1", code + "2", code + "1", code + "2");
                JSTree jsTree = new JSTree(createProcessItems(code, 10 * index), new ProcessItem(), 1, null, null);

                String res = se.call(jsTree, ScriptEngineBase.createFunction(s));
                System.out.println(Thread.currentThread().getName() + " : " + new SimpleDateFormat("hh:mm:ss:SSSSSSSSS").format(new Date()) + "  - " + index + " : " + res);
                DecimalFormat df = new DecimalFormat("#.0");
                int val = 200;
                String resultString = df.format(val + 150L * (index - 1));
                Assert.assertEquals(resultString, res);

                latch.countDown();
            });
        }

        try {
            latch.await(); // Wait for all threads to complete
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        executor.shutdown();


    }


    @Test
    public void testCompilationPerformance() {
        List<MDTNode> nodes = IntStream.rangeClosed(1, 300_000)
                .mapToObj(i -> {
                    MDTNode node = new MDTNode(i, i < 50000 ? MDTNodeTypes.VARIABLE : MDTNodeTypes.NODE);

                    node.setEquation(" tree.lookup(\"code" + i + "\")+tree.lookup(\"code" + (i + 1) + "\")");
                    return node;
                })
                .toList();

        long start = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(1);

        ScriptEngineBase scriptEngine = ScriptEngineFactory.get();

        nodes.stream().filter(mdtNode -> mdtNode.getType() == MDTNodeTypes.VARIABLE).forEach(mdtNode -> {
            String script = ScriptEngineBase.createFunction(mdtNode.getEquation());
            count.getAndIncrement();
            for (int i = 0; i < 1; i++) {
                scriptEngine.compile(script, null);
            }
        });
        Assert.assertEquals(50000, count.get());
        System.out.println("Node Size : " + count.get());
        long end = System.currentTimeMillis();
        System.out.println("compile time : " + (end - start));
    }
}
