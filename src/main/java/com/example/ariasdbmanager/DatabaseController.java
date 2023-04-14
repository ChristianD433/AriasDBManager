package com.example.ariasdbmanager;

import database.connection.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
@CrossOrigin(origins = "http://localhost:4200")
public class DatabaseController {
    private DatabaseConnectionService databaseConnectionService;


    @PostMapping("/connect/mysql")
    public ResponseEntity<?> connect(@RequestBody MySQLDatabaseConnectionInfo dbInfo) {
        this.databaseConnectionService = new MySQLDatabaseConnectionService(dbInfo);
        MySQLDatabaseConnectionService serverDatabaseConnectionService = (MySQLDatabaseConnectionService) databaseConnectionService;
        try {
            serverDatabaseConnectionService.createConeccion();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body("{\"message\": \"Error al conectarsse a la BBDD\"}");
        }
        List<String> listaBBDD = serverDatabaseConnectionService.getDatabases();
        if (listaBBDD == null || listaBBDD.isEmpty())
            return ResponseEntity.ok().body("{\"message\": \"No se encontraron bases de datos\"}");
        else return ResponseEntity.ok().body(listaBBDD);
    }

    @PostMapping("/connect/oracle")
    public ResponseEntity<?> connectOracle(@RequestBody OracleDatabaseConnectionInfo dbInfo) {
        this.databaseConnectionService = new OracleDatabaseConnectionService(dbInfo);
        return getResponseEntitySingleDBConection();
    }

    private ResponseEntity<?> getResponseEntitySingleDBConection() {
        try {
            databaseConnectionService.createConeccion();
        } catch (SQLException e) {
            return ResponseEntity.ok().body("{\"message\": \"Error al conectarse a la BBDD\"}");
        }
        ArrayList<String> listaTablas = databaseConnectionService.getTables();
        if (listaTablas != null)
            if (listaTablas.isEmpty())
                return ResponseEntity.ok().body("{\"message\": \"No se encontraron tablas\"}");
            else return ResponseEntity.ok().body(listaTablas);
        else return ResponseEntity.ok().body("{\"message\": \"Error al acceder a las tablas\"}");
    }


    @PostMapping("/connected/{databaseName}")
    public ResponseEntity<?> connect(@PathVariable("databaseName") String databaseName) {
        MySQLDatabaseConnectionService serverDatabaseConnectionService = (MySQLDatabaseConnectionService) databaseConnectionService;
        try {
            serverDatabaseConnectionService.enterDataBase(databaseName);
        } catch (SQLException e) {
            return ResponseEntity.ok().body("{\"message\": \"Error al acceder a la base de datos" + databaseName + "\"}");
        }
        ArrayList<String> listaTablas = serverDatabaseConnectionService.getTables();
        if (listaTablas != null) {
            if (listaTablas.isEmpty()) return ResponseEntity.ok().body("{\"message\": \"No se encontraron tablas\"}");
            else return ResponseEntity.ok().body(listaTablas);
        } else return ResponseEntity.ok().body("{\"message\": \"Error al acceder a las tablas\"}");
    }

    @GetMapping("/table/{tableName}/rows")
    public int getTableRows(@PathVariable("tableName") String tableName) {
        return databaseConnectionService.getTotalRows(tableName);
    }

    @GetMapping("/table/{tableName}/data")
    public HashMap<String, ArrayList<String>> getTableData(
            @PathVariable("tableName") String tableName,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize,
            @RequestParam(value = "sortColumn", required = false) String columnName,
            @RequestParam(value = "sortOrder", required = false) Boolean order) {
        return databaseConnectionService.getDataFromTable(tableName, page, pageSize, columnName, order);
    }

    @PostMapping("/connect/sqlite")
    public ResponseEntity<?> uploadAndConnectSQLite(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("sqlite", null);
            file.transferTo(tempFile);
        } catch (IOException e) {
            return ResponseEntity.ok().body("{\"message\": \"Error al subir la bbbdd\"}");
        }
        System.out.println(tempFile.getAbsolutePath());
        SQLiteDatabaseConnectionInfo dbInfo = new SQLiteDatabaseConnectionInfo(tempFile.getAbsolutePath(), file.getOriginalFilename());
        this.databaseConnectionService = new SQLiteDatabaseConnectionService(dbInfo);
        return getResponseEntitySingleDBConection();
    }

    @GetMapping("/connect/sqlite/download")
    public ResponseEntity<Resource> downloadSQLiteFile() throws IOException {
        // Código para crear archivo temporal sqlite
        File sqliteFile = new File(this.databaseConnectionService.getDbInfo().getDbUrl());
        Path path = Paths.get(sqliteFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prueb.db");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(sqliteFile.length()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(sqliteFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/sqlite/download/getfilename")
    public ResponseEntity<String> getSQLiteFileName() {
        SQLiteDatabaseConnectionInfo sqLiteDatabaseConnectionInfo = (SQLiteDatabaseConnectionInfo) this.databaseConnectionService.getDbInfo();
        String fileName = sqLiteDatabaseConnectionInfo.getFileName();
        return ResponseEntity.ok().body("{\"fileName\": \"" + fileName + "\"}");
    }

    @DeleteMapping("/close")
    public void closeDatabase(){
        try {
            this.databaseConnectionService.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/table/{tableName}/update")
    public ResponseEntity<?> updateTableData(
            @PathVariable("tableName") String tableName,
            @RequestBody Map<String, Object> data) {

        String valorOriginal = (String) data.get("originalValue");
        String column = (String) data.get("originalColumn");
        Map<String, String> valores = (Map<String, String>) data.get("myRow");

        try {
            this.databaseConnectionService.updateTableData(tableName, column, valorOriginal, valores);
        } catch (SQLException e) {
            return ResponseEntity.ok().body("{\"message\": \"ERROR: " + e.getSQLState() + ": " + e.getMessage() + "\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"Los datos se actualizaron correctamente\"}");
    }

}
