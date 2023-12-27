package com.shopme.admin.user.export;

import com.shopme.common.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AbstractExporter {
    public void setResponseHeader(HttpServletResponse response, String contentType,
                                  String extension, String prefix) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String timestamp = dateFormat.format(new Date());
        String fileName = prefix + timestamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;// Chỉ định tên file
        response.setHeader(headerKey, headerValue);
    }
}
