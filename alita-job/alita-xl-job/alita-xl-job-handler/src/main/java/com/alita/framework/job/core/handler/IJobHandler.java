package com.alita.framework.job.core.handler;

/**
 * IJobHandler
 *
 * @date 2022-11-24 23:06
 */
public interface IJobHandler {

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @throws Exception
     */
    void execute() throws Exception;


	/*@Deprecated
	public abstract ReturnT<String> execute(String param) throws Exception;*/

    /**
     * init handler, invoked when JobThread init
     */
   void init() throws Exception;


    /**
     * destroy handler, invoked when JobThread destroy
     */
    void destroy() throws Exception;

}
