package fpt.edu.capstone.utils;

import fpt.edu.capstone.dto.payment.RevenueResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<RevenueResponse> revenueResponses;

    public ExcelExporter(List<RevenueResponse> revenueResponses) {
        this.revenueResponses = revenueResponses;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Revenue");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Ngày", style);
        createCell(row, 1, "Nhà tuyển dụng", style);
        createCell(row, 2, "Dịch vụ mua", style);
        createCell(row, 3, "Thành tiền", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        int totalAmount =0;
        for (RevenueResponse r : revenueResponses) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            LocalDate createdAt = r.getCreatedAt().toLocalDate();

            createCell(row, columnCount++, createdAt.toString(), style);
            createCell(row, columnCount++, r.getRecruiterName(), style);
            createCell(row, columnCount++, r.getRentalPackageName(), style);
            createCell(row, columnCount++, r.getAmount(), style);
            totalAmount+=r.getAmount();
        }
        Row r = sheet.createRow(revenueResponses.size());
        createCell(r, 0, "TỔNG",style);
        createCell(r, 3,totalAmount, style);
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();

    }
}
