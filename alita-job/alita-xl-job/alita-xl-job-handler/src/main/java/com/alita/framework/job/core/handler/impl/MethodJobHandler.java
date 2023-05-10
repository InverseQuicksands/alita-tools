package com.alita.framework.job.core.handler.impl;

import com.alita.framework.job.core.handler.IJobHandler;

import java.lang.reflect.Method;

/**
 * MethodJobHandler
 *
 * @date 2022-11-25 23:24
 */
public class MethodJobHandler implements IJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }


    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @throws Exception
     */
    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 0) {
            method.invoke(target, new Object[paramTypes.length]);       // method-param can not be primitive-types
        } else {
            method.invoke(target);
        }
    }

    /**
     * init handler, invoked when JobThread init
     */
    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    /**
     * destroy handler, invoked when JobThread destroy
     */
    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
