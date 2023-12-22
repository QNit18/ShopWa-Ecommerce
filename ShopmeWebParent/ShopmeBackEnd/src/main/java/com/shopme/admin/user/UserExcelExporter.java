package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserExcelExporter extends AbstractExporter{

    private XSSFWorkbook workbook ; // Trang excel
    private XSSFSheet sheet ; // 1 bang trong excel

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users"); // Tao 1 trang tinh
        XSSFRow row = sheet.createRow(0); // Tạo hàng mới trên trang tính và đây là hàng số 0

        // Cell Style : cell là 1 ô
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        // Tạo các cột
        createCell(row, 0 , "User Id",  cellStyle);
        createCell(row, 1 , "Email",  cellStyle);
        createCell(row, 2 , "First Name",  cellStyle);
        createCell(row, 3 , "Last Name",  cellStyle);
        createCell(row, 4 , "Roles",  cellStyle);
        createCell(row, 5 , "Enabled",  cellStyle);
    }

    // Tạo 1 ô
    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style){
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof  Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);

    }

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine(); // Khởi tạo dòng field
        writeDataLine(listUsers); // Điền vào trong đó

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void writeDataLine(List<User> listUsers) {
        int rowIndex = 1;

        // Cell Style : cell là 1 ô
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        cellStyle.setFont(font);

        for (User user : listUsers) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getEmail(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIndex++, user.isEnabled(), cellStyle);
        }
    }
}
