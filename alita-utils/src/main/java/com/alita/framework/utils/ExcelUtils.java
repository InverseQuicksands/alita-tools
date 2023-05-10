//package com.alita.framework.utils;
//
//import com.google.common.collect.Maps;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.*;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.URLEncoder;
//import java.nio.channels.FileChannel;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created with IDEA
// *
// * @Description poi操作Excel的工具类，包含校验、读取、写入等操作
// * @Author Zhang Liang
// * @Date 2018/12/9
// */
//public class ExcelUtils<T> {
//
//    private static final String EXCEL_XLS = ".xls";
//    private static final String EXCEL_XLSX = ".xlsx";
//
//
//    /**
//     * 判断Excel的版本,获取Workbook
//     *
//     * @param fileNameAndPath
//     * @return
//     * @throws IOException
//     */
//    private static Workbook getWorkbok(String fileNameAndPath) throws IOException {
//        InputStream inputStream = null;
//        Workbook workbok = null;
//        try {
//            File file = new File(fileNameAndPath);
//            checkExcelVaild(file);
//            inputStream = new FileInputStream(file);
//            workbok = getWorkbok(inputStream, file);
//            inputStream.close();
//        } catch (FileNotFoundException e) {
//            throw new FileNotFoundException("文件获取失败～～～");
//        } catch (NullPointerException var1) {
//            throw new NullPointerException("文件名为空～～～");
//        }
//
//        return workbok;
//    }
//
//
//    /**
//     * 判断Excel的版本,获取Workbook
//     *
//     * @param inputStream
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    private static Workbook getWorkbok(InputStream inputStream, File file) throws IOException {
//        Workbook workbok = null;
//        checkExcelVaild(file);
//        try {
//            if (file.getName().endsWith(EXCEL_XLS)) {
//                //Excel 2003
//                workbok = new HSSFWorkbook(inputStream);
//            } else if (file.getName().endsWith(EXCEL_XLSX)) {
//                // Excel 2007-2016
//                workbok = new XSSFWorkbook(inputStream);
//            }
//            inputStream.close();
//        } catch (IOException var1) {
//            throw new IOException("Workbook创建失败～～～");
//        }
//
//        return workbok;
//    }
//
//
//    /**
//     * 判断Excel的版本,获取Workbook
//     *
//     * @param inputStream
//     * @param endsWithName
//     * @return
//     * @throws IOException
//     */
//    private static Workbook getWorkbok(InputStream inputStream, String endsWithName) throws IOException {
//        Workbook workbok = null;
//        try {
//            if (StringUtils.isNotBlank(endsWithName)) {
//                // wb = WorkbookFactory.create(in); 这种方式 Excel2003/2007-2016都是可以处理的
//                if (endsWithName.equals(EXCEL_XLS)) {
//                    //Excel 2003
//                    workbok = new HSSFWorkbook(inputStream);
//                } else if (endsWithName.equals(EXCEL_XLSX)) {
//                    // Excel 2007-2016
//                    workbok = new XSSFWorkbook(inputStream);
//                }
//            } else {
//                throw new IllegalArgumentException("文件名后缀不能为空～～～");
//            }
//            inputStream.close();
//        } catch (IOException var1) {
//            throw new IOException("Workbook创建失败～～～");
//        }
//
//        return workbok;
//    }
//
//
//    /**
//     * 判断文件是否是excel
//     *
//     * @throws Exception
//     */
//    private static void checkExcelVaild(File file) throws FileNotFoundException {
//        if (!file.exists()) {
//            throw new FileNotFoundException("该文件不存在～～～");
//        }
//        boolean existed = file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX));
//        if (!existed) {
//            throw new FileNotFoundException("文件不是Excel～～～");
//        }
//    }
//
//
//    /**
//     * 读取默认Sheet页的内容
//     *
//     * @param filepath 文件全路径
//     */
//    public static String readExcel(String filepath) throws IOException {
//        String readContext = "";
//        try {
//            Workbook workbok = getWorkbok(filepath);
//            Sheet sheet = workbok.getSheetAt(0);
//            //循环处理每个sheet页
//            readContext = readExcelSheet(sheet);
//        } catch (FileNotFoundException e) {
//            throw new FileNotFoundException("该文件不存在～～～");
//        }
//        return readContext;
//    }
//
//
//    /**
//     * 读取指定Sheet页的内容
//     *
//     * @param filepath 文件全路径
//     * @param sheetNo  sheet序号,从0开始,如果读取全文sheetNo设置null
//     */
//    public List readExcel(String filepath, Integer sheetNo, T obj) throws Exception {
//        List list = null;
//        ExcelUtils excelUtils = new ExcelUtils();
//        Workbook workbook = getWorkbok(filepath);
//
//        if (sheetNo == null) {
//            int numberOfSheets = workbook.getNumberOfSheets();
//            for (int i = 0; i <= numberOfSheets; i++) {
//                Sheet sheet = workbook.getSheetAt(i);
//                if (sheet == null) {
//                    continue;
//                }
//                //循环处理每个sheet页
//                list = excelUtils.readExcelSheet(obj, sheet);
//            }
//        } else {
//            Sheet sheet = workbook.getSheetAt(sheetNo);
//            if (sheet != null) {
//                //循环处理每个sheet页
//                list = excelUtils.readExcelSheet(obj, sheet);
//            }
//        }
//
//        return list;
//    }
//
//
//    /**
//     * 读取默认Sheet页的内容
//     * @param sheet
//     * @return
//     */
//    private static String readExcelSheet(Sheet sheet) {
//        StringBuffer buffer = new StringBuffer();
//        // 得到excel的总记录条数
//        int totalRow = sheet.getLastRowNum();
//        for (int i = 0; i <= totalRow; i++) {
//            Row row = sheet.getRow(i);
//            if (row == null) {
//                continue;
//            }
//            // 列数
//            int columNos = row.getLastCellNum();
//            for (int j = 0; j <= columNos; j++) {
//                Cell cell = row.getCell(j);
//                if (cell != null) {
//                    cell.setCellType(CellType.STRING);
//                    Object value = getValue(cell);
//                    buffer.append(value);
//                }
//            }
//        }
//        return buffer.toString();
//    }
//
//
//    /**
//     * 读取Excel到对象中，对象的属性必须和Excel的列一致
//     *
//     * @param obj   对象
//     * @param sheet sheet页
//     * @return Excel内容的对象集合
//     * @throws NoSuchMethodException
//     * @throws InvocationTargetException
//     * @throws IllegalAccessException
//     */
//    private List readExcelSheet(T obj, Sheet sheet) {
//        List<T> list = new ArrayList<T>();
//        // 得到excel的总记录条数(从0开始，即实际行数-1)
//        int rowNos = sheet.getLastRowNum();
//        // 遍历行
//        for (int i = 0; i <= rowNos; i++) {
//            Row row = sheet.getRow(i);
//            if (row != null) {
//                // 列数
//                int columNos = row.getLastCellNum();
//                T t = RowHandler(columNos, row, obj);
//                list.add(t);
//            }
//        }
//        return list;
//    }
//
//
//    private T RowHandler(int cells, Row row, T obj)  {
//        Class clazz = obj.getClass();
//        Field[] fields1 = clazz.getDeclaredFields();
//        Field[] fields = new Field[fields1.length];
//        // 数组复制 其中：src表示源数组，srcPos表示源数组要复制的起始位置，desc表示目标数组，length表示要复制的长度。
//        System.arraycopy(fields1, 0, fields, 0, fields.length);
//
//        for (int j = 0; j <= cells; j++) {
//            Cell cell = row.getCell(j);
//            if (cell != null) {
//                cell.setCellType(CellType.STRING);
//                Object value = getValue(cell);
//
//                String fieldName = fields[j].getName();
//                String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//                Method setMethod = null;
//                try {
//                    setMethod = clazz.getMethod(setMethodName, new Class[]{});
//                    setMethod.invoke(obj, value);
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (Exception var1) {
//                    var1.printStackTrace();
//                }
//            }
//        }
//
//        return obj;
//    }
//
//
//    private static Object getValue(Cell cell) {
//        Object obj = null;
//        switch (cell.getCellTypeEnum()) {
//            case BOOLEAN:
//                obj = cell.getBooleanCellValue();
//                break;
//            case ERROR:
//                obj = cell.getErrorCellValue();
//                break;
//            case NUMERIC:
//                obj = cell.getNumericCellValue();
//                break;
//            case STRING:
//                obj = cell.getStringCellValue();
//                break;
//            default:
//                break;
//        }
//        return obj;
//    }
//
//
//    /**
//     * 生成Excel，兼容 Excel 2003/2007-2016,这种方式 Excel2003/2007-2016都是可以处理的
//     *
//     * @param list 集合
//     * @param fileName 生成的文件名
//     */
//    public void generateExcel(List<T> list, String fileName) {
//        try {
//            InputStream inputStream = ExcelUtils.class.getResourceAsStream("");
//            Workbook workbook = getWorkbok(inputStream, EXCEL_XLSX);
//            Sheet sheet = workbook.getSheet("Sheet1");
//            int rownum = sheet.getLastRowNum();
//            for (T t : list) {
//                rownum++;
//                Row row = sheet.createRow(rownum);
////                Person person = (Person) t;
////                row.createCell(0).setCellValue(person.getUserName());
////                row.createCell(1).setCellValue(person.getSignTime());
////                row.createCell(2).setCellValue("");
////                row.createCell(3).setCellValue("");
////                row.createCell(4).setCellValue("");
////                row.createCell(5).setCellValue("");
////                row.createCell(6).setCellValue("密码");
////                row.createCell(7).setCellValue("");
////                row.createCell(8).setCellValue(person.getSignDate());
//            }
//            File file = new File(fileName + EXCEL_XLSX);
//            OutputStream outputStream = new FileOutputStream(file);
//            workbook.write(outputStream);
//            outputStream.close();
//            workbook.close();
//        } catch (Exception e) {
//            new RuntimeException("生成Excel失败～～～");
//        }
//    }
//
//
//    /**
//     * 生成Excel，兼容 Excel 2003/2007-2016,这种方式 Excel2003/2007-2016都是可以处理的
//     *
//     * @param obj 对象
//     * @param list 集合
//     * @param fileName 生成的文件名
//     */
//    public void generateExcel(T obj, List<T> list, String fileName) throws Exception{
//        try {
//            InputStream inputStream = ExcelUtils.class.getResourceAsStream("/META-INF/11/Template.xlsx");
//            Workbook workbook = getWorkbok(inputStream, EXCEL_XLSX);
//            // 动态赋值，缺点，所有属性都进行赋值，不能选定单一属性进行赋值
//            iteratorRow(list, obj, workbook);
//
//            File file = new File(System.getProperty("user.dir") +
//                    File.separator + fileName + EXCEL_XLSX);
//            OutputStream outputStream = new FileOutputStream(file);
//            workbook.write(outputStream);
//            outputStream.close();
//            workbook.close();
//        } catch (Exception e) {
//            new RuntimeException("生成Excel失败～～～");
//        }
//    }
//
//
//
//    public Response.ResponseBuilder ResponseOutExcel(T obj, List<T> list, String filepath, String filename)
//                throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Workbook workbook = getWorkbok(filepath);
//        iteratorRow(list, obj, workbook);
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        workbook.write(bos);
//        Response.ResponseBuilder response = Response.ok(bos.toByteArray());
//        String encodefilename = URLEncoder.encode(filename,"UTF-8");
//        response.header("Content-Disposition","attachment;filename*=UTF-8''"+encodefilename+".xls");
//        return  response;
//    }
//
//
//    private void iteratorRow (List<T> list, T obj, Workbook workbook)
//            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Class clazz = obj.getClass();
//        Field[] fields1 = clazz.getDeclaredFields();
//        Field[] fields = new Field[fields1.length-2];
//        System.arraycopy(fields1,2,fields,0,fields.length);
//        Sheet sheet =  workbook.getSheet("Sheet1");
//        int rownum = sheet.getLastRowNum();
//        for(T t:list){
//            rownum++;
//            Row row = workbook.getSheet("Sheet1").createRow(rownum);
//            for(int i = 0;i < fields.length-1;i++){
//                String fieldName = fields[i].getName();
//                String getMethodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
//                Method getMethod = clazz.getMethod(getMethodName,new Class[]{});
//                Object value = getMethod.invoke(t,new Object[]{});
//                String cellValue = "";
//                cellValue = null != value?value.toString():"";
//                Cell cell = row.createCell(i);
//                cell.setCellValue(cellValue);
//            }
//        }
//
//    }
//
//
//    /**
//     * 设置格式
//     *
//     * @param wb
//     * @return
//     */
//    private static Map<String, CellStyle> createStyles(Workbook wb) {
//        Map<String, CellStyle> styles = Maps.newHashMap();
//
//        // 标题样式
//        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
//        // 水平对齐
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        // 垂直对齐
//        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        // 样式锁定
//        titleStyle.setLocked(true);
//        // 前景色
//        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
//        //设置字体样式
//        Font titleFont = wb.createFont();
//        // 字号
//        titleFont.setFontHeightInPoints((short) 16);
//        // 加粗
//        titleFont.setBold(true);
//        // 字体
//        titleFont.setFontName("微软雅黑");
//        titleStyle.setFont(titleFont);
//        styles.put("title", titleStyle);
//
//        // 文件头样式
//        XSSFCellStyle headerStyle = (XSSFCellStyle) wb.createCellStyle();
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//        // 颜色填充方式
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerStyle.setWrapText(true);
//        // 设置边界
//        headerStyle.setBorderRight(BorderStyle.THIN);
//        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderLeft(BorderStyle.THIN);
//        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderTop(BorderStyle.THIN);
//        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderBottom(BorderStyle.THIN);
//        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        Font headerFont = wb.createFont();
//        headerFont.setFontHeightInPoints((short) 12);
//        // 字体颜色
//        headerFont.setColor(IndexedColors.WHITE.getIndex());
//        titleFont.setFontName("微软雅黑");
//        headerStyle.setFont(headerFont);
//        styles.put("header", headerStyle);
//
//        Font cellStyleFont = wb.createFont();
//        cellStyleFont.setFontHeightInPoints((short) 12);
//        cellStyleFont.setColor(IndexedColors.BLUE_GREY.getIndex());
//        cellStyleFont.setFontName("微软雅黑");
//
//        // 正文样式A
//        XSSFCellStyle cellStyleA = (XSSFCellStyle) wb.createCellStyle();
//        // 居中设置
//        cellStyleA.setAlignment(HorizontalAlignment.CENTER);
//        cellStyleA.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyleA.setWrapText(true);
//        cellStyleA.setBorderRight(BorderStyle.THIN);
//        cellStyleA.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderLeft(BorderStyle.THIN);
//        cellStyleA.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderTop(BorderStyle.THIN);
//        cellStyleA.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderBottom(BorderStyle.THIN);
//        cellStyleA.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setFont(cellStyleFont);
//        styles.put("cellA", cellStyleA);
//
//        // 正文样式B:添加前景色为浅黄色
//        XSSFCellStyle cellStyleB = (XSSFCellStyle) wb.createCellStyle();
//        cellStyleB.setAlignment(HorizontalAlignment.CENTER);
//        cellStyleB.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyleB.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
//        cellStyleB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyleB.setWrapText(true);
//        cellStyleB.setBorderRight(BorderStyle.THIN);
//        cellStyleB.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderLeft(BorderStyle.THIN);
//        cellStyleB.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderTop(BorderStyle.THIN);
//        cellStyleB.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderBottom(BorderStyle.THIN);
//        cellStyleB.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setFont(cellStyleFont);
//        styles.put("cellB", cellStyleB);
//
//        return styles;
//    }
//
//    /**
//     * 文件复制    Java NIO包中的transferFrom方法
//     * @param source 文件源位置
//     * @param dest 生成文件位置
//     * @throws IOException
//     */
//    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
//        FileChannel inputChannel = null;
//        FileChannel outputChannel = null;
//        try {
//            inputChannel = new FileInputStream(source).getChannel();
//            outputChannel = new FileOutputStream(dest).getChannel();
//            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//        } finally {
//            inputChannel.close();
//            outputChannel.close();
//        }
//    }
//
//
//}
