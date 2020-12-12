package com.qy.ticket.util;

import com.qy.ticket.dto.manager.SumRecordDTO;
import com.qy.ticket.dto.manager.TblRecordDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.qy.ticket.util.DayPDFUtils.*;

/**
 * @author zhaozha
 * @date 2020/1/14 上午10:53
 */
public class MonthPDFUtils {
  public static final String IMG = ResourceUtils.CLASSPATH_URL_PREFIX + "img/QY.png";
  // 明细表头
  public static final String[] detailsTableHeader = {"日期", "①充值金额", "②退款金额", "③销售金额", "销售票数"};
  // 明细间隔
  public static final int[] detailsCellsWidth = {1, 1, 1, 1, 1};
  public static final int[] sumCellsWidth = {1, 1, 2, 2};

  public static void export(
      String name,
      String time,
      HttpServletResponse response,
      List<TblRecordDTO> tblRecordDTOS,
      Integer proportion,
      SumRecordDTO sumRecordDTO,
      String productName)
      throws Exception {
    String FONT =
        ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "font/simhei.ttf").toString();
    String pdfName = name + ".pdf";
    // 设置
    response.setContentType("application/pdf");
    response.setHeader(
        "content-disposition", "inline;filename=" + URLEncoder.encode(pdfName, "UTF-8"));
    // A4
    Document document = new Document(new RectangleReadOnly(595F, 842F));
    PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
    document.open();
    // 字体
    BaseFont bfHei = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    Font font = new Font(bfHei, 10);
    // 第一行标题
    Paragraph paragraphOne = new Paragraph("轻游（深圳）科技有限公司", new Font(bfHei, 12));
    paragraphOne.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraphOne);

    Paragraph paragraphF = new Paragraph("合作单位：" + name, new Font(bfHei, 10));
    paragraphF.setAlignment(Element.ALIGN_LEFT);
    document.add(paragraphF);

    // 第二行时间
    Paragraph paragraphTwo = null;
    if (time.length() == 4) {
      paragraphTwo =
          new Paragraph(
              "时间："
                  + time
                  + "                                                                 "
                  + "打印时间："
                  + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
              new Font(bfHei, 10));
    } else {
      paragraphTwo =
          new Paragraph(
              "时间："
                  + time
                  + "                                                              "
                  + "打印时间："
                  + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
              new Font(bfHei, 10));
    }
    paragraphTwo.setAlignment(Element.ALIGN_LEFT);
    document.add(paragraphTwo);
    document.add(blankRow6);
    PdfPTable wxTable = getTable(detailsCellsWidth);
    setPdfTableTitle(wxTable, font, detailsTableHeader, "");
    setPdfTableBody(wxTable, font, tblRecordDTOS, detailsTableHeader);
    document.add(wxTable);
    document.add(blankRow6);
    document.add(blankRow6);
    document.add(blankRow6);
    document.add(blankRow6);
    Paragraph paragraphThree = new Paragraph("景区" + productName + "收入分成表", new Font(bfHei, 12));
    paragraphThree.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraphThree);
    document.add(blankRow6);
    document.add(blankRow6);
    document.add(blankRow6);
    document.add(blankRow6);
    PdfPTable sumTable = getTable(sumCellsWidth);
    String[] sumTableHeader = {
      "日期", "销售总额", name + "分成" + (100 - proportion) + "%", "轻游（深圳）科技有限公司分成" + proportion + "%"
    };
    setSumTableTitle(sumTable, font, sumTableHeader);
    setSumTableBody(sumTable, font, sumRecordDTO, time, sumTableHeader, proportion);
    document.add(sumTable);
    document.add(blankRow6);
    PdfContentByte canvas = writer.getDirectContent();
    Paragraph tail = new Paragraph("轻游（深圳）科技有限公司", font);
    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, tail, 368, 145, 0);
    Paragraph tailTime = new Paragraph(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), font);
    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, tailTime, 390, 131, 0);

    // Image对象
    Image img = Image.getInstance(ResourceUtils.getURL(IMG));
    img.setAlignment(Image.LEFT | Image.TEXTWRAP);
    img.setBorder(Image.BOX);
    img.setBorderWidth(10);
    img.setAbsolutePosition(384, 70);
    img.setBorderColor(BaseColor.WHITE);
    img.scaleToFit(122, 122);

    document.add(img);
    document.close();
    writer.close();
  }

  public static void setSumTableBody(
      PdfPTable table,
      Font font,
      SumRecordDTO sumRecordDTO,
      String time,
      String[] sumTableHeader,
      Integer proportion)
      throws Exception {
    for (int i = 0; i < sumTableHeader.length; i++) {
      String value = "";
      switch (i) {
        case 0:
          value = time;
          break;
        case 1:
          value = sumRecordDTO.getIncome() + "";
          break;
        case 2:
          value =
              ((sumRecordDTO.getIncome().add(sumRecordDTO.getWxFee()))
                      .multiply(new BigDecimal(100 - proportion))
                      .divide(new BigDecimal("100"))
                      .setScale(2, BigDecimal.ROUND_HALF_UP))
                  + "";
          break;
        case 3:
          value =
              ((sumRecordDTO.getIncome().add(sumRecordDTO.getWxFee()))
                      .multiply(new BigDecimal(proportion))
                      .divide(new BigDecimal("100"))
                      .setScale(2, BigDecimal.ROUND_HALF_UP))
                  + "";
          break;
        default:
          value = "";
          break;
      }
      PdfPCell cell = new PdfPCell();
      cell.setFixedHeight(18);
      Paragraph para = new Paragraph(value, font);
      cell.setPhrase(para);
      cell.setBorderWidth(1);
      cell.setBorderColor(BaseColor.BLACK);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }
  }

  // 填充汇总表头
  public static void setSumTableTitle(PdfPTable table, Font font, String[] sumTableHeader)
      throws Exception {
    for (int i = 0; i < sumTableHeader.length; i++) {
      PdfPCell cell = new PdfPCell();
      cell.setFixedHeight(18);
      Paragraph para = new Paragraph(sumTableHeader[i], font);
      cell.setPhrase(para);
      cell.setBorderWidth(1);
      cell.setBorderColor(BaseColor.BLACK);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }
  }

  private static void setPdfTableBody(
      PdfPTable table, Font font, List<TblRecordDTO> tblRecordDTOS, String[] detailsTableHeader)
      throws Exception {
    for (TblRecordDTO tblRecordDTO : tblRecordDTOS) {
      for (int i = 0; i < detailsTableHeader.length; i++) {
        String value = "";
        switch (i) {
          case 0:
            value = tblRecordDTO.getTime();
            break;
          case 1:
            value = tblRecordDTO.getAmount() + "";
            break;
          case 2:
            value = tblRecordDTO.getRefundAmount() + "";
            break;
          case 3:
            value = tblRecordDTO.getIncome().add(tblRecordDTO.getWxFee()) + "";
            break;
          case 4:
            value = tblRecordDTO.getEffectiveNum() + "";
            break;
          default:
            value = "";
            break;
        }
        Paragraph para = new Paragraph(value, font);
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(para);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
      }
    }
  }
}
