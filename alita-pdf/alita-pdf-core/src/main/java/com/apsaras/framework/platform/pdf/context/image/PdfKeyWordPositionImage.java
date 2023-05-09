package com.apsaras.framework.platform.pdf.context.image;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * PDF文件中的指定文字位置添加图片.
 *
 * @date 2023-02-13 22:38
 */
public class PdfKeyWordPositionImage {

    private static final Logger logger = LoggerFactory.getLogger(PdfKeyWordPositionImage.class);


    /**
     * 得到关键字位置.
     *
     * @param srcPdf 源文件
     * @param key_word 关键字
     * @param pageNum 文档页数
     * @param imagePath 图片路径
     */
    public static void getKeyWordsLocation(String srcPdf, String targetPdf, String key_word, int pageNum, String imagePath) {
        RegexBasedLocationExtractionStrategy strategy = new RegexBasedLocationExtractionStrategy(key_word);
        try {
            //得到需要插入的图片
            ImageData imageData = ImageDataFactory.create(imagePath);

            //核心思路为对PdfDocument对象采用某种Strategy，这里使用RegexBasedLocationExtractionStrategy
            PdfReader pdfReader = new PdfReader(srcPdf);
            //生成新的PDF文件
            PdfWriter pdfWriter = new PdfWriter(targetPdf);
            PdfDocument pdfDoc = new PdfDocument(pdfReader, pdfWriter);
            //int pageNum = pd.getNumberOfPages();获取新pdf的总页数
            Document document = new Document(pdfDoc);

            PdfDocumentContentParser contentParser = new PdfDocumentContentParser(pdfDoc);
            //文本内容具体解析借助使用PdfDocumentContentParser类(实质使用PdfCanvasProcessor进行处理)，对待处理页面装配合适策略
            contentParser.processContent(pageNum, strategy);

            //获取处理结果
            Collection<IPdfTextLocation> resultantLocations = strategy.getResultantLocations();
            //自定义结果处理
            if (!resultantLocations.isEmpty()) {
                for (IPdfTextLocation item : resultantLocations) {
                    Rectangle boundRectangle = item.getRectangle();
                    logger.debug("关键字：{}", item.getText());
                    logger.debug("关键字\"" + key_word + "\" 的坐标为 x: " + boundRectangle.getX() + " , y: " + boundRectangle.getY());
                    Image image = new Image(imageData)
                            .scaleAbsolute(70, 40)
                            .setFixedPosition(pageNum, boundRectangle.getRight() + 5f, boundRectangle.getBottom());
                    document.add(image);
                }
                document.close();
            } else {
                logger.warn("关键字 \"" + key_word + "\" 未找到！");
            }
            pdfReader.close();
            pdfDoc.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


}
