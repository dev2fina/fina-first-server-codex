package net.fina.server.processing.dependency;

import java.util.List;

public class DependencyItemTopologicalSort {
    private int time;

    public void DFS(List<DependencyItem> tmp) {
        for (DependencyItem item : tmp) {
            item.status = DependencyItem.Status.UNKNOWN;
        }
        for (DependencyItem item : tmp) {
            if (item.status == DependencyItem.Status.UNKNOWN) {
                DFS_VISIT(tmp, item);
            }
        }
    }

    private void DFS_VISIT(List<DependencyItem> tmp, DependencyItem item) {
        item.status = DependencyItem.Status.IN_PROGRESS;
        time = time + 1;
        item.start = time;
        for (long dependent : item.dependentIds) {
            DependencyItem tmpCell = new DependencyItem();
            tmpCell.id = dependent;
            int index = tmp.indexOf(tmpCell);
            if (index >= 0) {
                tmpCell = tmp.get(index);
                if (tmpCell.status == DependencyItem.Status.UNKNOWN) {
                    DFS_VISIT(tmp, tmpCell);
                }
            }
        }
        item.status = DependencyItem.Status.FINISHED;
        time = time + 1;
        item.end = time;
    }
}