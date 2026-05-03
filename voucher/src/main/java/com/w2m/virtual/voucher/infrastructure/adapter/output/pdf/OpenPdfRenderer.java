package com.w2m.virtual.voucher.infrastructure.adapter.output.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.w2m.virtual.voucher.application.port.output.PdfRendererOutputPort;
import com.w2m.virtual.voucher.domain.Voucher;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Renderer de voucher con OpenPDF — A4, márgenes 36pt, encabezado cyan W2M, tabla de datos,
 * localizador prominente, total y footer.
 */
@Component
public class OpenPdfRenderer implements PdfRendererOutputPort {

    private static final Color CYAN = new Color(14, 165, 233);   // #0EA5E9
    private static final Color INK = new Color(15, 23, 42);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color SOFT_BG = new Color(241, 250, 255);

    @Override
    public byte[] render(Voucher v) {
        Document doc = new Document(PageSize.A4, 48, 48, 48, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, CYAN);
            Paragraph title = new Paragraph("World to Meet Virtual", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            title.setSpacingAfter(4f);
            doc.add(title);

            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, INK);
            Paragraph subtitle = new Paragraph("Voucher de reserva", subtitleFont);
            subtitle.setSpacingAfter(24f);
            doc.add(subtitle);

            // Locator block (prominent)
            PdfPTable locTable = new PdfPTable(1);
            locTable.setWidthPercentage(100);
            PdfPCell labelCell = new PdfPCell(new Phrase("LOCALIZADOR",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, MUTED)));
            labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            labelCell.setBorder(0);
            labelCell.setPaddingTop(14f);
            labelCell.setBackgroundColor(SOFT_BG);
            locTable.addCell(labelCell);

            PdfPCell codeCell = new PdfPCell(new Phrase(v.localizador(),
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 28, CYAN)));
            codeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            codeCell.setBorderColor(CYAN);
            codeCell.setBorderWidth(1.5f);
            codeCell.setBackgroundColor(SOFT_BG);
            codeCell.setPaddingBottom(16f);
            codeCell.setPaddingTop(4f);
            locTable.addCell(codeCell);
            locTable.setSpacingAfter(24f);
            doc.add(locTable);

            // Data table
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
            PdfPTable data = new PdfPTable(2);
            data.setWidthPercentage(100);
            data.setWidths(new float[]{1f, 2f});
            addRow(data, "Hotel", v.hotelName());
            addRow(data, "Ciudad", v.city());
            addRow(data, "Check-in", v.checkInDate().format(df));
            addRow(data, "Check-out", v.checkOutDate().format(df));
            addRow(data, "Huéspedes", String.valueOf(v.guests()));
            addRow(data, "Titular", v.holderName());
            addRow(data, "Email", v.holderEmail());
            data.setSpacingAfter(20f);
            doc.add(data);

            // Total prominent
            Font totalLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, MUTED);
            Font totalValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, INK);
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{1f, 1f});
            PdfPCell tlc = new PdfPCell(new Phrase("TOTAL", totalLabel));
            tlc.setBorder(0);
            tlc.setPaddingTop(10f);
            tlc.setPaddingBottom(10f);
            totalTable.addCell(tlc);
            PdfPCell tvc = new PdfPCell(new Phrase(
                    String.format("%s %s", v.totalAmount().toPlainString(), v.currency()),
                    totalValue));
            tvc.setBorder(0);
            tvc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tvc.setPaddingTop(6f);
            tvc.setPaddingBottom(10f);
            totalTable.addCell(tvc);
            totalTable.setSpacingAfter(28f);
            doc.add(totalTable);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, MUTED);
            Paragraph footer = new Paragraph(
                    "Reserva confirmada — Conserva este voucher para el check-in.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
        } catch (Exception ex) {
            try { doc.close(); } catch (Exception ignored) {}
            throw new IllegalStateException("Error generando PDF voucher", ex);
        }
        return out.toByteArray();
    }

    private static void addRow(PdfPTable t, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, INK);
        PdfPCell lc = new PdfPCell(new Phrase(label.toUpperCase(), labelFont));
        lc.setBorder(0);
        lc.setPaddingTop(6f);
        lc.setPaddingBottom(6f);
        t.addCell(lc);
        PdfPCell vc = new PdfPCell(new Phrase(value == null ? "" : value, valueFont));
        vc.setBorder(0);
        vc.setPaddingTop(6f);
        vc.setPaddingBottom(6f);
        t.addCell(vc);
    }
}
