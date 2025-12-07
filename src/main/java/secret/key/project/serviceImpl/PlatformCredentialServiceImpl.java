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
import secret.key.project.mapper.PlatformCredentialMapper;
import secret.key.project.repository.PlatformCredentialRepository;
import secret.key.project.service.PlatformCredentialService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PlatformCredentialServiceImpl implements PlatformCredentialService {

    private final PlatformCredentialRepository platformCredentialRepository;

    public PlatformCredentialServiceImpl(PlatformCredentialRepository platformCredentialRepository){
        this.platformCredentialRepository = platformCredentialRepository;
    }

    @Override
    public Page<PlatformCredentialDTO> getPlatformCredentialByPagination(Pageable pageable) {
        log.info("Listado con paginación de las platformas OK!");
        return platformCredentialRepository.findAll(pageable).map(PlatformCredentialMapper::toDTO);
    }

    @Override
    public PlatformCredentialDTO createPlatformCredential(PlatformCredentialDTO platformCredentialDTO) {

        if(platformCredentialDTO == null){
            throw new IllegalArgumentException("La plataforma no puede ser nula!");
        }

        PlatformCredential entity = PlatformCredentialMapper.toEntity(platformCredentialDTO);
        PlatformCredential saved =  platformCredentialRepository.save(entity);

        log.info("Plataforma registrada: {}", saved);
        return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public PlatformCredentialDTO updatePlarformCredential(PlatformCredentialDTO platformCredentialDTO, String id) {

    if (id == null){
        throw new IllegalArgumentException("El id no puede ser nulo!!");
    }

    PlatformCredential entity = platformCredentialRepository.findById(id).orElseThrow(() -> {
        log.error("Error al actualizar, plataforma no encontrada con el id: {}", id);
        return new PlatformCredentialNoEncontradoException("Plataforma no encontrada! con el id: " + id);
    });

    entity.setName(platformCredentialDTO.getName());
    entity.setUrl(platformCredentialDTO.getUrl());
    entity.setUsername(platformCredentialDTO.getUsername());
    entity.setPassword(platformCredentialDTO.getPassword());
    entity.setCreatedDate(platformCredentialDTO.getCreatedDate());

    PlatformCredential saved = platformCredentialRepository.save(entity);

    log.info("Plataforma actualizada: {}", saved);
    return PlatformCredentialMapper.toDTO(saved);
    }

    @Override
    public void deletePlatformCredential(String id) {

        if (id == null){
            throw new IllegalArgumentException("El id no puede ser nulo!!");
        } else if (!platformCredentialRepository.existsById(id)){
            log.error("Error al eliminar, plataforma no encontrada con el id: {}", id);
            throw new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el id:" + id);
        }

        platformCredentialRepository.deleteById(id);
        log.info("Plataforma eliminada!");
    }

    @Override
    public PlatformCredentialDTO getPlatformCredentialByName(String name) {

        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio!!");
        }

        PlatformCredential entity = platformCredentialRepository.findByName(name).orElseThrow(() -> {
            log.error("Error al buscar, plataforma no encontrada con el nombre: {}", name);
            return new PlatformCredentialNoEncontradoException("Plataforma no encontrada con el nombre: " + name);
        });

        log.info("Plataforma encontrada: {}", entity);
        return PlatformCredentialMapper.toDTO(entity);
    }

    @Override
    public List<PlatformCredentialDTO> getAllPlatformCredentials() {

        List<PlatformCredential> list = platformCredentialRepository.findAll();

        log.info("Listado de las plataformas OK!");
        return list.stream().map(PlatformCredentialMapper::toDTO).toList();
    }

    @Override
    public byte[] exportarPlataformasExcel() {
        List<PlatformCredential> list = platformCredentialRepository.findAll();
        return generarExcel(list);
    }

    @Override
    public byte[] exportarPlataformasPDF() {

        List<PlatformCredential> list = platformCredentialRepository.findAll();
        return generarPDF(list);
    }

    //Métodos auxiliares para generar PDF
    private byte[] generarPDF (List<PlatformCredential> datos) {

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
            tableEncabezado.setWidths(new float[] { 50, 50 });

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
            document.add(new Paragraph("Report date: "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), infoFont));
            document.add(Chunk.NEWLINE);

            //Tabla y sus columnas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            PdfPCell header1 = new PdfPCell(new Paragraph("Platform", headerFont));
            header1.setBackgroundColor(new Color(41, 128, 185));
            header1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header1);

            PdfPCell header2 = new PdfPCell(new Paragraph("URL", headerFont));
            header2.setBackgroundColor(new Color(41, 128, 185));
            header2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header2);

            PdfPCell header3 = new PdfPCell(new Paragraph("Username", headerFont));
            header3.setBackgroundColor(new Color(41, 128, 185));
            header3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header3);

            PdfPCell header4 = new PdfPCell(new Paragraph("Password", headerFont));
            header4.setBackgroundColor(new Color(41, 128, 185));
            header4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header4);

            PdfPCell header5 = new PdfPCell(new Paragraph("Date created", headerFont));
            header5.setBackgroundColor(new Color(41, 128, 185));
            header5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(header5);

            //Datos
            for (PlatformCredential dato: datos){
                table.addCell(new PdfPCell(new Paragraph(dato.getName(), contentFont)));
                table.addCell(new PdfPCell(new Paragraph(dato.getUrl(), contentFont)));
                table.addCell(new PdfPCell(new Paragraph(dato.getUsername(), contentFont)));
                table.addCell(new PdfPCell(new Paragraph(dato.getPassword(), contentFont)));
                table.addCell(new PdfPCell(new Paragraph(dato.getCreatedDate().toString(), contentFont)));
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

    //Métodos auxiliares para generar el Excel
    private byte[] generarExcel (List<PlatformCredential> datos){

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Lista de credenciales de plataformas");

            Row headerRow = sheet.createRow(0);
            String [] columnas = {"Plataforma", "URL", "Username", "Password", "Fecha de creación"};

            for (int i=0; i<columnas.length; i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(crearEstiloEncabezado(workbook));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowNum = 1;
            for (PlatformCredential entity: datos){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entity.getName());
                row.createCell(1).setCellValue(entity.getUrl());
                row.createCell(2).setCellValue(entity.getUsername());
                row.createCell(3).setCellValue(entity.getPassword());
                row.createCell(4).setCellValue(entity.getCreatedDate().format(formatter));
            }

            for (int i=0; i<columnas.length; i++){
                sheet.autoSizeColumn(i);
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

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        estilo.setFont(font);
        return estilo;
    }

    //Seguridad
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsuarioException("Usuario no autenticado");
        }
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    public PlatformCredentialDTO createCredential(PlatformCredentialDTO dto) {
        String userId = getCurrentUserId();

        if (platformCredentialRepository.existsByUserIdAndName(userId, dto.getName())) {
            throw new IllegalArgumentException("Ya existe una credencial con ese nombre");
        }

        PlatformCredential credential = PlatformCredentialMapper.toEntity(dto);
        credential.setUserId(userId);
        credential.setCreatedDate(LocalDate.now());

        PlatformCredential saved = platformCredentialRepository.save(credential);
        return PlatformCredentialMapper.toDTO(saved);
    }

    public List<PlatformCredentialDTO> getAllCredentials() {
        String userId = getCurrentUserId();
        List<PlatformCredential> credentials = platformCredentialRepository.findByUserId(userId);
        return PlatformCredentialMapper.toDTOList(credentials);
    }

    public PlatformCredentialDTO getCredentialById(String id) {
        String userId = getCurrentUserId();
        PlatformCredential credential = platformCredentialRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new UsuarioException("Credencial no encontrada"));
        return PlatformCredentialMapper.toDTO(credential);
    }

    public PlatformCredentialDTO updateCredential(String id, PlatformCredentialDTO dto) {
        String userId = getCurrentUserId();

        PlatformCredential existing = platformCredentialRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new UsuarioException("Credencial no encontrada"));

        existing.setName(dto.getName());
        existing.setUrl(dto.getUrl());
        existing.setUsername(dto.getUsername());
        existing.setPassword(dto.getPassword());

        PlatformCredential updated = platformCredentialRepository.save(existing);
        return PlatformCredentialMapper.toDTO(updated);
    }


    public void deleteCredential(String id) {
        String userId = getCurrentUserId();

        PlatformCredential credential = platformCredentialRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new UsuarioException("Credencial no encontrada"));

        platformCredentialRepository.delete(credential);
    }
}
