package io.renren.modules.oss.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.renren.common.poi.PdfHelper;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.oss.entity.AssayQueryDto;
import io.renren.modules.oss.entity.AssayTestEntity;
import io.renren.modules.oss.service.AssayService;
import io.renren.modules.oss.util.AsianFontProvider;
import io.renren.modules.oss.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 查询相应结果
 */
@RestController
@RequestMapping("/assay")
public class AssayController {

    private static Logger logger = LoggerFactory.getLogger(AssayController.class);

    @Autowired
    private AssayService assayService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        AssayQueryDto dto = new AssayQueryDto();
        dto.setUsername((String) params.get("username"));
        dto.setSendExamineBeginTime((String) params.get("sendExamineBeginTime"));
        dto.setSendExamineEndTime((String) params.get("sendExamineEndTime"));
        dto.setOutPaientNo((String) params.get("outPaientNo"));
        dto.setDoctor((String) params.get("doctor"));
        dto.setReportBeginTime((String) params.get("reportBeginTime"));
        dto.setReportEndTime((String) params.get("reportEndTime"));
        dto.setBarCode((String) params.get("barCode"));
        dto.setPatientName((String) params.get("patientName"));
        dto.setReportType((String) params.get("reportType"));
        dto.setNormal((String) params.get("normal"));
        dto.setPrintStatus((String) params.get("printStatus"));
        dto.setInstrumentNo((String) params.get("instrumentNo"));
        dto.setMaterialStatus((String) params.get("materialStatus"));
        PageUtils page = assayService.queryPage(params, dto);

