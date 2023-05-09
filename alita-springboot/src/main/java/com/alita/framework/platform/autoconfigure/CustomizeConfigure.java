package com.alita.framework.platform.autoconfigure;

import com.alita.framework.platform.bean.CustomizeBeanImport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = { CustomizeBeanImport.class })
public class CustomizeConfigure {

}
