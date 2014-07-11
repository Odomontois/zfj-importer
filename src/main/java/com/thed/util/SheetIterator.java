package com.thed.util;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.thed.model.ImportJob;

public class SheetIterator implements Iterator<Sheet> {

	public static Iterable<Sheet> create(final Workbook wb, final ImportJob importJob) {
		return new Iterable<Sheet>() {
			
			@Override
			public Iterator<Sheet> iterator() {
				return new SheetIterator(wb, importJob);
			}
		};
	}
	
	private Workbook wb;
	private ImportJob importJob;
	private int sheetNo = -1;
	private Sheet next;
	
	public SheetIterator(Workbook wb, ImportJob importJob) {
		this.wb = wb;
		this.importJob = importJob;
		next = getNext();
		
	}

	@Override
	public boolean hasNext() {
		
		return next != null;
	}
	
	@Override
	public Sheet next() {
		Sheet next = this.next;
		this.next = getNext();
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
	private Sheet getNext() {
		while(sheetNo + 1 < wb.getNumberOfSheets()) {
			sheetNo++;
			Sheet next = wb.getSheetAt(sheetNo);
			
			if (importJob.getSheetFilter().isDefined()) {
				if (skipSheet(next, importJob)) {
					continue;
				}
				
			} else {
				if (sheetNo > 0) {
					break; // only process first sheet if sheet filter is not defined
				}
			}
			return next;
		} 
		return null;
	}
	
	private boolean skipSheet(Sheet sheet, ImportJob importJob) {
		if (importJob.getSheetFilter().nonEmpty()) {
			return !importJob.getSheetFilter().get().matcher(sheet.getSheetName()).matches();
		}
		return false;
	}

}
