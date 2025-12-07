package net.fina.server.tools.mdt.v2;

import net.fina.common.client.returns.ReturnTableType;
import org.apache.poi.ss.util.CellReference;

public class Option {
	private String sheetName;

	private String code;
	private String description;

	private CellReference startCell;
	private CellReference endCell;

	private ReturnTableType returnTableType;

	public Option(ReturnTableType returnTableType, String start, String end, String sheetName, String code, String description) {
		this.returnTableType = returnTableType;
		this.startCell = new CellReference(start);
		this.endCell = new CellReference(end);
		this.sheetName = sheetName;
		this.code = code;
		this.description = description;
	}

	public short getStartColumn() {
		return startCell.getCol();
	}

	public int getStartRow() {
		return startCell.getRow();
	}

	public short getEndColumn() {
		return endCell.getCol();
	}

	public int getEndRow() {
		return endCell.getRow();
	}

	public CellReference getStartCell() {
		return startCell;
	}

	public void setStartCell(CellReference startCell) {
		this.startCell = startCell;
	}

	public CellReference getEndCell() {
		return endCell;
	}

	public void setEndCell(CellReference endCell) {
		this.endCell = endCell;
	}

	public ReturnTableType getReturnTableType() {
		return returnTableType;
	}

	public void setReturnTableType(ReturnTableType returnTableType) {
		this.returnTableType = returnTableType;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

}
