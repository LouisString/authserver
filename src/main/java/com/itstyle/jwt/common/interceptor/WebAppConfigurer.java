package com.itstyle.jwt.common.interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/**
 * 拦截配置--调用链
 */
@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] patterns = new String[] { "/login","/*.html","/swagger-resources/**"};
		registry.addInterceptor(new SysInterceptor())
		                         .addPathPatterns("/**")
		                         .excludePathPatterns(patterns);
		super.addInterceptors(registry);
	}

}