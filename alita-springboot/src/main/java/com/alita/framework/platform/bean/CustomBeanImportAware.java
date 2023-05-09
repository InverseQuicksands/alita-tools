package com.alita.framework.platform.bean;

import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

public class CustomBeanImportAware implements ImportAware {

    /**
     * Set the annotation metadata of the importing @{@code Configuration} class.
     *
     * @param importMetadata
     */
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        
    }
}
