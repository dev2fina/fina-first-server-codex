package net.fina.server.processing;

import net.fina.server.processing.model.Dependent;
import net.fina.server.processing.model.ProcessItem;

import java.util.List;

public class ProcessItemTopologicalSort {
	private int time;

	public ProcessItemTopologicalSort() {
	}

	public void DFS(List<ProcessItem> tmp) {

		for (ProcessItem item : tmp) {
			item.dependent.status = Dependent.Status.UNKNOWN;
		}

		for (ProcessItem item : tmp) {
			if (item.dependent.status == Dependent.Status.UNKNOWN) {
				DFS_VISIT(tmp, item);
			}
		}

	}

	private void DFS_VISIT(List<ProcessItem> tmp, ProcessItem item) {
		item.dependent.status = Dependent.Status.IN_PROGRESS;
		time = time + 1;
		item.dependent.start = time;

		for (long dependent : item.dependent.dependentIds) {

			ProcessItem tmpCell = new ProcessItem();
			tmpCell.nodeId = dependent;

			int index = tmp.indexOf(tmpCell);

			if (index >= 0) {

				tmpCell = tmp.get(index);

				if (tmpCell.dependent.status == Dependent.Status.UNKNOWN) {
					DFS_VISIT(tmp, tmpCell);
				}

			}
		}

		item.dependent.status = Dependent.Status.FINISHED;
		time = time + 1;
		item.dependent.end = time;
	}
}
