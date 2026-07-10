package com.carlssonstudio.api.proposal;

import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import com.carlssonstudio.api.entity.ProposalComplexity;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProposalPdfGenerator {

    @Value("${app.proposals.storage-path:proposals}")
    private String storagePath;

    // Brand colors
    private static final DeviceRgb INDIGO =
        new DeviceRgb(99, 102, 241);
    private static final DeviceRgb INDIGO_LIGHT =
        new DeviceRgb(224, 231, 255);
    private static final DeviceRgb DARK =
        new DeviceRgb(26, 26, 46);
    private static final DeviceRgb GRAY =
        new DeviceRgb(107, 114, 128);
    private static final DeviceRgb LIGHT_GRAY =
        new DeviceRgb(243, 244, 246);
    private static final DeviceRgb GREEN =
        new DeviceRgb(16, 185, 129);

    public String generate(LeadResponse lead,
                           RecommendationResponse rec,
                           int timelineWeeks,
                           ProposalComplexity complexity)
            throws IOException {

        // Ensure storage directory exists
        Path dir = Paths.get(storagePath);
        Files.createDirectories(dir);

        String filename = String.format("proposal-%d-%s.pdf",
            lead.getId(), rec.getFoundationSlug());
        Path outputPath = dir.resolve(filename);

        PdfWriter writer =
            new PdfWriter(outputPath.toString());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(40, 50, 40, 50);

        PdfFont bold = PdfFontFactory.createFont(
            StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(
            StandardFonts.HELVETICA);
        PdfFont italic = PdfFontFactory.createFont(
            StandardFonts.HELVETICA_OBLIQUE);

        // ── HEADER ──────────────────────────────────────────
        Table header = new Table(
            UnitValue.createPercentArray(new float[]{60, 40}))
            .useAllAvailableWidth();

        Cell leftHeader = new Cell()
            .setBorder(Border.NO_BORDER)
            .add(new Paragraph("CARLSSON STUDIO")
                .setFont(bold).setFontSize(18)
                .setFontColor(INDIGO))
            .add(new Paragraph("Custom Business Software")
                .setFont(regular).setFontSize(9)
                .setFontColor(GRAY))
            .add(new Paragraph("carlssonstudio.com")
                .setFont(regular).setFontSize(9)
                .setFontColor(GRAY));

        Cell rightHeader = new Cell()
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT)
            .add(new Paragraph("PROJECT PROPOSAL")
                .setFont(bold).setFontSize(11)
                .setFontColor(DARK))
            .add(new Paragraph("Date: " +
                LocalDate.now().format(
                    DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .setFont(regular).setFontSize(9)
                .setFontColor(GRAY))
            .add(new Paragraph("Ref: CS-" + lead.getId())
                .setFont(regular).setFontSize(9)
                .setFontColor(GRAY));

        header.addCell(leftHeader);
        header.addCell(rightHeader);
        doc.add(header);

        // Divider
        doc.add(new LineSeparator(
            new com.itextpdf.kernel.pdf.canvas.draw
                .SolidLine(1.5f))
            .setStrokeColor(INDIGO)
            .setMarginTop(8).setMarginBottom(16));

        // ── CLIENT DETAILS ───────────────────────────────────
        doc.add(sectionHeading("CLIENT DETAILS", bold));

        Table clientTable = new Table(
            UnitValue.createPercentArray(new float[]{30, 70}))
            .useAllAvailableWidth()
            .setMarginBottom(16);

        addTableRow(clientTable, "Name",
            lead.getName(), regular, bold);
        addTableRow(clientTable, "Email",
            lead.getEmail(), regular, bold);
        addTableRow(clientTable, "Company",
            nullSafe(lead.getCompany(), "—"), regular, bold);
        addTableRow(clientTable, "Company Size",
            nullSafe(lead.getCompanySize(), "—") + " employees",
            regular, bold);
        addTableRow(clientTable, "Industry",
            lead.getIndustry(), regular, bold);
        addTableRow(clientTable, "Project Type",
            lead.getBuildType(), regular, bold);

        doc.add(clientTable);

        // ── RECOMMENDED FOUNDATION ───────────────────────────
        doc.add(sectionHeading("RECOMMENDED FOUNDATION", bold));

        // Foundation card
        Table foundationCard = new Table(
            UnitValue.createPercentArray(new float[]{70, 30}))
            .useAllAvailableWidth()
            .setBackgroundColor(INDIGO_LIGHT)
            .setBorder(new SolidBorder(INDIGO, 1))
            .setMarginBottom(16);

        Cell foundationLeft = new Cell()
            .setBackgroundColor(INDIGO_LIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(12)
            .add(new Paragraph(rec.getFoundationName())
                .setFont(bold).setFontSize(16)
                .setFontColor(INDIGO))
            .add(new Paragraph(rec.getMatchReason())
                .setFont(regular).setFontSize(9)
                .setFontColor(DARK)
                .setMarginTop(4));

        Cell foundationRight = new Cell()
            .setBackgroundColor(INDIGO_LIGHT)
            .setBorder(Border.NO_BORDER)
            .setPadding(12)
            .setTextAlignment(TextAlignment.RIGHT)
            .add(new Paragraph(rec.getMatchScore() + "%")
                .setFont(bold).setFontSize(28)
                .setFontColor(INDIGO))
            .add(new Paragraph("Match Score")
                .setFont(regular).setFontSize(8)
                .setFontColor(GRAY));

        foundationCard.addCell(foundationLeft);
        foundationCard.addCell(foundationRight);
        doc.add(foundationCard);

        // ── BUSINESS NEEDS ───────────────────────────────────
        doc.add(sectionHeading("IDENTIFIED BUSINESS NEEDS", bold));
        doc.add(tagList(lead.getProblems(), regular, DARK));

        // ── REQUESTED FEATURES ───────────────────────────────
        doc.add(sectionHeading("REQUESTED FEATURES", bold));
        doc.add(tagList(lead.getFeatures(), regular, INDIGO));

        // ── PROJECT SCOPE ────────────────────────────────────
        doc.add(sectionHeading("PROJECT SCOPE", bold));

        Table scopeTable = new Table(
            UnitValue.createPercentArray(
                new float[]{33, 33, 34}))
            .useAllAvailableWidth()
            .setMarginBottom(16);

        addScopeCell(scopeTable, "COMPLEXITY",
            complexity.name(), bold, regular,
            complexity == ProposalComplexity.HIGH ? INDIGO
            : complexity == ProposalComplexity.MEDIUM ? GRAY
            : GREEN);

        addScopeCell(scopeTable, "ESTIMATED TIMELINE",
            timelineWeeks + " weeks", bold, regular, INDIGO);

        addScopeCell(scopeTable, "APPROACH",
            "Foundation-first", bold, regular, GREEN);

        doc.add(scopeTable);
        
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        // ── DELIVERABLES ─────────────────────────────────────
        doc.add(sectionHeading("DELIVERABLES", bold));

        List<String> deliverables = List.of(
            "Full source code (private GitHub repository)",
            "Deployed application on agreed infrastructure",
            "Database schema with seed data",
            "API documentation (Swagger/OpenAPI)",
            "Admin user manual (PDF)",
            "30-day post-launch support",
            "Knowledge transfer session"
        );

        for (String item : deliverables) {
            doc.add(new Paragraph("✓  " + item)
                .setFont(regular).setFontSize(10)
                .setFontColor(DARK)
                .setMarginBottom(3));
        }

        doc.add(new Paragraph(" ").setMarginTop(8));

        // ── APPROACH ─────────────────────────────────────────
        doc.add(sectionHeading("OUR APPROACH", bold));

        doc.add(new Paragraph(
            "Carlsson Studio begins every project from a " +
            "proven, production-tested foundation — not a " +
            "blank page. The " + rec.getFoundationName() +
            " foundation has been built and deployed in " +
            "real business environments, which means your " +
            "project starts with working architecture, " +
            "tested patterns, and known edge cases already " +
            "handled. Customization is applied on top of " +
            "this solid base, significantly reducing " +
            "development time and risk.")
            .setFont(regular).setFontSize(10)
            .setFontColor(DARK)
            .setMarginBottom(16)
            .setPadding(14));

        // ── NEXT STEPS ───────────────────────────────────────
        doc.add(sectionHeading("NEXT STEPS", bold));

        String[][] steps = {
            {"1", "Reply to this proposal",
             "Confirm your interest and any adjustments"},
            {"2", "Discovery call",
             "30-minute call to align on requirements"},
            {"3", "Detailed specification",
             "Wireframes and technical spec document"},
            {"4", "Development begins",
             "Weekly progress updates throughout"}
        };

        for (String[] step : steps) {
            Table stepRow = new Table(
                UnitValue.createPercentArray(
                    new float[]{8, 30, 62}))
                .useAllAvailableWidth()
                .setMarginBottom(6);

            stepRow.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(step[0])
                    .setFont(bold).setFontSize(14)
                    .setFontColor(INDIGO)));
            stepRow.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(step[1])
                    .setFont(bold).setFontSize(10)
                    .setFontColor(DARK)));
            stepRow.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(step[2])
                    .setFont(regular).setFontSize(10)
                    .setFontColor(GRAY)));

            doc.add(stepRow);
        }

        // ── FOOTER ───────────────────────────────────────────
        doc.add(new Paragraph(" ").setMarginTop(20));
        doc.add(new LineSeparator(
            new com.itextpdf.kernel.pdf.canvas.draw
                .SolidLine(0.5f))
            .setStrokeColor(GRAY)
            .setMarginBottom(8));

        doc.add(new Paragraph(
            "Carlsson Studio  ·  carlssonstudio.com  ·  " +
            "hello@carlssonstudio.com  ·  " +
            "Independent Software Studio, Medan, Indonesia")
            .setFont(italic).setFontSize(8)
            .setFontColor(GRAY)
            .setTextAlignment(TextAlignment.CENTER));

        doc.close();

        log.info("Proposal PDF generated: {}", outputPath);
        return outputPath.toString();
    }

    // ── HELPERS ──────────────────────────────────────────────

    private Paragraph sectionHeading(String text, PdfFont font) {
        return new Paragraph(text)
            .setFont(font).setFontSize(9)
            .setFontColor(INDIGO)
            .setCharacterSpacing(1.5f)
            .setMarginTop(12).setMarginBottom(6);
    }

    private void addTableRow(Table table, String label,
                             String value,
                             PdfFont regular, PdfFont bold) {
        table.addCell(new Cell()
            .setBorder(Border.NO_BORDER)
            .setBackgroundColor(LIGHT_GRAY)
            .setPadding(5)
            .add(new Paragraph(label)
                .setFont(bold).setFontSize(9)
                .setFontColor(GRAY)));
        table.addCell(new Cell()
            .setBorder(Border.NO_BORDER)
            .setPadding(5)
            .add(new Paragraph(value)
                .setFont(regular).setFontSize(10)
                .setFontColor(DARK)));
    }

    private Div tagList(List<String> items,
                        PdfFont font, DeviceRgb color) {
        Div div = new Div().setMarginBottom(12);
        if (items == null || items.isEmpty()) {
            div.add(new Paragraph("—")
                .setFont(font).setFontSize(10));
            return div;
        }
        // Render as inline tags in a paragraph
        Paragraph p = new Paragraph();
        for (String item : items) {
            p.add(new Text("  " + item + "  ")
                .setFont(font).setFontSize(9)
                .setFontColor(color)
                .setBackgroundColor(INDIGO_LIGHT)
                .setBorderRadius(
                    new com.itextpdf.layout.properties
                        .BorderRadius(10)));
            p.add(new Text("  "));
        }
        div.add(p);
        return div;
    }

    private void addScopeCell(Table table, String label,
                              String value,
                              PdfFont bold, PdfFont regular,
                              DeviceRgb color) {
        table.addCell(new Cell()
            .setBackgroundColor(LIGHT_GRAY)
            .setBorder(new SolidBorder(ColorConstants.WHITE, 2))
            .setPadding(12)
            .setTextAlignment(TextAlignment.CENTER)
            .add(new Paragraph(label)
                .setFont(bold).setFontSize(8)
                .setFontColor(GRAY)
                .setCharacterSpacing(1f))
            .add(new Paragraph(value)
                .setFont(bold).setFontSize(14)
                .setFontColor(color)
                .setMarginTop(4)));
    }

    private String nullSafe(String value, String fallback) {
        return (value == null || value.isBlank())
            ? fallback : value;
    }
}