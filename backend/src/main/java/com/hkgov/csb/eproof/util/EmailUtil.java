package com.hkgov.csb.eproof.util;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Component
public class EmailUtil {

    private final Configuration configuration;
    public EmailUtil() {
        configuration = new Configuration(Configuration.VERSION_2_3_30);
    }

    public String getRenderedHtml(String emailTemplate, Map<String, Object> mergeMap) throws IOException, TemplateException {

        Template template = new Template(null,emailTemplate,configuration);

        StringWriter sw = new StringWriter();
        template.process(mergeMap,sw);

        return sw.toString();
    }

    public Map<String,Object> combineMapToDataModelMap(Map<String,Object> ... maps){
        Map<String,Object> dataModel = new java.util.HashMap<>();
        for(Map<String,Object> map : maps){
            dataModel.putAll(map);
        }
        return dataModel;
    }
}