        return R.ok().put("page", page);
    }

    @RequestMapping("/detail")
    public R getDetail(@RequestParam Map<String, Object> params) {
        PageUtils page = assayService.queryDetail(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/queryDetail")
    public ModelAndView getDetail() {
        List<AssayTestEntity> list = Lists.newArrayList();
        AssayTestEntity entity = new AssayTestEntity();
        entity.setContent("aaa");
        entity.setName("bbb");
        entity.setNormalValue("100-200");
        entity.setUnit("mg");
        list.add(entity);
        AssayTestEntity entity2 = new AssayTestEntity();
        entity2.setContent("ccc");
        entity2.setName("ddd");
        entity2.setNormalValue("100-200");
        entity2.setUnit("mg");
        list.add(entity2);
        Map<String, Object> returnMap = Maps.newHashMap();
        returnMap.put("assayList", list);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/modules/assay/assay");
        mv.addAllObjects(returnMap);

        return mv;
    }

    @RequestMapping("/downloadPdf")
    public void viewPdf(HttpServletRequest request, HttpServletResponse response) {
        //需要传递到页面的数据
        Map<Object, Object> data=new HashMap<Object, Object>();
        //存入一个集合
        List<Map> list = new ArrayList<Map>();

        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("shortHand","SAG");
        map1.put("item","血常规");
        map1.put("result","阴性");
        map1.put("tips","");
        map1.put("unit","");
        map1.put("interval","阴性");
        list.add(map1);

        Map<String, Object> map2 = Maps.newHashMap();
        map2.put("shortHand", "SAG");
        map2.put("item", "血常规");
        map2.put("result", "阴性");
        map2.put("tips", "");
        map2.put("unit", "");
        map2.put("interval", "阴性");
        list.add(map2);

        data.put("name", "http://www.xdemo.org/");
        data.put("nameList", list);


        try {
            Configuration freemarkerCfg = new Configuration();
            Writer out = new StringWriter();
            String currentPath = PathUtil.getCurrentPath() + "/templates/";

            //html所在文件夹
            freemarkerCfg.setDirectoryForTemplateLoading(new File(currentPath));
            freemarkerCfg.setDefaultEncoding("UTF-8");

            //html文件模板名称
            Template template = freemarkerCfg.getTemplate("tpl.html");
            template.setEncoding("UTF-8");
            // 合并数据模型与模板
            template.process(data, out); //将合并后的数据和模板写入到流中，这里使用的字符流
            out.flush();
            String content = out.toString();
            try {
                out.close();
            } catch (Exception e) {
                logger.error("writer 流关闭失败", e);
            }

            //生成pdf
            ITextRenderer render = PdfHelper.getRender();

            render.setDocumentFromString(content);
            render.layout();
            render.createPDF(response.getOutputStream());
            render.finishPDF();
            render = null;
            out.close();

        } catch (Exception e) {
            logger.error("viewPdf", e);
        }
    }




//    @RequestMapping("/downloadPdf")
//    public void viewPdf(HttpServletRequest request, HttpServletResponse response) {
//        //需要传递到页面的数据
//        List<AssayTestEntity> list = Lists.newArrayList();
//        AssayTestEntity entity = new AssayTestEntity();
//        entity.setContent("aaa");
//        entity.setName("bbb");
//        entity.setNormalValue("100-200");
//        entity.setUnit("mg");
//        list.add(entity);
//        AssayTestEntity entity2 = new AssayTestEntity();
//        entity2.setContent("ccc");
//        entity2.setName("ddd");
//        entity2.setNormalValue("100-200");
//        entity2.setUnit("mg");
//        list.add(entity2);
//
//        Map<String, Object> data = new HashMap();
//        data.put("assayList", list);
//        data.put("name", "刘明");
//        try {
//            Configuration freemarkerCfg = new Configuration();
//            Writer out = new StringWriter();
//            String currentPath = PathUtil.getCurrentPath() + "/templates/";
//
//            //html所在文件夹
//            freemarkerCfg.setDirectoryForTemplateLoading(new File(currentPath));
//            freemarkerCfg.setDefaultEncoding("UTF-8");
//
//            //html文件模板名称
//            Template template = freemarkerCfg.getTemplate("template.html");
//            template.setEncoding("UTF-8");
//            // 合并数据模型与模板
//            template.process(data, out); //将合并后的数据和模板写入到流中，这里使用的字符流
//            out.flush();
//            String content = out.toString();
//            try {
//                out.close();
//            } catch (Exception e) {
//                logger.error("writer 流关闭失败", e);
//            }
//
//            //生成pdf
//            // step 1
//            Rectangle rectangle = new Rectangle(PageSize.A4);
//            //页面反置
//            rectangle.rotate();
//            Document document = new Document(PageSize.A5.rotate());
//
//            // step 2
//            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
//            // step 3
//            document.open();
//            // step 4
//            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(content.getBytes("UTF-8")), null, Charset.forName("UTF-8"),new AsianFontProvider());
//            // step 5
//            document.close();
//
//        } catch (Exception e) {
//            logger.error("viewPdf", e);
//        }
//    }

//    @RequestMapping("/downloadPdf2")
//    public void createPdf(HttpServletRequest request, HttpServletResponse response) {
//        //需要传递到页面的数据
//        List<AssayEntity> list = Lists.newArrayList();
//        AssayEntity entity = new AssayEntity();
//        entity.setReportType("1");
//        entity.setSex("男");
//        entity.setExamineTime("2018-10-09");
//        entity.setReportType("2018-09-09");
//        entity.setAge("20");
//        entity.setBarCode("1009090909");
//        entity.setDepartment("门诊");
//        list.add(entity);
//
//
//        Map<String, Object> data = new HashMap();
//        data.put("assayList", list);
//        data.put("name","刘明");
//
//        try {
//            Configuration freemarkerCfg = new Configuration();
//            Writer out = new StringWriter();
//            String currentPath = PathUtil.getCurrentPath() + "/templates/";
//
//            //html所在文件夹
//            freemarkerCfg.setDirectoryForTemplateLoading(new File(currentPath));
//
//            //html文件模板名称
//            Template template = freemarkerCfg.getTemplate("template.html");
//            template.setEncoding("UTF-8");
//            // 合并数据模型与模板
//            template.process(data, out); //将合并后的数据和模板写入到流中，这里使用的字符流
//            out.flush();
//            String content = out.toString();
//            try {
//                out.close();
//            } catch (Exception e) {
//                logger.error("writer 流关闭失败", e);
//            }
//
//            ITextRenderer render = new ITextRenderer();
//            ITextFontResolver fontResolver = render.getFontResolver();
//            fontResolver.addFont("C:\\Windows\\Fonts\\simsunb.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//            // 解析html生成pdf
//
//            render.setDocumentFromString(content);
//            //解决图片相对路径的问题
//            String logoPath = PathUtil.getCurrentPath() + "/css/";
//            render.getSharedContext().setBaseURL(logoPath);
//            render.layout();
//            render.createPDF(response.getOutputStream());
//
//        } catch (Exception e) {
//            logger.error("viewPdf", e);
//        }
//    }
}
