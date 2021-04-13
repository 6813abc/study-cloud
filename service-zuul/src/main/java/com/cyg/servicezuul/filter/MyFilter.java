package com.cyg.servicezuul.filter;

import com.netflix.zuul.ZuulFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author cyg
 * @date 2021/1/28 11:01
 **/
@Component
public class MyFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(MyFilter.class);

    /**
     * 过滤器的类型 pre route post error
     **/
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 执行顺序，返回值越小，优先级越高
     **/
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 是否执行该过滤器
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }


    /**
     * 功能描述: <br>
     * 〈过滤器通过后的操作〉
     *
     * @author cyg
     * @date 2021/1/28 11:22
     */
    @Override
    public Object run() {
        log.info("通过过滤器");
        return null;
    }
}
