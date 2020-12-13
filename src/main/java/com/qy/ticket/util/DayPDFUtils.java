package com.qy.ticket.util;

import com.qy.ticket.dto.manager.SumRecordDTO;
import com.qy.ticket.entity.TblRecord;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhaozha
 * @date 2020/1/11 下午4:26
 */
public class DayPDFUtils {
    // 明细表头
    public static final String[] detailsTableHeader = {
            "序号", "票型", "售票时间", "①充值金额", "②退款金额", "③销售金额", "销售票数", "游客电话", "备注"
    };
    // 明细间隔
    public static final int[] detailsCellsWidth = {1, 1, 1, 1, 1, 1, 1, 1, 1};
    // 加粗表头字段
    public static final String boldTitle1 = "③销售金额";
    public static final String boldTitle2 = "微信提现额";
    // 空隙
    public static final Paragraph blankRow6 = new Paragraph(4f, " ");
    // 汇总表头
    public static final String[] sumTableHeader = {"日期", "销售票数", "微信自助扫码销售额", "微信手续费", "微信提现额"};
    // 汇总间隔
    public static final int[] sumCellsWidth = {1, 1, 1, 1, 1};

    public static void export(
            HttpServletResponse response,
            List<TblRecord> tblRecords,
            String pdfName,
            String time,
            SumRecordDTO sumRecordDTO)
            throws Exception {
        String FONT = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "font/simhei.ttf").toString();
        // 设置
        response.setContentType("application/pdf");
        response.setHeader(
                "content-disposition", "inline;filename=" + URLEncoder.encode(pdfName, "UTF-8"));
        // A4
        Document document = new Document(new RectangleReadOnly(842F, 595F));
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        // 字体
        BaseFont bfHei = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfHei, 10);
        // 第一行标题
        Paragraph paragraphOne = new Paragraph(pdfName, new Font(bfHei, 12));
        paragraphOne.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphOne);
        // 第二行时间
        Paragraph paragraphTwo =
                new Paragraph(
                        "时间："
                                + time
                                + "                                                                                                            "
                                + "打印时间："
                                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        new Font(bfHei, 10));
        paragraphTwo.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTwo);
        document.add(blankRow6);
        PdfPTable wxTable = getTable(detailsCellsWidth);
        setPdfTableTitle(wxTable, font, detailsTableHeader, boldTitle1);
        setPdfTableBody(wxTable, font, tblRecords, "合计销售金额", sumRecordDTO, detailsTableHeader);
        document.add(wxTable);
        Paragraph note = new Paragraph("备注：③销售金额=①充值金额-②退款金额", new Font(bfHei, 10));
        note.setAlignment(Element.ALIGN_LEFT);
        document.add(note);
        document.add(blankRow6);
        document.add(blankRow6);
        document.add(blankRow6);
        document.add(blankRow6);
        PdfPTable sumTable = getTable(sumCellsWidth);
        setSumTableTitle(sumTable, font);
        setSumTableBody(sumTable, font, sumRecordDTO, time);
        document.add(sumTable);
        document.add(blankRow6);
        Paragraph sign =
                new Paragraph("确认签字（现场）___________      审   核（财务）___________", new Font(bfHei, 10));
        sign.setAlignment(Element.ALIGN_RIGHT);
        document.add(sign);
        document.close();
        writer.close();
    }

    public static PdfPTable getTable(int[] cellsWidth) throws Exception {
        PdfPTable table = new PdfPTable(cellsWidth.length);
        table.setWidths(cellsWidth);
        table.setTotalWidth(800f);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        return (table);
    }

    public static void setPdfTableTitle(PdfPTable table, Font font, String[] detailsTableHeader, String boldTitle) throws Exception {
        for (int i = 0; i < detailsTableHeader.length; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(18);
            Paragraph para = new Paragraph(detailsTableHeader[i], font);
            cell.setPhrase(para);
            cell.setBorderWidth(1);
            cell.setBorderColor(BaseColor.BLACK);

            if (boldTitle.equals(detailsTableHeader[i])) {
                cell.setBorderWidthLeft(2);
                cell.setBorderWidthRight(2);
                cell.setBorderWidthTop(2);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            }
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private static void setPdfTableBody(PdfPTable table, Font font, List<TblRecord> tblRecords, String sumName, SumRecordDTO sumRecordDTO, String[] detailsTableHeader) throws Exception {
        Integer serialNumber = 0;
        for (TblRecord tblRecord : tblRecords) {
            serialNumber++;
            for (int i = 0; i < detailsTableHeader.length; i++) {
                String value = "";
                switch (i) {
                    case 0:
                        value = serialNumber + "";
                        break;
                    case 1:
                        value = tblRecord.getTicketName();
                        break;
                    case 2:
                        value = new SimpleDateFormat("HH:mm:ss").format(tblRecord.getTime());
                        break;
                    case 3:
                        value = NumberUtil.divide100(new BigDecimal(tblRecord.getAmount()));
                        break;
                    case 4:
                        value = NumberUtil.divide100(new BigDecimal(tblRecord.getRefundAmount()));
                        break;
                    case 5:
                        value = NumberUtil.divide100(new BigDecimal(tblRecord.getIncome()));
                        break;
                    case 6:
                        value = tblRecord.getEffectiveNum() + "";
                        break;
                    case 7:
                        value = tblRecord.getPhoneNum();
                        break;
                    case 8:
                        value = tblRecord.getReason();
                        break;
                    default:
                        value = "";
                        break;
                }
                Paragraph para = new Paragraph(value, font);
                PdfPCell cell = new PdfPCell();
                cell.setPhrase(para);
                cell.setBorderColor(BaseColor.GRAY);
                if (boldTitle1.equals(detailsTableHeader[i])) {
                    cell.setBorderColor(BaseColor.BLACK);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setBorderWidthLeft(2);
                    cell.setBorderWidthRight(2);
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }
        // 汇总金额
        for (int i = 0; i < detailsTableHeader.length; i++) {
            if (i == 4) {
                PdfPCell cell = new PdfPCell();
                Paragraph para = new Paragraph(sumName, font);
                cell.setPhrase(para);
                cell.setBorderColor(BaseColor.BLACK);
                cell.setBorderWidthLeft(2);
                cell.setBorderWidthRight(2);
                cell.setBorderWidthTop(2);
                cell.setBorderWidthBottom(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            } else if (i == 5) {
                PdfPCell cell = new PdfPCell();
                Paragraph para = new Paragraph(NumberUtil.divide100(new BigDecimal(sumRecordDTO.getIncome() + sumRecordDTO.getWxFee())), font);
                cell.setPhrase(para);
                cell.setBorderWidthRight(2);
                cell.setBorderWidthTop(2);
                cell.setBorderWidthBottom(2);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorderColor(BaseColor.BLACK);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            } else {
                PdfPCell cell = new PdfPCell();
                cell.setBorder(0);
                table.addCell(cell);
            }
        }
    }

    // 填充汇总表头
    public static void setSumTableTitle(PdfPTable table, Font font) throws Exception {
        for (int i = 0; i < sumTableHeader.length; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(18);
            Paragraph para = new Paragraph(sumTableHeader[i], font);
            cell.setPhrase(para);
            cell.setBorderWidth(1);
            cell.setBorderColor(BaseColor.BLACK);
            if (boldTitle2.equals(sumTableHeader[i])) {
                cell.setBorderWidthLeft(2);
                cell.setBorderWidthRight(2);
                cell.setBorderWidthTop(2);
            }
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    public static void setSumTableBody(PdfPTable table, Font font, SumRecordDTO sumRecordDTO, String time) throws Exception {
        for (int i = 0; i < sumTableHeader.length; i++) {
            String value = "";
            switch (i) {
                case 0:
                    value = time;
                    break;
                case 1:
                    value = sumRecordDTO.getEffectiveNum() + "";
                    break;
                case 2:
                    value = NumberUtil.divide100(new BigDecimal(sumRecordDTO.getIncome() + sumRecordDTO.getWxFee()));
                    break;
                case 3:
                    value = NumberUtil.divide100(new BigDecimal(sumRecordDTO.getWxFee()));
                    break;
                case 4:
                    value = NumberUtil.divide100(new BigDecimal(sumRecordDTO.getIncome()));
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

            if (boldTitle2.equals(sumTableHeader[i])) {
                cell.setBorderWidthLeft(2);
                cell.setBorderWidthRight(2);
                cell.setBorderWidthBottom(2);
            }

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }


}
