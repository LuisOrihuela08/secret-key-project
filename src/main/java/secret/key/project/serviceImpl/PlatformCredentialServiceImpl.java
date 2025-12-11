package secret.key.project.serviceImpl;

import java.awt.Color;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import secret.key.project.dto.PlatformCredentialDTO;
import secret.key.project.entity.PlatformCredential;
import secret.key.project.entity.User;
import secret.key.project.error.PlatformCredentialExporException;
import secret.key.project.error.PlatformCredentialNoEncontradoException;
import secret.key.project.error.UsuarioException;
import secret.key.project.error.UsuarioExceptionNoContentException;
import secret.key.project.mapper.PlatformCredentialMapper;
import secret.key.project.repository.PlatformCredentialRepository;
import secret.key.project.service.PlatformCredentialService;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlatformCredentialServiceImpl implements PlatformCredentialService {

    private final PlatformCredentialRepository platformCredentialRepository;

    public PlatformCredentialServiceImpl(PlatformCredentialRepository platformCredentialRepository) {
        this.platformCredentialRepository = platformCredentialRepository;
    }

    //Seguridad
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsuarioException("Usuario no autenticado");
        }
        //User user = (User) authentication.getPrincipal();
        //return user.getId();

        Object principal = authentication.getPrincipal();

        // En producción: principal es secret.key.project.entity.User
        if (principal instanceof User) {
            return ((User) principal).getId();
        }

        // En tests con @WithMockUser: principal es org.springframework.security.core.userdetails.User
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Retorna el username como ID para tests
            return ((org.springframework.security.core.userdetails.User) principal).getUsername();
        }

        // Fallback: si principal es un String (username)
        if (principal instanceof String) {
            return (String) principal;
        }

        throw new UsuarioException("Usuario no autenticado correctamente");
    }

    @Override
    public Page<PlatformCredentialDTO> getPlatformCredentialByPagination(Pageable pageable) {

        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            log.error("Paginación inválida: página {} , tamaño {}", pageable.getPageNumber(), pageable.getPageSize());
            throw new IllegalArgumentException("Los parámetros de paginación no pueden ser nulos o menores o iguales a cero!");
        }

        String userId = getCurrentUserId();
        Page<PlatformCredential> credentialPage = platformCredentialRepository.findByUserId(pageable, userId);


        if (credentialPage.isEmpty()) {
            log.error("El usuario no tiene plataformas registradas: {}", userId);
            throw new UsuarioExceptionNoContentException("El usuario no tiene plataformas registradas!");
        }

        log.info("Listado con paginación de las platformas OK! del usuario: {}", userId);
        return credentialPage.map(PlatformCredentialMapper::toDTO);
    }

    @Override
    public PlatformCredentialDTO createPlatformCredential(PlatformCredentialDTO platformCredentialDTO) {

        if (platformCredentialDTO == null) {
            log.error("La plataforma no puede ser nula!");
            throw new IllegalArgumentException("La plataforma no puede ser nula!");
        }

        String userId = getCurrentUserId();

        if (platformCredentialRepository.existsByUserIdAndName(userId, platformCredentialDTO.getName())) {
            log.error("La plataforma : {} ya existe!", platformCredentialDTO.getName());
            throw new PlatformCredentialExporException(("Ya existe una plataforma registrada con el nombre: " + platformCredentialDTO.getName()));
        }

        PlatformCredential entity = PlatformCredentialMapper.toEntity(platformCredentialDTO);

        entity.setUserId(userId);
        //entity.setCreatedDate(LocalDate.now());
        PlatformCredential saved = platformCredentialRepository.save(entity);

        log.info("Plataforma registrada: {}, del usuario: {}", saved, userId);
        return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public PlatformCredentialDTO updatePlarformCredential(PlatformCredentialDTO platformCredentialDTO, String id) {

        if (id == null || platformCredentialDTO == null) {
            log.error("La plataforma y/o id no puede ser nulo");
            throw new IllegalArgumentException("El id y/o plataforma no puede ser nulo!!");
        }

        String userId = getCurrentUserId();

        PlatformCredential existing = platformCredentialRepository.findByIdAndUserId(id, userId).orElseThrow(() -> {
            log.error("Error al actualizar, plataforma no encontrada con el id: {}", id);
            return new UsuarioExceptionNoContentException("Plataforma no encontrada con el id: " + id + " para el usuario: " + userId);

        });

        if (!existing.getName().equals(platformCredentialDTO.getName())) {
            if (platformCredentialRepository.existsByUserIdAndName(userId, platformCredentialDTO.getName())) {
                log.error("La plataforma: {} ya existe ", platformCredentialDTO.getName());
                throw new IllegalArgumentException("Ya existe una plataforma registrada con el nombre: " + platformCredentialDTO.getName());
            }
        }


        existing.setName(platformCredentialDTO.getName());
        existing.setUrl(platformCredentialDTO.getUrl());
        existing.setUsername(platformCredentialDTO.getUsername());
        existing.setPassword(platformCredentialDTO.getPassword());
        existing.setCreatedDate(platformCredentialDTO.getCreatedDate());

        PlatformCredential saved = platformCredentialRepository.save(existing);

        log.info("Plataforma actualizada: {}", saved);
        return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public void deletePlatformCredential(String id) {

        String userId = getCurrentUserId();

        if (id == null) {
            log.error("El id de la plataforma no puede ser nulo");
            throw new IllegalArgumentException("El id no puede ser nulo!!");
        }

        PlatformCredential existing = platformCredentialRepository.findByIdAndUserId(id, userId).orElseThrow(() -> {
            log.error("Error al eliminar, la plataforma no encontrada con el id: {}", id);
            return new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el id: " + id + " para el usuario: " + userId);

        });

        platformCredentialRepository.delete(existing);
        log.info("Plataforma eliminada!");
    }

    @Override
    public PlatformCredentialDTO getPlatformCredentialByName(String name) {

        String userId = getCurrentUserId();

        if (name == null || name.isEmpty()) {
            log.error("La plataforma no puede ser nula o vacia");
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio!!");
        }

        PlatformCredential entity = platformCredentialRepository.findByNameAndUserId(name, userId).orElseThrow(() -> {
            log.error("Plataforma no encontrada con el nombre: {}", name);
            return new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el nombre: " + name + " para el usuario: " + userId);
        });

        log.info("Plataforma encontrada: {}", entity);
        return PlatformCredentialMapper.toDTO(entity);
    }

    @Override
    public List<PlatformCredentialDTO> getAllPlatformCredentials() {

        String userId = getCurrentUserId();
        List<PlatformCredential> list = platformCredentialRepository.findByUserId(userId);

        if (list.isEmpty()) {
            log.error("La lista de plataformas esta vacia para el usuario: {}", userId);
            throw new PlatformCredentialNoEncontradoException("El usuario no tiene plataformas registradas!");
        }

        log.info("Listado de las plataformas OK!");
        return list.stream().map(PlatformCredentialMapper::toDTO).toList();
    }

    @Override
    public byte[] exportarPlataformasExcel() {

        String userId = getCurrentUserId();
        List<PlatformCredential> list = platformCredentialRepository.findByUserId(userId);

        if (list.isEmpty()) {
            log.error("Error al generar Excel, la lista de plataformas para el usuario: {} esta vacia", userId);
            throw new PlatformCredentialNoEncontradoException("El usuario no tiene plataformas registradas para generar el Excel!");
        }

        return generarExcel(list);
    }

    @Override
    public byte[] exportarPlataformasPDF() {

        String userId = getCurrentUserId();
        List<PlatformCredential> list = platformCredentialRepository.findByUserId(userId);

        if (list.isEmpty()) {
            log.error("Error al generar PDF, La lista de plataformas para el usuario: {} esta vacia", userId);
            throw new PlatformCredentialNoEncontradoException("El usuario no tiene plataformas registradas para generar el PDF!");
        }

        return generarPDF(list);
    }

    //PDF
    private byte[] generarPDF(List<PlatformCredential> datos) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            //Titulos y encabezados
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
            Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Font contentFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);


            //Encabezado
            PdfPTable tableEncabezado = new PdfPTable(2);
            tableEncabezado.setWidthPercentage(100);
            tableEncabezado.setSpacingAfter(10f);
            tableEncabezado.setWidths(new float[]{50, 50});

            // Título centrado que ocupa las dos columnas
            PdfPCell header0 = new PdfPCell(new Paragraph("MY CREDENTIALS", titleFont));
            header0.setColspan(2); // Ocupa las dos columnas
            header0.setBackgroundColor(new Color(41, 128, 185)); // 26, 188, 156 Verde turquesa
            header0.setHorizontalAlignment(Element.ALIGN_CENTER);
            header0.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header0.setPadding(15f);
            tableEncabezado.addCell(header0);
            document.add(tableEncabezado);

            //Subtitulo del documento
            document.add(new Paragraph("Report date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), infoFont));
            document.add(Chunk.NEWLINE);

            //Tabla y sus columnas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Headers
            table.addCell(createHeaderCell("Platform", headerFont));
            table.addCell(createHeaderCell("URL", headerFont));
            table.addCell(createHeaderCell("Username", headerFont));
            table.addCell(createHeaderCell("Password", headerFont));
            table.addCell(createHeaderCell("Date created", headerFont));

            //Datos
            for (PlatformCredential dato : datos) {
                table.addCell(createDataCell(dato.getName(), contentFont));
                table.addCell(createDataCell(dato.getUrl(), contentFont));
                table.addCell(createDataCell(dato.getUsername(), contentFont));
                table.addCell(createDataCell(dato.getPassword(), contentFont));
                table.addCell(createDataCell(dato.getCreatedDate().toString(), contentFont));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.close();
        } catch (DocumentException e) {
            log.error("Hubo un error al generar el PDF de las plataformas: {}", e.getMessage());
            throw new IllegalArgumentException("Error al generar el PDF de las plataformas: " + e.getMessage());
        }

        log.info("PDF generado exitosamente!");
        return baos.toByteArray();
    }

    // Método helper para el header
    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(new Color(41, 128, 185));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(1f);
        cell.setBorderColorBottom(new Color(200, 200, 200));
        return cell;
    }

    // Método helper para los datos
    private PdfPCell createDataCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text != null ? text : "", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(10f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(new Color(220, 220, 220));
        return cell;
    }

    //EXCEL
    private byte[] generarExcel(List<PlatformCredential> datos) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Lista de credenciales de plataformas");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] columnas = {"Platform", "URL", "Username", "Password", "Date created"};

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowNum = 1;
            for (PlatformCredential entity : datos) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(entity.getName());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(entity.getUrl());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(entity.getUsername());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(entity.getPassword());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(entity.getCreatedDate().format(formatter));
                cell4.setCellStyle(dataStyle);

                row.setHeightInPoints(25);

            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            log.info("Excel de las plaformas generado exitosamente!");
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Hubo un error al generar el excel de las plataformas: {}", e.getMessage());
            throw new PlatformCredentialExporException("Error al generar el excel de las plataformas: " + e.getMessage());
        }
    }

    //Estilo para el header
    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) style;
        XSSFColor customColor = new XSSFColor(new byte[]{41, (byte)128, (byte)185}, null);
        xssfCellStyle.setFillForegroundColor(customColor);
        xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());

        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        return style;

    }


}
